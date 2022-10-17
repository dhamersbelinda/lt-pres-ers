package be.uclouvain.lt.pres.ers.model;

import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.Period;
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
    private URI specification;
    private Period preservationEvidenceRetentionPeriod;
    private Duration preservationEvidenceRetentionDuration;
}
