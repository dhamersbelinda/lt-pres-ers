package be.uclouvain.lt.pres.ers.model;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

import lombok.Data;

@Data
public class ProfileDto {

    private URI profileIdentifier;
    private List<OperationDto> operations;
    private URI preservationEvidencePolicy;
    private OffsetDateTime validFrom;
    private OffsetDateTime validUntil;
    private PreservationStorageModel preservationStorageModel;
    private URI preservationGoal;
    private URI evidenceFormat;

    private URI schemeIdentifier;

}
