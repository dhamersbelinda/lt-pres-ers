package be.uclouvain.lt.pres.ers.model;

import lombok.Data;

import java.net.URI;
import java.util.List;

@Data
public class PODto {

//    private String value; //xmlData at the moment (don't know about encoding)
    //this is the only non-nullable field in general i think
    private URI formatId;
    //has to be non-null in our implem probably (but that is not the case in general)
    private String binaryValue; // Holds the binary data
    private String clientId; // Belongs to client so don't modify

    private List<String> relatedObjects; // Belongs to client so don't modify

    private DigestListDto digestList; // TODO support more than digestLists

    /*
    private URI mimeType;
    private URI pronomPUID;
    private List<RelatedObjectDto> relatedObjects;
     */
    //These fields might be needed later
}
