package be.uclouvain.lt.pres.ers.core.persistence.model;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

public interface Treeable {
    public boolean isRoot();

    public byte[] getHashValue();
}

