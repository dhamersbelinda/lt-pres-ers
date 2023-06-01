package be.uclouvain.lt.pres.ers.server.delegates;

import be.uclouvain.lt.pres.ers.core.exception.ProfileNotFoundException;
import be.uclouvain.lt.pres.ers.core.service.POService;
import be.uclouvain.lt.pres.ers.core.service.ProfileService;
import be.uclouvain.lt.pres.ers.model.PODto;
import be.uclouvain.lt.pres.ers.model.PreservePORequestDto;
import be.uclouvain.lt.pres.ers.model.ProfileDto;
import be.uclouvain.lt.pres.ers.server.api.PreservePOApiDelegate;
import be.uclouvain.lt.pres.ers.server.mapper.PresPOToPODtoMapper;
import be.uclouvain.lt.pres.ers.server.model.*;
import be.uclouvain.lt.pres.ers.server.model.DsbResultType.MajEnum;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// TODO is this ASYNC ? check for all API methods ...
@Component
@AllArgsConstructor
public class PreservePOApiDelegateImpl implements PreservePOApiDelegate {
    // TODO implement service in core
    private final ProfileService profileService;
    private final POService poService;
    // TODO maybe we need another mapper, but as we should only return a POID maybe not ...
    //we'll have to map both ways so if you need one both will be there
//    private final ProfileDtoMapper mapper;
    private final PresPOToPODtoMapper mapperPOType;

    @Override
    public ResponseEntity<PresPreservePOResponseType> preservePOPost(final PresPreservePOType request) {
        long start = System.nanoTime();
        // Validate inputs
        // TODO : optIn ?
        // TODO reqID ?
        // pro : verify profile is supported
        // TODO : Adapt client id with the JWT when it is done
        Integer clientId = 0;

        // We only support a single PO, for document groups send multiple digests in the digestList
        if(request.getPo() == null) {
            return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_REQUESTERERROR, MinEnum.PARAMETER_ERROR,
                    "Missing PO", HttpStatus.BAD_REQUEST);
        } else if(request.getPo().size() != 1) {
            return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_REQUESTERERROR,
                    MinEnum.PARAMETER_ERROR, "Exactly one PO per preservePO request", HttpStatus.BAD_REQUEST);
        }

        if(request.getPro() == null) {
            return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_REQUESTERERROR, MinEnum.PARAMETER_ERROR,
                    "Missing profile", HttpStatus.BAD_REQUEST);
        }

        // We do not support optional inputs
        if(request.getOptIn() != null){
            return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_REQUESTERERROR, MinEnum.NOT_SUPPORTED, "Optional inputs (optIn) are not supported", HttpStatus.BAD_REQUEST);
//            if(request.getOptIn().getPolicy() != null && request.getOptIn().getPolicy().size() != 0) {
//                return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_REQUESTERERROR, MinEnum.NOT_SUPPORTED, "Policies in optIn are not supported", HttpStatus.BAD_REQUEST);
//            }
//            if(request.getOptIn().getOther() != null && request.getOptIn().getOther().size() != 0) {
//                return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_REQUESTERERROR, MinEnum.NOT_SUPPORTED, "Other in optIn are not supported", HttpStatus.BAD_REQUEST);
//            }
        }


        final URI profileIdentifier; // TODO adapt everything according to the profile
        try {
            profileIdentifier = new URI(request.getPro());
        } catch (final URISyntaxException e) {
            return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_REQUESTERERROR, MinEnum.PARAMETER_ERROR,
                    "Profile:'" + request.getPro() + "' is not a valid URI.", HttpStatus.BAD_REQUEST);
        }

        final ProfileDto profileDto;
        try {
            profileDto = this.profileService.getProfile(profileIdentifier);
        } catch (final ProfileNotFoundException e) {
            return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_REQUESTERERROR, MinEnum.PARAMETER_ERROR, "Unknown profile :"+profileIdentifier, HttpStatus.NOT_FOUND);
        }

        // po : verify preservation object(s)
        List<PresPOType> pos = request.getPo();

        URI formatID;
        List<PODto> poDtos = new ArrayList<>(pos.size()); // TODO : will be sent to core service
        PODto temp;
        int idx = 1;
        for (PresPOType po : pos) {
            try {
                formatID = (po.getFormatId() == null) ? null : URI.create(po.getFormatId());
                if(! SubDOFormatID.DigestList.getUri().equals(formatID)){
                    return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_REQUESTERERROR, MinEnum.UNKNOWN_PO_FORMAT,
                            "Unsupported format ID: "+po.getFormatId(), HttpStatus.BAD_REQUEST);
                }
                if(pos.size() > 1) {
                    return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_REQUESTERERROR, MinEnum.PARAMETER_ERROR,
                            "Only a single PO is supported for digestLists format.", HttpStatus.BAD_REQUEST);
                }

                temp = mapperPOType.toPODto(po);

                poDtos.add(temp);
                idx++;
            } catch (URISyntaxException e) {
                return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_REQUESTERERROR, MinEnum.PARAMETER_ERROR,
                        po.getFormatId() + " is not a valid URI.", HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
                e.printStackTrace();
                return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_REQUESTERERROR, MinEnum.PARAMETER_ERROR,
                        "Error verifying PO "+ idx + " : "+e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        // TODO : adapt for other formats than digestLists
        String digestMethod = poDtos.get(0).getDigestList().getDigestMethod().getOid();
        PreservePORequestDto requestDto = new PreservePORequestDto(poDtos, profileDto, clientId, digestMethod);

        long beforeInsert = System.nanoTime();
        UUID poid = this.poService.insertPOs(requestDto);

        long end = System.nanoTime();

        // total, insert
        return this
                .buildResponse(
                        "%d %d".formatted(end-start, end-beforeInsert), poid, MajEnum.RESULTMAJOR_SUCCESS, null, "Success !",
                        HttpStatus.OK);
    }

    private ResponseEntity<PresPreservePOResponseType> buildResponse(final String reqId, UUID poid, final MajEnum maj,
                                                                     final MinEnum min, final String msg, final HttpStatus httpStatus) {
        final PresPreservePOResponseType response = new PresPreservePOResponseType();
        response.setReqId(reqId);
        if(poid != null)
            response.setPoId(poid.toString());

        final DsbResultType result = new DsbResultType();
        result.setMaj(maj);
        result.setMin((min != null) ? min.getUri().toString() : null);
        result.setMsg((msg != null) ? new DsbInternationalStringType().value(msg).lang("EN") : null);
        response.setResult(result);

        return ResponseEntity.status(httpStatus).body(response);
    }
    private ResponseEntity<PresPreservePOResponseType> buildResponse(final String reqId, final MajEnum maj,
                                                                     final MinEnum min, final String msg, final HttpStatus httpStatus) {
        return buildResponse(reqId, null, maj, min, msg, httpStatus);
    }
}