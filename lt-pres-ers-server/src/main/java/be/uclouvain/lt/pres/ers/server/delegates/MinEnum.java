package be.uclouvain.lt.pres.ers.server.delegates;

import java.net.URI;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum MinEnum {

    DELTA_POC_INTERNAL_PROBLEM(URI.create("http://uri.etsi.org/19512/error/DeltaPOCInternalProblem")),
    EXTERNAL_SERVICE_UNAVAILABLE(URI.create("http://uri.etsi.org/19512/error/externalServiceUnavailable")),
    INTERNAL_ERROR(URI.create("http://uri.etsi.org/19512/error/internalError")),
    LOW_SPACE(URI.create("http://uri.etsi.org/19512/warning/lowSpace")),
    NO_PERMISSION(URI.create("http://uri.etsi.org/19512/error/noPermission")),
    NO_SPACE_ERROR(URI.create("http://uri.etsi.org/19512/error/noSpaceError")),
    NOT_SUPPORTED(URI.create("http://uri.etsi.org/19512/error/notSupported")),
    PARAMETER_ERROR(URI.create("http://uri.etsi.org/19512/error/parameterError")),
    PO_FORMAT_ERROR(URI.create("http://uri.etsi.org/19512/error/POFormatError")),
    REQUEST_ONLY_PARTLY_SUCCESSFUL(URI.create("http://uri.etsi.org/19512/warning/requestOnlyPartlySuccessful")),
    TRANSFER_ERROR(URI.create("http://uri.etsi.org/19512/error/transferError")),
    UNKNOWN_DELTA_POC_TYPE(URI.create("http://uri.etsi.org/19512/error/unknownDeltaPOCType")),
    UNKNOWN_EVIDENCE_FORMAT(URI.create("http://uri.etsi.org/19512/error/unknownEvidenceFormat")),
    UNKNOWN_MODE(URI.create("http://uri.etsi.org/19512/error/unknownMode")),
    UNKNOWN_PO_FORMAT(URI.create("http://uri.etsi.org/19512/error/unknownPOFormat")),
    UNKNOWN_PO_ID(URI.create("http://uri.etsi.org/19512/error/unknownPOID")),
    UNKNOWN_VERSION_ID(URI.create("http://uri.etsi.org/19512/error/unknownVersionID"));

    @Getter
    private final URI uri;

}
