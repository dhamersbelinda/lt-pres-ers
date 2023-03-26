package be.uclouvain.lt.pres.ers.core.persistence.model.comparator;

import be.uclouvain.lt.pres.ers.core.persistence.model.Node;

import java.util.Comparator;

public class NodeInTreeIdComparator implements Comparator<Node> {
    @Override
    public int compare(Node o1, Node o2) {
        return Long.compare(o1.getInTreeId(), o2.getInTreeId());
    }
}
