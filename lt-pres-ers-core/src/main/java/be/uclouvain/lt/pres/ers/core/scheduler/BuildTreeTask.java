package be.uclouvain.lt.pres.ers.core.scheduler;

import be.uclouvain.lt.pres.ers.core.persistence.model.*;
import be.uclouvain.lt.pres.ers.core.persistence.model.comparator.NodeBinaryComparator;
import be.uclouvain.lt.pres.ers.core.persistence.model.dto.TreeCategoryDto;
import be.uclouvain.lt.pres.ers.core.persistence.repository.ClientRepository;
import be.uclouvain.lt.pres.ers.core.persistence.repository.POIDRepository;
import be.uclouvain.lt.pres.ers.core.persistence.repository.ProfileRepository;
import be.uclouvain.lt.pres.ers.core.persistence.repository.RootRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
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
    public final static int MAX_LEAVES = 1024;
    public final static int BRANCHING_FACTOR = 2;
    private final static boolean MIX_RENEWALS = true; // TODO take this into account
    private final static TemporalAmount RENEWAL_TIME_MARGIN = Period.of(1,0,0);

    public final static int NEW_POIDS_ONLY = 0;
    public final static int RENEWALS_ONLY = 1;
    public final static int NEW_POIDS_AND_RENEWALS = 2;

    private final ClientRepository clientRepository;
    private final RootRepository rootRepository;
    private final POIDRepository poidRepository;

    //    @Scheduled(cron = "* 59 23 * * ?") //  this should be every day at midnight
    @Scheduled(cron = "0 0 0 1/1 * ?") //  this should be every day at midnight (fancy)
//    @Scheduled(cron = "0 * * * * ?") // TODO : every minute at 0 sec for development purpose
    @SchedulerLock(name = "TaskScheduler_scheduledTask",
            lockAtLeastForString = "PT5s", lockAtMostForString = "PT25s") // TODO find proper duration
    public void scheduledTask() {
        this.task(NEW_POIDS_AND_RENEWALS, BRANCHING_FACTOR, MAX_LEAVES);
    }

    public long[] task(int mode, int branchingFactor, int maxLeaves) {
        long timeBeforeBuild = 0, timeEnd=0, timeAfterBuild=0, timeAfterTS=0, timeBeforeInsert=0, timeAfterInsert=0;
        long start = System.nanoTime();

        OffsetDateTime taskStart = OffsetDateTime.now();
        OffsetDateTime shiftedStart = taskStart.plus(RENEWAL_TIME_MARGIN);

        List<TreeCategoryDto> treeCategories;
        switch(mode) {
            case NEW_POIDS_ONLY -> treeCategories = poidRepository.getToPreserveCategoriesPOIDOnly(taskStart);
            case RENEWALS_ONLY -> treeCategories = rootRepository.getToPreserveCategoriesRootOnly(taskStart, shiftedStart);
            case NEW_POIDS_AND_RENEWALS -> treeCategories = poidRepository.getToPreserveCategoriesPOIDAndRoot(taskStart, shiftedStart);
            default -> {
                logger.error(String.format("Unknown mode value given to task():  %d", mode));
                return null;
            }
        }

        logger.info(String.format("Found %d categories to build trees for !", treeCategories.size()));

        List<Treeable> workingSet = new ArrayList<>(maxLeaves);
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
            alg = null;
            poidDone = (mode == RENEWALS_ONLY);
            rootDone = (mode == NEW_POIDS_ONLY);
            // TODO : here the client should always exist, but is it necessary ?
            try {
                c = clientRepository.getReferenceById(treeCategory.getClientId());
            } catch(EntityNotFoundException e) {
                continue;
            }
            try {
                alg = DigestAlgorithm.forOID(treeCategory.getDigestAlgorithm());
            } catch(IllegalArgumentException e) {
                logger.error("Could not find digest algorithm by xml id : "+treeCategory.getDigestAlgorithm()+"\nMessage for IllegalArgumentException :"+e.getMessage());
                e.printStackTrace();
            }
            if(alg == null) continue;

            poidOffset = rootOffset = 0;
            while(!(poidDone && rootDone)) {
                if(!poidDone) {
                    workingSet.addAll(poidRepository.getPOIDsForTree(taskStart, treeCategory.getClientId(), treeCategory.getDigestAlgorithm(), maxLeaves, 0));
                    poidOffset += workingSet.size();
                }

                if(poidDone || workingSet.size() < maxLeaves) {
                    poidDone = true;
                    if (!rootDone && (MIX_RENEWALS || workingSet.size() == 0)) {
                        tempNPoidQueried = workingSet.size();
                        workingSet.addAll(rootRepository.getRootsForTree(taskStart, shiftedStart, treeCategory.getClientId(), treeCategory.getDigestAlgorithm(), maxLeaves - workingSet.size(), 0));
                        rootOffset += workingSet.size() - tempNPoidQueried;
                        if (workingSet.size() < maxLeaves) rootDone = true;
                    }
                }
                if(workingSet.isEmpty()) {
                    logger.warn("Tree was going to be built for empty working set!");
                    continue;
                }
                hashTreeBase = new HashTreeBase(new TreeID(), treeCategory.getClientId(), alg, workingSet);

                timeBeforeBuild = System.nanoTime();

                Node rootNode = hashTreeBase.buildTree(branchingFactor);
                Root root = new Root();
                root.setNode(rootNode);
                rootNode.setRoot(root);
                root.setIsExtended(Boolean.FALSE);
                root.setDigestMethod(treeCategory.getDigestAlgorithm());
                root.setClientId(c);

                timeAfterBuild = System.nanoTime();

                try {
                    tsBinary = tspSource.getTimeStampResponse(alg, rootNode.getNodeValue());
                } catch(DSSException e) {
                    logger.error("Could not get timestamp ! "+e.getMessage());
                    continue;
                }
                timeAfterTS = System.nanoTime();
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
                timeBeforeInsert = System.nanoTime();
                rootRepository.save(root);
                timeAfterInsert = System.nanoTime();
                logger.info("Saved the tree.");

                workingSet.clear();
                timeEnd = System.nanoTime();
            }

        }
        logger.info("All trees built.");
        // total, fetching, build, ts, insert
        return new long[]{timeEnd-start, timeBeforeBuild - start, timeAfterBuild - timeBeforeBuild, timeAfterTS - timeAfterBuild, timeAfterInsert - timeBeforeInsert};
    }


    ProfileRepository profileRepository;
    public void insertRandomPOIDs(int nPOID) throws URISyntaxException {
        long start = System.nanoTime();
        List<POID> poids = new ArrayList<>();
        DigestAlgorithm alg = DigestAlgorithm.SHA256;

        Client c = new Client();

        URI profileID = new URI("https://uclouvain.be/en/faculties/epl/preservation-api/profile/v1.0");
        Optional<Profile> optProfile = profileRepository.findByProfileIdentifier(profileID);
        if(optProfile.isEmpty()) {
            return;
        }
        Profile profile = optProfile.get();

        Random rd = new Random();

        for (int i = 0; i < nPOID; i++) {
            POID poid1 = new POID();
            poid1.setClientId(c);
            poid1.setCreationDate(OffsetDateTime.now());
            poid1.setProfile(profile);
            byte[] bytes1 = new byte[alg.getSaltLength()==0 ? 32:alg.getSaltLength()];
            rd.nextBytes(bytes1);
            poid1.setDigestValue(bytes1);
            poid1.setDigestMethod(DigestAlgorithm.SHA256.getOid());

            PO po1 = new PO();
            poid1.setPo(po1);
            po1.setPoid(poid1);
            po1.setFormatId(new URI("http://uri.etsi.org/19512/format/DigestList"));
            DigestList dl1 = new DigestList();
            dl1.setPo(po1);
            po1.setDigestList(dl1);
            dl1.setDigestMethod(new URI(DigestAlgorithm.SHA256.getOid()));

            Digest d1 = new Digest();
            d1.setDigest(bytes1);
            dl1.setDigests(new ArrayList<>(List.of(new Digest[]{d1})));
            d1.setDigestList(dl1);
            poid1.setDigestValue(bytes1);

            poids.add(poid1);
        }

        poidRepository.saveAll(poids);
        logger.info("Inserted %d random POIDs in %d ns".formatted(nPOID, System.nanoTime()-start));


    }
}


































