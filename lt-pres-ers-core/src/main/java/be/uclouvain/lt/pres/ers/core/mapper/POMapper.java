package be.uclouvain.lt.pres.ers.core.mapper;

import be.uclouvain.lt.pres.ers.core.persistence.model.PO;
import be.uclouvain.lt.pres.ers.model.PODto;
import org.mapstruct.Mapper;

@Mapper
public interface POMapper {

    PODto toDto(PO po);

}
