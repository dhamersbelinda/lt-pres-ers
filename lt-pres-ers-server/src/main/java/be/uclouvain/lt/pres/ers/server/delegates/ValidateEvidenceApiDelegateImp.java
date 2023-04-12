package be.uclouvain.lt.pres.ers.server.delegates;

import be.uclouvain.lt.pres.ers.core.scheduler.BuildTreeTask;
import be.uclouvain.lt.pres.ers.server.api.RetrievePOApiDelegate;
import be.uclouvain.lt.pres.ers.server.api.ValidateEvidenceApiDelegate;
import be.uclouvain.lt.pres.ers.server.model.PresValidateEvidenceResponseType;
import be.uclouvain.lt.pres.ers.server.model.PresValidateEvidenceType;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ValidateEvidenceApiDelegateImp  implements ValidateEvidenceApiDelegate {

    private final BuildTreeTask task;

    @Override
    public ResponseEntity<PresValidateEvidenceResponseType> validateEvidencePost(PresValidateEvidenceType presValidateEvidenceType) {

        task.scheduledTask();

        return ValidateEvidenceApiDelegate.super.validateEvidencePost(presValidateEvidenceType);
    }
}
