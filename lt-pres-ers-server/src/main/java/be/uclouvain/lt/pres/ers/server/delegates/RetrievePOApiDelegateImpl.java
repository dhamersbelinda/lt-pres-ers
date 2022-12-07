/*package be.uclouvain.lt.pres.ers.server.delegates;

import be.uclouvain.lt.pres.ers.core.exception.ProfileNotFoundException;
import be.uclouvain.lt.pres.ers.core.service.ProfileService;
import be.uclouvain.lt.pres.ers.model.ProfileDto;
import be.uclouvain.lt.pres.ers.model.ProfileStatus;
import be.uclouvain.lt.pres.ers.server.api.RetrieveInfoApiDelegate;
import be.uclouvain.lt.pres.ers.server.mapper.ProfileDtoMapper;
import be.uclouvain.lt.pres.ers.server.model.*;
import be.uclouvain.lt.pres.ers.server.model.DsbResultType.MajEnum;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class RetrievePOApiDelegateImpl implements RetrieveInfoApiDelegate {

    private final ProfileService service;

    private final ProfileDtoMapper mapper;

    @Override
    public ResponseEntity<PresRetrieveInfoResponseType> retrieveInfoPost(final PresRetrieveInfoType request) {
        // Validate inputs
        final ProfileStatus status;
        try {
            status = (request.getStat() == null) ? ProfileStatus.ACTIVE
                    : ProfileStatus.fromStandardizedValue(request.getStat());
        } catch (final IllegalArgumentException e) {
            return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_REQUESTERERROR, MinEnum.PARAMETER_ERROR,
                    e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }

        final URI profileIdentifier;
        try {
            profileIdentifier = (request.getPro() == null) ? null : new URI(request.getPro());
        } catch (final URISyntaxException e) {
            return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_REQUESTERERROR, MinEnum.PARAMETER_ERROR,
                    request.getPro() + " is not a valid URI.", null, HttpStatus.BAD_REQUEST);
        }

        // If profile identifier is specified, fetch it and check its status
        if (profileIdentifier != null) {
            try {
                final ProfileDto profile = this.service.getProfile(profileIdentifier);
                switch (status) {
                case ALL:
                    return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_SUCCESS, null, null,
                            List.of(this.mapper.toPresProfileType(profile)), HttpStatus.OK);
                case ACTIVE:
                    if ((profile.getValidUntil() == null) || (profile.getValidUntil().isAfter(OffsetDateTime.now()))) {
                        return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_SUCCESS, null, null,
                                List.of(this.mapper.toPresProfileType(profile)), HttpStatus.OK);
                    } else {
                        return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_SUCCESS, null, null,
                                List.of(), HttpStatus.OK);
                    }
                case INACTIVE:
                    if ((profile.getValidUntil() != null) && (profile.getValidUntil().isBefore(OffsetDateTime.now()))) {
                        return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_SUCCESS, null, null,
                                List.of(this.mapper.toPresProfileType(profile)), HttpStatus.OK);
                    } else {
                        return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_SUCCESS, null, null,
                                List.of(), HttpStatus.OK);
                    }
                }
            } catch (final ProfileNotFoundException e) {
                return this.buildResponse(request.getReqId(), MajEnum.RESULTMAJOR_REQUESTERERROR,
                        MinEnum.PARAMETER_ERROR, e.getMessage(), null, HttpStatus.BAD_REQUEST);
            }
        }

        // Profile identifier is not specified, fetch profiles using the status which is
        // always not null
        return this
                .buildResponse(
                        request.getReqId(), MajEnum.RESULTMAJOR_SUCCESS, null, null, this.service.getProfiles(status)
                                .stream().map(this.mapper::toPresProfileType).collect(Collectors.toList()),
                        HttpStatus.OK);
    }

    private ResponseEntity<PresRetrieveInfoResponseType> buildResponse(final String reqId, final MajEnum maj,
            final MinEnum min, final String msg, final List<PresProfileType> profiles, final HttpStatus httpStatus) {
        final PresRetrieveInfoResponseType response = new PresRetrieveInfoResponseType();
        response.setReqId(reqId);

        final DsbResultType result = new DsbResultType();
        result.setMaj(maj);
        result.setMin((min != null) ? min.getUri().toString() : null);
        result.setMsg((msg != null) ? new DsbInternationalStringType().value(msg).lang("EN") : null);
        response.setResult(result);

        response.setPro(profiles);

        return ResponseEntity.status(httpStatus).body(response);
    }
}
*/