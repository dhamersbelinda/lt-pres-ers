package be.uclouvain.lt.pres.ers.core.mapper;

import be.uclouvain.lt.pres.ers.core.persistence.model.*;
import be.uclouvain.lt.pres.ers.core.persistence.model.comparator.DigestComparator;
import be.uclouvain.lt.pres.ers.core.persistence.repository.ClientRepository;
import be.uclouvain.lt.pres.ers.model.DigestListDto;
import be.uclouvain.lt.pres.ers.model.PODto;
import be.uclouvain.lt.pres.ers.model.PreservePORequestDto;
import be.uclouvain.lt.pres.ers.utils.ByteUtils;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.spi.DSSUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        uses = ProfileDtoMapperCore.class)
public abstract class PODtoMapperCore {
    //ProfileRepository repository;
    @Autowired
    protected ClientRepository clientRepository;

    @Mapping(target = "profile", source = "profileDto")
    //TODO handle this a bit better someday hehe
    //hope this is the right getter
    @Mapping(target = "po", expression = "java(toPO(requestDto.getPoDtos().get(0)))")
    @Mapping(target = "digestMethod", source = "digestMethod")
    @Mapping(target = "node", ignore = true) //TODO remove later
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "digestValue", ignore = true)
    public abstract POID toPreservePORequest(PreservePORequestDto requestDto);

    Client map(long clientId) {
        Optional<Client> c = clientRepository.findById(clientId);
        if(c.isEmpty()){
            throw new IllegalArgumentException("Unknown client : "+clientId);
        }
        return c.get();
    }

    @Mapping(target = "uid", source = "id")
    @Mapping(target = "poid", ignore = true)
    abstract PO toPO(PODto poDto);

    Set<RelatedObject> fromStringToRelObjSet(List<String> relatedObjects) {
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
    abstract DigestList toDigestList(DigestListDto digestListDto);

    URI map(DigestAlgorithm alg) throws URISyntaxException {
        if(alg == null) throw new IllegalArgumentException("Null digAlg");
        return new URI(alg.getOid());
    }

    List<Digest> map(List<byte[]> digests) {
        List<Digest> result = new ArrayList<>(digests.size());
        Digest d;
        for (byte[] digest : digests) {
            d = new Digest();
            d.setDigest(digest);
            result.add(d);
        }
        return result;
    }

//    default Set<Digest> fromDigestListToDigestSet(List<String> digestList) {
//
//        //TODO throu exception of dl not presetn
//        Set<Digest> set = digestList.stream().map((digest) -> {
//            Digest d = new Digest();
//            d.setDigest(digest);
//            return d;
//        }).collect(Collectors.toSet());
//        return set;
//    }

    @AfterMapping
    void setPoid(@MappingTarget POID poid) {
        poid.getPo().setPoid(poid);
    }

    @AfterMapping
    void setDigestListId(@MappingTarget DigestList dl) {
        // TODO : check if null/empty ? if so error ?
        //if (dl == null) throw new DigestListEmptyException();
        for (Digest digest : dl.getDigests()) {
            digest.setDigestList(dl);
        }
    }

    @AfterMapping
    void setPOObject(@MappingTarget PO po) {
        DigestList dl = po.getDigestList();
        dl.setPo(po);
        if (po.getRelatedObjects() != null) {
            po.getRelatedObjects().forEach((elem) -> elem.setPo(po));
        }
    }

    @AfterMapping
    void setPOIDDigestValue(@MappingTarget POID poid) {
        // TODO : check if null/empty ? if so error ?
        //if (dl == null) throw new DigestListEmptyException();
        List<Digest> list = poid.getPo().getDigestList().getDigests();
        byte[] concat;
        if(list.size() == 1) {
            concat = list.get(0).getDigest();
            poid.setDigestValue(concat);
            return;
        } else {
            list.sort(new DigestComparator());
            List<byte[]> byteArrayList = new ArrayList<>(list.size());
            for (Digest digest : list) {
                byteArrayList.add(digest.getDigest());
            }
            concat = ByteUtils.concat(byteArrayList);
        }
        DigestAlgorithm alg = DigestAlgorithm.forOID(poid.getDigestMethod());
        poid.setDigestValue(DSSUtils.digest(alg, concat));
    }
}
