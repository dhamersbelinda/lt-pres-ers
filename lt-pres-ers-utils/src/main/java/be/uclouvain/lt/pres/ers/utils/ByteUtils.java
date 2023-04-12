package be.uclouvain.lt.pres.ers.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ByteUtils {

    public static byte[] concat(List<byte[]> byteArrays) {
        int size = 0, i = 0;
        for (byte[] bytes : byteArrays) {
            if(bytes == null) throw new NullPointerException("Cannot invoke bytes.length as bytes is null, it was the "+i+"th argument.");
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

    public static byte[] concat(byte[]... byteArrays) {
        List<byte[]> args = new ArrayList<>(Arrays.asList(byteArrays));
        return concat(args);
    }

}
