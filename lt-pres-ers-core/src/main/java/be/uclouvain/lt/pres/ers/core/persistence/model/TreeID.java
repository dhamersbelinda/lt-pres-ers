package be.uclouvain.lt.pres.ers.core.persistence.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "Tree_id")
@Getter
@Setter
@NoArgsConstructor
public class TreeID {
    @Id
    @Column(name = "TREE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long treeId;
}
