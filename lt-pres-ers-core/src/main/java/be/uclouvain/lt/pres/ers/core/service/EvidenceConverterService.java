package be.uclouvain.lt.pres.ers.core.service;

import be.uclouvain.lt.pres.ers.core.XMLObjects.EvidenceRecordType;
import be.uclouvain.lt.pres.ers.core.persistence.model.POID;
import be.uclouvain.lt.pres.ers.core.persistence.model.dto.EvidenceRecordDto;

import java.util.List;
import java.util.UUID;

public interface EvidenceConverterService {
    public EvidenceRecordType toEvidenceRecordType(List<EvidenceRecordDto> evidenceRecordDtoList, UUID poid);

    public EvidenceRecordType toEvidenceRecordType(List<EvidenceRecordDto> evidenceRecordDtoList, POID poidObj);
}
