package be.uclouvain.lt.pres.ers.core.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class TemporaryRecordID implements Serializable {
    private static final long serialVersionUID = 5111464369421729023L;
    public UUID poid; // corresponds to the POID's key
    public Integer digNum;

}
