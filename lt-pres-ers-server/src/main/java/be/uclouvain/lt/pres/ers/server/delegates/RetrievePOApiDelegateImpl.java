package be.uclouvain.lt.pres.ers.server.delegates;

import be.uclouvain.lt.pres.ers.core.XMLObjects.EvidenceRecordType;
import be.uclouvain.lt.pres.ers.core.XMLObjects.ObjectFactory;
import be.uclouvain.lt.pres.ers.core.XMLObjects.ObjectFactory;
import be.uclouvain.lt.pres.ers.core.exception.PONotFoundException;
import be.uclouvain.lt.pres.ers.core.persistence.model.POID;
import be.uclouvain.lt.pres.ers.core.service.impl.EvidenceRecordDTOToEvidenceRecordType;
import be.uclouvain.lt.pres.ers.core.persistence.model.dto.EvidenceRecordDto;
import be.uclouvain.lt.pres.ers.core.service.POService;
import be.uclouvain.lt.pres.ers.model.SubjectOfRetrieval;
import be.uclouvain.lt.pres.ers.server.api.RetrievePOApiDelegate;
import be.uclouvain.lt.pres.ers.server.mapper.ProfileDtoMapper;
import be.uclouvain.lt.pres.ers.server.model.*;
import be.uclouvain.lt.pres.ers.server.model.DsbResultType.MajEnum;
import eu.europa.esig.dss.xades.reference.CanonicalizationTransform;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.dom.DOMResult;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.*;

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

        if(request.getSor() != null && (!Objects.equals(request.getSor(), SubjectOfRetrieval.EVIDENCE.getStandardizedValue()) && !Objects.equals(request.getSor(), SubjectOfRetrieval.PO_WITH_EMBEDDED_EVIDENCE.getStandardizedValue()))) {
            return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_REQUESTERERROR, MinEnum.PARAMETER_ERROR,
                    "Invalid or unsupported field 'sor': '"+request.getSor()+"'", null, HttpStatus.BAD_REQUEST);
        }

        UUID poidUUID;
        try{
            poidUUID = UUID.fromString(request.getPoId());
        } catch(IllegalArgumentException e) {
            return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_REQUESTERERROR, MinEnum.PARAMETER_ERROR,
                    "Invalid POID", null, HttpStatus.BAD_REQUEST);
        }

        List<EvidenceRecordDto> result;
        try {
            result = service.getERFromPOID(poidUUID);
        }catch (PONotFoundException e) {
            return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_REQUESTERERROR, MinEnum.PARAMETER_ERROR,
                    "POID not found : '"+request.getPoId()+"'", null, HttpStatus.BAD_REQUEST);
        }
        if(result==null || result.isEmpty()) {
            return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_SUCCESS, MinEnum.REQUEST_ONLY_PARTLY_SUCCESSFUL,
                    "POID's evidence not yet generated", null, HttpStatus.OK);
        }

        //call converter with poid as arg
        EvidenceRecordType evidenceRecordType = converterService.toEvidenceRecordType(result, poidUUID);

        if(evidenceRecordType == null) {
            return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_REQUESTERERROR, MinEnum.PARAMETER_ERROR,
                    "POID not found : '"+request.getPoId()+"'", null, HttpStatus.BAD_REQUEST);
        }

        ObjectFactory objectFactory = new ObjectFactory();
        JAXBElement<EvidenceRecordType> er = objectFactory.createEvidenceRecord(evidenceRecordType);
//        String xmlString = null;
        byte[] canonicalized = null;
        try {
            JAXBContext context = JAXBContext.newInstance("be.uclouvain.lt.pres.ers.core.XMLObjects");
            Marshaller mar = context.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            DOMResult res = new DOMResult();
            mar.marshal(er, res);

            Document doc = (Document) res.getNode();
            CanonicalizationTransform transform = new CanonicalizationTransform("http://www.w3.org/2006/12/xml-c14n11");
            canonicalized = transform.getBytesAfterTransformation(doc);
            System.out.println(new String(canonicalized));
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        //TODO change here
        //TODO handle all error codes
        //TODO encode xmlValue here later
        if(canonicalized == null) {
            return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_REQUESTERERROR, MinEnum.PARAMETER_ERROR,
                    "POID not found : '"+request.getPoId()+"'", null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String ret = Base64.getEncoder().encodeToString(canonicalized);

        // TODO : handle ID and related objects
        return this.buildResponse(
                request.getReqId(), MajEnum.RESULTMAJOR_SUCCESS, null, null, ret, HttpStatus.OK
        );
    }

    private ResponseEntity<PresRetrievePOResponseType> buildResponse(final String reqId, final MajEnum maj,
                                                                     final MinEnum min, final String msg, final String b64XmlValue, final HttpStatus httpStatus) {
        final PresRetrievePOResponseType response = new PresRetrievePOResponseType();
        response.setReqId(reqId);


        if(b64XmlValue != null) {
            //create POs
            List<PresPOType> pos = new ArrayList<>();
            PresPOType po = new PresPOType();
            PresPOTypeXmlData xmlData = new PresPOTypeXmlData();
            xmlData.setB64Content(b64XmlValue);
            po.setXmlData(xmlData);
            po.setFormatId("urn:ietf:rfc:6283:EvidenceRecord"); // TODO : leave it hardcoded here ?
            pos.add(po);
            response.setPo(pos);
        }


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
