package be.uclouvain.lt.pres.ers.model;

import java.net.URI;
import java.util.List;

import lombok.Data;

@Data
public class OperationDto {

    private String name;
    private URI specification;
    private String description;
    private List<OperationInputDto> input;
    private List<OperationOutputDto> output;

}
