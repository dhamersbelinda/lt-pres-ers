package be.uclouvain.lt.pres.ers.model;

import be.uclouvain.lt.pres.ers.model.deserializer.DigestListDeserializer;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import lombok.Data;

import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Data
@JsonRootName(value = "pres-DigestListType")
@JsonDeserialize(using = DigestListDeserializer.class)
public class DigestListDto {

    @JsonProperty("DigestMethod")
    @JsonAlias("digAlg")
//    private URI digestMethod;
    private DigestAlgorithm digestMethod;
    @JsonProperty("DigestValue")
    @JsonAlias("digVal")

    private List<byte[]> digests;
//    private List<String> digests;
    // TODO : Handle "ev" field !

//    public void setDigests(List<String> digests) {
//        List<byte[]> result = new ArrayList<>(digests.size());
//        byte[] decodedContent;
//        int i = 0;
//        for (String digest : digests) {
//            decodedContent = Base64.getDecoder().decode(digest);
//            result.add(decodedContent);
//            i++;
//        }
//        this.digests = result;
//    }
}
