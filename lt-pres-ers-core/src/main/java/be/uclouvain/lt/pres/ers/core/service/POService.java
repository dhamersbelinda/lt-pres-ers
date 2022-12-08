/*package be.uclouvain.lt.pres.ers.core.service;

import be.uclouvain.lt.pres.ers.core.exception.POInsertionException;
import be.uclouvain.lt.pres.ers.core.exception.PONotFoundException;
import be.uclouvain.lt.pres.ers.core.exception.ProfileNotFoundException;
import be.uclouvain.lt.pres.ers.core.persistence.model.PO;
import be.uclouvain.lt.pres.ers.model.PODto;
import be.uclouvain.lt.pres.ers.model.ProfileDto;
import be.uclouvain.lt.pres.ers.model.ProfileStatus;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

/**
 * Service that allows to retrieve (active and inactive) profiles.
 */
/*
public interface POService {

    /**
     * Retrieves profiles whose validity dates correspond to given
     * <code>status</code>.
     *
     * @param status Target status of profiles to retrieve.
     * @return Profiles whose validity dates correspond to given
     *         <code>status</code>.
     */
    //List<ProfileDto> getProfiles(@NotNull ProfileStatus status);

    /**
     * Retrieves {@link ProfileDto profile} with identifier <code>identifier</code>.
     *
     * @param identifier The identifier of the target profile.
     * @return {@link ProfileDto profile} with identifier <code>identifier</code>.
     * @throws ProfileNotFoundException If there is no profile with identifier
     *                                  <code>identifier</code>.
     */
    /*
    //TODO URI as identifier ?
    PODto getPO(@NotNull URI identifier) throws PONotFoundException;

    //TODO String returned ?
    // TODO PO or Dto as arg ?
    //TODO implem exceptions
    String insertPO(@NotNull PO po) throws POInsertionException;

}
*/