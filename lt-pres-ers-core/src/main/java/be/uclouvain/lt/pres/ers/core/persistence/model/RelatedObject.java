/*package be.uclouvain.lt.pres.ers.core.persistence.model;

import lombok.*;

import javax.persistence.*;
import java.net.URI;
import java.util.Set;

@Entity
@Table(name = "RELATED_OBJECT")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RelatedObject {

    @Id
    @Column(name = "ID")
    @Setter(value = AccessLevel.PRIVATE) // Id is managed by DB
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "RELATED_OBJECT_NAME", nullable = false, length = 128)
    private String relatedObject;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EVIDENCE_IDENTIFIER", nullable = false, referencedColumnName = "ID")
    private Evidence evidence;

}
*/