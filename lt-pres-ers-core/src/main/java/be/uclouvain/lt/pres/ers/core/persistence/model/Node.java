package be.uclouvain.lt.pres.ers.core.persistence.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Nodes",
        uniqueConstraints = {
        @UniqueConstraint(columnNames = { "TREE_ID", "IN_TREE_ID" })
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
    private List<Node> children;

    @JoinColumn(name = "TREE_ID", referencedColumnName = "TREE_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_TREE_ID"))
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Include
    private TreeID treeId;

    @Column(name = "IN_TREE_ID", nullable = false)
    @ToString.Include
    private long inTreeId;

    @Column(name = "NODE_VALUE", nullable = false)
    @ToString.Include
    private byte[] nodeValue;

    @OneToOne(mappedBy = "node", optional = true, cascade = CascadeType.ALL)
    @Setter(AccessLevel.NONE)
    private Root root;

    @OneToOne(mappedBy = "node", optional = true, cascade = CascadeType.ALL)
    @Setter(AccessLevel.NONE)
    private POID poid;

    public void setRoot(Root root) {
//        if(root != null && this.poid != null) {
//            throw new IllegalArgumentException("Cannot set node's root value as its POID field is not null.");
//        }
//        root.getNode().setParent(this);
//        root.setIsExtended(true);
        this.root = root;
    }

    public void setPoid(POID poid) {
//        if(poid != null && this.root != null) {
//            throw new IllegalArgumentException("Cannot set node's poid value as its root field is not null.");
//        }
        this.poid = poid;
        poid.setNode(this);
    }

    // TODO : is it possible to do this ?
//    @OneToOne(mappedBy = "node", optional = true, cascade = CascadeType.ALL)
//    @Setter(AccessLevel.NONE)
//    private Treeable rootOrPOID;

//    public Treeable getTreeable() {
//
//    }

    // When creating leaves
    public void setLeafLink(Treeable t){
        if(t.isRoot()) {
            Root r = (Root) t;
            r.getNode().setParent(this);
            if(this.children == null) {
                this.children = new ArrayList<>(1);
            }
            this.children.add(r.getNode());
            r.setIsExtended(true);
//            this.setRoot((Root) t);
        } else {
            this.setPoid((POID) t);
        }
    }
}
