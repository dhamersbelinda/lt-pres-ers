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
import java.util.concurrent.TimeUnit;

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
    private final static byte[] tsBin = HexFormat.of().parseHex("30820a4006092a864886f70d010702a0820a3130820a2d020103310d300b0609608648016503040201306f060b2a864886f70d0109100104a060045e305c02010106032a0304302f300b0609608648016503040201042064ec88ca00b268e5ba1a35678a1b5316d212f4f366b2477232534a8aeca37f3c0210143b6a26a78b1eac32a5ec0e965c456f180f32303233303533313135343935305aa0820752308203573082023fa003020102020101300d06092a864886f70d01010d0500304d3110300e06035504030c07726f6f742d636131193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c55301e170d3232303131333136303333355a170d3234303131333136303333355a304d3110300e06035504030c07726f6f742d636131193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c5530820122300d06092a864886f70d01010105000382010f003082010a0282010100911ce1179de5e632cfa4a2c7c5e9ac64d5fa2939aaa91b408957da03634ee13c7d254d4297d16a5e1ef7e517f5f472b5165bdc726c0d0d38a1e9991a7af2fe794789d12c1e5f1d4bbf6ce75d8bf2ff8ba45962bc9213fe3b2df5c0504b2f51b7b0494773254fa328a40e4d299e9d26500c2ddaed4ef658e702993b6e361d5cd6dbbbe9f7d6331dc23680c348992e70e23abea9a301fd20db239b45737fe2e9a4f303a0f357a22d5e93d69cc258d92e8dad3b06a222d2d41c7fc3fd65c6ea9e822bb1765c691671e037d456a4036ba59063c896aa7dbcac28511775c6e1746c5b949d79253e1ce85b4267dcd599fa5e9f78acc83f75cee8ccca3fe759f539dadf0203010001a3423040300e0603551d0f0101ff040403020106301d0603551d0e041604147a4d33ab016ea3188b2708d53970e0e91153293e300f0603551d130101ff040530030101ff300d06092a864886f70d01010d05000382010100855f2fc59ce52e65b61673baeb476d4309556eba59488ac9638abc5df60e789e25ada275c55e57b52eb59532fe3ef041953441f2506e340b473e7abeab11b4e9f29f21a1a409c387eb6595fcb03da989d4a5680258ee5ce0350a97269ff49ad092c35461ddb71f0267253443eed49ed911293ab0767d41f56fac6f56c099f31b02b774809e41aebbc976a18b410607ebfef676fa4f2f7619102212327fbfd891804b5e291522d8d9d3ae10d28ca95dfb0e779c720a76039012a73278fa4203ec6b2599fcc000c47faea668909265623139b619b2ed48bcec01efdc53d626fbdf828b9a6707e85e67e2e867ab2f04a6985af1db97ac8bbe9577d579ee2bf40f59308203f3308202dba003020102020201f4300d06092a864886f70d01010b0500304d3110300e06035504030c07726f6f742d636131193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c55301e170d3232303231333136303334335a170d3233313231333136303334335a304e3111300f06035504030c08676f6f642d74736131193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c5530820122300d06092a864886f70d01010105000382010f003082010a0282010100adbe3c55d3fe37e62c32ffc58fc3e955dc21f09ea21f3a1d5a3ec1f3c6fcdadcdc7aeb64477eaa475874d3bd9ae705fe903389c56f03d937cb2182eba03308783457a6686150da5688f02ec760a483f662e1d84da57149142ed38429c539a6defb45931b9edeccf547ddd96b2b246aa51392683bab319a27bfa380577985c91a7875ee7e9c597bd99279e104433b9a255767402992722b78e4bf29a872ef62f68ac47165a0a2fae6ea51d785bb55baee9493afb857b5120f157d29dc8b12f84de0c56bfde555c018cb8b4803418766a4ae7274022f8b133edd696a66b77e0719dbeee232a1faa7afc7497c7ed9bc8358d00a263c17b04f30e6ae8ecbf25e424d0203010001a381db3081d8300e0603551d0f0101ff04040302078030160603551d250101ff040c300a06082b0601050507030830410603551d1f043a30383036a034a0328630687474703a2f2f6473732e6e6f77696e612e6c752f706b692d666163746f72792f63726c2f726f6f742d63612e63726c304c06082b060105050701010440303e303c06082b060105050730028630687474703a2f2f6473732e6e6f77696e612e6c752f706b692d666163746f72792f6372742f726f6f742d63612e637274301d0603551d0e04160414a3685af3aa9a718213c3af91640b410eff72af86300d06092a864886f70d01010b050003820101004890ffdbefcd61d3ae9695098f6c33adc7fc7ef857232ace88a1411d845d35f1deee614d703d35157690e03a5cf6fe68ec87de43265a1ad532b3bd75eba632f027c397086f6311759635185b0246fdba77d07d00a20083b107a2235e951e65c07d330af453f4a0a0afccaca693c1a0673678c55ce33e71bdd3e7deca02e61c5e67c0d9c35bb1533ba8cbfbc285a5f784c45afcb891e5b0316bb34495bffacad3260d22ecc497abe8a7b94f01b2d0e2e30342f43aa86b5c07856f712ce07c75ab73f919dd698033d9ae6ed7a49e7f3558f28f1e8f1b4cac1f3cdd10fc7abd10a656d82da151a44ba18796e455cdd5f91544fc50cb8fe8ccad27d3bf59b949d831318202503082024c0201013053304d3110300e06035504030c07726f6f742d636131193017060355040a0c104e6f77696e6120536f6c7574696f6e733111300f060355040b0c08504b492d54455354310b3009060355040613024c55020201f4300b0609608648016503040201a081d1301a06092a864886f70d010903310d060b2a864886f70d0109100104301c06092a864886f70d010905310f170d3233303533313135343935305a302b06092a864886f70d010934311e301c300b0609608648016503040201a10d06092a864886f70d01010b0500302f06092a864886f70d01090431220420b14c73e56edd5f8fdadad1ad923b65842436f08cb5a0ccce30b9d36fb42594c23037060b2a864886f70d010910022f31283026302430220420aa02b99f9c4b57291e3c1744e44647709293d0fe17bb4eed28c99e973c7e8473300d06092a864886f70d01010b05000482010034dd7d8c1d77f954d83bc21c3e7edf4558dbca6e5636a9cb02b701f7ed153ac18ef5762e96cb507a865c788e3c37ebc006f6eebf4205427b34fa3ec3a0bfbf985c62bd04731f144ed953ce1ffabf932e33efd8f87411f5bf2be1f197393df2a061ef1cd799ece44960f621380e986176978bcbc51f3319a22556bf8360616579a96ec49ed42823ed5383753521649f819de4dd6b10ab04224f11d42b6baee43be59a7665bd57ad25d8e71161bfecc8341d303dc7b4056e637507700ab3b0ca4aa8333a553f72058a8c0d4be171dd9d4fbeede5da0ba1e3aae968ffc73ef02de57b9dc38185f2be76fb8f869347fd4edf2965e54659a9a85fed91c35733cfe849");
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

//                try {
////                    tsBinary = tspSource.getTimeStampResponse(alg, rootNode.getNodeValue());
//                    tsBinary = tspSource.getTimeStampResponse(alg, rootNode.getNodeValue());
//                } catch(DSSException e) {
//                    logger.error("Could not get timestamp ! "+e.getMessage());
//                    continue;
//                }
                timeAfterTS = System.nanoTime();
                try {
                    timestampToken = new TimestampToken(tsBin, TimestampType.CONTENT_TIMESTAMP);
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
//                expirationDate = certificateTokens.get(0).getNotAfter().toInstant().atOffset(ZoneOffset.UTC);
//                expirationDate = OffsetDateTime.now().plusMonths(3);
                expirationDate = OffsetDateTime.now().plusYears(2);
                root.setCertValidUntil(expirationDate);
                root.setTimestamp(tsBin);

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

        Client c;
        try {
            c = clientRepository.getReferenceById(0L);
        } catch(EntityNotFoundException e) {
            logger.error("Could not insert");
            throw new RuntimeException("Could not insert, client not found");
        }

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

            // This is to have different creation dates in the DB
//            try {
//                TimeUnit.NANOSECONDS.sleep(600);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            long bef = System.nanoTime();
//            int m = 0;
//            Random rdn = new Random();
//            while(m<100000) {
//                rdn.nextInt();
//                m++;
//            }
//            logger.info("Loop time: %d".formatted(System.nanoTime() - bef));
        }

        poidRepository.saveAll(poids);
        logger.info("Inserted %d random POIDs in %d ns".formatted(nPOID, System.nanoTime()-start));


    }
}


































