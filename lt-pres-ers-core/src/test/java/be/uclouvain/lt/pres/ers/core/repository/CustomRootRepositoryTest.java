package be.uclouvain.lt.pres.ers.core.repository;

import be.uclouvain.lt.pres.ers.core.persistence.model.Client;
import be.uclouvain.lt.pres.ers.core.persistence.model.Node;
import be.uclouvain.lt.pres.ers.core.persistence.model.Root;
import be.uclouvain.lt.pres.ers.core.persistence.model.TreeID;
import be.uclouvain.lt.pres.ers.core.persistence.repository.ClientRepository;
import be.uclouvain.lt.pres.ers.core.persistence.repository.RootRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@ActiveProfiles("postgres")
public class CustomRootRepositoryTest {
    @Autowired
    RootRepository rootRepository;
    @Autowired
    ClientRepository clientRepository;

    @Test
    public void getRootsForTreeTest() {
        OffsetDateTime start = OffsetDateTime.now();
        OffsetDateTime startPlusOneYear = start.plusYears(1);
        String digestMethod = "digest_method_here";

        List<Root> roots = new ArrayList<>();
        Client c1 = new Client();
        clientRepository.save(c1);

        Client c2 = new Client();
        clientRepository.save(c2);
        long clientId1 = c1.getClientId(), clientId2 = c2.getClientId();
        TreeID treeID = new TreeID();

        Root tempRoot;
        Node tempNode;
        for (int i = 0; i < 4; i++) {
            tempNode = new Node();
            tempNode.setInTreeId(i);
            tempNode.setTreeId(treeID);
            tempNode.setNodeValue(new byte[]{(byte)i});

            tempRoot = new Root();
            tempRoot.setCertValidUntil(start);
            tempRoot.setIsExtended(false);
            tempRoot.setTimestamp(new byte[]{(byte) (i)});
            tempRoot.setClientId(i%2==0?c1:c2);
            tempRoot.setNode(tempNode);
            tempRoot.setDigestMethod(digestMethod);

            roots.add(tempRoot);
        }
        rootRepository.saveAll(roots);

        List<Root> result = rootRepository.getRootsForTree(start, startPlusOneYear, clientId1, digestMethod, 10, 0);
        System.out.println("Query done, result size : "+result.size());
        for (Root root : result) {
            // Checking that we can modify
            root.getNode().setInTreeId(400 + root.getNode().getInTreeId());
            System.out.println(root);
            rootRepository.save(root);
        }
    }

}
