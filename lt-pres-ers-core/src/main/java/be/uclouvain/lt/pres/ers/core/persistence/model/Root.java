package be.uclouvain.lt.pres.ers.core.persistence.model;

import be.uclouvain.lt.pres.ers.core.persistence.model.dto.TreeCategoryDto;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.xades.reference.CanonicalizationTransform;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.persistence.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Base64;


//@NamedNativeQuery(
//        name = "Root.getPOIDsForTree",
//        query = """
//                SELECT * FROM (
//                    (SELECT * FROM root WHERE CERT_VALID_UNTIL >= :DATE_NOW AND CERT_VALID_UNTIL <= :DATE_SHIFTED
//                        AND client_id = :CLIENT AND digest_method = :DIGEST_METHOD AND is_extended IS FALSE ORDER BY CERT_VALID_UNTIL ) AS r
//                    JOIN (SELECT * FROM nodes) AS n ON r.node_id = n.node_id ) AS ret;
//            """,
//        resultSetMapping = "RootNodeMapping"
//)
//@SqlResultSetMapping(name = "RootNodeMapping",
//        entities = {
//                @EntityResult(
//                        entityClass = Root.class,
//                        fields = {
//                                @FieldResult( name = "nodeId", column = "node_id" ),
//                                @FieldResult( name = "node", column = "node_id" ),
//                                @FieldResult( name = "clientId", column = "client_id" ),
//                                @FieldResult( name = "digestMethod", column = "digest_method" ),
//                                @FieldResult( name = "certValidUntil", column = "cert_valid_until" ),
//                                @FieldResult( name = "isExtended", column = "is_extended" ),
//                                @FieldResult( name = "timestamp", column = "root_timestamp" ),
//                        }
//                ),
//                @EntityResult(
//                        entityClass = Node.class,
//                        fields = {
//                                @FieldResult( name = "nodeId", column = "" ),
//                                @FieldResult( name = "parent", column = "parent_id" ),
//                                @FieldResult( name = "neighbour", column = "neighbour_id" ),
//                                @FieldResult( name = "treeId", column = "tree_id" ),
//                                @FieldResult( name = "inTreeId", column = "in_tree_id" ),
//                                @FieldResult( name = "nodeValue", column = "node_value" ),
//                        }
//                )
//        })
@Entity(name = "ROOT")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Root implements Treeable{

    @Id
    private long nodeId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "NODE_ID", referencedColumnName = "NODE_ID")
    private Node node;

    @JoinColumn(name = "CLIENT_ID", referencedColumnName = "CLIENT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_CLIENT_ID_ROOT"))
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude // Exclude as we have a proxy entity when building the tree
    private Client clientId;

    @ToString.Include
    private String clientID() {
        return this.clientId.getClientId().toString();
    }

    @Column(name = "DIGEST_METHOD", nullable = false, length = 512) // TODO : length ?
    private String digestMethod;

    @Column(name = "CERT_VALID_UNTIL", nullable = false)
    private OffsetDateTime certValidUntil;

    @Column(name = "IS_EXTENDED", nullable = false)
    private Boolean isExtended;

    @Column(name = "ROOT_TIMESTAMP", nullable = false)
    @ToString.Exclude
    private byte[] timestamp;

    @Override
    public boolean isRoot() {
        return true;
    }

    @Override
    public byte[] getHashValue() {
        // TODO : hash the ts
        DigestAlgorithm alg = DigestAlgorithm.forOID(this.digestMethod);
        // TODO : hash the canonicalization version of the timestamp element ...
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document doc;
        // TODO : do with classes ect ? avoid hardcoded ...
        String xml = "<TimeStamp Type=\"RFC3161\">"+ new String(Base64.getEncoder().encode(this.getTimestamp()), StandardCharsets.UTF_8)+"</TimeStamp>";

        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse( new InputSource( new StringReader( xml ) ) );
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException | SAXException e) {
            throw new RuntimeException(e);
        }

        CanonicalizationTransform transform = new CanonicalizationTransform("http://www.w3.org/2006/12/xml-c14n11");
        byte[] canonicalized = transform.getBytesAfterTransformation(doc);

        return DSSUtils.digest(alg, canonicalized);
    }
}
