package be.uclouvain.lt.pres.ers.model;

public enum PreservationStorageModel {

    WITH_STORAGE("WithStorage"), WITH_TEMPORARY_STORAGE("WithTemporaryStorage"), WITHOUT_STORAGE("WithoutStorage");

    private String value;

    private PreservationStorageModel(final String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
