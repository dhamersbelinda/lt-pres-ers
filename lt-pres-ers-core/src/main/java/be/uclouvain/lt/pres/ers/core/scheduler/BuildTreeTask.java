package be.uclouvain.lt.pres.ers.core.scheduler;

import be.uclouvain.lt.pres.ers.core.persistence.model.*;
import be.uclouvain.lt.pres.ers.core.persistence.repository.RootRepository;
import be.uclouvain.lt.pres.ers.core.persistence.repository.TemporaryRepository;
import lombok.AllArgsConstructor;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.List;

@Component
@AllArgsConstructor
public class BuildTreeTask {

    private final Logger logger = LoggerFactory.getLogger(BuildTreeTask.class);

    private final static int BRANCHING_FACTOR = 2;

    private final TemporaryRepository temporaryRepository;
    private final RootRepository rootRepository;

//    @Scheduled(cron = "* 59 23 * * ?") //  this should be every day at midnight
//    @Scheduled(cron = "0 0 0 1/1 * ?") //  this should be every day at midnight (fancy)
    @Scheduled(cron = "0 * * * * ?") // TODO : every minute at 0 sec for development purpose
    @SchedulerLock(name = "TaskScheduler_scheduledTask",
            lockAtLeastForString = "PT5s", lockAtMostForString = "PT25s") // TODO find proper duration
    public void scheduledTask() {
        OffsetDateTime start = OffsetDateTime.now();

        /*
            distinctTrees = SELECT DISTINCT client_id, dig_method FROM er_test_schema.temporary_records ORDER BY client_id, dig_method LIMIT XXXXX;
            foreach(client c, dig_meth m in distinctTrees):
                workingSet = SELECT * FROM er_test_schema.temporary_records WHERE client_id=c AND dig_meth=m   (no limit for simplicity, and it becomes 'heavy' after millions of entries only)
                root = buildTree(workingSet)
                root.setTimestamp(getTimestamp(root.getValue))
                save(root)
         */

        // TODO : support root nodes
        List<TemporaryRecord> temporaryRecords = temporaryRepository.findAllBy();

        if(temporaryRecords.isEmpty()) {
            logger.info("No temporary records to process this time (" + start + ").");
            return;
        }

        //make a list of HashTreeBases
        //collect all bases for tree construction
        List<HashTreeBase> hashTreeBaseList = new ArrayList<>();

        int prevClientId = temporaryRecords.get(0).getClientId();
//        URI prevDigestMethod = temporaryRecords.get(0).getDigestList().getDigestMethod();
        URI prevDigestMethod = temporaryRecords.get(0).getDigestList();
        int index = 1;
        HashTreeBase htb = new HashTreeBase();
        htb.setClientId(prevClientId);
        htb.setDigestMethod(prevDigestMethod);
        List<POCompressed> poCompressedList = new ArrayList<>();
        POCompressed poCompressed = new POCompressed();
        POID prevPoid = temporaryRecords.get(0).getPoid();
        poCompressed.setPoid(prevPoid);
        List<String> digests = new ArrayList<>();
        digests.add(temporaryRecords.get(0).getDigest());
        List<Integer> digNums = new ArrayList<>();
        digNums.add(temporaryRecords.get(0).getDigNum()); //TODO check how we handle this order (on insertion set binary ascending)
        while (index < temporaryRecords.size()) {
            TemporaryRecord curr = temporaryRecords.get(index);
            int currClientId = curr.getClientId();
//            URI currDigestMethod = curr.getDigestList().getDigestMethod();
            URI currDigestMethod = curr.getDigestList();
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
            root.setNode(rootNode);
            root.setTimestamp(timestamp);
            rootNode.setRoot(root);
            logger.info("Built a tree.");
            System.out.println(root);

            rootRepository.save(root);
            logger.info("Saved the tree.");
            System.out.println(root);
            // TODO : delete from temporary  table
        }


        // BIG TODO
        // find good hash method
        // some attributes (in the classes before construction of the tree) can be removed (or added)
    }

    private static Node buildTree(HashTreeBase input) {

        /*  InTreeId = (2^depth + i - 1)     i in [0, 2^depth[
           0             0
                       /   \
           1          1     2
                     / \   / \
           2        3   4 5   6
         */

        int nLeaves = input.getPoCompressedList().size();
        int depth = (int) Math.ceil(Math.log(nLeaves) / Math.log(BRANCHING_FACTOR)); // log_b (x) = ln(x)/ln(b)
        // int treeSize = (int) ((Math.pow(BRANCHING_FACTOR, depth+1) - 1)/((double) (BRANCHING_FACTOR - 1)));
        int mod = nLeaves % BRANCHING_FACTOR;
        int fullNum = mod == 0 ? nLeaves : nLeaves + BRANCHING_FACTOR - mod;
        Node[] buf = new Node[fullNum];
        int d = 0;
        int firstLvlNodeNum = (int) Math.pow(2,depth);
        Node temp;
        TreeID treeID = input.getTreeID();
        // transform leaves in node object & put in array
        for (POCompressed po:input.getPoCompressedList()) {
            temp = new Node();
            temp.setPoid(po.getPoid());
            temp.setNodeValue(po.getDigests().get(0)); // TODO : append and hash the digests in a single node AND add children to this node ...
            // TODO : for roots : verify that all verification data is present and if not get it then canonicalize then hash the canonical binary (rfc 6283 4.2.1)
            temp.setTreeId(treeID);
            temp.setInTreeId(firstLvlNodeNum - 1 + d);
            buf[d] = temp;
            d++;
        }

        int leapIndex = 0;   // Advance by BRANCHING_FACTOR
        int insertIndex = 0; // Advance of 1
        int runnerIndex = 0; // Used to scan children

        int realNum = nLeaves; //at current level
        Node currentNode;
        Node parentNode;
        Set<Node> children = new HashSet<>();
        // Loop on every 'floor' of the tree
        for (d = depth-1; d >= 0; d--) {
            firstLvlNodeNum = (int) Math.pow(2,d); // for parent's in_tree_id field
            // First check if we have to add dummy nodes
            if(realNum < fullNum){
                for (int i = realNum; i < fullNum; i++) {
                    currentNode = new Node();
                    currentNode.setTreeId(treeID);
                    currentNode.setInTreeId(2L * firstLvlNodeNum + i - 1); // 2* as currentNode is on the floor below !
                    // TODO set dummy value of proper length given hash algo
                    currentNode.setNodeValue("dummy");
                    buf[i] = currentNode;
                }
            }
            // Second reduce all nodes in the array to their parent
            for (leapIndex = 0; leapIndex < fullNum; leapIndex = leapIndex + BRANCHING_FACTOR) {
                int sum = 0; //placeholder for empty hash
                //create parent Node
                parentNode = new Node();
                parentNode.setTreeId(treeID);
                parentNode.setInTreeId(firstLvlNodeNum - 1 + insertIndex);
                children = new HashSet<>();
                for (runnerIndex = leapIndex; runnerIndex < leapIndex + BRANCHING_FACTOR; runnerIndex++) {
                    currentNode = buf[runnerIndex];
                    //set parent-child-neighbour relation
                    currentNode.setParent(parentNode);
                    children.add(currentNode);

                    //supposing branching factor of 2
                    //leapIndex + BRANCHING_FACTOR-1 - (runnerIndex-leapIndex)
                    // TODO : support more than one neighbour
                    currentNode.setNeighbour(buf[2*leapIndex - runnerIndex + BRANCHING_FACTOR - 1]);


                    sum = sum + 1; // TODO placeholder op to concatenate hash
                }

                // TODO compute concatenated hash value, set value in parent node
                parentNode.setNodeValue("parent");

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
}


































