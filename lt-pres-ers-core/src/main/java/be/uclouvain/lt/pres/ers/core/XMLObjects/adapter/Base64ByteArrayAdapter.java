package be.uclouvain.lt.pres.ers.core.XMLObjects.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.nio.charset.StandardCharsets;

public class Base64ByteArrayAdapter extends XmlAdapter<String, byte[]> {
    @Override
    public byte[] unmarshal(String v) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public String marshal(byte[] v) throws Exception {
        return new String(v, StandardCharsets.UTF_8);
    }
}
