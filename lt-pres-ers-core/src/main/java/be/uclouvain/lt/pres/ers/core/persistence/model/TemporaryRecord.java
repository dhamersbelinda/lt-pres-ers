package be.uclouvain.lt.pres.ers.core.persistence.model;

import lombok.*;

import javax.persistence.*;
import java.net.URI;
import java.util.UUID;

@Entity
@Table(name = "TEMPORARY_RECORDS")
@IdClass(TemporaryRecordID.class)
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TemporaryRecord {
    @Id
    @JoinColumn(name = "POID", referencedColumnName = "POID", foreignKey = @ForeignKey(name = "FK_POID"))
    private UUID poid;

    @Id
    @Column(name = "DIG_NUM")
    private Integer digNum;

    @Column(name = "DIG_METHOD", nullable = false, length = 2048)
    private URI digMethod;

    @Column(name = "VALUE", nullable = false, length = 128)
    private String value;

    @Column(name = "CLIENT_ID", nullable = false)
    private int clientId;

    /*
    //TODO faire gaffe que rien ne s'insère ici et que ça sert juste à avoir la FK
    @JoinColumn(name = "PROFILE_ID", nullable = false, referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Profile profile;

    @Column(name = "CLIENT_ID", nullable = false, length = 2048)
    private Integer clientId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private PO po;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "NODE_ID", referencedColumnName = "NODE_ID", foreignKey = @ForeignKey(name = "fk_node_id"))
    private Node node;
    */
}



