package be.uclouvain.lt.pres.ers.core.scheduler;

import be.uclouvain.lt.pres.ers.core.persistence.model.*;
import be.uclouvain.lt.pres.ers.core.persistence.model.comparator.NodeBinaryComparator;
import be.uclouvain.lt.pres.ers.utils.ByteUtils;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.spi.DSSUtils;
import lombok.*;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

//class that can be used to build a Merkle hash-tree

@Getter
@Setter
@ToString
@NoArgsConstructor
public class HashTreeBase {
    public HashTreeBase(TreeID treeID, Long clientId, DigestAlgorithm digestMethod, List<Treeable> leaves) {
        this.treeID = treeID;
        this.clientId = clientId;
        this.digestMethod = digestMethod;
        this.leaves = leaves;
    }

    private int BRANCHING_FACTOR = 2;

    private TreeID treeID;
    private Long clientId;
    private DigestAlgorithm digestMethod; //has become superfluous now, or maybe not ? correct type ?
//    private List<POCompressed> poCompressedList;
    private List<Treeable> leaves;

    public Node buildTree() {
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
        int depth = (int) Math.ceil(Math.log(nLeaves) / Math.log(BRANCHING_FACTOR)); // log_b (x) = ln(x)/ln(b)
        // int treeSize = (int) ((Math.pow(BRANCHING_FACTOR, depth+1) - 1)/((double) (BRANCHING_FACTOR - 1)));
        int mod = nLeaves % BRANCHING_FACTOR;
        int fullNum = mod == 0 ? nLeaves : nLeaves + BRANCHING_FACTOR - mod;
        Node[] buf = new Node[fullNum];
        int d = 0;
        int firstLvlNodeNum = (int) ((Math.pow(BRANCHING_FACTOR, depth) - 1)/((double) (BRANCHING_FACTOR - 1)));
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

        int leapIndex = 0;   // Advance by BRANCHING_FACTOR
        int insertIndex = 0; // Advance of 1
        int runnerIndex = 0; // Used to scan children

        int realNum = nLeaves; //at current level
        Node currentNode;
        Node parentNode;
        List<Node> children;
        List<byte[]> toConcat = new ArrayList<>(BRANCHING_FACTOR);
        byte[] toHash;
        // Loop on every 'floor' of the tree
        for (d = depth-1; d >= 0; d--) {
            firstLvlNodeNum = (int) ((Math.pow(BRANCHING_FACTOR, d) - 1)/((double) (BRANCHING_FACTOR - 1))); // for parent's in_tree_id field
            // First check if we have to add dummy nodes
            if(realNum < fullNum){
                int lowerLvlNodeNum = firstLvlNodeNum + ((int) Math.pow(BRANCHING_FACTOR, d));
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
            for (leapIndex = 0; leapIndex < fullNum; leapIndex = leapIndex + BRANCHING_FACTOR) {
                int sum = 0; //placeholder for empty hash
                //create parent Node
                parentNode = new Node();
                parentNode.setTreeId(treeID);
                parentNode.setInTreeId(firstLvlNodeNum + insertIndex);
                children = new ArrayList<>(BRANCHING_FACTOR);
                toConcat.clear();
                for (runnerIndex = leapIndex; runnerIndex < leapIndex + BRANCHING_FACTOR; runnerIndex++) {
                    currentNode = buf[runnerIndex];
                    //set parent-child-neighbour relation
                    currentNode.setParent(parentNode);
                    children.add(currentNode);

                    //supposing branching factor of 2
                    //leapIndex + BRANCHING_FACTOR-1 - (runnerIndex-leapIndex)
                    // TODO : support more than one neighbour
//                    currentNode.setNeighbour(buf[2*leapIndex - runnerIndex + BRANCHING_FACTOR - 1]);

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
            realNum = fullNum / BRANCHING_FACTOR;
            mod = realNum % BRANCHING_FACTOR;
            fullNum = mod == 0 ? realNum : realNum + BRANCHING_FACTOR - mod ;
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
