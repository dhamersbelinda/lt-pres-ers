package be.uclouvain.lt.pres.ers.core.scheduler;

import be.uclouvain.lt.pres.ers.core.persistence.model.Digest;
import be.uclouvain.lt.pres.ers.core.persistence.model.POID;
import be.uclouvain.lt.pres.ers.core.persistence.model.TreeID;
import be.uclouvain.lt.pres.ers.core.persistence.model.Treeable;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import lombok.*;

import javax.xml.crypto.dsig.DigestMethod;
import java.net.URI;
import java.util.List;

//class that can be used to build a Merkle hash-tree

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HashTreeBase {
    private TreeID treeID;
    private Long clientId;
    private DigestAlgorithm digestMethod; //has become superfluous now, or maybe not ? correct type ?
//    private List<POCompressed> poCompressedList;
    private List<Treeable> leaves;
}
