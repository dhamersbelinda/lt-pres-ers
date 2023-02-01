package be.uclouvain.lt.pres.ers.core.persistence.model;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class TemporaryRecordID implements Serializable {
    public UUID poid; // corresponds to the POID's key, /!\ FK to request
    public Integer digNum;
}
