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
            alg = null;
            poidDone = false;
            rootDone = false;
            // TODO : here the client should always exist, but is it necessary ?
            try {
                c = clientRepository.getReferenceById(treeCategory.getClientId());
            } catch(EntityNotFoundException e) {
                continue;
            }
            try {
                alg = DigestAlgorithm.forXML(treeCategory.getDigestAlgorithm());
            } catch(IllegalArgumentException e) {
                logger.error("Could not find digest algorithm by xml id : "+treeCategory.getDigestAlgorithm()+"\nMessage for IllegalArgumentException :"+e.getMessage());
                e.printStackTrace();
            }
            if(alg == null) continue;

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

                Node rootNode = hashTreeBase.buildTree();
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
    }
}


































