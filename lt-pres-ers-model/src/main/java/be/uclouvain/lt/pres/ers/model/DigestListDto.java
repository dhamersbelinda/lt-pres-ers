package be.uclouvain.lt.pres.ers.model;

import lombok.Data;

import java.net.URI;
import java.util.List;

@Data
public class DigestListDto {

    private URI digestMethod;
    private List<String> digests;

}
