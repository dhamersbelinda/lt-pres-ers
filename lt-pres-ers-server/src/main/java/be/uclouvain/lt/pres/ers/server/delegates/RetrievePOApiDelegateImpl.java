package be.uclouvain.lt.pres.ers.server.delegates;

import be.uclouvain.lt.pres.ers.core.XMLObjects.EvidenceRecordType;
import be.uclouvain.lt.pres.ers.core.XMLObjects.ObjectFactory;
import be.uclouvain.lt.pres.ers.core.service.impl.EvidenceRecordDTOToEvidenceRecordType;
import be.uclouvain.lt.pres.ers.core.persistence.model.dto.EvidenceRecordDto;
import be.uclouvain.lt.pres.ers.core.service.POService;
import be.uclouvain.lt.pres.ers.server.api.RetrievePOApiDelegate;
import be.uclouvain.lt.pres.ers.server.mapper.ProfileDtoMapper;
import be.uclouvain.lt.pres.ers.server.model.*;
import be.uclouvain.lt.pres.ers.server.model.DsbResultType.MajEnum;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class RetrievePOApiDelegateImpl implements RetrievePOApiDelegate {
    private final Logger logger = LoggerFactory.getLogger(RetrievePOApiDelegate.class);
    private final POService service;
    private final EvidenceRecordDTOToEvidenceRecordType converterService;

    private final ProfileDtoMapper mapper;

    @Override
    public ResponseEntity<PresRetrievePOResponseType> retrievePOPost(final PresRetrievePOType request) {


        // VersionID : If versionID is present return error ? => we don't use versioning but could add an immutable versionID for each document, then check it after DB fetch ??
        /* SubjectOfRetrieval (sor) : "If this element is missing POwithEmbeddedEvidence shall be used as default value"
            - if not specified (or equals to default) and POFromat is digestList then error ? (cannot embed in a digest list ... ??)
            - else adapt return type,
         */


        // retrieve ER object from core using POID => how to know which digest from the digest list we should get the ER for ????
        /* PO to set in response will have :
            the evidence (ER in xml or ASN1 ?) base64 encoded in binary data
            formatID set to urn:ietf:rfc:6283:EvidenceRecord (XML ER) or urn:ietf:rfc:4998:EvidenceRecord (ASN1) (ADAPT this if we support other things than digestLists)
            MimeType set to null (for digest lists at least)
            PronomId ??
            ID we receive from db (that we received from client in preservePO call)
            RelatedObjects we receive from db (that we received from client in preservePO call)
         */
        UUID poid = UUID.fromString(request.getPoId());

        List<EvidenceRecordDto> result = service.getERFromPOID(poid);
        //call converter with poid as arg
        EvidenceRecordType evidenceRecordType = converterService.toEvidenceRecordType(result, poid);

        ObjectFactory objectFactory = new ObjectFactory();
        JAXBElement<EvidenceRecordType> er = objectFactory.createEvidenceRecord(evidenceRecordType);
        String xmlString = null;

        try {
            JAXBContext context = JAXBContext.newInstance("be.uclouvain.lt.pres.ers.core.XMLObjects");
            Marshaller mar = context.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            StringWriter sw = new StringWriter();
            mar.marshal(er, sw);

            xmlString = sw.toString();
            System.out.println(xmlString);
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        //TODO change here
        //TODO handle all error codes
        /*
        StringBuilder stringBuilder = new StringBuilder("ER from DB for "+ poid.toString() +", size = "+ result.size() +" raw :\n");
        for (EvidenceRecordDto ert:result) {
            stringBuilder.append("\t");
            stringBuilder.append(ert);
            stringBuilder.append("\n");
        }
//        logger.info(stringBuilder.toString());
        System.out.print(stringBuilder.toString());
         */
        //TODO encode xmlValue here later
        // TODO : handle ID and related objects
        return this.buildResponse(
                request.getReqId(), MajEnum.RESULTMAJOR_SUCCESS, null, null, xmlString, HttpStatus.OK
        );
    }

    private ResponseEntity<PresRetrievePOResponseType> buildResponse(final String reqId, final MajEnum maj,
            final MinEnum min, final String msg, final String b64XmlValue, final HttpStatus httpStatus) {
        final PresRetrievePOResponseType response = new PresRetrievePOResponseType();
        response.setReqId(reqId);

        //create POs
        List<PresPOType> pos = new ArrayList<>();
        PresPOType po = new PresPOType();
        PresPOTypeXmlData xmlData = new PresPOTypeXmlData();
        xmlData.setB64Content(b64XmlValue);
        po.setXmlData(xmlData);
        po.setFormatId("urn:ietf:rfc:6283:EvidenceRecord"); // TODO : leave it hardcoded here ?
        pos.add(po);
        response.setPo(pos);

        //content of ResponseEntity
        //content of PresRetrievePOResponseType
        //reqId, optOut, result, po (List<PresPOType>)
        //optional POID element
        //zero or more instances of PO element
        //content of PresPOType
        //...

        final DsbResultType result = new DsbResultType();
        result.setMaj(maj);
        result.setMin((min != null) ? min.getUri().toString() : null);
        result.setMsg((msg != null) ? new DsbInternationalStringType().value(msg).lang("EN") : null);
        response.setResult(result);

        return ResponseEntity.status(httpStatus).body(response);
    }
}
