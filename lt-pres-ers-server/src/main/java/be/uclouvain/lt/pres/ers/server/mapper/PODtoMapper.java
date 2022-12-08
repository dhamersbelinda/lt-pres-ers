package be.uclouvain.lt.pres.ers.server.mapper;

import be.uclouvain.lt.pres.ers.model.PODto;
import be.uclouvain.lt.pres.ers.server.model.PresPOType;
import be.uclouvain.lt.pres.ers.server.model.PresPOTypeBinaryData;
import be.uclouvain.lt.pres.ers.server.model.PresPOTypeXmlData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.net.URI;
import java.util.List;

@Mapper
public interface PODtoMapper {

    //ignore = true is for attributes that are not going to be used on the model side i guess
    //how to implement the choice between binary and xml ?
    //how to show optionality ?
    //custom implementation of xml data type or just represent (as string) ?
    //where should the tests on the format take place ?
    //Are we going to separate types for binary and xml and digest in the model ?
    //should we have separate DTO types ?

    //for the moment we keep only xmlData, as a simple string

    @Mapping(target = "binaryData", ignore = true)
    //TODO do we need binary data or xmlData or both ?
    // where does DigestList fit in ?
    @Mapping(target = "xmlData", source = "value")
    //we assume value is a string for now
    @Mapping(target = "formatId", source = "formatId")
    //formatId needs to be specific if it is an evidence
    //What is the formatId when it is a digestList ? -> see A.1.6
    @Mapping(target = "mimeType", ignore  = true)
    //since the implem of f2 requires formatId, we don't need mimeType
    //we will only handle digestlists and evidences, which have their specific formatIds
    @Mapping(target = "pronomId", ignore = true)
    //TODO maybe later if we need additional classification info we might need pronomId
    @Mapping(target = "id", source = "uid")
    //For unique identification within larger data structure -> is this the POID we can return ?
    @Mapping(target = "relObj", ignore = true)
    //TODO ignored for now but will be needed later when submitting several POs
    //doesn't seem to have to adhere to a specific format -> list of strings for now
    PresPOType toPresPOType(PODto dto);


    default String toString(final URI uri) {
        return uri.toString();
    }

    /*
    default PresPOTypeBinaryData toPresPOTypeBinaryData() {

    }
     */

    default PresPOTypeXmlData toPresPOTypeXmlData(String xmlData) {
        return new PresPOTypeXmlData().b64Content(xmlData);
    }




}