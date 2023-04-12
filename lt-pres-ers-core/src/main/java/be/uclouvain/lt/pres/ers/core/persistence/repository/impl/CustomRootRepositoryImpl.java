package be.uclouvain.lt.pres.ers.core.persistence.repository.impl;

import be.uclouvain.lt.pres.ers.core.persistence.model.Root;
import be.uclouvain.lt.pres.ers.core.persistence.repository.CustomRootRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;

import java.time.OffsetDateTime;
import java.util.List;

public class CustomRootRepositoryImpl implements CustomRootRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Root> getRootsForTree(OffsetDateTime dateNow, OffsetDateTime dateShifted, long clientId, String digestMethod, int nValues, int offset) {
        Session session = entityManager.unwrap(Session.class);
        // Note : The USING statement is mysql compatible
        NativeQuery<Root> q = ((NativeQuery<Root>) (session.createNativeQuery("""
                WITH
                    r AS (SELECT * FROM root WHERE CERT_VALID_UNTIL >= :DATE_NOW AND CERT_VALID_UNTIL <= :DATE_SHIFTED AND client_id = :CLIENT AND digest_method = :DIGEST_METHOD AND is_extended IS FALSE ORDER BY CERT_VALID_UNTIL LIMIT :N_VALUES OFFSET :OFFSET ),
                    n AS (SELECT * FROM nodes)
                SELECT * FROM r JOIN n USING (node_id);
            """).addEntity("root",Root.class))).addJoin( "n", "root.node");
        q.setParameter("DATE_NOW", dateNow);
        q.setParameter("DATE_SHIFTED", dateShifted);
        q.setParameter("CLIENT", clientId);
        q.setParameter("DIGEST_METHOD", digestMethod);
        q.setParameter("N_VALUES", nValues);
        q.setParameter("OFFSET", offset);

        return q.getResultList();
    }
}
