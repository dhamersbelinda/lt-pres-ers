package be.uclouvain.lt.pres.ers.core.persistence.model.evidence;

import be.uclouvain.lt.pres.ers.core.persistence.model.POID;
import lombok.*;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ADOGroupID implements Serializable {
    //@Type(type="uuid-char") necessary ?
    //if we also change TemporaryRecordID we can use the same ID for both (#reusable, #spaghetti)
    public POID poid; // corresponds to the POID's key, /!\ FK to request/POID
    public Integer digNum;
}