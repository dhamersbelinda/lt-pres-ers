package be.uclouvain.lt.pres.ers.core.persistence.model;

import java.net.URI;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "OPERATION")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Operation {

    @Id
    @Column(name = "ID")
    @Setter(value = AccessLevel.PRIVATE) // Id is managed by DB
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME", nullable = false, length = 128)
    private String name;

    @Column(name = "SPECIFICATION", nullable = false, length = 2048)
    private URI specification;

    @Column(name = "DESCRIPTION", nullable = false, length = 2048)
    private String description;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "operation", cascade = CascadeType.ALL)
    private Set<OperationInput> inputs;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "operation", cascade = CascadeType.ALL)
    private Set<OperationOutput> outputs;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PROFILE_IDENTIFIER", nullable = false, referencedColumnName = "PROFILE_IDENTIFIER")
    private Profile profile;

}
