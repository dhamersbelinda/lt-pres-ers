package be.uclouvain.lt.pres.ers.server.delegates;

import be.uclouvain.lt.pres.ers.core.exception.ProfileNotFoundException;
import be.uclouvain.lt.pres.ers.core.persistence.model.dto.EvidenceRecordDto;
import be.uclouvain.lt.pres.ers.core.scheduler.BuildTreeTask;
import be.uclouvain.lt.pres.ers.core.service.POService;
import be.uclouvain.lt.pres.ers.core.service.ProfileService;
import be.uclouvain.lt.pres.ers.model.ProfileDto;
import be.uclouvain.lt.pres.ers.model.ProfileStatus;
import be.uclouvain.lt.pres.ers.server.api.RetrieveInfoApiDelegate;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class RetrievePOApiDelegateImpl implements RetrievePOApiDelegate {
    private final Logger logger = LoggerFactory.getLogger(RetrievePOApiDelegate.class);
    private final POService service;

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

        StringBuilder stringBuilder = new StringBuilder("ER from DB for "+ poid.toString() +", size = "+ result.size() +" raw :\n");
        for (EvidenceRecordDto er:result) {
            stringBuilder.append("\t");
            stringBuilder.append(er);
            stringBuilder.append("\n");
        }
//        logger.info(stringBuilder.toString());
        System.out.print(stringBuilder.toString());

        return null;
    }

    private ResponseEntity<PresRetrievePOResponseType> buildResponse(final String reqId, final MajEnum maj,
            final MinEnum min, final String msg, final List<PresProfileType> profiles, final HttpStatus httpStatus) {
//        final PresRetrieveInfoResponseType response = new PresRetrieveInfoResponseType();
//        response.setReqId(reqId);
//
//        final DsbResultType result = new DsbResultType();
//        result.setMaj(maj);
//        result.setMin((min != null) ? min.getUri().toString() : null);
//        result.setMsg((msg != null) ? new DsbInternationalStringType().value(msg).lang("EN") : null);
//        response.setResult(result);
//
//        response.setPro(profiles);
//
//        return ResponseEntity.status(httpStatus).body(response);
        return null;
    }
}
