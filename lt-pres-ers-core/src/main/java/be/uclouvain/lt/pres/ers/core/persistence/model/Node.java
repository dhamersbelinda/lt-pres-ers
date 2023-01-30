package be.uclouvain.lt.pres.ers.core.persistence.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "Nodes",
        uniqueConstraints = {
        @UniqueConstraint(columnNames = { "TREE_ID", "IN_TREE_ID" }),
        @UniqueConstraint(columnNames = { "NEIGHBOUR_ID" })}) // TODO : Here with unique neighbour we assume a binary tree !

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Node {
    @Id
    @Column(name = "NODE_ID")
    @Setter(value = AccessLevel.PRIVATE) // Id is managed by DB
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long nodeID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID", referencedColumnName = "NODE_ID", foreignKey = @ForeignKey(name = "fk_parent_id"))
    @Getter
    @Setter
    private Node parent;

    @JoinColumn(name = "NEIGHBOUR_ID", referencedColumnName = "NODE_ID", foreignKey = @ForeignKey(name = "fk_neighbour_id"))
    @ManyToOne(fetch = FetchType.LAZY)
    @Getter
    @Setter
    private Node neighbour;

    @Column(name = "TREE_ID")
    @Setter
    private long treeID;

    @Column(name = "IN_TREE_ID")
    @Setter
    private long inTreeID;
}