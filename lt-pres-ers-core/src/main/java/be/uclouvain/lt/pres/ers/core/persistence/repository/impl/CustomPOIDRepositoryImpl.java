package be.uclouvain.lt.pres.ers.core.persistence.repository.impl;

import be.uclouvain.lt.pres.ers.core.persistence.model.POID;
import be.uclouvain.lt.pres.ers.core.persistence.model.Root;
import be.uclouvain.lt.pres.ers.core.persistence.repository.CustomPOIDRepository;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.OffsetDateTime;
import java.util.List;

public class CustomPOIDRepositoryImpl implements CustomPOIDRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<POID> getPOIDsForTree(OffsetDateTime dateNow, long clientId, String digestMethod, int nValues, int offset) {
        Session session = entityManager.unwrap(Session.class);
        // Note : The USING statement is mysql compatible
        NativeQuery<POID> q = ((NativeQuery<POID>) ((session.createNativeQuery("""
                WITH
                    p AS (SELECT * FROM POIDs WHERE creation_date < :DATE_NOW AND client_id = :CLIENT AND digest_method = :DIGEST_METHOD AND node_id IS NULL ORDER BY creation_date LIMIT :N_VALUES OFFSET :OFFSET )
                SELECT * FROM p JOIN po ON p.poid=po.req_id JOIN digestlist ON digestlist.po_id=po.id JOIN digest ON digest.digestlist_id=digestlist.id;
            """).addEntity("poid",POID.class))).addJoin( "po", "poid.po").addJoin("digestlist","po.digestList").addJoin("digest","digestlist.digests").addEntity("poid",POID.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY));
        q.setParameter("DATE_NOW", dateNow);
        q.setParameter("CLIENT", clientId);
        q.setParameter("DIGEST_METHOD", digestMethod);
        q.setParameter("N_VALUES", nValues);
        q.setParameter("OFFSET", offset);

        return q.getResultList();
    }
}
