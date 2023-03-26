package be.uclouvain.lt.pres.ers.core.service.impl;

import be.uclouvain.lt.pres.ers.core.XMLObjects.*;
import be.uclouvain.lt.pres.ers.core.persistence.model.Digest;
import be.uclouvain.lt.pres.ers.core.persistence.model.POID;
import be.uclouvain.lt.pres.ers.core.persistence.model.dto.EvidenceRecordDto;
import be.uclouvain.lt.pres.ers.core.persistence.repository.POIDRepository;
import be.uclouvain.lt.pres.ers.core.service.EvidenceConverterService;
import be.uclouvain.lt.pres.ers.utils.BinaryOrderComparator;
import eu.europa.esig.dss.enumerations.TimestampType;
import eu.europa.esig.dss.validation.timestamp.TimestampToken;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.tsp.TSPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.util.*;

//TODO which ones here are needed ?
@Service
@Validated
@Transactional // TODO to avoid maybe
@AllArgsConstructor
public class EvidenceRecordDTOToEvidenceRecordType implements EvidenceConverterService {

    private final POIDRepository poidRepository;
    public static final String ALGO_ID_C14N_OMIT_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";

    @Override
    public EvidenceRecordType toEvidenceRecordType(List<EvidenceRecordDto> evidenceRecordDtoList, UUID poid) {
        Optional<POID> optPoidObj = poidRepository.findById(poid);

        if (optPoidObj.isPresent()) {
            POID poidObj = optPoidObj.get();
            System.out.println(poidObj);
            System.out.println(poidObj.getPo());
            System.out.println(poidObj.getPo().getDigestList());
            System.out.println(poidObj.getPo().getDigestList().getDigests());
            // TODO change to "findById" and check for nulls ect if not found
            return EvidenceRecordType.build(evidenceRecordDtoList, poidObj);
        }
        System.out.println("optional is empty");
        return null;
    }


    public EvidenceRecordType toEvidenceRecordType(List<EvidenceRecordDto> evidenceRecordDtoList, POID poidObj) {

        // initialize all structures
        EvidenceRecordType evidenceRecordType = new EvidenceRecordType();
        //no encryptionInformation (yet)
        //no supportingInformationList (yet)
        ArchiveTimeStampSequenceType archiveTimeStampSequenceType = new ArchiveTimeStampSequenceType();
        List<ArchiveTimeStampSequenceType.ArchiveTimeStampChain> archiveTimeStampChains = archiveTimeStampSequenceType.getArchiveTimeStampChain();

        int chainOrder = 1;
        int sequenceOrder = 1;
        int timeStampOrder = 1;

        ArchiveTimeStampSequenceType.ArchiveTimeStampChain archiveTimeStampChain = new ArchiveTimeStampSequenceType.ArchiveTimeStampChain();
        archiveTimeStampChain.setOrder(chainOrder);
        List<ArchiveTimeStampType> archiveTimeStampTypeList = archiveTimeStampChain.getArchiveTimeStamp();



        //set digestMethod
        //getting the digestmethod (one per chain -> one per "big" tree)
        DigestMethodType digestMethodType = new DigestMethodType();
        digestMethodType.setAlgorithm(poidObj.getDigestMethod());
        archiveTimeStampChain.setDigestMethod(digestMethodType);

        //set canonicalization method : set fixed object
        CanonicalizationMethodType canonicalizationMethodType = new CanonicalizationMethodType();
        canonicalizationMethodType.setAlgorithm(ALGO_ID_C14N_OMIT_COMMENTS); // is this correct ?

        HashTreeType hashTreeType = new HashTreeType();
        List<HashTreeType.Sequence> hashTreeTypeSequenceList = hashTreeType.getSequence();

        boolean isGroup = false;
        List<Digest> digestList = poidObj.getPo().getDigestList().getDigests();
        if (digestList.size() > 1) { //we have a group
            //we need to sort digestList according to the binary order
            isGroup = true;
            digestList.sort(new Comparator<Digest>() {
                @Override
                public int compare(Digest o1, Digest o2) {
                    return BinaryOrderComparator.compareBytes(o1.getDigest(), o2.getDigest());
                }
            });
            //fill up the first level (sequence) of the hash tree
            HashTreeType.Sequence sequence = new HashTreeType.Sequence();
            sequence.setOrder(sequenceOrder);
            sequenceOrder++;
            List<byte[]> digestValues = sequence.getDigestValue();
            for (Digest digest : digestList) {
                digestValues.add(Base64.getEncoder().encode(digest.getDigest()));
            }
            //add the sequence
            hashTreeTypeSequenceList.add(sequence);
        }

        long currParent = evidenceRecordDtoList.get(0).getParent();
        long currTreeId = evidenceRecordDtoList.get(0).getTreeId();
        int currTreeCounter = 0;

        HashTreeType.Sequence sequence = new HashTreeType.Sequence();
        List<byte[]> digestValues = sequence.getDigestValue();

        ArchiveTimeStampType archiveTimeStampType = new ArchiveTimeStampType();

        //do first iteration
        //need to capture BRANCHING_FACTOR elements and not just BRANCHING_FACTOR - 1

        for (int index = 0; index < evidenceRecordDtoList.size(); index++) {
            //we iterate on each value
            EvidenceRecordDto evidenceRecordDto = evidenceRecordDtoList.get(index);
            if (evidenceRecordDto.getTreeId() == currTreeId) {
                currTreeCounter++;
            } else {
                currTreeId = evidenceRecordDto.getTreeId();
                currTreeCounter = 1;
            }
            if (isGroup && evidenceRecordDto.isStart()) { //we ignore the start since it is already included
                continue;
            }


            if (evidenceRecordDto.getInTreeNum() == 0) { //root node
                if (currTreeCounter == 1) { // leaf node at the same time
                    digestValues.add(Base64.getEncoder().encode(evidenceRecordDto.getNodeValue()));
                }

                byte[] tsbytes = evidenceRecordDto.getTimestamp();// is this the right conversion?
                try {
                    TimestampToken timeStampToken = new TimestampToken(tsbytes, TimestampType.CONTENT_TIMESTAMP);
                    byte[] encoded = timeStampToken.getTimeStamp().getEncoded("DER");

                    TimeStampType timeStampType = new TimeStampType();
                    TimeStampType.TimeStampToken timestampToken = new TimeStampType.TimeStampToken();
                    timestampToken.setType("RFC3161");
                    List<Object> content = timestampToken.getContent();
                    content.add(encoded); //TODO check if this is right
                    timeStampType.setTimeStampToken(timestampToken);

                    hashTreeTypeSequenceList.add(sequence);

                    archiveTimeStampType.setTimeStamp(timeStampType);
                    archiveTimeStampType.setHashTree(hashTreeType);
                    archiveTimeStampType.setOrder(timeStampOrder);
                    archiveTimeStampTypeList.add(archiveTimeStampType);
                    timeStampOrder++;

                    if (index < evidenceRecordDtoList.size() - 1) { //we are not yet at the end
                        archiveTimeStampType = new ArchiveTimeStampType();
                        hashTreeType = new HashTreeType();
                        hashTreeTypeSequenceList = hashTreeType.getSequence();
                        sequence = new HashTreeType.Sequence();
                        digestValues = sequence.getDigestValue();

                        sequenceOrder = 1;

                        //set current parent
                        currParent = evidenceRecordDto.getParent(); // necessary ?
                    }
                } catch (TSPException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (CMSException e) {
                    e.printStackTrace();
                }
            } else { //non-root node
                if (evidenceRecordDto.getParent().longValue() != currParent) { //we need to create a new level
                    // add the current sequence and create a new one
                    hashTreeTypeSequenceList.add(sequence);
                    sequence = new HashTreeType.Sequence();
                    digestValues = sequence.getDigestValue();

                    sequence.setOrder(sequenceOrder);
                    sequenceOrder++;

                    // set new current parent
                    currParent = evidenceRecordDto.getParent();
                }
                digestValues.add(Base64.getEncoder().encode(evidenceRecordDto.getNodeValue()));
            }
        }
        archiveTimeStampChains.add(archiveTimeStampChain);
        evidenceRecordType.setArchiveTimeStampSequence(archiveTimeStampSequenceType);
        return evidenceRecordType;
    }
}
