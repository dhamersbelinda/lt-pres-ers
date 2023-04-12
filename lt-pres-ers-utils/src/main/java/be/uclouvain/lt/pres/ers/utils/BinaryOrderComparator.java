package be.uclouvain.lt.pres.ers.utils;

import java.util.Comparator;

public class BinaryOrderComparator implements Comparator<byte[]> {
    @Override
    public int compare(byte[] o1, byte[] o2) {
        return compareBytes(o1, o2);
    }

    public static int compareBytes(byte[] o1, byte[] o2) {
        if(o1 == null){
            if(o2 == null) return 0;
            else return -1;
        } else {
            if(o2 == null) return 1;
        }

        int runner = 0, min = Math.min(o1.length, o2.length), cmp;

        for (; runner < min; runner++) {
            cmp = Byte.compare(o1[runner], o2[runner]);
            if(cmp!= 0) {
                return cmp;
            }
        }

        // shorter in length is considered smaller
        return o1.length - o2.length;
    }
}

