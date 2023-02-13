package be.uclouvain.lt.pres.ers.core.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Root {

    @Id
    private long nodeId;

    @OneToOne(cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "NODE_ID", referencedColumnName = "NODE_ID")
    private Node node;

    @Column(name = "TIMESTAMP")
    private int timestamp;
}
