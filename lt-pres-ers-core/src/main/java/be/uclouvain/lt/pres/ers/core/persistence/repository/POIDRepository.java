package be.uclouvain.lt.pres.ers.core.persistence.repository;

import be.uclouvain.lt.pres.ers.core.persistence.model.POID;
import be.uclouvain.lt.pres.ers.core.persistence.model.dto.EvidenceRecordDto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface POIDRepository extends CrudRepository<POID, UUID> {

    //TODO change here
    @EntityGraph(value = "request-entity-graph", type = EntityGraphType.FETCH)
    Optional<POID> findById(UUID id);

    @EntityGraph(value = "request-entity-graph", type = EntityGraphType.FETCH)
    Stream<POID> streamAllBy();

//    @Query(name = "POID.getERPathFromPOID", nativeQuery = true)
    @Query(nativeQuery = true)
    List<EvidenceRecordDto> getERPathFromPOID(@Param("POID") UUID poid);
}
