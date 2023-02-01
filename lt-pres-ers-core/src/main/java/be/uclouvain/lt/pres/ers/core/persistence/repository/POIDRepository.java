package be.uclouvain.lt.pres.ers.core.persistence.repository;

import be.uclouvain.lt.pres.ers.core.persistence.model.POID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface POIDRepository extends CrudRepository<POID, UUID> {

    //TODO change here
    @EntityGraph(value = "request-entity-graph", type = EntityGraphType.FETCH)
    Optional<POID> findById(UUID id);

    @EntityGraph(value = "request-entity-graph", type = EntityGraphType.FETCH)
    Stream<POID> streamAllBy();


}
