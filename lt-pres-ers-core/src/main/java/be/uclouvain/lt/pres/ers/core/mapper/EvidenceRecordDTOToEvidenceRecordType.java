package be.uclouvain.lt.pres.ers.core.mapper;

import be.uclouvain.lt.pres.ers.core.XMLObjects.ArchiveTimeStampSequenceType;
import be.uclouvain.lt.pres.ers.core.XMLObjects.EvidenceRecordType;
import be.uclouvain.lt.pres.ers.core.XMLObjects.HashTreeType;
import be.uclouvain.lt.pres.ers.core.XMLObjects.TimeStampType;
import be.uclouvain.lt.pres.ers.core.persistence.model.dto.EvidenceRecordDto;
import eu.europa.esig.dss.enumerations.TimestampType;
import eu.europa.esig.dss.validation.timestamp.TimestampToken;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.tsp.TSPException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EvidenceRecordDTOToEvidenceRecordType {

    public int BRANCHING_FACTOR = 2;

    public EvidenceRecordType toEvidenceRecordType(List<EvidenceRecordDto> evidenceRecordDtoList) {
        //TODO where digestMethod ?
        //TODO where canonicalizationMethod ?
        //TODO where value ? in ts ?
        //TODO how to find if group or not ? by checking inTreeNums ?

        //TODO du coup la root est incluse juste pour le ts

        //why is inTreeNum long ?


        // while inTreeNum is the same
        //when treeId differs : we can go to the next time stamp
        // or when we reach zero in the inTreeNum
        // when reaching zero : we need to add the timestamp
            // if there is no parent relation then we actually have a hash-tree renewal
            // otherwise it is a timestamp renewal
        // adding the timestamp
            // get the asn form of the timestamptoken in bouncy castle ts ?


        EvidenceRecordType evidenceRecordType = new EvidenceRecordType();
        //no encryptionInformation (yet)
        //no supportingInformationList (yet)
        ArchiveTimeStampSequenceType archiveTimeStampSequenceType = new ArchiveTimeStampSequenceType();

        List<ArchiveTimeStampSequenceType.ArchiveTimeStampChain> archiveTimeStampChains = new ArrayList<>();
        long inTreeNum = evidenceRecordDtoList.get(0).getInTreeNum();

        int chainOrder = 1;
        int hashTreeOrder = 1;

        //do first iteration
        //need to capture BRANCHING_FACTOR elements and not just BRANCHING_FACTOR - 1
        ArchiveTimeStampSequenceType.ArchiveTimeStampChain archiveTimeStampChain = new ArchiveTimeStampSequenceType.ArchiveTimeStampChain();

        HashTreeType hashTreeType = new HashTreeType();

        for (int index = 0; index < evidenceRecordDtoList.size(); index++) {
            EvidenceRecordDto evidenceRecordDto = evidenceRecordDtoList.get(index);

            //set digestMethod
            //set canonicalization method : set fixed object

            //getting the timestamp
            byte[] tsbytes = evidenceRecordDto.getTimestamp().getBytes();// is this the right conversion?
            try {
                //bouncycastle time
                TimestampToken timeStampToken = new TimestampToken(tsbytes, TimestampType.CONTENT_TIMESTAMP);
                timeStampToken.getTimeStamp().getEncoded("DER");

                TimeStampType timeStampType = new TimeStampType();
                TimeStampType.TimeStampToken timestampToken = new TimeStampType.TimeStampToken();
                timestampToken.setType("RFC");

                //timeStampType.setTimeStampToken();
            } catch (TSPException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (CMSException e) {
                e.printStackTrace();
            }
        }

        evidenceRecordType.setArchiveTimeStampSequence(archiveTimeStampSequenceType);
        return evidenceRecordType;
    }
}
