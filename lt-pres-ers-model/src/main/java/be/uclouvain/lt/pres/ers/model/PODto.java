package be.uclouvain.lt.pres.ers.model;

import lombok.Data;

import java.net.URI;
import java.util.List;

@Data
public class PODto {

    private URI evidenceIdentifier;
    private URI mimeType;
    private URI pronomPUID;
    private List<RelatedObjectDto> relatedObjects;
    //RelatedObject is just supposed to a string (that identifies related objects i guess ?)
    private URI poIdentifier;
    private URI versionIdentifier;
}
