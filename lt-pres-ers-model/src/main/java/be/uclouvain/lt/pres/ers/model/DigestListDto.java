package be.uclouvain.lt.pres.ers.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import java.net.URI;
import java.util.List;

@Data
@JsonRootName(value = "pres-DigestListType")
public class DigestListDto {
    @JsonProperty("DigestMethod")
    @JsonAlias("digAlg")
    private URI digestMethod;
    @JsonProperty("DigestValue")
    @JsonAlias("digVal")
    private List<String> digests;
    // TODO : Handle "ev" field !

}
