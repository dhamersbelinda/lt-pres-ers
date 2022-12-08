package be.uclouvain.lt.pres.ers.model;

import lombok.Data;

import java.net.URI;
import java.util.List;

@Data
public class PODto {

    private String value; //xmlData at the moment (don't know about encoding)
    private URI formatId;
    private URI id; //is this the POID ?

    /*
    private URI mimeType;
    private URI pronomPUID;
    private List<RelatedObjectDto> relatedObjects;
     */
    //These fields might be needed later
}
