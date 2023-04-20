package be.uclouvain.lt.pres.ers.core.service;

import be.uclouvain.lt.pres.ers.core.XMLObjects.EvidenceRecordType;
import be.uclouvain.lt.pres.ers.core.XMLObjects.HashTreeType;
import be.uclouvain.lt.pres.ers.core.XMLObjects.ObjectFactory;
import be.uclouvain.lt.pres.ers.core.XMLObjects.TimeStampType;
import be.uclouvain.lt.pres.ers.core.persistence.model.Digest;
import be.uclouvain.lt.pres.ers.core.persistence.model.DigestList;
import be.uclouvain.lt.pres.ers.core.persistence.model.PO;
import be.uclouvain.lt.pres.ers.core.persistence.model.POID;
import be.uclouvain.lt.pres.ers.core.persistence.model.dto.EvidenceRecordDto;
import be.uclouvain.lt.pres.ers.core.persistence.repository.POIDRepository;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.given;

//@SpringBootTest
public class EvidenceConverterServiceTest {
    /*
    @MockBean(POIDRepository.class)
    private POIDRepository poidRepository;

    @Autowired
    EvidenceConverterService evidenceConverterService;
     */

    private String ts = "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";
    /*
    @Autowired
    private POIDRepository poidRepository;
     */

    public EvidenceConverterServiceTest() {
        super();
    }

    @Test
    public void conversion() throws URISyntaxException {

        List<EvidenceRecordDto> evidenceRecordDtoList = new ArrayList<>();
        /*
        evidenceRecordDtoList.add(new EvidenceRecordDto(1L,null, "test".getBytes(StandardCharsets.UTF_8), 2L, 0L, "ts".getBytes(StandardCharsets.UTF_8), true));

        when(this.poidRepository.getERPathFromPOID(any())).thenReturn(evidenceRecordDtoList);
        */
        PO po = new PO();
        DigestList dl = new DigestList();
        po.setDigestList(dl);
        dl.setDigestMethod(new URI(DigestAlgorithm.SHA256.getOid()));
        List<Digest> dlist = new ArrayList<>();
        dlist.add(new Digest(null, "test".getBytes(StandardCharsets.UTF_8),null));
        dl.setDigests(dlist);
        POID r = new POID();
        r.setPo(po);
        r.setDigestMethod(DigestAlgorithm.SHA256.getOid());
        //when(this.poidRepository.findById(any())).thenReturn(java.util.Optional.of(r));

        //construct example
        /*
        evidenceRecordDtoList.add(new EvidenceRecordDto(25L, null, "test".getBytes(StandardCharsets.UTF_8), 7L, 0L, Base64.getDecoder().decode(ts),true));
        */
        evidenceRecordDtoList.add(new EvidenceRecordDto(4L, 2L, "test".getBytes(StandardCharsets.UTF_8), 1L, 3L, Base64.getDecoder().decode(ts),false));
        evidenceRecordDtoList.add(new EvidenceRecordDto(5L, 2L, "test".getBytes(StandardCharsets.UTF_8), 1L, 3L, Base64.getDecoder().decode(ts),true));
        evidenceRecordDtoList.add(new EvidenceRecordDto(3L, 1L, "test".getBytes(StandardCharsets.UTF_8), 1L, 2L, Base64.getDecoder().decode(ts),false));
        evidenceRecordDtoList.add(new EvidenceRecordDto(1L, 14L, "test".getBytes(StandardCharsets.UTF_8), 1L, 0L, Base64.getDecoder().decode(ts),false));

        evidenceRecordDtoList.add(new EvidenceRecordDto(14L, 12L, "test".getBytes(StandardCharsets.UTF_8), 3L, 3L, Base64.getDecoder().decode(ts),false));
        evidenceRecordDtoList.add(new EvidenceRecordDto(15L, 12L, "test".getBytes(StandardCharsets.UTF_8), 3L, 4L, Base64.getDecoder().decode(ts),false));
        evidenceRecordDtoList.add(new EvidenceRecordDto(13L, 11L, "test".getBytes(StandardCharsets.UTF_8), 3L, 2L, Base64.getDecoder().decode(ts),false));
        evidenceRecordDtoList.add(new EvidenceRecordDto(11L, 22L, "test".getBytes(StandardCharsets.UTF_8), 3L, 0L, Base64.getDecoder().decode(ts),false));

        evidenceRecordDtoList.add(new EvidenceRecordDto(22L, 21L, "test".getBytes(StandardCharsets.UTF_8), 5L, 1L, Base64.getDecoder().decode(ts),false));
        evidenceRecordDtoList.add(new EvidenceRecordDto(23L, 21L, "test".getBytes(StandardCharsets.UTF_8), 5L, 2L, Base64.getDecoder().decode(ts),false));
        evidenceRecordDtoList.add(new EvidenceRecordDto(21L, 24L, "test".getBytes(StandardCharsets.UTF_8), 5L, 0L, Base64.getDecoder().decode(ts),false));

        evidenceRecordDtoList.add(new EvidenceRecordDto(24L, null, "test".getBytes(StandardCharsets.UTF_8), 6L, 0L, Base64.getDecoder().decode(ts),false));

        EvidenceRecordType er = EvidenceRecordType.build(evidenceRecordDtoList, r);


        String xmlString = null;
        ObjectFactory objectFactory = new ObjectFactory();
        JAXBElement<EvidenceRecordType> evidenceRecordTypeJAXBElement = objectFactory.createEvidenceRecord(er);

        try {
            JAXBContext context = JAXBContext.newInstance("be.uclouvain.lt.pres.ers.core.XMLObjects");


            //Setup schema validator
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            File file = new File("C:/Users/belin/lt-pres-ers/lt-pres-ers-core/src/main/java/be/uclouvain/lt/pres/ers/core/XMLObjects/globalSchema.xsd");
            Schema xmlSchema = sf.newSchema(file);
            Marshaller mar = context.createMarshaller();
            mar.setSchema(xmlSchema);
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);


            StringWriter sw = new StringWriter();
            mar.marshal(evidenceRecordTypeJAXBElement, sw);

            xmlString = sw.toString();
            System.out.println(xmlString);
        } catch (JAXBException | SAXException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void conversion1Node() throws URISyntaxException {

        List<EvidenceRecordDto> evidenceRecordDtoList = new ArrayList<>();
        /*
        evidenceRecordDtoList.add(new EvidenceRecordDto(1L,null, "test".getBytes(StandardCharsets.UTF_8), 2L, 0L, "ts".getBytes(StandardCharsets.UTF_8), true));

        when(this.poidRepository.getERPathFromPOID(any())).thenReturn(evidenceRecordDtoList);
        */
        PO po = new PO();
        DigestList dl = new DigestList();
        po.setDigestList(dl);
        dl.setDigestMethod(new URI(DigestAlgorithm.SHA256.getOid()));
        List<Digest> dlist = new ArrayList<>();
        dlist.add(new Digest(null, "test".getBytes(StandardCharsets.UTF_8),null));
        dl.setDigests(dlist);
        POID r = new POID();
        r.setPo(po);
        r.setDigestMethod(DigestAlgorithm.SHA256.getOid());
        //when(this.poidRepository.findById(any())).thenReturn(java.util.Optional.of(r));

        //construct example
        /*
        evidenceRecordDtoList.add(new EvidenceRecordDto(25L, null, "test".getBytes(StandardCharsets.UTF_8), 7L, 0L, Base64.getDecoder().decode(ts),true));
        */
        evidenceRecordDtoList.add(new EvidenceRecordDto(1L, null, "test".getBytes(StandardCharsets.UTF_8), 1L, 0L, Base64.getDecoder().decode(ts),true));


        EvidenceRecordType er = EvidenceRecordType.build(evidenceRecordDtoList, r);


        String xmlString = null;
        ObjectFactory objectFactory = new ObjectFactory();
        JAXBElement<EvidenceRecordType> evidenceRecordTypeJAXBElement = objectFactory.createEvidenceRecord(er);

        try {
            JAXBContext context = JAXBContext.newInstance("be.uclouvain.lt.pres.ers.core.XMLObjects");
            Marshaller mar = context.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            StringWriter sw = new StringWriter();
            mar.marshal(evidenceRecordTypeJAXBElement, sw);

            xmlString = sw.toString();
            System.out.println(xmlString);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void conversion3VRNode() throws URISyntaxException {

        List<EvidenceRecordDto> evidenceRecordDtoList = new ArrayList<>();
        /*
        evidenceRecordDtoList.add(new EvidenceRecordDto(1L,null, "test".getBytes(StandardCharsets.UTF_8), 2L, 0L, "ts".getBytes(StandardCharsets.UTF_8), true));

        when(this.poidRepository.getERPathFromPOID(any())).thenReturn(evidenceRecordDtoList);
        */
        PO po = new PO();
        DigestList dl = new DigestList();
        po.setDigestList(dl);
        dl.setDigestMethod(new URI(DigestAlgorithm.SHA256.getOid()));
        List<Digest> dlist = new ArrayList<>();
        dlist.add(new Digest(null, "test".getBytes(StandardCharsets.UTF_8),null));
        dl.setDigests(dlist);
        POID r = new POID();
        r.setPo(po);
        r.setDigestMethod(DigestAlgorithm.SHA256.getOid());
        //when(this.poidRepository.findById(any())).thenReturn(java.util.Optional.of(r));

        //construct example
        /*
        evidenceRecordDtoList.add(new EvidenceRecordDto(25L, null, "test".getBytes(StandardCharsets.UTF_8), 7L, 0L, Base64.getDecoder().decode(ts),true));
        */
        evidenceRecordDtoList.add(new EvidenceRecordDto(26L, 27L, "test".getBytes(StandardCharsets.UTF_8), 1L, 0L, Base64.getDecoder().decode(ts),true));
        evidenceRecordDtoList.add(new EvidenceRecordDto(27L, 28L, "test".getBytes(StandardCharsets.UTF_8), 2L, 0L, Base64.getDecoder().decode(ts),false));
        evidenceRecordDtoList.add(new EvidenceRecordDto(28L, null, "test".getBytes(StandardCharsets.UTF_8), 3L, 0L, Base64.getDecoder().decode(ts),false));


        EvidenceRecordType er = EvidenceRecordType.build(evidenceRecordDtoList, r);


        String xmlString = null;
        ObjectFactory objectFactory = new ObjectFactory();
        JAXBElement<EvidenceRecordType> evidenceRecordTypeJAXBElement = objectFactory.createEvidenceRecord(er);

        try {
            JAXBContext context = JAXBContext.newInstance("be.uclouvain.lt.pres.ers.core.XMLObjects");


            //Setup schema validator
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            File file = new File("C:/Users/belin/lt-pres-ers/lt-pres-ers-core/src/main/java/be/uclouvain/lt/pres/ers/core/XMLObjects/globalSchema.xsd");
            Schema xmlSchema = sf.newSchema(file);
            Marshaller mar = context.createMarshaller();
            mar.setSchema(xmlSchema);
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            StringWriter sw = new StringWriter();
            mar.marshal(evidenceRecordTypeJAXBElement, sw);

            xmlString = sw.toString();
            //System.out.println(xmlString);
        } catch (JAXBException | SAXException e) {
            e.printStackTrace();
        }
    }
}
