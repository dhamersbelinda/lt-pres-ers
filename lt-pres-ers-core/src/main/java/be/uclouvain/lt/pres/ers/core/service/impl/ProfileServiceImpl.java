package be.uclouvain.lt.pres.ers.core.service.impl;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import be.uclouvain.lt.pres.ers.core.exception.ProfileNotFoundException;
import be.uclouvain.lt.pres.ers.core.mapper.ProfileMapper;
import be.uclouvain.lt.pres.ers.core.persistence.model.Profile;
import be.uclouvain.lt.pres.ers.core.persistence.repository.ProfileRepository;
import be.uclouvain.lt.pres.ers.core.service.ProfileService;
import be.uclouvain.lt.pres.ers.model.ProfileDto;
import be.uclouvain.lt.pres.ers.model.ProfileStatus;
import lombok.AllArgsConstructor;

@Service
@Validated
@Transactional
@AllArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository repository;

    private final ProfileMapper mapper;

    @Override
    public Stream<ProfileDto> getProfiles(final ProfileStatus status) {
        switch (status) {
        case ALL:
            try (final Stream<Profile> stream = this.repository.streamAll()) {
                return stream.map(this.mapper::toDto);
            }
        case ACTIVE:
            try (final Stream<Profile> stream = this.repository
                    .findByValidUntilIsNullOrValidUntilAfter(OffsetDateTime.now())) {
                return stream.map(this.mapper::toDto);
            }
        case INACTIVE:
            try (final Stream<Profile> stream = this.repository
                    .findByValidUntilIsNotNullAndValidUntilBefore(OffsetDateTime.now())) {
                return stream.map(this.mapper::toDto);
            }
        default:
            throw new IllegalArgumentException("Unhandled value: " + status);
        }
    }

    @Override
    public ProfileDto getProfile(final URI identifier) throws ProfileNotFoundException {
        return this.mapper.toDto(this.repository.findById(identifier)
                .orElseThrow(() -> new ProfileNotFoundException("There is no profile with identifier" + identifier)));
    }

}
