package be.uclouvain.lt.pres.ers.model;

import java.net.URI;
import java.util.List;

import lombok.Data;

@Data
public class FormatDto {

    private URI formatId;
    private List<ParameterDto> parameters;

}
