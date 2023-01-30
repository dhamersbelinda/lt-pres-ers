package be.uclouvain.lt.pres.ers.core.persistence.model;

import javax.persistence.Embeddable;
import java.util.UUID;

@Embeddable
public class TemporaryRecordID {
    public PreservePORequest poid; // corresponds to the POID's key
}
