package be.uclouvain.lt.pres.ers.core.persistence.repository;

import be.uclouvain.lt.pres.ers.core.persistence.model.Node;
import be.uclouvain.lt.pres.ers.core.persistence.model.Root;
import be.uclouvain.lt.pres.ers.core.persistence.model.dto.TreeCategoryDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface RootRepository extends CrudRepository<Root, Node>, CustomRootRepository {
    // Query to get the unique pairs (client_id, digest_method) of roots that must be extended
    // The below query is defined in the Root entity class
    @Query(nativeQuery = true)
    List<TreeCategoryDto> getToPreserveCategoriesRootOnly(@Param("DATE_NOW") OffsetDateTime dateNow, @Param("DATE_SHIFTED") OffsetDateTime dateShifted);
}
