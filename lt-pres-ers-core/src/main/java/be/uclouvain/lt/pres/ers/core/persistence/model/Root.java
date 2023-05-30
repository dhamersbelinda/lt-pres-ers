package be.uclouvain.lt.pres.ers.core.persistence.model;

import be.uclouvain.lt.pres.ers.core.XMLObjects.TimeStampType;
import be.uclouvain.lt.pres.ers.core.persistence.model.dto.TreeCategoryDto;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.TimestampType;
import eu.europa.esig.dss.model.TimestampBinary;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.validation.timestamp.TimestampToken;
import eu.europa.esig.dss.xades.reference.CanonicalizationTransform;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.tsp.TSPException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.persistence.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Base64;

@NamedNativeQuery(
        name = "Root.getToPreserveCategoriesRootOnly",
        query = """
                   SELECT DISTINCT client_id, digest_method FROM root WHERE :DATE_NOW <= cert_valid_until AND cert_valid_until <= :DATE_SHIFTED ;
                   """,
        resultSetMapping = "TreeCategoryDtoMapping"
)

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
    @ToString.Exclude
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
        // TODO : check if all the validation data is present
//        TimestampToken timestampToken;
//        TimestampBinary tsBinary = ;
//        try {
//            timestampToken = new TimestampToken(tsBinary.getBytes(), TimestampType.CONTENT_TIMESTAMP);
//            // TODO : better errors and signalling
//        } catch (TSPException e) {
//            logger.error("TSP Exception ! "+e.getMessage());
//            continue;
//        } catch (IOException e) {
//            logger.error("IO Exception ! "+e.getMessage());
//            continue;
//        } catch (CMSException e) {
//            logger.error("CMS Exception ! "+e.getMessage());
//            continue;
//        }

        // TODO : hash the ts
        DigestAlgorithm alg = DigestAlgorithm.forOID(this.digestMethod);

        // TODO : hash the canonicalization version of the timestamp element ...
        TimeStampType timeStamp = new TimeStampType();
        TimeStampType.TimeStampToken timeStampToken = new TimeStampType.TimeStampToken();
        timeStamp.setTimeStampToken(timeStampToken);
        timeStampToken.setType("RFC3161");
        timeStampToken.getContent().add(new String(Base64.getEncoder().encode(this.getTimestamp()), StandardCharsets.UTF_8));

        JAXBElement<TimeStampType> timeStampJAXBElement = new JAXBElement<TimeStampType>(new QName("urn:ietf:params:xml:ns:ers", "TimeStamp"), TimeStampType.class, null, timeStamp);

        try {
            JAXBContext context = JAXBContext.newInstance("be.uclouvain.lt.pres.ers.core.XMLObjects");
            Marshaller mar = context.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            DOMResult res = new DOMResult();
            mar.setProperty(Marshaller.JAXB_FRAGMENT, true);
            mar.marshal(timeStampJAXBElement, res);

            Document doc = (Document) res.getNode();
            CanonicalizationTransform transform = new CanonicalizationTransform("http://www.w3.org/2006/12/xml-c14n11");
            byte[] canonicalized = transform.getBytesAfterTransformation(doc);
            System.out.println(new String(canonicalized));
            return DSSUtils.digest(alg, canonicalized);
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException(); // TODO : better handling here
        }
    }
}
