package be.uclouvain.lt.pres.ers.core.persistence.model;

import lombok.*;

import javax.persistence.*;
import java.net.URI;
import java.util.Set;

@NamedEntityGraph(name = "po-entity-graph", attributeNodes = {
        @NamedAttributeNode(value = "digestList", subgraph = "digestList-subgraph") }, subgraphs = {
        @NamedSubgraph(name = "digestList-subgraph", attributeNodes = {
                @NamedAttributeNode(value = "digests", subgraph = "digest-subgraph")}),
        @NamedSubgraph(name = "digest-subgraph" , attributeNodes = {
                @NamedAttributeNode(value = "digest")}) }) //don't know of the last step is really necessary

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

    //TODO has to be non-null in our implem
    @Column(name = "FORMAT_IDENTIFIER", nullable = false, length = 2048)
    private URI formatId;
    //TODO does this have to be joined with the Format type ?

    @Column(name = "UNIQUE_IDENTIFIER", nullable = true, length = 2048)
    private String uid;

    @Column(name = "MIMETYPE", nullable = true, length = 2048)
    private URI mimeType;

    @Column(name = "PRONOM_ID", nullable = true, length = 2048)
    private URI pronomId;

    /*
    // TODO how to represent xml ? does the length need to be adjusted ?
    @Column(name = "PO_VALUE", nullable = false, length = 2048)
    private String value;
     */

    @OneToMany(mappedBy = "po", cascade = CascadeType.ALL)
    private Set<RelatedObject> relatedObjects;

    // TODO with one to one we consider only one PO per request
    @OneToOne(fetch = FetchType.LAZY, optional = false) //TODO set the optional here later (false if possible)
    //@OneToOne //check if this does what it should
    @JoinColumn(name = "REQ_ID", nullable = false, referencedColumnName = "POID")
    //how to make po_id the same value as id of po ?
    //@MapsId("id")
    private POID poid;




    @OneToOne(fetch = FetchType.LAZY, mappedBy = "po", cascade = CascadeType.ALL, optional = false)
    //@JoinColumn(name = "DIGESTLIST_ID", nullable = false, referencedColumnName = "ID")
    private DigestList digestList;


}
