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

        final DigestListDto digestList = new DigestListDto();
        final Iterator<String> rootFieldNameIter = root.fieldNames();
        while (rootFieldNameIter.hasNext()) {
            String fieldName = rootFieldNameIter.next();
            switch (fieldName) {
                case "digAlg" -> {
                    String extracted = root.get(fieldName).textValue();
                    extracted = OidUtils.stringToOidString(extracted);
                    DigestAlgorithm alg = DigestAlgorithm.forOID(extracted); // Warning : for OID here !
                    digestList.setDigestMethod(alg);
                }
                case "digVal" -> {
                    JsonNode arrayRoot = root.get(fieldName);
                    if(! arrayRoot.isArray()) throw new IllegalArgumentException("Field 'digVal' in digest list JSON is not an array");
                    String value;
                    List<byte[]> result = new ArrayList<>();
                    for (JsonNode jsonNode : arrayRoot) {
                        value = jsonNode.textValue();
                        // TODO : Here we assume a specific string encoding (ISO 8859 1, see decode() documentation)
                        result.add(Base64.getDecoder().decode(value));
                    }
                    digestList.setDigests(result);
                }
                // TODO : support evidences ?
                case "ev" -> throw new IllegalArgumentException("'ev' field present in digest list, evidences are not supported.");
                default -> throw new IllegalArgumentException("Unknown field in pres-DigestListType JSON : " + fieldName);
            }
        }
        if(digestList.getDigests() == null || digestList.getDigests().isEmpty())
            throw new IllegalArgumentException("Missing or empty digVal");

        if(digestList.getDigestMethod() == null)
            throw new IllegalArgumentException("Missing digAlg");

        return digestList;
    }
}
