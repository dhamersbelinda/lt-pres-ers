package be.uclouvain.lt.pres.ers.core.persistence.model;

import be.uclouvain.lt.pres.ers.core.persistence.model.dto.EvidenceRecordDto;
import be.uclouvain.lt.pres.ers.core.persistence.model.dto.TreeCategoryDto;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@NamedEntityGraph(name = "request-entity-graph", attributeNodes = {
        @NamedAttributeNode(value = "clientId"),
        @NamedAttributeNode(value = "po", subgraph = "po-subgraph"),
        @NamedAttributeNode(value = "node", subgraph = "node-subgraph")
        },

        subgraphs = {
        @NamedSubgraph(name = "po-subgraph", attributeNodes = {
                @NamedAttributeNode(value = "digestList", subgraph = "digestList-subgraph") }),
        @NamedSubgraph(name = "digestList-subgraph", attributeNodes = {
                @NamedAttributeNode(value = "digests", subgraph = "digest-subgraph")}),
        @NamedSubgraph(name = "digest-subgraph" , attributeNodes = {
                @NamedAttributeNode(value = "digest")}),


        @NamedSubgraph(name = "node-subgraph", attributeNodes = {
               @NamedAttributeNode(value = "parent"),
               @NamedAttributeNode(value = "neighbour"),
                @NamedAttributeNode(value = "treeId"),
                @NamedAttributeNode(value = "inTreeId")
        })

        }
)
@NamedNativeQuery(
        name = "POID.getERPathFromPOID",
        query = """
                    WITH
                        -- Get node ID from POID
                        Doc AS (SELECT node_id FROM poids WHERE poid=:POID),
                        -- Get complete path from found node_id to highest possible point (aka most recent ts'ed root)
                        Path AS (
                            WITH RECURSIVE cte (node_id, parent_id, neighbour_id, tree_id, in_tree_num, node_value) AS
                                               (
                                                   SELECT N.* FROM Nodes N WHERE N.node_id = (SELECT node_id FROM Doc)
                                                   UNION ALL
                                                   SELECT N.* FROM Nodes N JOIN cte C ON N.node_id = C.parent_id
                                               )
                            SELECT C.*, R.root_timestamp FROM cte C LEFT JOIN root R ON C.node_id = R.node_id),
                        -- first part : get reduced hash tree, but misses all root related nodes
                        -- second part : adds the doc itself and root nodes (not root leaf nodes)
                        -- third part : adds the root leaf nodes
                        r AS (
                                                -- Get the very first node and mark it as start
                                                (SELECT *, TRUE AS Start FROM Path P WHERE P.node_id = (SELECT node_id FROM Doc))
                                                    UNION
                                                -- Get all neighbours but exclude (NOT IN) non roots in the direct path and the document
                                                (SELECT N.*, P.root_timestamp AS timestamp, FALSE AS Start FROM Path P JOIN Nodes N ON N.parent_id = P.parent_id
                                                        WHERE N.node_id NOT IN
                                                            (SELECT node_id FROM Path WHERE root_timestamp IS NULL OR node_id = (SELECT node_id FROM Doc)))
                                                    UNION
                                        --      (SELECT *, FALSE AS Start FROM Path P WHERE P.neighbour_id IS NULL AND P.node_id != (SELECT node_id FROM Doc))
                                        --           UNION
                                                -- Add the root node's parents, we must self join as we identify them from roots s
                                                (SELECT P2.*, FALSE AS Start FROM Path P1 JOIN Path P2 ON P1.parent_id=P2.node_id WHERE P1.in_tree_num = 0)
                        )
                        -- Order everything for easier processing (ASSUMES TREEID IS INCREMENTAL AND INCREASES WITH NEW TREES !)
                       SELECT * FROM r ORDER BY tree_id ASC, in_tree_num DESC;
                    """,
        resultSetMapping = "EvidenceRecordDtoMapping"
)
@SqlResultSetMapping(name = "EvidenceRecordDtoMapping",
        classes = @ConstructorResult(
                targetClass = EvidenceRecordDto.class,
                columns = {
                        @ColumnResult(name = "node_id", type = Long.class),
                        @ColumnResult(name = "parent_id", type = Long.class),
                        @ColumnResult(name = "node_value", type = byte[].class),
                        @ColumnResult(name = "tree_id", type = Long.class),
                        @ColumnResult(name = "in_tree_num", type = Long.class),
                        @ColumnResult(name = "root_timestamp", type = byte[].class),
                        @ColumnResult(name = "start", type = Boolean.class)}))
@NamedNativeQuery(
        name = "POID.getToPreserveCategoriesPOIDAndRoot",
        query = """
                    WITH
                        poids1 AS (SELECT DISTINCT client_id, digest_method FROM (
                                    ((SELECT poid, client_id FROM POIDs WHERE creation_date < :DATE_NOW) AS pds\s
                                    JOIN
                                    (SELECT id, req_id FROM PO) AS po ON pds.poid = po.req_id) AS pds_po
                                    JOIN\s
                                    (SELECT id, digest_method FROM digestlist) AS dg ON pds_po.id=dg.id) AS r),
                        roots1 AS (SELECT DISTINCT client_id, digest_method FROM root WHERE :DATE_NOW <= cert_valid_until AND cert_valid_until <= :DATE_SHIFTED)\s
                    SELECT * FROM (SELECT * FROM poids1 UNION DISTINCT (SELECT * FROM roots1)) AS r1;
                    """,
        resultSetMapping = "TreeCategoryDtoMapping"
)
@SqlResultSetMapping(name = "TreeCategoryDtoMapping",
        classes = @ConstructorResult(
                targetClass = TreeCategoryDto.class,
                columns = {
                        @ColumnResult(name = "client_id", type = Long.class),
                        @ColumnResult(name = "digest_method", type = String.class)}))
@Entity
@Table(name = "POIDs")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class POID implements Treeable{
    @Id
//    @Column(name = "POID", nullable = false, updatable = false)
    @Setter(value = AccessLevel.PRIVATE) // Id is managed by DB
//    @GeneratedValue(generator = "UUID")
//    @GenericGenerator(
//            name = "UUID",
//            strategy = "org.hibernate.id.UUIDGenerator"
//    )
//    @Type(type="uuid-char")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "POID", nullable = false, updatable = false, columnDefinition = "UUID default gen_random_uuid()")
    private UUID id; //-> will become the POID

    //TODO faire gaffe que rien ne s'insère ici et que ça sert juste à avoir la FK
    @JoinColumn(name = "PROFILE_ID", nullable = false, referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @ToString.Exclude //removed cascadetype , cascade = CascadeType.DETACH
    private Profile profile;

    @JoinColumn(name = "CLIENT_ID", referencedColumnName = "CLIENT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_CLIENT_ID_POID"))
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private Client clientId;

    @Column(name = "DIGEST_METHOD", nullable = false)
    private String digestMethod;

    @Column(name = "CREATION_DATE", nullable = false)
    private OffsetDateTime creationDate;

    @Column(name = "DIGEST_VALUE", nullable = false)
    private byte[] digestValue;

    @OneToOne(fetch = FetchType.LAZY, cascade=CascadeType.ALL, optional = false, mappedBy = "poid")
    @ToString.Exclude
    private PO po;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "NODE_ID", referencedColumnName = "NODE_ID", foreignKey = @ForeignKey(name = "fk_node_id"))
    @ToString.Exclude
    private Node node;

    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public byte[] getHashValue() {
        return this.digestValue;
    }
}
