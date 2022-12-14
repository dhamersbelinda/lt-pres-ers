package be.uclouvain.lt.pres.ers.server.delegates;

import be.uclouvain.lt.pres.ers.core.exception.ProfileNotFoundException;
import be.uclouvain.lt.pres.ers.core.service.ProfileService;
import be.uclouvain.lt.pres.ers.model.PODto;
import be.uclouvain.lt.pres.ers.model.ProfileDto;
import be.uclouvain.lt.pres.ers.server.api.PreservePOApiDelegate;
import be.uclouvain.lt.pres.ers.server.mapper.PresPOTypeMapper;
import be.uclouvain.lt.pres.ers.server.model.*;
import be.uclouvain.lt.pres.ers.server.model.DsbResultType.MajEnum;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

// TODO is this ASYNC ? check for all API methods ...
@Component
@AllArgsConstructor
public class PreservePOApiDelegateImpl implements PreservePOApiDelegate {
    // TODO implement service in core
    private final ProfileService profileService;
    // TODO maybe we need another mapper, but as we should only return a POID maybe not ...
    //we'll have to map both ways so if you need one both will be there
//    private final ProfileDtoMapper mapper;
    private final PresPOTypeMapper mapperPOType;

    @Override
    public ResponseEntity<PresPreservePOResponseType> preservePOPost(final PresPreservePOType request) {
        // Validate inputs
        // TODO : optIn ?
        // TODO reqID ?
        // pro : verify profile is supported
        final URI profileIdentifier; // TODO adapt everything according to the profile
        try {
            profileIdentifier = (request.getPro() == null) ? null : new URI(request.getPro());
        } catch (final URISyntaxException e) {
            return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_REQUESTERERROR, MinEnum.PARAMETER_ERROR,
                    request.getPro() + " is not a valid URI.", HttpStatus.BAD_REQUEST);
        }

        try {
            final ProfileDto profile = this.profileService.getProfile(profileIdentifier);
        } catch (final ProfileNotFoundException e) {
            return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_REQUESTERERROR,
                    MinEnum.PARAMETER_ERROR, e.getMessage(), HttpStatus.BAD_REQUEST);
        }


        // po : verify preservation object(s)

        List<PresPOType> pos = request.getPo();
        if(pos == null) {
            return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_REQUESTERERROR, MinEnum.PARAMETER_ERROR,
                    "Missing po", HttpStatus.BAD_REQUEST);
        }

        URI formatID;
        List<PODto> poDtos = new ArrayList<>(pos.size()); // TODO : will be sent to core service
        int idx = 1;
        for (PresPOType po : pos) {
            try {
                formatID = (po.getFormatId() == null) ? null : URI.create(po.getFormatId());
                if(! SubDOFormatID.DigestList.getUri().equals(formatID)){
                    return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_REQUESTERERROR, MinEnum.PARAMETER_ERROR,
                            "Unsupported format ID: "+po.getFormatId(), HttpStatus.BAD_REQUEST);
                }

                poDtos.add(mapperPOType.toPODto(po));
                idx++;
            } catch (URISyntaxException e) {
                return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_REQUESTERERROR, MinEnum.PARAMETER_ERROR,
                        po.getFormatId() + " is not a valid URI.", HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
                return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_REQUESTERERROR, MinEnum.PARAMETER_ERROR,
                        "Error verifying PO "+ idx + " : "+e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }

        // Profile identifier is not specified, fetch profiles using the status which is
        // always not null
        return this
                .buildResponse(
                        request.getReqId(), MajEnum.RESULTMAJOR_SUCCESS, null, "Success !",
                        HttpStatus.OK);
    }

    private ResponseEntity<PresPreservePOResponseType> buildResponse(final String reqId, final MajEnum maj,
                                                                     final MinEnum min, final String msg, final HttpStatus httpStatus) {
        final PresPreservePOResponseType response = new PresPreservePOResponseType();
        response.setReqId(reqId);

        final DsbResultType result = new DsbResultType();
        result.setMaj(maj);
        result.setMin((min != null) ? min.getUri().toString() : null);
        result.setMsg((msg != null) ? new DsbInternationalStringType().value(msg).lang("EN") : null);
        response.setResult(result);

        return ResponseEntity.status(httpStatus).body(response);
    }
}