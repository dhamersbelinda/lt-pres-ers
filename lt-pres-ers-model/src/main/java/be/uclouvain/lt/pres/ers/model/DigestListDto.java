package be.uclouvain.lt.pres.ers.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.net.URI;
import java.util.List;

@Data
public class DigestListDto {
    @JsonProperty("DigestMethod")
    @JsonAlias("digAlg")
    private URI digestMethod;
    @JsonProperty("DigestValue")
    @JsonAlias("digVal")
    private List<String> digests;


}
