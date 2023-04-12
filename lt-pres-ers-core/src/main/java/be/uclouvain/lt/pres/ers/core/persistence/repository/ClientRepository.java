package be.uclouvain.lt.pres.ers.core.persistence.repository;

import be.uclouvain.lt.pres.ers.core.persistence.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
