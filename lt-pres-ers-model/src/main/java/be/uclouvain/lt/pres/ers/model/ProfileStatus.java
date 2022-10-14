package be.uclouvain.lt.pres.ers.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ProfileStatus {

    ACTIVE("active"), INACTIVE("inactive"), ALL("all");

    @Getter
    private String standardizedValue;

    private static final Map<String, ProfileStatus> fromStandardizedValuesToInstances = Map.of("active", ACTIVE,
            "inactive", INACTIVE, "all", ALL);

    public static ProfileStatus fromStandardizedValue(final String standardizedValue) {
        if (fromStandardizedValuesToInstances.containsKey(standardizedValue)) {
            return fromStandardizedValuesToInstances.get(standardizedValue);
        } else {
            throw new IllegalArgumentException(
                    "No ProfileStatus enum constant with standardized value: " + standardizedValue);
        }
    }
}
