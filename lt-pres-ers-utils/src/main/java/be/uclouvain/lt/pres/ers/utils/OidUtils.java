package be.uclouvain.lt.pres.ers.utils;

import java.net.URI;
import java.util.Locale;

public class OidUtils {
    public static String stringToOidString(String uri){
        String value = uri.toLowerCase(Locale.ENGLISH);
        if(value.startsWith("urn:oid:") && value.length() > 8){
            value = value.substring(8);
        }
        return value;
    }

    public static String uidToOidString(URI uri){
        return stringToOidString(uri.toString());
    }
}