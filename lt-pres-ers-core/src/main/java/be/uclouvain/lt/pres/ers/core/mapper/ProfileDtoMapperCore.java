package be.uclouvain.lt.pres.ers.core.mapper;

import be.uclouvain.lt.pres.ers.core.persistence.model.*;
import be.uclouvain.lt.pres.ers.model.*;
import org.mapstruct.Mapper;

import org.mapstruct.Mapping;

import javax.persistence.ManyToOne;

@Mapper
public interface ProfileDtoMapperCore {


    Profile toProfile(ProfileDto profileDto);

    // TODO : Set with after mapping ?

    @Mapping(target = "profile", ignore = true) //TODO remove this later
    Operation toOperation(OperationDto operationDto);

    @Mapping(target = "operation", ignore = true) //TODO remove this later
    OperationInput toOperationInput(OperationInputDto operationInputDto);

    @Mapping(target = "operation", ignore = true) //TODO remove this later
    OperationOutput toOperationOutput(OperationOutputDto operationOutputDto);

    @Mapping(target = "parentFormat", ignore = true) //TODO remove this later
    Parameter toParameter(ParameterDto parameterDto);
}