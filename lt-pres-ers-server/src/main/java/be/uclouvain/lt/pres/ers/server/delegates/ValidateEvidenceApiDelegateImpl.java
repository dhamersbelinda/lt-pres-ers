package be.uclouvain.lt.pres.ers.server.delegates;

import be.uclouvain.lt.pres.ers.core.scheduler.BuildTreeTask;
import be.uclouvain.lt.pres.ers.server.api.ValidateEvidenceApiDelegate;
import be.uclouvain.lt.pres.ers.server.model.PresValidateEvidenceResponseType;
import be.uclouvain.lt.pres.ers.server.model.PresValidateEvidenceType;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ValidateEvidenceApiDelegateImpl implements ValidateEvidenceApiDelegate {
    private final Logger logger = LoggerFactory.getLogger(ValidateEvidenceApiDelegateImpl.class);
    private final BuildTreeTask task;

    @Override
    public ResponseEntity<PresValidateEvidenceResponseType> validateEvidencePost(PresValidateEvidenceType presValidateEvidenceType) {

        logger.info(String.format("Called validate with reqId=%s",presValidateEvidenceType.getReqId()));
        if(presValidateEvidenceType.getReqId() != null) {
            switch (presValidateEvidenceType.getReqId()) {
                case "0" -> task.task(BuildTreeTask.NEW_POIDS_ONLY);
                case "1" -> task.task(BuildTreeTask.RENEWALS_ONLY);
                case "2" -> task.task(BuildTreeTask.NEW_POIDS_AND_RENEWALS);
                default -> task.scheduledTask();
            }
        } else {
            task.scheduledTask();
        }

        return ValidateEvidenceApiDelegate.super.validateEvidencePost(presValidateEvidenceType);
    }
}
