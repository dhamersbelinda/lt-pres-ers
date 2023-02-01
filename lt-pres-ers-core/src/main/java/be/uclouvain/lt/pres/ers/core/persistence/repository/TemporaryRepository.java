package be.uclouvain.lt.pres.ers.core.persistence.repository;

import be.uclouvain.lt.pres.ers.core.persistence.model.TemporaryRecord;
import be.uclouvain.lt.pres.ers.core.persistence.model.TemporaryRecordID;
import org.springframework.data.repository.CrudRepository;

import java.net.URI;

public interface TemporaryRepository extends CrudRepository<TemporaryRecord, TemporaryRecordID> {
}
