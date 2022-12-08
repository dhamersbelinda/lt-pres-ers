package be.uclouvain.lt.pres.ers.server.delegates;

import java.net.URI;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SubDOFormatID {
    CAdES(URI.create("http://uri.etsi.org/ades/CAdES")),
    XAdES(URI.create("http://uri.etsi.org/ades/XAdES")),
    PAdES(URI.create("http://uri.etsi.org/ades/PAdES")),
    ASiC_E(URI.create("http://uri.etsi.org/ades/ASiC/type/ASiC-E")),
    XAIP(URI.create("http://www.bsi.bund.de/tr-esor/xaip/1.2")),
    DigestList(URI.create("http://uri.etsi.org/19512/format/DigestList"));
    // TODO Add other elements from 119 512 Annex A ?
    @Getter
    private final URI uri;

}
