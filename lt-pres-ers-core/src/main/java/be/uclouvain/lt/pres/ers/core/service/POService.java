package be.uclouvain.lt.pres.ers.core.service;

import be.uclouvain.lt.pres.ers.core.exception.POInsertionException;
import be.uclouvain.lt.pres.ers.core.exception.PONotFoundException;
import be.uclouvain.lt.pres.ers.core.exception.ProfileNotFoundException;
import be.uclouvain.lt.pres.ers.core.persistence.model.PO;
import be.uclouvain.lt.pres.ers.core.persistence.model.dto.EvidenceRecordDto;
import be.uclouvain.lt.pres.ers.model.PODto;
import be.uclouvain.lt.pres.ers.model.PreservePORequestDto;
import be.uclouvain.lt.pres.ers.model.ProfileDto;
import be.uclouvain.lt.pres.ers.model.ProfileStatus;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Service that allows to insert Preservation Objects (POs) and retrieve the evidence of a PO.
 */

public interface POService {

    //TODO URI as identifier ?
    PODto getPO(@NotNull UUID identifier) throws PONotFoundException;

    //TODO String returned ?
    // TODO PO or Dto as arg ?
    //TODO implem exceptions
    UUID insertPOs(@NotNull PreservePORequestDto requestDto) throws POInsertionException;

    public List<EvidenceRecordDto> getERFromPOID(UUID poid);

}
