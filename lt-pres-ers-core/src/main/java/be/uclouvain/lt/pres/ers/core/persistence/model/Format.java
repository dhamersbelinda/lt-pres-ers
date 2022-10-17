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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "FORMAT")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Format {

    @Id
    @Column(name = "ID")
    @Setter(value = AccessLevel.PRIVATE) // Id is managed by DB
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "FORMAT_ID", nullable = false, length = 2048)
    private URI formatId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentFormat", cascade = CascadeType.ALL)
    private Set<Parameter> parameters;

}
