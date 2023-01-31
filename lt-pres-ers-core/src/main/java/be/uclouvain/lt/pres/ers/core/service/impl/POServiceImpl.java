package be.uclouvain.lt.pres.ers.core.service.impl;

import be.uclouvain.lt.pres.ers.core.exception.POInsertionException;
import be.uclouvain.lt.pres.ers.core.exception.PONotFoundException;
import be.uclouvain.lt.pres.ers.core.mapper.PODtoMapperCore;
import be.uclouvain.lt.pres.ers.core.mapper.POMapper;
import be.uclouvain.lt.pres.ers.core.persistence.model.PO;
import be.uclouvain.lt.pres.ers.core.persistence.model.PreservePORequest;
import be.uclouvain.lt.pres.ers.core.persistence.repository.PORepository;
import be.uclouvain.lt.pres.ers.core.service.POService;
import be.uclouvain.lt.pres.ers.model.PODto;
import be.uclouvain.lt.pres.ers.model.PreservePORequestDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
    public String insertPOs(PreservePORequestDto requestDto) throws POInsertionException {
        //map into a request object + save it
        AtomicReference<String> toReturn = new AtomicReference<>();
        PreservePORequest request = this.dtoMapper.toPreservePORequest(requestDto);
        PreservePORequest req = this.repository.save(request);
        toReturn.set(req.getId().toString());



        //add received POID to temp table



        return toReturn.get();
    }



}
