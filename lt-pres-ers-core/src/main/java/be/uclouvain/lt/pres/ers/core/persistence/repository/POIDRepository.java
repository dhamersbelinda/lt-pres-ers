package be.uclouvain.lt.pres.ers.core.persistence.repository;

import be.uclouvain.lt.pres.ers.core.persistence.model.POID;
import be.uclouvain.lt.pres.ers.core.persistence.model.dto.EvidenceRecordDto;
import be.uclouvain.lt.pres.ers.core.persistence.model.dto.TreeCategoryDto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface POIDRepository extends JpaRepository<POID, UUID>, CustomPOIDRepository {

    //TODO change here
    @EntityGraph(value = "request-entity-graph", type = EntityGraphType.FETCH)
    Optional<POID> findById(UUID id);

    @EntityGraph(value = "request-entity-graph", type = EntityGraphType.FETCH)
    Stream<POID> streamAllBy();

//    @Query(name = "POID.getERPathFromPOID", nativeQuery = true)
    @Query(nativeQuery = true)
    List<EvidenceRecordDto> getERPathFromPOID(@Param("POID") UUID poid);

    // Used to get all the unique tuples (client_id, digest_method) for which a (new) POID or a root node must be preserved
    // The below two queries are defined in the POID entity class
    @Query(nativeQuery = true)
    List<TreeCategoryDto> getToPreserveCategoriesPOIDAndRoot(@Param("DATE_NOW") OffsetDateTime dateNow, @Param("DATE_SHIFTED") OffsetDateTime dateShifted);

    @Query(nativeQuery = true)
    List<TreeCategoryDto> getToPreserveCategoriesPOIDOnly(@Param("DATE_NOW") OffsetDateTime dateNow);

//    @Query(value= """
//                    SELECT * FROM POIDs p WHERE creation_date < :DATE_NOW AND client_id = :CLIENT AND digest_method = :DIGEST_METHOD AND node_id IS NULL ORDER BY creation_date LIMIT :N_VALUES OFFSET :OFFSET ;
//                """, nativeQuery = true)
//    List<POID> getPOIDsForTree(@Param("DATE_NOW") OffsetDateTime dateNow, @Param("CLIENT") long clientId, @Param("DIGEST_METHOD") String digestMethod, @Param("N_VALUES") int nValues, @Param("OFFSET") int offset);
}
