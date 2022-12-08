package be.uclouvain.lt.pres.ers.core.persistence.model;

import lombok.*;

import javax.persistence.*;
import java.net.URI;
import java.util.Set;

//TODO make this inherit from PO

// TODO change this whole graph

@NamedEntityGraph(name = "profile-entity-graph", attributeNodes = {
        @NamedAttributeNode(value = "operations", subgraph = "operation-subgraph") }, subgraphs = {
                @NamedSubgraph(name = "operation-subgraph", attributeNodes = {
                        @NamedAttributeNode(value = "inputs", subgraph = "operation-input-subgraph"),
                        @NamedAttributeNode("outputs") }),
                @NamedSubgraph(name = "operation-input-subgraph", attributeNodes = {
                        @NamedAttributeNode(value = "format", subgraph = "format-subgraph") }),
                @NamedSubgraph(name = "format-subgraph", attributeNodes = {
                        @NamedAttributeNode(value = "parameters", subgraph = "parameter-subgraph") }),
                @NamedSubgraph(name = "parameter-subgraph", attributeNodes = {
                        @NamedAttributeNode(value = "format", subgraph = "format-subgraph-2") }),
                @NamedSubgraph(name = "format-subgraph-2", attributeNodes = {
                        @NamedAttributeNode(value = "parameters") }) })
@Entity
@Table(name = "EVIDENCE")
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

    // evidenceIdentifier can be a natural primary key, but JPA 2.2 does not allow
    // the use of converters on attributes annotated with @Id. So, we use a
    // surrogate key and we put a unique key constraint on this column.
    @Column(name = "EVIDENCE_IDENTIFIER", nullable = false, length = 2048)
    private URI evidenceIndentifier;

    // TODO how to represent xml ?
    @Column(name = "XML_VALUE", nullable = false, length = 2048)
    private String value;

    @Column(name = "FORMAT_IDENTIFIER", nullable = true, length = 2048)
    private URI formatIdentifier;

    @Column(name = "MIME_TYPE", nullable = true, length = 2048)
    private URI mimeType;

    @Column(name = "PRONOM_PUID", nullable = true, length = 2048)
    private URI pronomPUID;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "evidence", cascade = CascadeType.ALL)
    private Set<RelatedObject> relatedObjects;

    @Column(name = "PO_ID", nullable = true, length = 2048)
    private URI poIdentifier;

    @Column(name = "VERSION_ID", nullable = true, length = 2048)
    private URI versionIdentifier;
    

}
