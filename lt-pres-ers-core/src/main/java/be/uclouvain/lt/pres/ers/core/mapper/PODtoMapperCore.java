package be.uclouvain.lt.pres.ers.core.mapper;

import be.uclouvain.lt.pres.ers.core.persistence.model.*;
import be.uclouvain.lt.pres.ers.core.persistence.repository.ProfileRepository;
import be.uclouvain.lt.pres.ers.model.DigestListDto;
import be.uclouvain.lt.pres.ers.model.PODto;
import be.uclouvain.lt.pres.ers.model.PreservePORequestDto;
import be.uclouvain.lt.pres.ers.model.ProfileDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(uses = ProfileDtoMapperCore.class)
public interface PODtoMapperCore {
    //ProfileRepository repository;

    @Mapping(target = "profile", source = "profileDto")
    //TODO handle this a bit better someday hehe
    //hope this is the right getter
    @Mapping(target = "po", expression = "java(toPO(requestDto.getPoDtos().get(0)))")
    @Mapping(target = "node", ignore = true) //TODO remove later
    PreservePORequest toPreservePORequest(PreservePORequestDto requestDto);

    /*
    default Profile toProfile(ProfileDto profileDto) {
        URI id = profileDto.getProfileIdentifier();

    }
     */




    @Mapping(target = "uid", source = "id")
    @Mapping(target = "req", ignore = true)
    PO toPO(PODto poDto);

    default Set<RelatedObject> fromStringToRelObjSet(List<String> relatedObjects) {
        if (relatedObjects == null) return null;
        return relatedObjects.stream().map((elem) -> {
            RelatedObject relObj = new RelatedObject();
            relObj.setRelatedObject(elem);
            return relObj;
        }).collect(Collectors.toSet());
    }

    @Mapping(target = "digestMethod", source = "digestMethod")
    @Mapping(target = "digests", source = "digests") // from List<String> to Set<Digest>
    @Mapping(target = "po", ignore = true) //TODO check if this is really correct
    //don't want to set null
    DigestList toDigestList(DigestListDto digestListDto);

    default Set<Digest> fromDigestListToDigestSet(List<String> digestList) {
        //TODO throu exception of dl not presetn
        Set<Digest> set = digestList.stream().map((digest) -> {
            Digest d = new Digest();
            d.setDigest(digest);
            return d;
        }).collect(Collectors.toSet());
        return set;
    }

    @AfterMapping
    default void setReqId(@MappingTarget PreservePORequest req) {
        req.getPo().setReq(req);
    }

    @AfterMapping
    default void setDigestListId(@MappingTarget DigestList dl) {
        // TODO : check if null/empty ? if so error ?
        //if (dl == null) throw new DigestListEmptyException();
        for (Digest digest : dl.getDigests()) {
            digest.setDigestList(dl);
        }
    }



    @AfterMapping
    default void setPOObject(@MappingTarget PO po) {
        if (po.getRelatedObjects() != null) {
            DigestList dl = po.getDigestList();
            dl.setPo(po);
            po.getRelatedObjects().forEach((elem) -> elem.setPo(po));
        }
    }

}
