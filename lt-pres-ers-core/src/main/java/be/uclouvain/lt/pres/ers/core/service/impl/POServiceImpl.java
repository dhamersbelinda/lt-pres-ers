package be.uclouvain.lt.pres.ers.core.service.impl;

import be.uclouvain.lt.pres.ers.core.exception.POInsertionException;
import be.uclouvain.lt.pres.ers.core.exception.PONotFoundException;
import be.uclouvain.lt.pres.ers.core.exception.ProfileNotFoundException;
import be.uclouvain.lt.pres.ers.core.mapper.PODtoMapperCore;
import be.uclouvain.lt.pres.ers.core.mapper.POMapper;
import be.uclouvain.lt.pres.ers.core.mapper.ProfileMapper;
import be.uclouvain.lt.pres.ers.core.persistence.model.PO;
import be.uclouvain.lt.pres.ers.core.persistence.model.Profile;
import be.uclouvain.lt.pres.ers.core.persistence.repository.PORepository;
import be.uclouvain.lt.pres.ers.core.persistence.repository.ProfileRepository;
import be.uclouvain.lt.pres.ers.core.service.POService;
import be.uclouvain.lt.pres.ers.core.service.ProfileService;
import be.uclouvain.lt.pres.ers.model.PODto;
import be.uclouvain.lt.pres.ers.model.ProfileDto;
import be.uclouvain.lt.pres.ers.model.ProfileStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Validated
@Transactional
@AllArgsConstructor
public class POServiceImpl implements POService {

    private final PORepository repository;

    private final POMapper mapper;
    private final PODtoMapperCore dtoMapper;


    @Override
    public PODto getPO(long identifier) throws PONotFoundException {
        return this.mapper.toDto(this.repository.findById(identifier)
                .orElseThrow(() -> new PONotFoundException("There is no preservation object with identifier " + identifier)));
    }

    @Override
    public String insertPOs(List<PODto> poDtoList) throws POInsertionException {
        poDtoList.stream().map(this.dtoMapper::toPO).forEach((po) -> {
            this.repository.save(po);
        });
        return null;
    }



}
