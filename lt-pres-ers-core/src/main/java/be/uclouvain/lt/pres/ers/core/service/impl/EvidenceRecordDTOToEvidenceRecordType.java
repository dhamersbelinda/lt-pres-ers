package be.uclouvain.lt.pres.ers.core.service.impl;

import be.uclouvain.lt.pres.ers.core.XMLObjects.*;
import be.uclouvain.lt.pres.ers.core.exception.PONotFoundException;
import be.uclouvain.lt.pres.ers.core.persistence.model.POID;
import be.uclouvain.lt.pres.ers.core.persistence.model.dto.EvidenceRecordDto;
import be.uclouvain.lt.pres.ers.core.persistence.repository.POIDRepository;
import be.uclouvain.lt.pres.ers.core.service.EvidenceConverterService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import java.util.*;

//TODO which ones here are needed ?
@Service
@Validated
@Transactional // TODO to avoid maybe
@AllArgsConstructor
public class EvidenceRecordDTOToEvidenceRecordType implements EvidenceConverterService {

    private final POIDRepository poidRepository;

    @Override
    public EvidenceRecordType toEvidenceRecordType(List<EvidenceRecordDto> evidenceRecordDtoList, UUID poid) {
        Optional<POID> optPoidObj = poidRepository.findById(poid);

        if (optPoidObj.isPresent()) {
            POID poidObj = optPoidObj.get();
            // TODO change to "findById" and check for nulls ect if not found
            return EvidenceRecordType.build(evidenceRecordDtoList, poidObj);
        }
        throw new PONotFoundException();
    }
}
