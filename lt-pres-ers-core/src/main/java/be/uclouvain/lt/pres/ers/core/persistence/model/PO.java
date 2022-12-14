package be.uclouvain.lt.pres.ers.core.persistence.model;

import lombok.*;

import javax.persistence.*;
import java.net.URI;
import java.util.Set;

@NamedEntityGraph(name = "po-entity-graph", attributeNodes = {
        @NamedAttributeNode(value = "digestList", subgraph = "digestList-subgraph") }, subgraphs = {
        @NamedSubgraph(name = "digestList-subgraph", attributeNodes = {
                @NamedAttributeNode(value = "digests", subgraph = "digest-subgraph")}),
        @NamedSubgraph(name = "digest-subgraph", attributeNodes = {
                @NamedAttributeNode("digest")}) }) //don't know of the last step is really necessary

@Entity
@Table(name = "PO")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class PO {

    @Id
    @Column(name = "ID")
    @Setter(value = AccessLevel.PRIVATE) // Id is managed by DB
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //we can use this as the POID for the moment, as long as we don't submit sets
    //the poid needs to be returned as a string at the end, so it needs to be converted on receipt

    @Column(name = "UNIQUE_IDENTIFIER", nullable = true, length = 2048)
    private URI uid;
    //nullable = true because it doesn't necessarily have an id when submitted

    // TODO how to represent xml ? does the length need to be adjusted ?
    @Column(name = "VALUE", nullable = false, length = 2048)
    private String value;

    //TODO has to be non-null in our implem
    @Column(name = "FORMAT_IDENTIFIER", nullable = true, length = 2048)
    private URI formatId;
    //TODO does this have to be joined with the Format type ?


    @OneToOne
    @JoinColumn(name = "DIGESTLIST_ID", nullable = false, referencedColumnName = "ID")
    private DigestList digestList;

    //TODO fields for later
    /*
    @Column(name = "MIME_TYPE", nullable = true, length = 2048)
    private URI mimeType;

    @Column(name = "PRONOM_PUID", nullable = true, length = 2048)
    private URI pronomPUID;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "evidence", cascade = CascadeType.ALL)
    private Set<RelatedObject> relatedObjects;
     */

}
