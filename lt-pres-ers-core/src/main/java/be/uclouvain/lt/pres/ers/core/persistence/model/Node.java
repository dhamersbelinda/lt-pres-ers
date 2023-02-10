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
@ToString
@NoArgsConstructor
public class Node {
    @Id
    @Column(name = "NODE_ID")
    @Setter(value = AccessLevel.PRIVATE) // Id is managed by DB
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long nodeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID", referencedColumnName = "NODE_ID", foreignKey = @ForeignKey(name = "FK_PARENT_ID"))
    private Node parent;

    @OneToMany(mappedBy="parent", fetch=FetchType.LAZY)
    private Set<Node> children;

    @JoinColumn(name = "NEIGHBOUR_ID", referencedColumnName = "NODE_ID", foreignKey = @ForeignKey(name = "FK_NEIGHBOUR_ID"))
    @OneToOne(fetch = FetchType.LAZY) // TODO : Change to support more than one neighbour ?
    private Node neighbour;

    @JoinColumn(name = "TREE_ID", referencedColumnName = "TREE_ID", foreignKey = @ForeignKey(name = "FK_TREE_ID"))
    @ManyToOne(fetch = FetchType.LAZY)
    private TreeID treeId;

    @Column(name = "IN_TREE_ID", nullable = false)
    private long inTreeId;

    @OneToOne(mappedBy = "nodeId", optional = true)
    private Root root;

    @OneToOne(mappedBy = "node", optional = true)
    private POID poid;
}
