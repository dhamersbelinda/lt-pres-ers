package be.uclouvain.lt.pres.ers.core.persistence.repository;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.stream.Stream;

import org.springframework.data.repository.CrudRepository;

import be.uclouvain.lt.pres.ers.core.persistence.model.Profile;

public interface ProfileRepository extends CrudRepository<Profile, URI> {

    // Find all active profiles
    Stream<Profile> findByValidUntilIsNullOrValidUntilAfter(OffsetDateTime now);

    // Find all inactive profiles
    Stream<Profile> findByValidUntilIsNotNullAndValidUntilBefore(OffsetDateTime now);

    Stream<Profile> streamAll();

}
