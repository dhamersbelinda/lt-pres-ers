package be.uclouvain.lt.pres.ers.core.scheduler;

import be.uclouvain.lt.pres.ers.core.persistence.model.*;
import be.uclouvain.lt.pres.ers.core.persistence.repository.TemporaryRepository;
import lombok.AllArgsConstructor;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.List;

@Component
@AllArgsConstructor //TODO is this annotation here correct ? copied by imitation of services ("necessary" to have the repo)
public class BuildTreeTask {

    private final static int BRANCHING_FACTOR = 2;

    private final TemporaryRepository temporaryRepository;

//    @Scheduled(cron = "* 59 23 * * ?") // TODO : this should be every day at midnight
//    @Scheduled(cron = "0 0 0 1/1 * ?") // TODO : this should be every day at midnight (fancy)
    @Scheduled(cron = "0 * * * * ?") // TODO : every minute at 0 sec for development purpose
    @SchedulerLock(name = "TaskScheduler_scheduledTask",
            lockAtLeastForString = "PT5s", lockAtMostForString = "PT25s") // TODO find proper duration
    public void scheduledTask() {
        System.out.println("Done at " + OffsetDateTime.now());

        List<TemporaryRecord> temporaryRecords = temporaryRepository.findAllBy();

        //make a list of HashTreeBases

        temporaryRecords.sort(new Comparator<TemporaryRecord>() {
            @Override
            public int compare(TemporaryRecord o1, TemporaryRecord o2) {
                int comp = Integer.compare(o1.getClientId(), o2.getClientId());
                //separate by clients
                if (comp == 0) {
                    int comp1 = o1.getDigestList().getDigestMethod().compareTo(o2.getDigestList().getDigestMethod());
                    if (comp1 == 0) {
                        //sort by POID
                        int comp2 = o1.getPoid().getId().compareTo(o2.getPoid().getId());
                        if (comp2 == 0) {
                            //sort by digest value
                            //compare by binary form (how to obtain ?)
                            int comp3 = 0;
                            //TODO complete here, sort by binary value
                        }
                        return comp2;
                    }
                    return comp1;
                }
                return comp;
            }
        });

        //collect all bases for tree construction
        List<HashTreeBase> hashTreeBaseList = new ArrayList<>();

        int prevClientId = temporaryRecords.get(0).getClientId();
        URI prevDigestMethod = temporaryRecords.get(0).getDigestList().getDigestMethod();
        int index = 1;
        HashTreeBase htb = new HashTreeBase();
        htb.setClientId(prevClientId);
        htb.setDigestMethod(prevDigestMethod);
        List<POCompressed> poCompressedList = new ArrayList<>();
        POCompressed poCompressed = new POCompressed();
        POID prevPoid = temporaryRecords.get(0).getPoid();
        poCompressed.setPoid(prevPoid);
        List<Digest> digests = new ArrayList<>();
        digests.add(temporaryRecords.get(0).getDigest());
        List<Integer> digNums = new ArrayList<>();
        digNums.add(temporaryRecords.get(0).getDigNum()); //TODO check how we handle this order
        while (index < temporaryRecords.size()) {
            TemporaryRecord curr = temporaryRecords.get(index);
            int currClientId = curr.getClientId();
            URI currDigestMethod = curr.getDigestList().getDigestMethod();
            //continue checking for POID

            if (currClientId != prevClientId || !currDigestMethod.equals(prevDigestMethod)) {
                htb.setPoCompressedList(poCompressedList);
                hashTreeBaseList.add(htb);

                htb = new HashTreeBase();
                htb.setClientId(currClientId);
                htb.setDigestMethod(currDigestMethod);

                prevClientId = currClientId;
                prevDigestMethod = currDigestMethod;

                poCompressedList = new ArrayList<>();
            }

            if (!prevPoid.equals(curr.getPoid())) { //we create a new POCompressed
                poCompressed.setDigests(digests);
                poCompressed.setDigNums(digNums);

                poCompressedList.add(poCompressed);

                //new digests and dignums and POCompressed
                digests = new ArrayList<>();
                digNums = new ArrayList<>();
                poCompressed = new POCompressed();
                poCompressed.setPoid(curr.getPoid());

                prevPoid = curr.getPoid();

                //there might be a potential problem here with the references (but probably not)
                //(check POCompressedList)
            }
            digests.add(curr.getDigest());
            digNums.add(curr.getDigNum());

            index++;
        }
        poCompressed.setDigests(digests);
        poCompressed.setDigNums(digNums);

        poCompressedList.add(poCompressed);
        htb.setPoCompressedList(poCompressedList);
        hashTreeBaseList.add(htb);

        //all trees are in HashTreeBase
        for (HashTreeBase hashTreeBase: hashTreeBaseList) {
            TreeID treeID = new TreeID();
            hashTreeBase.setTreeID(treeID);
            Node rootNode = buildTree(hashTreeBase);
            // TODO : get a real timestamp with DSS
            int timestamp = 2;

            Root root = new Root();
            root.setNode(rootNode); // necessary ? useful ? TODO : EXPERIMENT
            root.setTimestamp(timestamp);
            rootNode.setRoot(root);

            // TODO : insert rootNode, check cascade types !
        }


        //BIG TODO
        //concatenation : which canonicalization ?
        // find good hash method
        // impose digestMethod on client ?
        // some attributes (in the classes before construction of the tree) can be removed (or added)

        // building the tree : array layout, array size depending on nbr of leaves (trimmed)
        /*
            List<TemporaryRecords> toStore; => what we receive
            int branchingFactor = 2;  => parameter, number of childs of each node, must be >=2

            toStore.sort(); => sort in a way if we have to (binary ascending would be great)
            int nLeaves = toStore.length();
            int depth = Math.ceil(Math.log(branchingFactor ,nLeaves));    // root at depth 0
            int treeSize = (branchingFactor ^ (depth+1)   - 1)/(branchingFactor - 1)     // https://math.stackexchange.com/questions/664608/number-of-nodes-in-binary-tree-given-number-of-leaves#:~:text=The%20number%20of%20nodes%20would,the%20number%20of%20leaf%20nodes.
            TreeNode[] tree = new TreeNode[treeSize] => cannot trim the array's size here ... but will be trimmed in DB storage ! idx 0 = root
            // index of 'first' node at depth d : branchingFactor^d  - 1
            int i = 0, d = 0, g = 0;
            // add all leaves
            foreach(TemporaryRecord leaf:toStore) {
                tree[branchingFactor^depth +i-2] = leaf;
                i++;
            }
            int nNodesLayer;
            String toConcat; => or byte array ? idk yet how we store hash values
            for(d=depth-1 ; d >= 0 ; d--) {
                // per layer
                nNodesLayer = Math.pow(branchingFactor,d);
                for(i=0 ; i < nNodesLayer ; i++) { // programmed explicitly : foreach parent { foreach of its child {concatenate value}}, probably better to do it like forall nodes in the child layer
                    for(g=0 ; g < branchingFactor ; g++){
                        toConcat.append(tree[(branchingFactor^(d+1)) + i*branchingFactor + g].getDigest());
                    }
                    tree[(branchingFactor^(d)) + i] = hash(toConcat);
                    toConcat = "";
                }
            }
         */

    }
    // TODO : support root nodes
    private static Node buildTree(HashTreeBase input) {

        // InTreeId = (2^depth + i - 1)     i in [0, 2^depth[
        /*
           0             0
                       /   \
           1          1     2
                     / \   / \
           2        3   4 5   6
         */

        int nLeaves = input.getPoCompressedList().size();
        int depth = (int) Math.ceil(Math.log(nLeaves) / Math.log(BRANCHING_FACTOR)); // log_b (x) = ln(x)/ln(b)
        // int treeSize = (int) ((Math.pow(BRANCHING_FACTOR, depth+1) - 1)/((double) (BRANCHING_FACTOR - 1)));

        Node[] buf = new Node[nLeaves % 2 == 0 ? nLeaves : nLeaves + 1];
        int d = 0;
        int firstLvlNodeNum = (int) Math.pow(2,depth);
        Node temp;
        TreeID treeID = input.getTreeID();
        // transform leaves in node object & put in array
        for (POCompressed po:input.getPoCompressedList()) {
            temp = new Node();
            temp.setPoid(po.getPoid());
            // TODO : treeid : create table to generate those IDs on the DB side, before calling this function insert in table to generate id and add it to the HashTreeBase object
            temp.setTreeId(treeID);
            temp.setInTreeId(firstLvlNodeNum - 1 + d);
            buf[d] = temp;
            d++;
        }



        //number of nodes on a level given depth (knowing previous number of nodes)
        //ceil_even(num_prev_level / 2)

        int leapIndex = 0; //advance by 2
        int insertIndex = 0;
        int full_num = nLeaves % BRANCHING_FACTOR == 0 ? nLeaves : nLeaves + BRANCHING_FACTOR - (nLeaves % BRANCHING_FACTOR);

        int runnerIndex = 0;
        int real_num = nLeaves; //at current level
        // Loop on every 'floor' of the tree
        Node currentNode;
        Node parentNode;
        Set<Node> children = new HashSet();
        for (d = depth-1; d >= 0; d++) {
            firstLvlNodeNum = (int) Math.pow(2,d);
            // reduce all nodes in the array to their parent
            for (leapIndex = 0; leapIndex < full_num; leapIndex = leapIndex + BRANCHING_FACTOR) {
                int sum = 0; //placeholder for empty hash
                //create parent Node
                parentNode = new Node();
                parentNode.setTreeId(treeID);
                for (runnerIndex = leapIndex; runnerIndex < leapIndex + BRANCHING_FACTOR; runnerIndex++) {
                    //might go over the real number
                    if (runnerIndex > real_num - 1) {
                        //generate random hash (keep ref)
                        currentNode = new Node();
                    } else {
                        currentNode = buf[runnerIndex];
                    }
                    //set parent-child-neighbour relation
                    currentNode.setParent(parentNode);
                    children.add(currentNode);
                    // InTreeId = (2^depth + i - 1)     i in [0, 2^depth[
        /*
           0             0
                       /   \
           1          1     2
                     / \   / \
           2        3   4 5   6
         */

                    currentNode.setInTreeId(firstLvlNodeNum - 1 + insertIndex);

                    //supposing branching factor of 2
                    //leapIndex + BRANCHING_FACTOR-1 - (runnerIndex-leapIndex)
                    currentNode.setNeighbour(buf[2*leapIndex - runnerIndex + BRANCHING_FACTOR - 1]);


                    sum = sum + 1; //placeholder op to concatenate hash
                }

                //compute concatenated hash value, set value in parent node

                parentNode.setChildren(children);
                //insert parent Node

                insertIndex++;
                sum = 0;
            }
            real_num = full_num / BRANCHING_FACTOR;
            full_num = full_num % 2 == 0 ? full_num : full_num + 1;
            insertIndex = 0;
        }
        /*
            [ x x x x x x
              | |
              &
                  | |
                &
                     | |
                  &
         */
        return buf[0];
    }
}


































