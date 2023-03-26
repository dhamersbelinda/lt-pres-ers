package be.uclouvain.lt.pres.ers.core.scheduler;

import be.uclouvain.lt.pres.ers.core.persistence.model.*;
import be.uclouvain.lt.pres.ers.core.persistence.model.comparator.NodeInTreeIdComparator;
import be.uclouvain.lt.pres.ers.utils.BinaryOrderComparator;
import be.uclouvain.lt.pres.ers.utils.ByteUtils;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.spi.DSSUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // The setup() is not used in all tests but avoids duplicate code
public class BuildTreeUnitTest {

    @Spy
    HashTreeBase htbMock;

    @BeforeEach
    public void setup() {
        this.htbMock = spy(HashTreeBase.class);
        doReturn("dummy".getBytes(StandardCharsets.UTF_8)).when(htbMock).getDummyBytes(anyInt(), nullable(SecureRandom.class));
    }

    private Node createNode(TreeID treeID, Long inTreeID, byte[] nodeValue, List<Node> children){
        Node r = new Node();
        r.setTreeId(treeID);
        r.setInTreeId(inTreeID);
        r.setNodeValue(nodeValue);
        r.setChildren(children);
        if(children != null && !children.isEmpty()){
            for (Node child : children) {
                child.setParent(r);
            }
        }
        return r;
    }

    // Top down check
    private void checkTreesEqualStructure(Node root1, Node root2) {
        assertTrue(checkNodesEqual(root1, root2), "Those nodes are not equal : \n"+ root1 + "\n" + root2);
        assertTrue(checkNodesEqual(root1, root2), "Those node's parents are not equal : \n"+ root1 + " with parent "+ root1.getParent() + "\n" + root2 + " with parent "+ root2.getParent());
        if(root1.getChildren() == null && root2.getChildren() == null) {
            // check leaf link ?
        } else if (root1.getChildren() != null && root2.getChildren() != null) {
            assertEquals(root1.getChildren().size(), root2.getChildren().size(), "Those nodes have a different number of children : " + root1 + "\n" + root2);
            root1.getChildren().sort(new NodeInTreeIdComparator());
            root2.getChildren().sort(new NodeInTreeIdComparator());
            for (int i = 0; i < root2.getChildren().size(); i++) {
                assertTrue(checkNodesEqual(root1.getChildren().get(i), root2.getChildren().get(i)), "Those node's "+ i +"th child is not equal : \n"+ root1 + " whose child is "+ root1.getChildren().get(i) + "\n" + root2 + " whose child is "+  root2.getChildren().get(i));
                checkTreesEqualStructure(root1.getChildren().get(i), root2.getChildren().get(i));
            }
        } else {
            fail("Not the same children : \nNode 1's children : "+root1.getChildren()+"\nNode 2's children : "+root2.getChildren());
        }
    }

    private boolean checkNodesEqual(Node n1, Node n2) {
        if(n1 == null && n2 == null) {
            return true;
        } else if (n1 == null || n2 == null) {
            return false;
        }
        if(!n1.getTreeId().equals(n2.getTreeId())) return false;
        if(n1.getInTreeId() != n2.getInTreeId()) return false;
        if(!Arrays.equals(n1.getNodeValue(), n2.getNodeValue())) return false;

        return true;
    }

    // Binary or not does not change anything here
    private Node getSinglePoidTree(Treeable poid, TreeID treeID) {
        return createNode(treeID, 0L, poid.getHashValue(), null);
    }

    private Node getDoublePoidBinaryTree(List<Treeable> leaves, TreeID treeID, DigestAlgorithm alg) {
        Node n1 = createNode(treeID, 1L, leaves.get(0).getHashValue(), null);
        Node n2 = createNode(treeID, 2L, leaves.get(1).getHashValue(), null);

        byte[] concat = BinaryOrderComparator.compareBytes(leaves.get(0).getHashValue(), leaves.get(1).getHashValue()) > 0 ?
                ByteUtils.concat(leaves.get(1).getHashValue(), leaves.get(0).getHashValue()) :
                ByteUtils.concat(leaves.get(0).getHashValue(), leaves.get(1).getHashValue());

        return createNode(treeID, 0L, DSSUtils.digest(alg, concat), new ArrayList<>(List.of(new Node[]{n1, n2})));
    }

    private Node getTriplePoidBinaryTree(List<Treeable> leaves, TreeID treeID, DigestAlgorithm alg) {
        Node n3 = createNode(treeID, 3L, leaves.get(0).getHashValue(), null);
        Node n4 = createNode(treeID, 4L, leaves.get(1).getHashValue(), null);

        Node n5 = createNode(treeID, 5L, leaves.get(2).getHashValue(), null);
        // Dummy node
        Node n6 = createNode(treeID, 6L, this.htbMock.getDummyBytes(1,null), null);

        byte[] concat34 = BinaryOrderComparator.compareBytes(leaves.get(0).getHashValue(), leaves.get(1).getHashValue()) > 0 ?
                ByteUtils.concat(leaves.get(1).getHashValue(), leaves.get(0).getHashValue()) :
                ByteUtils.concat(leaves.get(0).getHashValue(), leaves.get(1).getHashValue());
        Node n1 = createNode(treeID, 1L, DSSUtils.digest(alg, concat34), new ArrayList<>(List.of(new Node[]{n3, n4})));

        byte[] concat56 = BinaryOrderComparator.compareBytes(n5.getNodeValue(), n6.getNodeValue()) > 0 ?
                ByteUtils.concat(n6.getNodeValue(), n5.getNodeValue()) :
                ByteUtils.concat(n5.getNodeValue(), n6.getNodeValue());
        Node n2 = createNode(treeID, 2L, DSSUtils.digest(alg, concat56), new ArrayList<>(List.of(new Node[]{n5, n6})));

        byte[] concat12 = BinaryOrderComparator.compareBytes(n1.getNodeValue(), n2.getNodeValue()) > 0 ?
                ByteUtils.concat(n2.getNodeValue(), n1.getNodeValue()) :
                ByteUtils.concat(n1.getNodeValue(), n2.getNodeValue());

        return createNode(treeID, 0L, DSSUtils.digest(alg, concat12), new ArrayList<>(List.of(new Node[]{n1, n2})));
    }

    @Test
    public void testSinglePOID() {
        List<Treeable> leaves = new ArrayList<>();

        POID poid1 = new POID();
        byte[] bytes1 = "poid1".getBytes(StandardCharsets.UTF_8);
        poid1.setDigestValue(bytes1);

        leaves.add(poid1);

        TreeID treeID = new TreeID();

        HashTreeBase htb = new HashTreeBase(treeID, 1L, DigestAlgorithm.SHA256, leaves);

        Node root = htb.buildTree();

        checkTreesEqualStructure(getSinglePoidTree(poid1, treeID), root);

        assertEquals(0, root.getInTreeId(), "Wrong in tree ID !");
        assertEquals(bytes1, root.getNodeValue(), "Wrong node value !");
        assertTrue(root.getChildren() == null || root.getChildren().size() == 0, "Root has children");
        assertNull(root.getParent(), "Root has a parent");
    }

    @Test
    public void testTwoPOID() {
        List<Treeable> leaves = new ArrayList<>();
        DigestAlgorithm alg = DigestAlgorithm.SHA256;

        HashMap<byte[], Boolean> digests = new HashMap<>();

        POID poid1 = new POID();
        byte[] bytes1 = "poid1".getBytes(StandardCharsets.UTF_8);
        poid1.setDigestValue(bytes1);

        digests.put(bytes1, false);
        leaves.add(poid1);

        POID poid2 = new POID();
        byte[] bytes2 = "poid2".getBytes(StandardCharsets.UTF_8);
        poid2.setDigestValue(bytes2);

        digests.put(bytes2, false);
        leaves.add(poid2);

        TreeID treeID = new TreeID();

        HashTreeBase htb = new HashTreeBase(treeID, 1L, alg, leaves);

        Node root = htb.buildTree();

        Node correct = getDoublePoidBinaryTree(leaves, treeID, DigestAlgorithm.SHA256);
        checkTreesEqualStructure(correct, root);
    }

    @Test
    public void testThreePOID() {
        List<Treeable> leaves = new ArrayList<>();
        DigestAlgorithm alg = DigestAlgorithm.SHA256;

        HashMap<byte[], Boolean> digests = new HashMap<>();

        POID poid1 = new POID();
        byte[] bytes1 = "poid1".getBytes(StandardCharsets.UTF_8);
        poid1.setDigestValue(bytes1);

        digests.put(bytes1, false);
        leaves.add(poid1);

        POID poid2 = new POID();
        byte[] bytes2 = "poid2".getBytes(StandardCharsets.UTF_8);
        poid2.setDigestValue(bytes2);

        digests.put(bytes2, false);
        leaves.add(poid2);

        POID poid3 = new POID();
        byte[] bytes3 = "poid3".getBytes(StandardCharsets.UTF_8);
        poid3.setDigestValue(bytes3);

        digests.put(bytes3, false);
        leaves.add(poid3);

        TreeID treeID = new TreeID();

        htbMock.setTreeID(treeID);
        htbMock.setClientId(1L);
        htbMock.setDigestMethod(alg);
        htbMock.setLeaves(leaves);

        Node root = htbMock.buildTree();

        Node correct = getTriplePoidBinaryTree(leaves, treeID, DigestAlgorithm.SHA256);
        checkTreesEqualStructure(correct, root);
    }
}
