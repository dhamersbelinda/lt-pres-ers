package be.uclouvain.lt.pres.ers.core.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "OPERATION_INPUT")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class OperationInput {

    @Id
    @Column(name = "ID")
    @Setter(value = AccessLevel.PRIVATE) // Id is managed by DB
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME", nullable = false, length = 128)
    private String name;

    @Column(name = "DESCRIPTION", nullable = false, length = 2048)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "OPERATION_ID", nullable = false, referencedColumnName = "ID")
    private Operation operation;

}
