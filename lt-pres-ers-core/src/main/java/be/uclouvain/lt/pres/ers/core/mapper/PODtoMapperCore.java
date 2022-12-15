package be.uclouvain.lt.pres.ers.core.mapper;

import be.uclouvain.lt.pres.ers.core.persistence.model.Digest;
import be.uclouvain.lt.pres.ers.core.persistence.model.DigestList;
import be.uclouvain.lt.pres.ers.core.persistence.model.PO;
import be.uclouvain.lt.pres.ers.model.DigestListDto;
import be.uclouvain.lt.pres.ers.model.PODto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface PODtoMapperCore {

    @Mapping(target = "uid", source = "clientId")
    @Mapping(target = "value", source = "binaryValue")
    //@Mapping(target = "formatId", source = "formatId")
    //@Mapping(target = "digestList", source = "digestList")
    PO toPO(PODto poDto);

    @Mapping(target = "digestMethod", source = "digestMethod")
    @Mapping(target = "digests", source = "digests") // from List<String> to Set<Digest>
    @Mapping(target = "po", ignore = true) //TODO check if this is really correct
    //don't want to set null
    DigestList toDigestList(DigestListDto digestListDto);

    default Set<Digest> fromDigestListToDigestSet(List<String> digestList) {
        Set<Digest> set = digestList.stream().map((digest) -> {
            Digest d = new Digest();
            d.setDigest(digest);
            return d;
        }).collect(Collectors.toSet());
        return set;
    }

    @AfterMapping
    default void setDigestListId(@MappingTarget DigestList dl) {
        for (Digest digest : dl.getDigests()) {
            digest.setDigestList(dl);
        }
    }


    @AfterMapping
    default void setPOObject(@MappingTarget PO po) {
        DigestList dl = po.getDigestList();
        dl.setPo(po);
    }
    /*
    URI uid; -> URI uid
    String value; -> String value
    URI formatId; -> URI formatId
    DigestList digestList; -> DigestListDto digestList
        URI digestMethod;        -> URI digestMethod
        Set<Digest> digests;     -> List<String> digests
            String digest
     */

}
