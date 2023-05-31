package be.uclouvain.lt.pres.ers.core.persistence.repository;

import be.uclouvain.lt.pres.ers.core.persistence.model.POID;
import be.uclouvain.lt.pres.ers.core.persistence.model.Root;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface CustomPOIDRepository {

    List<POID> getPOIDsForTree(OffsetDateTime dateNow, long clientId, String digestMethod, int nValues, int offset);

//    @Query(value= """
//                    SELECT * FROM POIDs p WHERE creation_date < :DATE_NOW AND client_id = :CLIENT AND digest_method = :DIGEST_METHOD AND node_id IS NULL ORDER BY creation_date LIMIT :N_VALUES OFFSET :OFFSET ;
//                """, nativeQuery = true)
//    List<POID> getPOIDsForTree(@Param("DATE_NOW") OffsetDateTime dateNow, @Param("CLIENT") long clientId, @Param("DIGEST_METHOD") String digestMethod, @Param("N_VALUES") int nValues, @Param("OFFSET") int offset);

//    List<Root> getRootsForTree(OffsetDateTime dateNow, OffsetDateTime dateShifted, long clientId, String digestMethod, int nValues, int offset);
}
