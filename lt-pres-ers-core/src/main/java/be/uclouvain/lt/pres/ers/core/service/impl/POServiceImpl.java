/*package be.uclouvain.lt.pres.ers.core.service.impl;

import be.uclouvain.lt.pres.ers.core.exception.POInsertionException;
import be.uclouvain.lt.pres.ers.core.exception.ProfileNotFoundException;
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

    /*
    @Override
    public List<ProfileDto> getProfiles(final ProfileStatus status) {
        switch (status) {
        case ALL:
            try (final Stream<Profile> stream = this.repository.streamAllBy()) {
                return stream.map(this.mapper::toDto).collect(Collectors.toList());
            }
        case ACTIVE:
            try (final Stream<Profile> stream = this.repository
                    .findByValidUntilIsNullOrValidUntilAfter(OffsetDateTime.now())) {
                return stream.map(this.mapper::toDto).collect(Collectors.toList());
            }
        case INACTIVE:
            try (final Stream<Profile> stream = this.repository
                    .findByValidUntilIsNotNullAndValidUntilBefore(OffsetDateTime.now())) {
                return stream.map(this.mapper::toDto).collect(Collectors.toList());
            }
        default:
            throw new IllegalArgumentException("Unhandled value: " + status);
        }
    }
     */
/*
    @Override
    public PODto getPO(URI identifier) throws ProfileNotFoundException {
        return null;
    }

    @Override
    public String insertPO(PO po) throws POInsertionException {
        return null;
    }
    */

    /*
    @Override
    public ProfileDto getProfile(final URI identifier) throws ProfileNotFoundException {
        return this.mapper.toDto(this.repository.findByProfileIdentifier(identifier)
                .orElseThrow(() -> new ProfileNotFoundException("There is no profile with identifier " + identifier)));
    }


}
*/