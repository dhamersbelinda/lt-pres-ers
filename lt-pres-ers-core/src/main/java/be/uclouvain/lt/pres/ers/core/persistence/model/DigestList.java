package be.uclouvain.lt.pres.ers.core.persistence.model;

import lombok.*;

import javax.persistence.*;
import java.net.URI;
import java.util.Set;

@Entity
@Table(name = "DIGESTLIST")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class DigestList {

    @Id
    @Column(name = "ID")
    @Setter(value = AccessLevel.PRIVATE) // Id is managed by DB
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "DIGEST_METHOD", nullable = false, length = 128)
    private URI digestMethod;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "digestList", cascade = CascadeType.ALL)
    private Set<Digest> digests;

    @OneToOne(fetch = FetchType.LAZY, optional = false) //TODO set the optional here later (false if possible)
    //@OneToOne //check if this does what it should
    @JoinColumn(name = "PO_ID", nullable = false, referencedColumnName = "ID")
    //how to make po_id the same value as id of po ?
    private PO po;
}
