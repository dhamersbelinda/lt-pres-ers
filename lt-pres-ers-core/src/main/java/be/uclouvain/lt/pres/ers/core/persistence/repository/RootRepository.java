package be.uclouvain.lt.pres.ers.core.persistence.repository;

import be.uclouvain.lt.pres.ers.core.persistence.model.Node;
import be.uclouvain.lt.pres.ers.core.persistence.model.POID;
import be.uclouvain.lt.pres.ers.core.persistence.model.Root;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface RootRepository extends CrudRepository<Root, Node>, CustomRootRepository {

//    @Query(value= """
//                SELECT * FROM root WHERE CERT_VALID_UNTIL >= :DATE_NOW AND CERT_VALID_UNTIL <= :DATE_SHIFTED AND client_id = :CLIENT AND digest_method = :DIGEST_METHOD AND is_extended IS FALSE ORDER BY CERT_VALID_UNTIL LIMIT :N_VALUES OFFSET :OFFSET ;
//            """
//            ,nativeQuery = true)
//    List<Root> getRootsForTree(@Param("DATE_NOW") OffsetDateTime dateNow, @Param("DATE_SHIFTED") OffsetDateTime dateShifted, @Param("CLIENT") long clientId, @Param("DIGEST_METHOD") String digestMethod, @Param("N_VALUES") int nValues, @Param("OFFSET") int offset);
}
