package be.uclouvain.lt.pres.ers.core.mapper;

import org.mapstruct.Mapper;

import be.uclouvain.lt.pres.ers.core.persistence.model.Profile;
import be.uclouvain.lt.pres.ers.model.ProfileDto;

@Mapper
public interface ProfileMapper {

    ProfileDto toDto(Profile profile);

}
