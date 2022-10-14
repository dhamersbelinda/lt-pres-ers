package be.uclouvain.lt.pres.ers.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PreservationStorageModel {

    WITH_STORAGE("WithStorage"), WITH_TEMPORARY_STORAGE("WithTemporaryStorage"), WITHOUT_STORAGE("WithoutStorage");

    @Getter
    private final String standardizedValue;

    private static final Map<String, PreservationStorageModel> fromStandardizedValuesToInstances = Map.of("WithStorage",
            WITH_STORAGE, "WithTemporaryStorage", WITH_TEMPORARY_STORAGE, "WithoutStorage", WITHOUT_STORAGE);

    public static PreservationStorageModel fromStandardizedValue(final String standardizedValue) {
        if (fromStandardizedValuesToInstances.containsKey(standardizedValue)) {
            return fromStandardizedValuesToInstances.get(standardizedValue);
        } else {
            throw new IllegalArgumentException(
                    "No PreservationStorageModel enum constant with standardized value: " + standardizedValue);
        }
    }
}
