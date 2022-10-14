package be.uclouvain.lt.pres.ers.core.persistence.model;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import be.uclouvain.lt.pres.ers.model.PreservationStorageModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NamedEntityGraph(name = "profile-entity-graph", attributeNodes = {
        @NamedAttributeNode(value = "operations", subgraph = "operation-subgraph") }, subgraphs = {
                @NamedSubgraph(name = "operation-subgraph", attributeNodes = { @NamedAttributeNode("inputs"),
                        @NamedAttributeNode("outputs") }) })
@Entity
@Table(name = "PROFILE")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Profile {

    @Id
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

}
