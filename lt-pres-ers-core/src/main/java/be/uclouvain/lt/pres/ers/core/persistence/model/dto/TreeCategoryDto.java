package be.uclouvain.lt.pres.ers.core.persistence.model.dto;

import be.uclouvain.lt.pres.ers.core.persistence.model.Client;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class TreeCategoryDto {

    private Long clientId;

    private String digestAlgorithm;
}
