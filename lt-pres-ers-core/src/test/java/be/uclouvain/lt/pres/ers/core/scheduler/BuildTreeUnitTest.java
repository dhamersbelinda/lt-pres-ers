package be.uclouvain.lt.pres.ers.core.scheduler;

import be.uclouvain.lt.pres.ers.core.persistence.model.*;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BuildTreeUnitTest {

//    @Autowired
//    BuildTreeTask task;

    public BuildTreeUnitTest() {
        super();
    }

    @Test
    public void testSinglePOID() {
        List<Treeable> leaves = new ArrayList<>();

        POID poid1 = new POID();
        byte[] bytes1 = "poid1".getBytes(StandardCharsets.UTF_8);
        poid1.setDigestValue(bytes1);

        leaves.add(poid1);

        HashTreeBase htb = new HashTreeBase(new TreeID(), 1L, DigestAlgorithm.SHA256, leaves);

        Node root = BuildTreeTask.buildTree(htb);

        Assertions.assertEquals(0, root.getInTreeId(), "Wrong in tree ID !");
        Assertions.assertEquals(bytes1, root.getNodeValue(), "Wrong node value !");
        Assertions.assertTrue(root.getChildren() == null || root.getChildren().size() == 0, "Root has children");
        Assertions.assertNull(root.getParent(), "Root has a parent");
    }

    @Test
    public void testTwoPOID() {
        List<Treeable> leaves = new ArrayList<>();

        POID poid1 = new POID();
        byte[] bytes1 = "poid1".getBytes(StandardCharsets.UTF_8);
        poid1.setDigestValue(bytes1);

        leaves.add(poid1);

        POID poid2 = new POID();
        byte[] bytes2 = "poid2".getBytes(StandardCharsets.UTF_8);
        poid1.setDigestValue(bytes2);

        leaves.add(poid2);

        HashTreeBase htb = new HashTreeBase(new TreeID(), 1L, DigestAlgorithm.SHA256, leaves);

        Node root = BuildTreeTask.buildTree(htb);

        Assertions.assertEquals(0, root.getInTreeId(), "Wrong in tree ID !");
        Assertions.assertEquals(bytes1, root.getNodeValue(), "Wrong node value !");
        Assertions.assertTrue(root.getChildren() == null || root.getChildren().size() == 0, "Root has children");
        Assertions.assertNull(root.getParent(), "Root has a parent");
    }
}
