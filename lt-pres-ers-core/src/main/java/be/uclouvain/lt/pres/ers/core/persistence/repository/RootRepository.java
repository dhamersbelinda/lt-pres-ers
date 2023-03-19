package be.uclouvain.lt.pres.ers.core.persistence.repository;

import be.uclouvain.lt.pres.ers.core.persistence.model.Node;
import be.uclouvain.lt.pres.ers.core.persistence.model.Root;
import org.springframework.data.repository.CrudRepository;

public interface RootRepository extends CrudRepository<Root, Node>, CustomRootRepository {
}
