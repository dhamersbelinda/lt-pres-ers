package be.uclouvain.lt.pres.ers.core.scheduler;

import be.uclouvain.lt.pres.ers.core.persistence.model.*;
import be.uclouvain.lt.pres.ers.core.persistence.model.comparator.NodeBinaryComparator;
import be.uclouvain.lt.pres.ers.core.persistence.model.dto.TreeCategoryDto;
import be.uclouvain.lt.pres.ers.core.persistence.repository.ClientRepository;
import be.uclouvain.lt.pres.ers.core.persistence.repository.POIDRepository;
import be.uclouvain.lt.pres.ers.core.persistence.repository.RootRepository;
import be.uclouvain.lt.pres.ers.core.persistence.repository.TemporaryRepository;
import be.uclouvain.lt.pres.ers.utils.ByteUtils;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.TimestampType;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.TimestampBinary;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.service.http.commons.TimestampDataLoader;
import eu.europa.esig.dss.service.tsp.OnlineTSPSource;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.validation.timestamp.TimestampToken;
import lombok.AllArgsConstructor;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.tsp.TSPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAmount;
import java.util.*;
import java.util.List;

@Component
@AllArgsConstructor
public class BuildTreeTask {

    private final Logger logger = LoggerFactory.getLogger(BuildTreeTask.class);
    // TODO : use a modified or a set of composite tsp sources
    final String tspServer = "http://dss.nowina.lu/pki-factory/tsa/good-tsa";

    private final static int BRANCHING_FACTOR = 2;
    private final static int MAX_LEAVES = 1000;
    private final static boolean MIX_RENEWALS = true; // TODO take this into account
    private final static TemporalAmount RENEWAL_TIME_MARGIN = Period.of(1,0,0);

    private final TemporaryRepository temporaryRepository;
    private final ClientRepository clientRepository;
    private final RootRepository rootRepository;
    private final POIDRepository poidRepository;

    //    @Scheduled(cron = "* 59 23 * * ?") //  this should be every day at midnight
//    @Scheduled(cron = "0 0 0 1/1 * ?") //  this should be every day at midnight (fancy)
    @Scheduled(cron = "0 * * * * ?") // TODO : every minute at 0 sec for development purpose
    @SchedulerLock(name = "TaskScheduler_scheduledTask",
            lockAtLeastForString = "PT5s", lockAtMostForString = "PT25s") // TODO find proper duration
    public void scheduledTask() {
        OffsetDateTime taskStart = OffsetDateTime.now();
        OffsetDateTime shiftedStart = taskStart.plus(RENEWAL_TIME_MARGIN);

        List<TreeCategoryDto> treeCategories = poidRepository.getToPreserveCategoriesPOIDAndRoot(taskStart, taskStart.plusYears(1));

        logger.info(String.format("Found %d categories to build trees for !", treeCategories.size()));

        List<Treeable> workingSet = new ArrayList<>(MAX_LEAVES);
        HashTreeBase hashTreeBase;
        Client c;
        DigestAlgorithm alg;

        OnlineTSPSource tspSource = new OnlineTSPSource(tspServer);
        tspSource.setDataLoader(new TimestampDataLoader());
        TimestampBinary tsBinary;
        TimestampToken timestampToken;
        List<CertificateToken> certificateTokens;
        OffsetDateTime expirationDate;

        boolean poidDone, rootDone;
        int poidOffset, rootOffset, tempNPoidQueried;
        for (TreeCategoryDto treeCategory : treeCategories) {
            poidDone = false;
            rootDone = false;
            // TODO : here the client should always exist, but is it necessary ?
            try {
                c = clientRepository.getReferenceById(treeCategory.getClientId());
            } catch(EntityNotFoundException e) {
                continue;
            }
            try {
                alg = DigestAlgorithm.forOID(treeCategory.getDigestAlgorithm());
            } catch(IllegalArgumentException e) {
                continue;
            }

            poidOffset = rootOffset = 0;
            while(!(poidDone && rootDone)) {
                workingSet.addAll(poidRepository.getPOIDsForTree(taskStart, treeCategory.getClientId(), treeCategory.getDigestAlgorithm(), MAX_LEAVES, poidOffset));
                poidOffset += workingSet.size();

                if(workingSet.size() < MAX_LEAVES && (MIX_RENEWALS || workingSet.size() == 0)) {
                    poidDone = true;
                    tempNPoidQueried = workingSet.size();
                    workingSet.addAll(rootRepository.getRootsForTree(taskStart, shiftedStart, treeCategory.getClientId(), treeCategory.getDigestAlgorithm(), MAX_LEAVES - workingSet.size(), rootOffset));
                    rootOffset += workingSet.size() - tempNPoidQueried;
                    if(workingSet.size() < MAX_LEAVES) rootDone = true;
                }

                hashTreeBase = new HashTreeBase(new TreeID(), treeCategory.getClientId(), alg, workingSet);

                Node rootNode = buildTree(hashTreeBase);
                Root root = new Root();
                root.setNode(rootNode);
                rootNode.setRoot(root);
                root.setIsExtended(Boolean.FALSE);
                root.setDigestMethod(treeCategory.getDigestAlgorithm());
                root.setClientId(c);


                try {
                    tsBinary = tspSource.getTimeStampResponse(alg, rootNode.getNodeValue());
                } catch(DSSException e) {
                    logger.error("Could not get timestamp ! "+e.getMessage());
                    continue;
                }

                try {
                    timestampToken = new TimestampToken(tsBinary.getBytes(), TimestampType.CONTENT_TIMESTAMP);
                    // TODO : better errors and signalling
                } catch (TSPException e) {
                    logger.error("TSP Exception ! "+e.getMessage());
                    continue;
                } catch (IOException e) {
                    logger.error("IO Exception ! "+e.getMessage());
                    continue;
                } catch (CMSException e) {
                    logger.error("CMS Exception ! "+e.getMessage());
                    continue;
                }

                certificateTokens = timestampToken.getCertificates();
                if(certificateTokens == null || certificateTokens.size() == 0) {
                    logger.error("No certificates in the timestamp token ! "+timestampToken);
                    continue;
                }
                expirationDate = certificateTokens.get(0).getNotAfter().toInstant().atOffset(ZoneOffset.UTC);

                root.setCertValidUntil(expirationDate);
                root.setTimestamp(tsBinary.getBytes());

                logger.info("Built a tree.");
                System.out.println(root);

                rootRepository.save(root);
                logger.info("Saved the tree.");
                System.out.println(root);
            }

        }
        logger.info("All trees built.");

        /*
            distinctTrees = SELECT DISTINCT client_id, dig_method FROM er_test_schema.temporary_records ORDER BY client_id, dig_method LIMIT XXXXX;
            foreach(client c, dig_meth m in distinctTrees):
                workingSet = SELECT * FROM er_test_schema.temporary_records WHERE client_id=c AND dig_meth=m   (no limit for simplicity, and it becomes 'heavy' after millions of entries only)
                root = buildTree(workingSet)
                root.setTimestamp(getTimestamp(root.getValue))
                save(root)
         */

//        // TODO : support root nodes
//        List<TemporaryRecord> temporaryRecords = temporaryRepository.findAllBy();
//
//        if(temporaryRecords.isEmpty()) {
//            logger.info("No temporary records to process this time (" + start + ").");
//            return;
//        }
//
//        //make a list of HashTreeBases
//        //collect all bases for tree construction
//        List<HashTreeBase> hashTreeBaseList = new ArrayList<>();
//
//        int prevClientId = temporaryRecords.get(0).getClientId();
////        URI prevDigestMethod = temporaryRecords.get(0).getDigestList().getDigestMethod();
//        URI prevDigestMethod = temporaryRecords.get(0).getDigestMethod();
//        int index = 1;
//        HashTreeBase htb = new HashTreeBase();
//        htb.setClientId(prevClientId);
//        htb.setDigestMethod(prevDigestMethod);
//        List<POCompressed> poCompressedList = new ArrayList<>();
//        POCompressed poCompressed = new POCompressed();
//        POID prevPoid = temporaryRecords.get(0).getPoid();
//        poCompressed.setPoid(prevPoid);
//        List<byte[]> digests = new ArrayList<>();
//        digests.add(temporaryRecords.get(0).getDigest());
//        List<Integer> digNums = new ArrayList<>();
//        digNums.add(temporaryRecords.get(0).getDigNum()); //TODO check how we handle this order (on insertion set binary ascending)
//        while (index < temporaryRecords.size()) {
//            TemporaryRecord curr = temporaryRecords.get(index);
//            int currClientId = curr.getClientId();
////            URI currDigestMethod = curr.getDigestList().getDigestMethod();
//            URI currDigestMethod = curr.getDigestMethod();
//            //continue checking for POID
//
//            if (currClientId != prevClientId || !currDigestMethod.equals(prevDigestMethod)) {
//                htb.setPoCompressedList(poCompressedList);
//                hashTreeBaseList.add(htb);
//
//                htb = new HashTreeBase();
//                htb.setClientId(currClientId);
//                htb.setDigestMethod(currDigestMethod);
//
//                prevClientId = currClientId;
//                prevDigestMethod = currDigestMethod;
//
//                poCompressedList = new ArrayList<>();
//            }
//
//            if (!prevPoid.equals(curr.getPoid())) { //we create a new POCompressed
//                poCompressed.setDigests(digests);
//                poCompressed.setDigNums(digNums);
//
//                poCompressedList.add(poCompressed);
//
//                //new digests and dignums and POCompressed
//                digests = new ArrayList<>();
//                digNums = new ArrayList<>();
//                poCompressed = new POCompressed();
//                poCompressed.setPoid(curr.getPoid());
//
//                prevPoid = curr.getPoid();
//
//                //there might be a potential problem here with the references (but probably not)
//                //(check POCompressedList)
//            }
//            digests.add(curr.getDigest());
//            digNums.add(curr.getDigNum());
//
//            index++;
//        }
//        poCompressed.setDigests(digests);
//        poCompressed.setDigNums(digNums);
//
//        poCompressedList.add(poCompressed);
//        htb.setPoCompressedList(poCompressedList);
//        hashTreeBaseList.add(htb);
//
//        //all trees are in HashTreeBase
//        for (HashTreeBase hashTreeBase: hashTreeBaseList) {
//            TreeID treeID = new TreeID();
//            hashTreeBase.setTreeID(treeID);
//            Node rootNode = buildTree(hashTreeBase);
//            // TODO : get a real timestamp with DSS
//
//            OffsetDateTime ts = OffsetDateTime.now().plusMinutes(2);
//
//            Root root = new Root();
//            root.setNode(rootNode);
//            root.setCertValidUntil(ts);
//            root.setIsExtended(Boolean.FALSE);
//            rootNode.setRoot(root);
//            logger.info("Built a tree.");
//            System.out.println(root);
//
//            rootRepository.save(root);
//            logger.info("Saved the tree.");
//            System.out.println(root);
//            // TODO : delete from temporary  table
//        }


        // BIG TODO
        // find good hash method
        // some attributes (in the classes before construction of the tree) can be removed (or added)
    }

    public static Node buildTree(HashTreeBase input) {

        /*  InTreeId = (2^depth + i - 1)     i in [0, 2^depth[
           0             0
                       /   \
           1          1     2
                     / \   / \
           2        3   4 5   6
         */

        int nLeaves = input.getLeaves().size();
        int depth = (int) Math.ceil(Math.log(nLeaves) / Math.log(BRANCHING_FACTOR)); // log_b (x) = ln(x)/ln(b)
        // int treeSize = (int) ((Math.pow(BRANCHING_FACTOR, depth+1) - 1)/((double) (BRANCHING_FACTOR - 1)));
        int mod = nLeaves % BRANCHING_FACTOR;
        int fullNum = mod == 0 ? nLeaves : nLeaves + BRANCHING_FACTOR - mod;
        Node[] buf = new Node[fullNum];
        int d = 0;
        int firstLvlNodeNum = (int) ((Math.pow(BRANCHING_FACTOR, depth) - 1)/((double) (BRANCHING_FACTOR - 1)));
        Node temp;
        TreeID treeID = input.getTreeID();
        // transform leaves in node object & put in array
        for (Treeable leaf:input.getLeaves()) {
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
                    currentNode.setNodeValue("dummy".getBytes(StandardCharsets.UTF_8));
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

                parentNode.setNodeValue(DSSUtils.digest(input.getDigestMethod(), toHash));

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


































