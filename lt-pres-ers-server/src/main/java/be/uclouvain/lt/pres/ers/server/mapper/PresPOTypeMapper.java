package be.uclouvain.lt.pres.ers.server.mapper;

import be.uclouvain.lt.pres.ers.model.DigestListDto;
import be.uclouvain.lt.pres.ers.model.PODto;
import be.uclouvain.lt.pres.ers.server.model.PresPOType;
import be.uclouvain.lt.pres.ers.server.model.PresPOTypeBinaryData;
import be.uclouvain.lt.pres.ers.server.model.PresPOTypeXmlData;
import com.fasterxml.jackson.databind.DeserializationFeature;
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

    @Mapping(target = "clientId", source = "id") //from String to URI
    @Mapping(target = "formatId", source = "formatId") //from String to String (this actually 'belongs' to the customer's structure
    @Mapping(target = "binaryValue", expression = "java(binaryOrXML(presPOType.getBinaryData(), presPOType.getXmlData()))") //This is the (b64 encoded ?) string we get from inside the PO TODO adapt this if we acutally keep the xml data
    @Mapping(target = "digestList", expression = "java(mapToDigestList(presPOType))") //from String to DigestListDto
    //this is the DigestList Java object you need at the interface
            //we can create it here and you can examine it at the interface
    @Mapping(target = "relatedObjects", source = "relObj")
    //TODO add other fields here later

    PODto toPODto(PresPOType presPOType) throws IllegalArgumentException, IOException, URISyntaxException;

    default URI toURI(final String string) { //from String to URI
        return URI.create(string);
    }

    //from PresPOTypeXmlData to String
    default String toString(PresPOTypeXmlData presPOTypeXmlData) {
        return presPOTypeXmlData.getB64Content();
    }

    default String binaryOrXML(PresPOTypeBinaryData binary, PresPOTypeXmlData xml) throws IllegalArgumentException {
        if(binary == null && xml == null) {
            throw new IllegalArgumentException("No binary or xml data");
        } else if(binary != null && xml != null) {
            throw new IllegalArgumentException("Ambiguity : both binary and xml data");
        } else if(binary != null){
            return binary.getValue();
        } else {
            return xml.getB64Content();
        }
    }

    //from String to DigestListDTO
    // TODO : Do we keep the logic here or do we add a POJO and handle the logic elsewhere ?
    default DigestListDto mapToDigestList(PresPOType presPOType) throws IllegalArgumentException, IOException {
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
            String b64Content = presPOType.getBinaryData().getValue();
            if(b64Content == null) {
                throw new IllegalArgumentException("Missing value in BinaryData.");
            }
            // Decode base 64
            try {
                byte[] decodedContent = Base64.getDecoder().decode(b64Content);
                // parse JSON
                JsonMapper jsonMapper = new JsonMapper();
                jsonMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
                dld = jsonMapper.readValue(decodedContent, DigestListDto.class);
            } catch(IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid base64 encoding.");
            } catch(Exception e) {
                throw new IllegalArgumentException("Invalid JSON syntax, do not forget the root braces, the top level element must be named 'pres-DigestListType'.");
            }
        }

        URI digAlg = dld.getDigestMethod();
        // TODO : verify more algos ! only SHA156 here ! RFC 3061
        if(!Objects.equals(digAlg, DigestAlgEnum.SHA256.getUri())) {
            throw new IllegalArgumentException("Invalid or unsupported digest algorithm : "+digAlg);
        }
        // TODO : Check digVal according to digAlg ?

        return dld;
    }



}