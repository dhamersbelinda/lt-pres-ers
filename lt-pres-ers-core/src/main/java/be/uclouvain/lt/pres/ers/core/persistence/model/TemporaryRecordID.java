package be.uclouvain.lt.pres.ers.core.persistence.model;

import lombok.*;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class TemporaryRecordID implements Serializable {
    @Type(type="uuid-char")
    public UUID poid; // corresponds to the POID's key, /!\ FK to request/POID
    public Integer digNum;
}
