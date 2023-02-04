package be.uclouvain.lt.pres.ers.core.persistence.repository;

import be.uclouvain.lt.pres.ers.core.persistence.model.TemporaryRecord;
import be.uclouvain.lt.pres.ers.core.persistence.model.TemporaryRecordID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.net.URI;
import java.util.List;

public interface TemporaryRepository extends CrudRepository<TemporaryRecord, TemporaryRecordID> {

    @EntityGraph(value = "tempRecord-entity-graph", type = EntityGraph.EntityGraphType.FETCH)
    //TODO find annotation for size limit
    List<TemporaryRecord> findAllBy();

    //TODO do we need an annotation here ?
    void deleteAllBy();
}
