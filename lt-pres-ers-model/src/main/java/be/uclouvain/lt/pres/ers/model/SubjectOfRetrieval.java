package be.uclouvain.lt.pres.ers.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
public enum SubjectOfRetrieval {

    PO("PO"), EVIDENCE("Evidence"), PO_WITH_EMBEDDED_EVIDENCE("POWithEmbeddedEvidence"), PO_WITH_DETACHED_EVIDENCE("POWithDetachedEvidence");

    @Getter
    private final String standardizedValue;

    private static final Map<String, SubjectOfRetrieval> fromStandardizedValuesToInstances = Map.of("PO",
            PO, "Evidence", EVIDENCE, "POWithEmbeddedEvidence", PO_WITH_EMBEDDED_EVIDENCE, "POWithDetachedEvidence", PO_WITH_EMBEDDED_EVIDENCE);

    public static SubjectOfRetrieval fromStandardizedValue(final String standardizedValue) {
        if (fromStandardizedValuesToInstances.containsKey(standardizedValue)) {
            return fromStandardizedValuesToInstances.get(standardizedValue);
        } else {
            throw new IllegalArgumentException(
                    "No SubjectOfRetrieval enum constant with standardized value: " + standardizedValue);
        }
    }
}
