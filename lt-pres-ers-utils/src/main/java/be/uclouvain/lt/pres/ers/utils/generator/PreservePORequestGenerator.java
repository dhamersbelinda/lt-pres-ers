package be.uclouvain.lt.pres.ers.utils.generator;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.spi.DSSUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class PreservePORequestGenerator {

    public static String generateRandomValidDigestListPORequest(int nDigests, DigestAlgorithm digestAlgorithm) {
        List<byte[]> b64Digests = new ArrayList<>(nDigests);
        byte[] temp = new byte[30];
        for (int i = 0; i < nDigests; i++) {
            new Random().nextBytes(temp);
            b64Digests.add(Base64.getEncoder().encode(DSSUtils.digest(digestAlgorithm, temp)));
        }
        return generateValidPORequestFromB64Digests(b64Digests, digestAlgorithm);
//        String toB64 =
//                """
//                      {
//                      "pres-DigestListType": {
//                        "digAlg":"%s",
//                        "digVal":%s
//                      }
//                    }
//                """.formatted(digestMethod.getOid(), byteArrayListToJSONArray(digests));
//        byte[] b64 = Base64.getEncoder().encode(toB64.getBytes(StandardCharsets.UTF_8));
//        return """
//                {
//                  "pro": "https://uclouvain.be/en/faculties/epl/preservation-api/profile/v1.0",
//                  "po": [
//                    {
//                      "binaryData": {
//                        "value": "%s"
//                      },
//                      "formatId": "http://uri.etsi.org/19512/format/DigestList"
//                    }
//                  ]
//                }
//                """.formatted(new String(b64, StandardCharsets.UTF_8));
    }

    private static String byteArrayListToJSONArray(List<byte[]> list) {
        StringBuilder builder = new StringBuilder("[");
        boolean start = true;
        for (byte[] bytes : list) {
            if(!start) {
                builder.append(",\"");
            } else {
                start = false;
                builder.append("\"");
            }
            builder.append(new String(bytes, StandardCharsets.UTF_8));
            builder.append("\"");
        }
        builder.append("]");
        return builder.toString();
    }

    public static String generateValidPORequestFromDocs(List<String> docs, DigestAlgorithm digestAlgorithm) {
        List<byte[]> b64Digests = new ArrayList<>(docs.size());
        for (String doc : docs) {
            b64Digests.add(Base64.getEncoder().encode(DSSUtils.digest(digestAlgorithm, doc.getBytes(StandardCharsets.UTF_8))));
        }
        return generateValidPORequestFromB64Digests(b64Digests, digestAlgorithm);
    }

    public static String generateValidPORequestFromB64Digests(List<byte[]> digests, DigestAlgorithm digestAlgorithm) {
        String toB64 = "{\"digAlg\":\"%s\",\"digVal\":%s}".formatted(digestAlgorithm.getOid(), byteArrayListToJSONArray(digests));
        byte[] b64 = Base64.getEncoder().encode(toB64.getBytes(StandardCharsets.UTF_8));
        return """
                {
                  "pro": "https://uclouvain.be/en/faculties/epl/preservation-api/profile/v1.0",
                  "po": [
                    {
                      "binaryData": {
                        "value": "%s"
                      },
                      "formatId": "http://uri.etsi.org/19512/format/DigestList"
                    }
                  ]
                }
                """.formatted(new String(b64, StandardCharsets.UTF_8));
    }
}
