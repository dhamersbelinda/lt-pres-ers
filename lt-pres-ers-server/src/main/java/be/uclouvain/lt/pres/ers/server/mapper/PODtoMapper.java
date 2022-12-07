/*package be.uclouvain.lt.pres.ers.server.mapper;

import be.uclouvain.lt.pres.ers.model.EvidenceDto;
import be.uclouvain.lt.pres.ers.server.model.PresEvidenceType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.net.URI;
import java.util.List;

@Mapper
public interface PODtoMapper {

    //ignore = true is for attributes that are not going to be used i guess
    //how to implement the choice between binary and xml ?
    //how to show optionality ?
    //custom implementation of xml data type or just represent (as string) ?
    // how are lists implemented ?

    @Mapping(target = "id", source = "evidenceIdentifier")
    @Mapping(target = "mimeType", source = "mimeType")
    @Mapping(target = "pronomId", source = "pronomPUID")
    //@Mapping(target = "relObj", source = "relatedObjects")
    @Mapping(target = "poId", source = "poIdentifier")
    @Mapping(target = "verId", source = "versionIdentifier")
    PresEvidenceType toPresEvidenceType(EvidenceDto dto);


    default String toString(final URI uri) {
        return uri.toString();
    }

    default List<String> toStringList(final URI uri) {
        return List.of(uri.toString());
    }


}
*/