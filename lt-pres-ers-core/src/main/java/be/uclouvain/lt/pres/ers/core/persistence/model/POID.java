package be.uclouvain.lt.pres.ers.core.persistence.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@NamedEntityGraph(name = "request-entity-graph", attributeNodes = {
        @NamedAttributeNode(value = "clientId"),
        @NamedAttributeNode(value = "po", subgraph = "po-subgraph"),
        @NamedAttributeNode(value = "node", subgraph = "node-subgraph")
        },

        subgraphs = {
        @NamedSubgraph(name = "po-subgraph", attributeNodes = {
                @NamedAttributeNode(value = "digestList", subgraph = "digestList-subgraph") }),
        @NamedSubgraph(name = "digestList-subgraph", attributeNodes = {
                @NamedAttributeNode(value = "digests", subgraph = "digest-subgraph")}),
        @NamedSubgraph(name = "digest-subgraph" , attributeNodes = {
                @NamedAttributeNode(value = "digest")}),


        @NamedSubgraph(name = "node-subgraph", attributeNodes = {
               @NamedAttributeNode(value = "parent"),
               @NamedAttributeNode(value = "neighbour"),
                @NamedAttributeNode(value = "treeId"),
                @NamedAttributeNode(value = "inTreeId")
        })

        }
)

@Entity
@Table(name = "POIDs")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class POID {
    @Id
    @Column(name = "POID", nullable = false, updatable = false)
    @Setter(value = AccessLevel.PRIVATE) // Id is managed by DB
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Type(type="uuid-char")
    private UUID id; //-> will become the POID

    //TODO faire gaffe que rien ne s'insère ici et que ça sert juste à avoir la FK
    //@Transient
    //@Detached
    @JoinColumn(name = "PROFILE_ID", nullable = false, referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false) //removed cascadetype , cascade = CascadeType.DETACH
    private Profile profile;

    @Column(name = "CLIENT_ID", nullable = false, length = 2048)
    private Integer clientId;

    @OneToOne(fetch = FetchType.LAZY, cascade=CascadeType.ALL, optional = false, mappedBy = "req")
    //@JoinColumn
    private PO po;


    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "NODE_ID", referencedColumnName = "NODE_ID", foreignKey = @ForeignKey(name = "fk_node_id"))
    private Node node;
}
