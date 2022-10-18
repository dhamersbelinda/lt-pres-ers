package be.uclouvain.lt.pres.ers.core.service;

import java.net.URI;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import be.uclouvain.lt.pres.ers.core.exception.ProfileNotFoundException;
import be.uclouvain.lt.pres.ers.model.ProfileDto;
import be.uclouvain.lt.pres.ers.model.ProfileStatus;

@SpringBootTest
public class ProfileServiceTest {

    @Autowired
    private ProfileService service;

    public ProfileServiceTest() {
        super();
    }

    @Test
    public void testGetProfilesNullStatus() {
        Assertions.assertThrows(ConstraintViolationException.class, () -> this.service.getProfiles(null));
    }

    @Test
    public void testGetProfileNullURI() {
        Assertions.assertThrows(ConstraintViolationException.class, () -> this.service.getProfile(null));
    }

    @Test
    public void testGetProfileUnknownURI() {
        Assertions.assertThrows(ProfileNotFoundException.class,
                () -> this.service.getProfile(URI.create("urn:unknown")));
    }

    @Test
    public void testGetInactiveProfiles() {
        Assertions.assertEquals(0, this.service.getProfiles(ProfileStatus.INACTIVE).size());
    }

    @Test
    public void testGetActiveProfiles() {
        Assertions.assertEquals(1, this.service.getProfiles(ProfileStatus.ACTIVE).size());
    }

    @Test
    public void testGetAllProfiles() {
        final List<ProfileDto> profiles = this.service.getProfiles(ProfileStatus.ALL);
        Assertions.assertEquals(1, profiles.size());
        // TODO: test attributes of the sole returned profile
    }

    @Test
    public void testGetProfile() {
        final URI profileIdentifier = URI.create("https://uclouvain.be/en/faculties/epl/preservation-api/profile/v1.0");
        final ProfileDto profile = this.service.getProfile(profileIdentifier);
        Assertions.assertEquals(profileIdentifier, profile.getProfileIdentifier());
        // TODO: test other attributes of the sole returned profile
    }
}
