package be.uclouvain.lt.pres.ers.core.persistence.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;

//@NamedNativeQuery(
//        name = "POID.getERPathFromPOID",
//        query = """
//                    WITH
//                        -- Get node ID from POID
//                        Doc AS (SELECT node_id FROM poids WHERE poid=:POID),
//                        -- Get complete path from found node_id to highest possible point (aka most recent ts'ed root)
//                        Path AS (
//                            WITH RECURSIVE cte (node_id, parent_id, neighbour_id, tree_id, in_tree_num) AS
//                                               (
//                                                   SELECT N.* FROM Nodes N WHERE N.node_id = (SELECT node_id FROM Doc)
//                                                   UNION ALL
//                                                   SELECT N.* FROM Nodes N JOIN cte C ON N.node_id = C.parent_id
//                                               )
//                            SELECT C.*, R.timestamp FROM cte C LEFT JOIN root R ON C.node_id = R.node_id),
//                        -- first part : get reduced hash tree, but misses all root related nodes
//                        -- second part : adds the doc itself and root nodes (not root leaf nodes)
//                        -- third part : adds the root leaf nodes
//                        r AS ( \s
//                            -- Get the very first node and mark it as start
//                            (SELECT *, TRUE AS Start FROM Path P WHERE P.node_id = (SELECT node_id FROM Doc))
//                                UNION
//                            -- Get all neighbours but exclude (NOT IN) non roots in the direct path and the document
//                            (SELECT N.*, P.timestamp AS timestamp, FALSE AS Start FROM Path P JOIN Nodes N ON N.parent_id = P.parent_id\s
//                                    WHERE N.node_id NOT IN\s
//                                        (SELECT node_id FROM Path WHERE timestamp IS NULL OR node_id = (SELECT node_id FROM Doc)))
//                                UNION
//                    --      (SELECT *, FALSE AS Start FROM Path P WHERE P.neighbour_id IS NULL AND P.node_id != (SELECT node_id FROM Doc))
//                    --           UNION
//                            -- Add the root node's parents, we must self join as we identify them from roots \s
//                            (SELECT P2.*, FALSE AS Start FROM Path P1 JOIN Path P2 ON P1.parent_id=P2.node_id WHERE P1.neighbour_id IS NULL)
//                        )
//                    -- Order everything for easier processing (ASSUMES TREEID IS INCREMENTAL AND INCREASES WITH NEW TREES !)
//                    SELECT * FROM r ORDER BY tree_id ASC, in_tree_num DESC;
//                    """,
//        resultSetMapping = "EvidenceRecordDtoMapping"
//)
//@SqlResultSetMapping(name = "EvidenceRecordDtoMapping",
//        classes = @ConstructorResult(
//                targetClass = EvidenceRecordDto.class,
//                columns = {
//                        @ColumnResult(name = "node_id", type = Long.class),
//                        @ColumnResult(name = "parent_id", type = Long.class),
//                        @ColumnResult(name = "neighbour_id", type = Long.class),
//                        @ColumnResult(name = "tree_id", type = Long.class),
//                        @ColumnResult(name = "in_tree_num", type = Long.class),
//                        @ColumnResult(name = "timestamp", type = String.class), // TODO : adapt for binary
//                        @ColumnResult(name = "start", type = Boolean.class)}))
@Getter
@Setter
@AllArgsConstructor
@ToString
public class EvidenceRecordDto {
    private Long nodeId;

    // If null we reached the latest timestamp
    private Long parent;

    private byte[] nodeValue;

    // The tree this node belongs to
    private Long treeId;

    // Specific numbering of nodes in a tree : per floor, from root to leaves and 'left to right', root is 0, always assumes a complete tree (when the tree is not complete there will be jumps in the numbering)
    // sorted descending
    private Long inTreeNum;

    private byte[] timestamp;

    // True if this is the requested document's (or document group) node
    private boolean start;
}
