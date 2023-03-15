package be.uclouvain.lt.pres.ers.model.deserializer;

import be.uclouvain.lt.pres.ers.model.DigestListDto;
import be.uclouvain.lt.pres.ers.utils.OidUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;

import java.io.IOException;
import java.util.*;

public class DigestListDeserializer extends JsonDeserializer<DigestListDto> {

    @Override
    public DigestListDto deserialize(final JsonParser parser, final DeserializationContext content)
            throws IOException, JsonProcessingException {
        final ObjectCodec codec = parser.getCodec();
        final JsonNode root = codec.readTree(parser);

        final Iterator<String> rootFieldNameIter = root.fieldNames();
        while (rootFieldNameIter.hasNext()) {
            String field = rootFieldNameIter.next();
            if(!Objects.equals(field, "pres-DigestListType")) {
                throw new IllegalArgumentException("Unknown root-level field : "+field);
            }
        }

        final JsonNode node = root.get("pres-DigestListType");
//        final JsonNode node = root;
        if(node == null || node.isNull()) {
            throw new IllegalArgumentException("Empty field 'pres-DigestListType'");
        }

        final DigestListDto digestList = new DigestListDto();
        final Iterator<String> fieldNameIter = node.fieldNames();

        while (fieldNameIter.hasNext()) {
            System.out.println("Iterating ...");
            final String fieldName = fieldNameIter.next();
            switch (fieldName) {
                case "digAlg" -> {
                    System.out.println("Iterating digAlg");
//                    URI digAlg;
//                    try {
//                        digAlg = new URI(node.get(fieldName).textValue());
//                    } catch (URISyntaxException e) {
//                        throw new IllegalArgumentException("Cannot parse URI in field digAlg");
//                    }
                    String extracted = node.get(fieldName).textValue();
                    extracted = OidUtils.stringToOidString(extracted);
                    DigestAlgorithm alg = DigestAlgorithm.forOID(extracted);
                    digestList.setDigestMethod(alg);
                }
                case "digVal" -> {
                    System.out.println("Iterating digAlg");
                    JsonNode arrayRoot = node.get(fieldName);
                    if(! arrayRoot.isArray()) throw new IllegalArgumentException("Field 'digVal' in digest list JSON is not an array");
                    String value;
                    List<byte[]> result = new ArrayList<>();
                    for (JsonNode jsonNode : arrayRoot) {
                        value = jsonNode.textValue();
                        System.out.println("      digAlg : "+value);
                        // TODO : Here we assume a specific string encoding (ISO 8859 1, see decode() documentation)
                        result.add(Base64.getDecoder().decode(value));
                    }
                    digestList.setDigests(result);
                }
                // TODO : support evidences ?
                case "ev" -> throw new IllegalArgumentException("'ev' field present in digest list, evidences are not supported.");
                default -> throw new IllegalArgumentException("Unknown field in digest list JSON : " + fieldName);
            }
        }

        return digestList;
    }
}
