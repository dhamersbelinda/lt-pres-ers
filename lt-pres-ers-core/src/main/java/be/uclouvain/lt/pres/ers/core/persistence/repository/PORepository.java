package be.uclouvain.lt.pres.ers.core.persistence.repository;

import be.uclouvain.lt.pres.ers.core.persistence.model.PO;
import be.uclouvain.lt.pres.ers.core.persistence.model.PreservePORequest;
import be.uclouvain.lt.pres.ers.core.persistence.model.Profile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.repository.CrudRepository;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.stream.Stream;

public interface PORepository extends CrudRepository<PreservePORequest, URI> {

    //TODO change here
    @EntityGraph(value = "po-entity-graph", type = EntityGraphType.FETCH)
    Optional<PO> findById(Long id);

    @EntityGraph(value = "po-entity-graph", type = EntityGraphType.FETCH)
    Stream<PO> streamAllBy();


}
