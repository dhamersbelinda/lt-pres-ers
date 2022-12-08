package be.uclouvain.lt.pres.ers.server.mapper;

import be.uclouvain.lt.pres.ers.model.DigestListDto;
import be.uclouvain.lt.pres.ers.model.PODto;
import be.uclouvain.lt.pres.ers.server.model.PresPOType;

import java.net.URI;

public class ApiToDtoMapper {
    PODto toPODto(PresPOType presPOType) {
        PODto poDto = new PODto();
        if (presPOType.getFormatId() != null)
            poDto.setFormatId(URI.create(presPOType.getFormatId()));
        if (presPOType.getId() != null) {
            poDto.setUid(URI.create(presPOType.getId()));
        }
        if (presPOType.getXmlData() != null) {
            poDto.setValue(presPOType.getXmlData().getB64Content());
        }
        //TODO set other fields when necessary
        return poDto;
    }

    /*
    DigestListDto toPODto(DigestList dl) {
        DigestListDto dlDto = new DigestListDto();
        dlDto.setDigestMethod(URI.create(dl.getDigestMethod()));
        dlDto.setDigests(dl.getDigests());
        return dlDto;
    }

     */
}
