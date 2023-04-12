package be.uclouvain.lt.pres.ers.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.URI;
import java.util.List;

@Data
@AllArgsConstructor
public class PreservePORequestDto {
    private List<PODto> poDtos;
    private ProfileDto profileDto;
    private Integer clientId;
    private String digestMethod;
}
