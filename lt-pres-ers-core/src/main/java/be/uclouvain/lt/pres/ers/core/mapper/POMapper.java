package be.uclouvain.lt.pres.ers.core.mapper;

import be.uclouvain.lt.pres.ers.core.persistence.model.Digest;
import be.uclouvain.lt.pres.ers.core.persistence.model.DigestList;
import be.uclouvain.lt.pres.ers.core.persistence.model.PO;
import be.uclouvain.lt.pres.ers.model.DigestListDto;
import be.uclouvain.lt.pres.ers.model.PODto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface POMapper {

    @Mapping(target = "id", source = "uid")
    //@Mapping(target = "binaryValue", source = "value")
    @Mapping(target = "formatId", source = "formatId")
    @Mapping(target = "digestList", source = "digestList")
    @Mapping(target = "relatedObjects", ignore = true)
    PODto toDto(PO po);

    @Mapping(target = "digestMethod", source = "digestMethod")
    @Mapping(target = "digests", source = "digests") // from Set<Digest> to List<String>
    DigestListDto toDigestDto(DigestList digestList);

    default List<String> fromDigestSetToStringList(Set<Digest> digestSet) {
        List<String> digests = digestSet.stream().map((d) -> {
            return d.getDigest();
        }).collect(Collectors.toList());
        return digests;
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
