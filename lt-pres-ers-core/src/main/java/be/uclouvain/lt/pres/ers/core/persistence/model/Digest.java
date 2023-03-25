package be.uclouvain.lt.pres.ers.core.persistence.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.net.URI;
import java.util.Set;

@Entity
@Table(name = "DIGEST")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Digest implements Serializable {

    @Id
    @Column(name = "ID")
    @Setter(value = AccessLevel.PRIVATE) // Id is managed by DB
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "DIGEST_VALUE", nullable = false, length = 128)
    private byte[] digest;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) //what about this ???
    @JoinColumn(name = "DIGESTLIST_ID", nullable = false, referencedColumnName = "ID")
    private DigestList digestList;

}
