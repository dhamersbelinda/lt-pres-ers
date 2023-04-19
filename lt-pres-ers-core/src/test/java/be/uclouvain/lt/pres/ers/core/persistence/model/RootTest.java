package be.uclouvain.lt.pres.ers.core.persistence.model;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.spi.DSSUtils;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class RootTest {
    @Test
    public void testGetHashValue() {
        Root root = new Root();
        root.setTimestamp("test".getBytes(StandardCharsets.UTF_8));
        root.setDigestMethod(DigestAlgorithm.SHA256.getOid());

        // "test" in base64: "dGVzdA=="
        // "<TimeStamp xmlns="urn:ietf:params:xml:ns:ers"><TimeStampToken Type="RFC3161">dGVzdA==</TimeStampToken></TimeStamp>"

        assertArrayEquals(DSSUtils.digest(DigestAlgorithm.SHA256, "<TimeStamp xmlns=\"urn:ietf:params:xml:ns:ers\"><TimeStampToken Type=\"RFC3161\">dGVzdA==</TimeStampToken></TimeStamp>".getBytes(StandardCharsets.UTF_8)),root.getHashValue());
    }
}
