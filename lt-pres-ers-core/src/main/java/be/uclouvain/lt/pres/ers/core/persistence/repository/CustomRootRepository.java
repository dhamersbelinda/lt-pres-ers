package be.uclouvain.lt.pres.ers.core.persistence.repository;

import be.uclouvain.lt.pres.ers.core.persistence.model.Root;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface CustomRootRepository {

    List<Root> getRootsForTree(OffsetDateTime dateNow, OffsetDateTime dateShifted, long clientId, String digestMethod, int nValues, int offset);
}
