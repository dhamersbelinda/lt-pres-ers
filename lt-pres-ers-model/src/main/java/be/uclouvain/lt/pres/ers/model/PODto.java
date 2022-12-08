package be.uclouvain.lt.pres.ers.model;

import lombok.Data;

import java.net.URI;
import java.util.List;

@Data
public class PODto {

    private String value; //xmlData at the moment (don't know about encoding)
    //this is the only non-nullable field in general i think
    private URI formatId;
    //has to be non-null in our implem probably (but that is not the case in general)
    private URI uid; //is this the POID ? The POID refers to the set of SubDOS
    //renamed id into uid so as not to mix with id in model
    //should be nullable or not ? -> at the moment yes because null at submission probably
    //POID will be given by db

    /*
    private URI mimeType;
    private URI pronomPUID;
    private List<RelatedObjectDto> relatedObjects;
     */
    //These fields might be needed later
}
