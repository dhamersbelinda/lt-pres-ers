package be.uclouvain.lt.pres.ers.core.scheduler;

import be.uclouvain.lt.pres.ers.core.persistence.model.*;
import be.uclouvain.lt.pres.ers.core.persistence.model.comparator.NodeBinaryComparator;
import be.uclouvain.lt.pres.ers.utils.ByteUtils;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.spi.DSSUtils;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

//class that can be used to build a Merkle hash-tree

@Getter
@Setter
@ToString
@NoArgsConstructor
public class HashTreeBase {

    private final Logger logger = LoggerFactory.getLogger(HashTreeBase.class);

    public HashTreeBase(TreeID treeID, Long clientId, DigestAlgorithm digestMethod, List<Treeable> leaves) {
        this.treeID = treeID;
        this.clientId = clientId;
        this.digestMethod = digestMethod;
        this.leaves = leaves;
    }

    private TreeID treeID;
    private Long clientId;
    private DigestAlgorithm digestMethod; //has become superfluous now, or maybe not ? correct type ?
//    private List<POCompressed> poCompressedList;
    private List<Treeable> leaves;

    public Node buildTree(int branchingFactor) {
        /*  InTreeId = (2^depth + i - 1)     i in [0, 2^depth[
           0             0
                       /   \
           1          1     2
                     / \   / \
           2        3   4 5   6
         */

        SecureRandom rd = new SecureRandom();
        int size = this.digestMethod.getSaltLength() > 0 ?
                this.digestMethod.getSaltLength() :
                32;
        byte[] dummyBuffer;

        int nLeaves = this.leaves.size();
        int depth = (int) Math.ceil(Math.log(nLeaves) / Math.log(branchingFactor)); // log_b (x) = ln(x)/ln(b)
        // int treeSize = (int) ((Math.pow(branchingFactor, depth+1) - 1)/((double) (branchingFactor - 1)));
        int mod = nLeaves % branchingFactor;
        int fullNum = mod == 0 ? nLeaves : nLeaves + branchingFactor - mod;
        Node[] buf = new Node[fullNum];
        int d = 0;
        int firstLvlNodeNum = (int) ((Math.pow(branchingFactor, depth) - 1)/((double) (branchingFactor - 1)));
        Node temp;
        TreeID treeID = this.treeID;
        // transform leaves in node object & put in array
        for (Treeable leaf:this.leaves) {
            temp = new Node();
            temp.setLeafLink(leaf);
            temp.setNodeValue(leaf.getHashValue()); // TODO : append and hash the digests in a single node AND add children to this node ...
            // TODO : for roots : verify that all verification data is present and if not get it then canonicalize then hash the canonical binary (rfc 6283 4.2.1)
            temp.setTreeId(treeID);
            temp.setInTreeId(firstLvlNodeNum + d);
            buf[d] = temp;
            d++;
        }

        int leapIndex = 0;   // Advance by branchingFactor
        int insertIndex = 0; // Advance of 1
        int runnerIndex = 0; // Used to scan children

        int realNum = nLeaves; //at current level
        Node currentNode;
        Node parentNode;
        List<Node> children;
        List<byte[]> toConcat = new ArrayList<>(branchingFactor);
        byte[] toHash;
        // Loop on every 'floor' of the tree
        for (d = depth-1; d >= 0; d--) {
            firstLvlNodeNum = (int) ((Math.pow(branchingFactor, d) - 1)/((double) (branchingFactor - 1))); // for parent's in_tree_id field
            // First check if we have to add dummy nodes
            if(realNum < fullNum){
                int lowerLvlNodeNum = firstLvlNodeNum + ((int) Math.pow(branchingFactor, d));
                for (int i = realNum; i < fullNum; i++) {
                    currentNode = new Node();
                    currentNode.setTreeId(treeID);
                    currentNode.setInTreeId(lowerLvlNodeNum + i);
                    // TODO set dummy value of proper length given hash algo
                    dummyBuffer = getDummyBytes(size, rd);
                    currentNode.setNodeValue(dummyBuffer);
                    buf[i] = currentNode;
                }
            }
            // Second reduce all nodes in the array to their parent
            for (leapIndex = 0; leapIndex < fullNum; leapIndex = leapIndex + branchingFactor) {
                int sum = 0; //placeholder for empty hash
                //create parent Node
                parentNode = new Node();
                parentNode.setTreeId(treeID);
                parentNode.setInTreeId(firstLvlNodeNum + insertIndex);
                children = new ArrayList<>(branchingFactor);
                toConcat.clear();
                for (runnerIndex = leapIndex; runnerIndex < leapIndex + branchingFactor; runnerIndex++) {
                    currentNode = buf[runnerIndex];
                    //set parent-child-neighbour relation
                    currentNode.setParent(parentNode);
                    children.add(currentNode);

                    //supposing branching factor of 2
                    //leapIndex + branchingFactor-1 - (runnerIndex-leapIndex)
                    // TODO : support more than one neighbour
//                    currentNode.setNeighbour(buf[2*leapIndex - runnerIndex + branchingFactor - 1]);

                    sum = sum + 1; // TODO placeholder op to concatenate hash
                }

                // TODO compute concatenated hash value, set value in parent node
                // Sort the chi
                children.sort(new NodeBinaryComparator());
                for (Node child : children) {
                    toConcat.add(child.getNodeValue());
                }
                toHash = ByteUtils.concat(toConcat);

                parentNode.setNodeValue(DSSUtils.digest(this.digestMethod, toHash));

                parentNode.setChildren(children);
                //insert parent Node
                buf[insertIndex] = parentNode;
                insertIndex++;
                sum = 0;
            }
            realNum = fullNum / branchingFactor;
            mod = realNum % branchingFactor;
            fullNum = mod == 0 ? realNum : realNum + branchingFactor - mod ;
            insertIndex = 0;
        }
        return buf[0];
    }

    // public to allow partial stubbing ...
    public byte[] getDummyBytes(int sizeInBytes, SecureRandom random) {
        if(sizeInBytes <= 0) throw new IllegalArgumentException("sizeInBytes cannot be negative or null.");
        byte[] r = new byte[sizeInBytes];
        random.nextBytes(r);
        return r;
    }
}
