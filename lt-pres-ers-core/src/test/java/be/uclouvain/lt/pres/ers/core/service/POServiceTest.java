package be.uclouvain.lt.pres.ers.core.service;

import be.uclouvain.lt.pres.ers.core.exception.ProfileNotFoundException;
import be.uclouvain.lt.pres.ers.model.DigestListDto;
import be.uclouvain.lt.pres.ers.model.PODto;
import be.uclouvain.lt.pres.ers.model.ProfileDto;
import be.uclouvain.lt.pres.ers.model.ProfileStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolationException;
import be.uclouvain.lt.pres.ers.core.mapper.PODtoMapperCore;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class POServiceTest {

    @Autowired
    private POService service;

    @Autowired
    private PODtoMapperCore mapper;

    public POServiceTest() {
        super();
    }

    /*
    @Test
    public void testInsertPOS() {
        //create object
        PODto poDto = new PODto();
        poDto.setBinaryValue("this is the value");
        poDto.setFormatId(URI.create("format:id"));
        poDto.setClientId("u:id");

        //create DigestList
        DigestListDto dlDto = new DigestListDto();
        dlDto.setDigestMethod(URI.create("digest:method"));
        dlDto.setDigests(new ArrayList<>(Arrays.asList("digest1", "digest2")));

        poDto.setDigestList(dlDto);

        System.out.println(poDto);
        System.out.println(mapper.toPO(poDto));


        String poid = this.service.insertPOs(new ArrayList<>(Arrays.asList(poDto)));
        System.out.println(poid);
    }

*/
    
    /*
    @Test
    public void testGetProfile() {
        final URI profileIdentifier = URI.create("https://uclouvain.be/en/faculties/epl/preservation-api/profile/v1.0");
        final ProfileDto profile = this.service.getProfile(profileIdentifier);
        Assertions.assertEquals(profileIdentifier, profile.getProfileIdentifier());
        // TODO: test other attributes of the sole returned profile
    }
    */
}
