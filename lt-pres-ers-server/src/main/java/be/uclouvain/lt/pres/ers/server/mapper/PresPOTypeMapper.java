package be.uclouvain.lt.pres.ers.server.mapper;

import be.uclouvain.lt.pres.ers.model.DigestListDto;
import be.uclouvain.lt.pres.ers.model.PODto;
import be.uclouvain.lt.pres.ers.server.model.PresPOType;
import be.uclouvain.lt.pres.ers.server.model.PresPOTypeXmlData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.net.URI;

@Mapper
public interface PresPOTypeMapper {

    @Mapping(target = "uid", source = "id") //from String to URI
    @Mapping(target = "formatId", source = "formatId") //from String to URI
    @Mapping(target = "value", source = "xmlData") //from PresPOTypeXmlData to String
    //This is the (b64 encoded ?) string we get from inside the PO
    @Mapping(target = "digestList", expression = "java(mapToDigestList(presPOType.getXmlData().getB64Content()))")
    //from String to DigestListDto
    //this is the DigestList Java object you need at the interface
            //we can create it here and you can examine it at the interface
    //TODO add other fields here later
    PODto toPODto(PresPOType presPOType);

    default URI toURI(final String string) { //from String to URI
        return URI.create(string);
    }

    //from PresPOTypeXmlData to String
    default String toString(PresPOTypeXmlData presPOTypeXmlData) {
        return presPOTypeXmlData.getB64Content();
    }

    //from String to DigestListDTO
    default DigestListDto mapToDigestList(String valueString) {
        DigestListDto dld = new DigestListDto();

        //setDigestMethod -> URI
        //setDigests -> List<String>

        //do whatever needs to be done here in terms of conversion

        return dld;
    }

}