package be.uclouvain.lt.pres.ers.core.persistence.model;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "Nodes",
        uniqueConstraints = {
        @UniqueConstraint(columnNames = { "TREE_ID", "IN_TREE_ID" })//,
        //@UniqueConstraint(columnNames = { "NEIGHBOUR_ID" }) // TODO : Here with unique neighbour we assume a binary tree !
})

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class Node {
    @Id
    @Column(name = "NODE_ID")
    @Setter(value = AccessLevel.PRIVATE) // Id is managed by DB
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private long nodeId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "PARENT_ID", referencedColumnName = "NODE_ID", foreignKey = @ForeignKey(name = "FK_PARENT_ID"))
    private Node parent;

    @OneToMany(mappedBy="parent", fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Include
    private Set<Node> children;

    @JoinColumn(name = "NEIGHBOUR_ID", referencedColumnName = "NODE_ID", foreignKey = @ForeignKey(name = "FK_NEIGHBOUR_ID"))
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // TODO : Change to support more than one neighbour ?
    private Node neighbour;

    @JoinColumn(name = "TREE_ID", referencedColumnName = "TREE_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_TREE_ID"))
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Include
    private TreeID treeId;

    @Column(name = "IN_TREE_ID", nullable = false)
    @ToString.Include
    private long inTreeId;

    @Column(name = "NODE_VALUE", nullable = false)
    @ToString.Include
    private String nodeValue;

    @OneToOne(mappedBy = "node", optional = true)
    private Root root;

    @OneToOne(mappedBy = "node", optional = true, cascade = CascadeType.ALL)
    @Setter(AccessLevel.NONE)
    private POID poid;

    public void setPoid(POID poid) {
        this.poid = poid;
        poid.setNode(this);
    }
}
