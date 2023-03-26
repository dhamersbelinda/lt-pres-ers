package be.uclouvain.lt.pres.ers.core.service.impl;

import be.uclouvain.lt.pres.ers.core.XMLObjects.*;
import be.uclouvain.lt.pres.ers.core.persistence.model.Digest;
import be.uclouvain.lt.pres.ers.core.persistence.model.POID;
import be.uclouvain.lt.pres.ers.core.persistence.model.dto.EvidenceRecordDto;
import be.uclouvain.lt.pres.ers.core.persistence.repository.POIDRepository;
import be.uclouvain.lt.pres.ers.core.service.EvidenceConverterService;
import be.uclouvain.lt.pres.ers.utils.BinaryOrderComparator;
import eu.europa.esig.dss.enumerations.TimestampType;
import eu.europa.esig.dss.validation.timestamp.TimestampToken;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.tsp.TSPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.util.*;

//TODO which ones here are needed ?
@Service
@Validated
@Transactional // TODO to avoid maybe
@AllArgsConstructor
public class EvidenceRecordDTOToEvidenceRecordType implements EvidenceConverterService {

    private final POIDRepository poidRepository;
    public static final String ALGO_ID_C14N_OMIT_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";

    @Override
    public EvidenceRecordType toEvidenceRecordType(List<EvidenceRecordDto> evidenceRecordDtoList, UUID poid) {
        Optional<POID> optPoidObj = poidRepository.findById(poid);

        if (optPoidObj.isPresent()) {
            POID poidObj = optPoidObj.get();
//            System.out.println(poidObj);
//            System.out.println(poidObj.getPo());
//            System.out.println(poidObj.getPo().getDigestList());
//            System.out.println(poidObj.getPo().getDigestList().getDigests());
            // TODO change to "findById" and check for nulls ect if not found
            return EvidenceRecordType.build(evidenceRecordDtoList, poidObj);
        }
        return null;
    }
}
