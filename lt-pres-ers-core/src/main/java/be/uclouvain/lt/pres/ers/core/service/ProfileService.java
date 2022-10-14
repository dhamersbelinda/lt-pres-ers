package be.uclouvain.lt.pres.ers.core.service;

import java.net.URI;
import java.util.List;

import javax.validation.constraints.NotNull;

import be.uclouvain.lt.pres.ers.core.exception.ProfileNotFoundException;
import be.uclouvain.lt.pres.ers.model.ProfileDto;
import be.uclouvain.lt.pres.ers.model.ProfileStatus;

/**
 * Service that allows to retrieve (active and inactive) profiles.
 */
public interface ProfileService {

    /**
     * Retrieves profiles whose validity dates correspond to given
     * <code>status</code>.
     *
     * @param status Target status of profiles to retrieve.
     * @return Profiles whose validity dates correspond to given
     *         <code>status</code>.
     */
    List<ProfileDto> getProfiles(@NotNull ProfileStatus status);

    /**
     * Retrieves {@link ProfileDto profile} with identifier <code>identifier</code>.
     *
     * @param identifier The identifier of the target profile.
     * @return {@link ProfileDto profile} with identifier <code>identifier</code>.
     * @throws ProfileNotFoundException If there is no profile with identifier
     *                                  <code>identifier</code>.
     */
    ProfileDto getProfile(@NotNull URI identifier) throws ProfileNotFoundException;

}
