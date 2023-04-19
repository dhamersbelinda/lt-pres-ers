package be.uclouvain.lt.pres.ers.utils;

import be.uclouvain.lt.pres.ers.utils.generator.PreservePORequestGenerator;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PreservePORequestGeneratorTest {

    @Test
    public void generateRandomValidDigestListPORequestTest() {
        System.out.println(PreservePORequestGenerator.generateRandomValidDigestListPORequest(1, DigestAlgorithm.SHA256));
    }

    @Test
    public void generateRandomValidPORequestFromDocTest() {
        List<String> docs = new ArrayList<>(1);

        docs.add("Sasha");
        System.out.println(PreservePORequestGenerator.generateValidPORequestFromDocs(docs, DigestAlgorithm.SHA256));

        docs.clear();
        docs.add("Belinda");
        System.out.println(PreservePORequestGenerator.generateValidPORequestFromDocs(docs, DigestAlgorithm.SHA256));

        docs.clear();
        docs.add("Jean-Emmanuel");
        System.out.println(PreservePORequestGenerator.generateValidPORequestFromDocs(docs, DigestAlgorithm.SHA256));

        docs.clear();
        docs.add("Jean");
        System.out.println(PreservePORequestGenerator.generateValidPORequestFromDocs(docs, DigestAlgorithm.SHA256));

        docs.clear();
        docs.add("Yves");
        System.out.println(PreservePORequestGenerator.generateValidPORequestFromDocs(docs, DigestAlgorithm.SHA256));
    }
}
