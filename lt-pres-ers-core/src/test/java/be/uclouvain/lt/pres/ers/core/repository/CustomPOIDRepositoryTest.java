package be.uclouvain.lt.pres.ers.core.repository;

import be.uclouvain.lt.pres.ers.core.persistence.model.*;
import be.uclouvain.lt.pres.ers.core.persistence.repository.ClientRepository;
import be.uclouvain.lt.pres.ers.core.persistence.repository.POIDRepository;
import be.uclouvain.lt.pres.ers.core.persistence.repository.ProfileRepository;
import be.uclouvain.lt.pres.ers.core.persistence.repository.RootRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@ActiveProfiles("postgres")
public class CustomPOIDRepositoryTest {

    @Autowired
    POIDRepository poidRepository;
    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    ClientRepository clientRepository;

    @Test
    public void getRootsForTreeTest() throws URISyntaxException {
        OffsetDateTime start = OffsetDateTime.now();
        OffsetDateTime startMid = start.plusDays(1);
        OffsetDateTime startPlusOneYear = start.plusYears(1);
        String digestMethod1 = "digest_method_1_here";
        String digestMethod2 = "digest_method_2_here";

        Optional<Profile> proOpt = profileRepository.findByProfileIdentifier(new URI("https://uclouvain.be/en/faculties/epl/preservation-api/profile/v1.0"));
        if(proOpt.isEmpty()) {
            throw new RuntimeException("Profile not found ...");
        }
        Profile pro = proOpt.get();

        List<POID> poids = new ArrayList<>();
        Client c1 = new Client();
        clientRepository.save(c1);

        Client c2 = new Client();
        clientRepository.save(c2);
        long clientId1 = c1.getClientId(), clientId2 = c2.getClientId();

        POID tempPOID;
        PO tempPO;
        DigestList tempDL;
        Digest tempD;
        List<Digest> tempL;
        for (int i = 0; i < 10; i++) {
            tempPO = new PO();
            tempDL = new DigestList();
            tempPO.setDigestList(tempDL);
            tempDL.setPo(tempPO);

            tempPO.setFormatId(new URI("http://uri.etsi.org/19512/format/DigestList"));

            tempDL.setDigestMethod(i%2==0 ? new URI(digestMethod1):new URI(digestMethod2));

            tempL = new ArrayList<>(i+1);
            for (int j = 0; j < i+1; j++) {
                tempD = new Digest();
                tempD.setDigest(new byte[i*j]);
                tempD.setDigestList(tempDL);
                tempL.add(tempD);
            }
            tempDL.setDigests(tempL);

            tempPOID = new POID();
            tempPOID.setPo(tempPO);
            tempPO.setPoid(tempPOID);
            tempPOID.setDigestMethod(i%2==0 ? digestMethod1:digestMethod2);
            tempPOID.setClientId(i%2==0 ? c1:c2);
            tempPOID.setProfile(pro);
            tempPOID.setCreationDate(i>5? start:startMid);
            tempPOID.setDigestValue(new byte[]{1,2,3});

            poids.add(tempPOID);
        }

        poidRepository.saveAll(poids);

        List<POID> result = poidRepository.getPOIDsForTree(startMid, clientId1, digestMethod1, 10,0);
        System.out.println("Query done, result size (expected 2) : "+result.size());
        assertEquals(2, result.size());
        for (POID poid : result) {
            System.out.println(poid.getPo().getDigestList().getDigests());
        }

        result = poidRepository.getPOIDsForTree(startMid, clientId2, digestMethod1, 10,0);
        System.out.println("Query done, result size (expected 0) : "+result.size());
        assertEquals(0, result.size());

        result = poidRepository.getPOIDsForTree(startPlusOneYear, clientId1, digestMethod1, 10,0);
        System.out.println("Query done, result size (expected 5) : "+result.size());
        assertEquals(5, result.size());

        result = poidRepository.getPOIDsForTree(startPlusOneYear, clientId1, digestMethod1, 3,0);
        System.out.println("Query done, result size (expected 3) : "+result.size());
        assertEquals(3, result.size());
    }

}
