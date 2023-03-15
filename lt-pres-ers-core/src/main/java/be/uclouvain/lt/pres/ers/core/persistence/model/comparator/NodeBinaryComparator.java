package be.uclouvain.lt.pres.ers.core.persistence.model.comparator;

import be.uclouvain.lt.pres.ers.core.persistence.model.Node;
import be.uclouvain.lt.pres.ers.utils.BinaryOrderComparator;

import java.util.Comparator;

public class NodeBinaryComparator implements Comparator<Node> {
    @Override
    public int compare(Node o1, Node o2) {
        return BinaryOrderComparator.compareBytes(o1.getNodeValue(), o2.getNodeValue());
    }
}
