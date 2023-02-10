package be.uclouvain.lt.pres.ers.core.scheduler;

import be.uclouvain.lt.pres.ers.core.persistence.model.Digest;
import be.uclouvain.lt.pres.ers.core.persistence.model.POID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.xml.crypto.dsig.DigestMethod;
import java.net.URI;
import java.util.List;

//class that can be used to build a Merkle hash-tree

@Getter
@Setter
@ToString
@NoArgsConstructor
public class HashTreeBase {
    private Integer clientId;
    private URI digestMethod; //has become superfluous now, or maybe not ? correct type ?
    private List<POCompressed> poCompressedList;
}
