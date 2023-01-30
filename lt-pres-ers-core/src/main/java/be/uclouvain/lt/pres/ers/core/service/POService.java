package be.uclouvain.lt.pres.ers.core.service;

import be.uclouvain.lt.pres.ers.core.exception.POInsertionException;
import be.uclouvain.lt.pres.ers.core.exception.PONotFoundException;
import be.uclouvain.lt.pres.ers.core.exception.ProfileNotFoundException;
import be.uclouvain.lt.pres.ers.core.persistence.model.PO;
import be.uclouvain.lt.pres.ers.model.PODto;
import be.uclouvain.lt.pres.ers.model.PreservePORequestDto;
import be.uclouvain.lt.pres.ers.model.ProfileDto;
import be.uclouvain.lt.pres.ers.model.ProfileStatus;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

/**
 * Service that allows to retrieve (active and inactive) profiles.
 */

public interface POService {

    //TODO URI as identifier ?
    PODto getPO(@NotNull long identifier) throws PONotFoundException;

    //TODO String returned ?
    // TODO PO or Dto as arg ?
    //TODO implem exceptions
    String insertPOs(@NotNull PreservePORequestDto requestDto) throws POInsertionException;

}
