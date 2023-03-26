package be.uclouvain.lt.pres.ers.core.mapper;

import be.uclouvain.lt.pres.ers.core.persistence.model.Digest;
import be.uclouvain.lt.pres.ers.core.persistence.model.DigestList;
import be.uclouvain.lt.pres.ers.core.persistence.model.PO;
import be.uclouvain.lt.pres.ers.core.persistence.model.POID;
import be.uclouvain.lt.pres.ers.model.DigestListDto;
import be.uclouvain.lt.pres.ers.model.PODto;
import be.uclouvain.lt.pres.ers.model.PreservePORequestDto;
import be.uclouvain.lt.pres.ers.utils.OidUtils;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.net.URI;
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

    default DigestAlgorithm map(URI digestURI) {
        return DigestAlgorithm.forOID(OidUtils.uidToOidString(digestURI));
    }

    default List<byte[]> map(List<Digest> digests) {
        List<byte[]> result = new ArrayList<>(digests.size());
        for (Digest digest : digests) {
            result.add(digest.getDigest());
        }
        return result;
    }

//    default List<String> fromDigestSetToStringList(Set<Digest> digestSet) {
//        List<String> digests = digestSet.stream().map((d) -> {
//            return d.getDigest();
//        }).collect(Collectors.toList());
//        return digests;
//    }
    /*
    URI uid; -> URI uid
    String value; -> String value
    URI formatId; -> URI formatId
    DigestList digestList; -> DigestListDto digestList
        URI digestMethod;        -> URI digestMethod
        Set<Digest> digests;     -> List<String> digests
            String digest
     */

    /*
    //TODO maybe move this to another mapper file to be cleaner
    //from POID to PreservePORequestDto
    PreservePORequestDto toDto(POID poid);
     */
}
