package be.uclouvain.lt.pres.ers.core.service.impl;

import be.uclouvain.lt.pres.ers.core.exception.POInsertionException;
import be.uclouvain.lt.pres.ers.core.exception.PONotFoundException;
import be.uclouvain.lt.pres.ers.core.exception.ProfileNotFoundException;
import be.uclouvain.lt.pres.ers.core.mapper.PODtoMapperCore;
import be.uclouvain.lt.pres.ers.core.mapper.POMapper;
import be.uclouvain.lt.pres.ers.core.persistence.model.*;
import be.uclouvain.lt.pres.ers.core.persistence.repository.POIDRepository;
import be.uclouvain.lt.pres.ers.core.persistence.repository.ProfileRepository;
import be.uclouvain.lt.pres.ers.core.persistence.repository.TemporaryRepository;
import be.uclouvain.lt.pres.ers.core.service.POService;
import be.uclouvain.lt.pres.ers.model.PODto;
import be.uclouvain.lt.pres.ers.model.PreservePORequestDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

@Service
@Validated
@Transactional
@AllArgsConstructor
public class POServiceImpl implements POService {

    private final POIDRepository poidRepository;
    private final ProfileRepository profileRepository;
    private final TemporaryRepository temporaryRepository;

    private final POMapper mapper;
    private final PODtoMapperCore dtoMapper;


    @Override
    public PODto getPO(UUID identifier) throws PONotFoundException {
        return this.mapper.toDto(this.poidRepository.findById(identifier)
                .orElseThrow(() -> new PONotFoundException("There is no preservation object with identifier " + identifier)).getPo());
    }

    @Override
    public String insertPOs(PreservePORequestDto requestDto) throws POInsertionException {
        //map into a request object + save it
        AtomicReference<String> toReturn = new AtomicReference<>();
        POID request = this.dtoMapper.toPreservePORequest(requestDto);
        // TODO this is a duplicated SELECT call with the check in server ...
        Profile profile = this.profileRepository.findByProfileIdentifier(request.getProfile().getProfileIdentifier())
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found"));
        request.setProfile(profile);
        POID req = this.poidRepository.save(request);
        toReturn.set(req.getId().toString());

        //add received POID to temp table
        //for each digest in each digestlist
        //TODO modif here if going to handle several POs by request
        Iterator<Digest> iterator = request.getPo().getDigestList().getDigests().iterator();
        IntStream.range(0, request.getPo().getDigestList().getDigests().size()).forEach((index) -> {
            Digest digest = iterator.next();
            TemporaryRecord temp = new TemporaryRecord();
            temp.setPoid(req);
            temp.setDigNum(index);
            temp.setDigestList(req.getPo().getDigestList());
            temp.setDigest(digest);
            temp.setClientId(req.getClientId());
            this.temporaryRepository.save(temp);
        });

        return toReturn.get();
    }



}
