package be.uclouvain.lt.pres.ers.server.delegates;

import be.uclouvain.lt.pres.ers.core.scheduler.BuildTreeTask;
import be.uclouvain.lt.pres.ers.server.api.ValidateEvidenceApiDelegate;
import be.uclouvain.lt.pres.ers.server.model.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ValidateEvidenceApiDelegateImpl implements ValidateEvidenceApiDelegate {
    private final Logger logger = LoggerFactory.getLogger(ValidateEvidenceApiDelegateImpl.class);
    private final BuildTreeTask task;

    @Override
    public ResponseEntity<PresValidateEvidenceResponseType> validateEvidencePost(PresValidateEvidenceType presValidateEvidenceType) {

        logger.info(String.format("Called validate with reqId=%s",presValidateEvidenceType.getReqId()));
        if(presValidateEvidenceType.getReqId() != null) {
            switch (presValidateEvidenceType.getReqId().charAt(0)) {
                case '0' -> task.task(BuildTreeTask.NEW_POIDS_ONLY, BuildTreeTask.BRANCHING_FACTOR, BuildTreeTask.MAX_LEAVES);
                case '1' -> task.task(BuildTreeTask.RENEWALS_ONLY, BuildTreeTask.BRANCHING_FACTOR, BuildTreeTask.MAX_LEAVES);
                case '2' -> task.task(BuildTreeTask.NEW_POIDS_AND_RENEWALS, BuildTreeTask.BRANCHING_FACTOR, BuildTreeTask.MAX_LEAVES);
                case '4' -> {
                    String times = this.buildTreeExperiment(presValidateEvidenceType.getReqId());
                    if(times == null) {
                        return buildResponse(null, DsbResultType.MajEnum.RESULTMAJOR_RESPONDERERROR, null, null,HttpStatus.INTERNAL_SERVER_ERROR);
                    } else {
                        return buildResponse(times, DsbResultType.MajEnum.RESULTMAJOR_REQUESTERERROR, null, null,HttpStatus.OK);
                    }

                }
                case'5' -> {
                    String[] params = presValidateEvidenceType.getReqId().split(" ");
                    if(params.length < 2) return null; // "4 L"
                    int L = Integer.parseInt(params[1]);
                    try {
                        task.insertRandomPOIDs(L);
                    } catch (URISyntaxException e) {
                        logger.warn("Failed to insert random POIDs : "+e.getMessage());
                        return null;
                    }
                }
                default -> {
                    return buildResponse(null, DsbResultType.MajEnum.RESULTMAJOR_REQUESTERERROR, null, "unknown reqId",HttpStatus.BAD_REQUEST);
                }
            }
        } else {
            task.scheduledTask();
        }

        return buildResponse(null, DsbResultType.MajEnum.RESULTMAJOR_REQUESTERERROR, null, null,HttpStatus.OK);
    }

    private String buildTreeExperiment(String paramsRaw) {
        String[] params = paramsRaw.split(" ");
        if(params.length < 4) return null; // "4 B L maxL"
        int B = Integer.parseInt(params[1]);
        int L = Integer.parseInt(params[2]);
        int maxL = Integer.parseInt(params[3]);

        if(B < 2 || L < 1 || maxL < 1) return null;

        try {
            task.insertRandomPOIDs(L);
        } catch (URISyntaxException e) {
            logger.warn("Failed to insert random POIDs : "+e.getMessage());
            return null;
        }

        // [total, fetching, build, ts, insert]
        long[] times = task.task(BuildTreeTask.NEW_POIDS_ONLY, B, maxL);
        return Long.toString(times[0]) + " " + Long.toString(times[1]) + " " +Long.toString(times[2]) + " " +Long.toString(times[3]) + " " +Long.toString(times[4]) ;
    }

    private ResponseEntity<PresValidateEvidenceResponseType> buildResponse(final String reqId, final DsbResultType.MajEnum maj, final MinEnum min, final String msg, final HttpStatus httpStatus) {
        final PresValidateEvidenceResponseType response = new PresValidateEvidenceResponseType();
        response.setReqId(reqId);

        final DsbResultType result = new DsbResultType();
        result.setMaj(maj);
        result.setMin((min != null) ? min.getUri().toString() : null);
        result.setMsg((msg != null) ? new DsbInternationalStringType().value(msg).lang("EN") : null);
        response.setResult(result);

        return ResponseEntity.status(httpStatus).body(response);
    }
}
