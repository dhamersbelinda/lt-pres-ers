package be.uclouvain.lt.pres.ers.core.persistence.model;

import lombok.*;

import javax.persistence.*;
import java.net.URI;

@NamedEntityGraph(name = "tempRecord-entity-graph",
    attributeNodes = {
        @NamedAttributeNode(value = "poid"),
        @NamedAttributeNode(value = "digNum"),
//        @NamedAttributeNode(value = "digestList", subgraph = "digestList-subgraph"),
        @NamedAttributeNode(value = "digestMethod"),
        @NamedAttributeNode(value ="digest"),
        @NamedAttributeNode(value = "clientId")
    }
//    ,
//    subgraphs = {
//        @NamedSubgraph(name = "digestList-subgraph", attributeNodes = {
//                @NamedAttributeNode(value = "digestMethod")
//        })
//    }

)

@Entity
@Table(name = "TEMPORARY_RECORDS")
@IdClass(TemporaryRecordID.class)
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TemporaryRecord {
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "POID", referencedColumnName = "POID", nullable = false, foreignKey = @ForeignKey(name = "FK_POID"))
//    @JoinColumns({
//            @JoinColumn(name = "POID", referencedColumnName = "POID", nullable = false, foreignKey = @ForeignKey(name = "FK_POID")),
//            @JoinColumn(name = "CLIENT_ID", nullable = false, referencedColumnName = "CLIENT_ID") //TODO put foreign key (if link should be made later)
//    })
    private POID poid;

    @Id
    @Column(name = "DIG_NUM")
    private Integer digNum;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "DIG_METHOD", nullable = false, referencedColumnName = "DIGEST_METHOD")
//    private DigestList digestList;

    @Column(name = "DIG_METHOD", nullable = false)
    private URI digestMethod;

//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "VALUE", nullable = false, referencedColumnName = "DIGEST_VALUE")
//    private Digest digest;

    @Column(name = "VALUE")
    private byte[] digest;

    //TODO make foreign key out of this
    @Column(name = "CLIENT_ID", nullable = false)
    private Long clientId;

}



