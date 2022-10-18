package be.uclouvain.lt.pres.ers.core.persistence.model;

import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.Period;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import be.uclouvain.lt.pres.ers.model.PreservationStorageModel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
@Table(name = "PROFILE")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Profile {

    @Id
    @Column(name = "ID")
    @Setter(value = AccessLevel.PRIVATE) // Id is managed by DB
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // profileIdentifier can be a natural primary key, but JPA 2.2 does not allow
    // the use of converters on attributes annotated with @Id. So, we use a
    // surrogate key and we put a unique key constraint on this column.
    @Column(name = "PROFILE_IDENTIFIER", nullable = false, length = 2048)
    private URI profileIdentifier;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "profile", cascade = CascadeType.ALL)
    private Set<Operation> operations;

    @Column(name = "PRESERVATION_EVIDENCE_POLICY", nullable = false, length = 2048)
    private URI preservationEvidencePolicy;

    @Column(name = "VALID_FROM", nullable = false)
    private OffsetDateTime validFrom;

    @Column(name = "VALID_UNTIL", nullable = true)
    private OffsetDateTime validUntil;

    @Column(name = "PRESERVATION_STORAGE_MODEL", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private PreservationStorageModel preservationStorageModel;

    @Column(name = "PRESERVATION_GOAL", nullable = false, length = 2048)
    private URI preservationGoal;

    @Column(name = "EVIDENCE_FORMAT", nullable = false, length = 2048)
    private URI evidenceFormat;

    @Column(name = "SCHEME_IDENTIFIER", nullable = true, length = 2048)
    private URI schemeIdentifier;

    @Column(name = "SPECIFICATION", nullable = true, length = 2048)
    private URI specification;

    @Column(name = "PRESERVATION_EVIDENCE_RETENTION_PERIOD", nullable = true, length = 32)
    private Period preservationEvidenceRetentionPeriod;

    @Column(name = "PRESERVATION_EVIDENCE_RETENTION_DURATION", nullable = true, length = 32)
    private Duration preservationEvidenceRetentionDuration;

}
