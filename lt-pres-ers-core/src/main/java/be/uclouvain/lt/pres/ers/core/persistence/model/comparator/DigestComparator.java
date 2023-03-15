package be.uclouvain.lt.pres.ers.core.persistence.model.comparator;

import be.uclouvain.lt.pres.ers.core.persistence.model.Digest;
import be.uclouvain.lt.pres.ers.utils.BinaryOrderComparator;

import java.util.Comparator;

public class DigestComparator implements Comparator<Digest> {
    // TODO for sorting purposes, better to add compareTo in Digest ?
    @Override
    public int compare(Digest o1, Digest o2) {
        return BinaryOrderComparator.compareBytes(o1.getDigest(), o2.getDigest());
    }
}
