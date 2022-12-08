package be.uclouvain.lt.pres.ers.server.mapper;

import be.uclouvain.lt.pres.ers.model.DigestListDto;
import be.uclouvain.lt.pres.ers.model.PODto;
import be.uclouvain.lt.pres.ers.server.model.PresPOType;
import be.uclouvain.lt.pres.ers.server.model.PresPOTypeXmlData;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Objects;

@Mapper
public interface PresPOTypeMapper {

    @Mapping(target = "uid", source = "id") //from String to URI
    @Mapping(target = "formatId", source = "formatId") //from String to URI
    @Mapping(target = "value", source = "xmlData") //from PresPOTypeXmlData to String
    //This is the (b64 encoded ?) string we get from inside the PO
    @Mapping(target = "digestList", expression = "java(mapToDigestList(presPOType))")
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
    // TODO : Do we keep the logic here or do we add a POJO and handle the logic elsewhere ?
    default DigestListDto mapToDigestList(PresPOType presPOType) throws IllegalArgumentException, IOException, URISyntaxException {
        DigestListDto dld;

        if(presPOType.getXmlData() == null && presPOType.getBinaryData() == null){
            // TODO what if : both are present but one has 'empty' data ? Check deeper ?
            throw new IllegalArgumentException("Missing XML or binary data.");
        } else if(presPOType.getXmlData() != null && presPOType.getBinaryData() != null) {
            // TODO : ask if both present what do we do ?
            throw new IllegalArgumentException("Ambiguity : both XML and binary data present.");
        }

        if(presPOType.getXmlData() != null) {
            // verify PresPOTypeXmlData object
            if(presPOType.getXmlData().getB64Content() == null) {
                throw new IllegalArgumentException("Missing base64 content in XMLData.");
            }
            String b64Content = presPOType.getXmlData().getB64Content();
            // Decode base 64
            // TODO is XML data encoded in base 64 ? Here we consider that yes
            byte[] decodedContent = Base64.getDecoder().decode(b64Content);
            // parse XML
            XmlMapper xmlMapper = new XmlMapper();
            dld = xmlMapper.readValue(decodedContent, DigestListDto.class);

        } else {
            // parse JSON from binary
            // verify PresPOTypeBinaryData object
            if(presPOType.getBinaryData().getValue() == null) {
                throw new IllegalArgumentException("Missing value  in BinaryData.");
            }
            String b64Content = presPOType.getBinaryData().getValue();
            // Decode base 64
            // TODO is XML data encoded in base 64 ? Here we consider that yes
            byte[] decodedContent = Base64.getDecoder().decode(b64Content);
            // parse JSON

            JsonMapper jsonMapper = new JsonMapper();
            dld = jsonMapper.readValue(decodedContent, DigestListDto.class);
        }

        URI digAlg = dld.getDigestMethod();
        // TODO : verify more algos ! only SHA156 here !
        if(!Objects.equals(digAlg, new URI("urn:oid:2.16.840.1.101.3.4.2.1"))) {
            throw new IllegalArgumentException("Invalid or unsupported digest algorithm : "+digAlg);
        }
        // TODO : Check digVal according to digAlg !

        return dld;
    }



}