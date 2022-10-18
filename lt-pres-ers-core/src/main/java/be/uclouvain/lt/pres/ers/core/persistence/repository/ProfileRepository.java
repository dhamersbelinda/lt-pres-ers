package be.uclouvain.lt.pres.ers.core.persistence.repository;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.repository.CrudRepository;

import be.uclouvain.lt.pres.ers.core.persistence.model.Profile;

public interface ProfileRepository extends CrudRepository<Profile, URI> {

    // Find all active profiles
    @EntityGraph(value = "profile-entity-graph", type = EntityGraphType.FETCH)
    Stream<Profile> findByValidUntilIsNullOrValidUntilAfter(OffsetDateTime now);

    // Find all inactive profiles
    @EntityGraph(value = "profile-entity-graph", type = EntityGraphType.FETCH)
    Stream<Profile> findByValidUntilIsNotNullAndValidUntilBefore(OffsetDateTime now);

    @EntityGraph(value = "profile-entity-graph", type = EntityGraphType.FETCH)
    Stream<Profile> streamAllBy();

    @EntityGraph(value = "profile-entity-graph", type = EntityGraphType.FETCH)
    Optional<Profile> findByProfileIdentifier(URI profileIdentifier);

}
