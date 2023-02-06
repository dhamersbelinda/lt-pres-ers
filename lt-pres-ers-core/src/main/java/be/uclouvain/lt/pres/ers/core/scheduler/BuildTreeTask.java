package be.uclouvain.lt.pres.ers.core.scheduler;

import be.uclouvain.lt.pres.ers.core.persistence.model.TemporaryRecord;
import be.uclouvain.lt.pres.ers.core.persistence.repository.TemporaryRepository;
import lombok.AllArgsConstructor;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor //TODO is this annotation here correct ? copied by imitation of services
public class BuildTreeTask {


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
                    //sort by POID
                    int comp1 = o1.getPoid().getId().compareTo(o2.getPoid().getId());
                    if (comp1 == 0) {
                        //sort by digest value
                        int comp2 =
                    }
                }
                return comp;
            }
        });

        int prevClient =
        do {
            temp
        } while ();


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

        // Add timestamps to be extended to temporary table
        // Get nbr of items per (user, algo) from temp table
        // Foreach pair (user, algo)
            // query SELECT * FROM Temp WHERE user=user AND algo=algo LIMIT i,i+X (i starts at 0, X is max leaf per tree)
            // each query response :
        //          HashTreeBase(
        //          clientId,
        //          digestMethod,
        //          List of POIDs
        //              List of values
        //              List of dignums (maybe not necessary)
        //          )

            // Building tree (1 tree per HashTreeBase):
                // create list of base nodes
                    // Each base node has (Node object)
                        // treeId (empty)
                        // inTreeId (incremental)
                        // combined PK, but we probably/maybe need inTreeId for computation

                        // broId (empty)
                        // a children field (empty, nullable) -> list
                        // a parent field (empty, nullable)

                        // value
                        // AODG object
                            //



    }
}
