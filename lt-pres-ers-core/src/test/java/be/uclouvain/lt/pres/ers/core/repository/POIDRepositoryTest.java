package be.uclouvain.lt.pres.ers.core.repository;

import be.uclouvain.lt.pres.ers.core.persistence.model.*;
import be.uclouvain.lt.pres.ers.core.persistence.model.dto.TreeCategoryDto;
import be.uclouvain.lt.pres.ers.core.persistence.repository.ClientRepository;
import be.uclouvain.lt.pres.ers.core.persistence.repository.POIDRepository;
import be.uclouvain.lt.pres.ers.core.persistence.repository.ProfileRepository;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@ActiveProfiles("postgres")
class POIDRepositoryTest {
    @Autowired
    private POIDRepository poidRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private ClientRepository clientRepository;

    @Test
    void findByIdTest() throws URISyntaxException {
        URI profileID = new URI("https://uclouvain.be/en/faculties/epl/preservation-api/profile/v1.0");
        Optional<Profile> optProfile = profileRepository.findByProfileIdentifier(profileID);
        if(optProfile.isEmpty()) {
            fail("Could not find profile.");
        }
        Profile profile = optProfile.get();
        OffsetDateTime now = OffsetDateTime.now();
        Client c1 = new Client();

        clientRepository.save(c1);

        byte[] digest = "sasha1".getBytes(StandardCharsets.UTF_8);

        POID poid1 = new POID();
        poid1.setProfile(profile);
        poid1.setClientId(c1);
        poid1.setCreationDate(now);
        poid1.setDigestMethod(DigestAlgorithm.SHA256.getOid());
        poid1.setDigestValue(digest);
        PO po1 = new PO();
        poid1.setPo(po1);
        DigestList digestList1 = new DigestList();
        Digest digest1 = new Digest();
        digest1.setDigest(digest);
        digestList1.setDigests(new ArrayList<>(List.of(new Digest[]{digest1})));
        digestList1.setDigestMethod(new URI(DigestAlgorithm.SHA256.getOid()));
        po1.setDigestList(digestList1);
        po1.setFormatId(new URI("http://uri.etsi.org/19512/format/DigestList"));
        po1.setPoid(poid1);

        poid1 = poidRepository.save(poid1);

        String uuid = poid1.getId().toString();
        System.out.println("Performing findById()");
        Optional<POID> ret = poidRepository.findById(UUID.fromString(uuid));
        System.out.println("Performed findById()");
        if(ret.isEmpty()) fail("meh");
        else System.out.println("It's present ! "+ret.get());
    }

    @Test
    void getToPreserveCategoriesPOIDAndRootTest() throws URISyntaxException {
        URI profileID = new URI("https://uclouvain.be/en/faculties/epl/preservation-api/profile/v1.0");
        Optional<Profile> optProfile = profileRepository.findByProfileIdentifier(profileID);
        if(optProfile.isEmpty()) {
            fail("Could not find profile.");
        }
        Profile profile = optProfile.get();
        OffsetDateTime now = OffsetDateTime.now();
        Client c1 = new Client(), c2 = new Client();

        clientRepository.save(c1);
        clientRepository.save(c2);

        ArrayList<POID> poidList = new ArrayList<>();

        POID poid1 = new POID();
        poid1.setProfile(profile);
        poid1.setClientId(c1);
        poid1.setCreationDate(now);
        PO po1 = new PO();
        poid1.setPo(po1);
        DigestList digestList1 = new DigestList();
        Digest digest1 = new Digest();
        digest1.setDigest("sasha1".getBytes(StandardCharsets.UTF_8));
        digestList1.setDigests(new ArrayList<>(List.of(new Digest[]{digest1})));
        digestList1.setDigestMethod(new URI(DigestAlgorithm.SHA256.getOid()));
        po1.setDigestList(digestList1);
        po1.setFormatId(new URI("http://uri.etsi.org/19512/format/DigestList"));
        po1.setPoid(poid1);
        poidList.add(poid1);

        POID poid2 = new POID();
        poid2.setProfile(profile);
        poid2.setClientId(c1);
        poid2.setCreationDate(now);
        PO po2 = new PO();
        poid2.setPo(po2);
        DigestList digestList2 = new DigestList();
        Digest digest2 = new Digest();
        digest2.setDigest("sasha2".getBytes(StandardCharsets.UTF_8));
        digestList2.setDigests(new ArrayList<>(List.of(new Digest[]{digest2})));
        digestList2.setDigestMethod(new URI(DigestAlgorithm.SHA512.getOid()));
        po2.setDigestList(digestList2);
        po2.setFormatId(new URI("http://uri.etsi.org/19512/format/DigestList"));
        po2.setPoid(poid2);
        poidList.add(poid2);

        POID poid3 = new POID();
        poid3.setProfile(profile);
        poid3.setClientId(c2);
        poid3.setCreationDate(now);
        PO po3 = new PO();
        poid3.setPo(po3);
        DigestList digestList3 = new DigestList();
        Digest digest3 = new Digest();
        digest3.setDigest("sasha3".getBytes(StandardCharsets.UTF_8));
        digestList3.setDigests(new ArrayList<>(List.of(new Digest[]{digest3})));
        digestList3.setDigestMethod(new URI(DigestAlgorithm.SHA512.getOid()));
        po3.setDigestList(digestList3);
        po3.setFormatId(new URI("http://uri.etsi.org/19512/format/DigestList"));
        po3.setPoid(poid3);
        poidList.add(poid3);

        POID poid4 = new POID();
        poid4.setProfile(profile);
        poid4.setClientId(c2);
        poid4.setCreationDate(now);
        PO po4 = new PO();
        poid4.setPo(po4);
        DigestList digestList4 = new DigestList();
        Digest digest4 = new Digest();
        digest4.setDigest("sasha4".getBytes(StandardCharsets.UTF_8));
        digestList4.setDigests(new ArrayList<>(List.of(new Digest[]{digest4})));
        digestList4.setDigestMethod(new URI(DigestAlgorithm.SHA256.getOid()));
        po4.setDigestList(digestList4);
        po4.setFormatId(new URI("http://uri.etsi.org/19512/format/DigestList"));
        po4.setPoid(poid4);
        poidList.add(poid4);

        poidRepository.saveAll(poidList);

        UUID uuid = poid1.getId();
        System.out.println(uuid);

        Optional<POID> test = poidRepository.findById(uuid);

        if(!test.isPresent()) System.out.println("Not present ...");
        else System.out.println("Present !");

        List<TreeCategoryDto> result = poidRepository.getToPreserveCategoriesPOIDAndRoot(now.plusMinutes(1), now.plusMinutes(10));

        for (TreeCategoryDto treeCategoryDto : result) {
            System.out.println(treeCategoryDto);
        }
        // TODO : more in depth test
        assertEquals(4, result.size());

//        System.out.println(profiles.get(0).getProfileIdentifier().toString());
//        assertEquals(1, profiles.size());
    }
}
