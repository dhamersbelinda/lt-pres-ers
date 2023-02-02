package be.uclouvain.lt.pres.ers.core.scheduler;

import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class BuildTreeTask {

//    @Scheduled(cron = "* 59 23 * * ?") // TODO : this should be every day at midnight
//    @Scheduled(cron = "0 0 0 1/1 * ?") // TODO : this should be every day at midnight (fancy)
    @Scheduled(cron = "0 * * * * ?") // TODO : every minute at 0 sec for development purpose
    @SchedulerLock(name = "TaskScheduler_scheduledTask",
            lockAtLeastForString = "PT5s", lockAtMostForString = "PT25s") // TODO find proper duration
    public void scheduledTask() {
        System.out.println("Done at " + OffsetDateTime.now());

        // building the tree : array layout, array size depending on nbr of leaves (trimmed)

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
