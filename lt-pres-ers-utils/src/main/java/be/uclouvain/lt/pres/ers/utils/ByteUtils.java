package be.uclouvain.lt.pres.ers.utils;

import java.util.List;

public class ByteUtils {

    public static byte[] concat(List<byte[]> byteArrays) {
        int size = 0;
        for (byte[] bytes : byteArrays) {
            size += bytes.length;
        }
        byte[] concat = new byte[size];
        int runner = 0;
        for (byte[] bytes : byteArrays) {
            System.arraycopy(bytes, 0, concat, runner, bytes.length);
            runner += bytes.length;
        }
        return concat;
    }

}
