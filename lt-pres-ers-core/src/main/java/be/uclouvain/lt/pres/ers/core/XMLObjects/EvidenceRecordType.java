
package be.uclouvain.lt.pres.ers.core.XMLObjects;

import be.uclouvain.lt.pres.ers.core.persistence.model.Digest;
import be.uclouvain.lt.pres.ers.core.persistence.model.POID;
import be.uclouvain.lt.pres.ers.core.persistence.model.dto.EvidenceRecordDto;
import be.uclouvain.lt.pres.ers.utils.BinaryOrderComparator;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.TimestampType;
import eu.europa.esig.dss.validation.timestamp.TimestampToken;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.tsp.TSPException;

import javax.xml.bind.annotation.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * <p>Java class for EvidenceRecordType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EvidenceRecordType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EncryptionInformation" type="{urn:ietf:params:xml:ns:ers}EncryptionInfo" minOccurs="0"/>
 *         &lt;element name="SupportingInformationList" type="{urn:ietf:params:xml:ns:ers}SupportingInformationType" minOccurs="0"/>
 *         &lt;element name="ArchiveTimeStampSequence" type="{urn:ietf:params:xml:ns:ers}ArchiveTimeStampSequenceType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Version" use="required" type="{http://www.w3.org/2001/XMLSchema}decimal" fixed="1.0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EvidenceRecordType", namespace = "urn:ietf:params:xml:ns:ers", propOrder = {
    "encryptionInformation",
    "supportingInformationList",
    "archiveTimeStampSequence"
})
public class EvidenceRecordType {
    // TODO : move elsewhere this constant ? also support other ?
    @XmlTransient
    public static final String ALGO_ID_C14N_OMIT_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";

    @XmlElement(name = "EncryptionInformation", namespace = "urn:ietf:params:xml:ns:ers")
    protected EncryptionInfo encryptionInformation;
    @XmlElement(name = "SupportingInformationList", namespace = "urn:ietf:params:xml:ns:ers")
    protected SupportingInformationType supportingInformationList;
    @XmlElement(name = "ArchiveTimeStampSequence", namespace = "urn:ietf:params:xml:ns:ers", required = true)
    protected ArchiveTimeStampSequenceType archiveTimeStampSequence;
    @XmlAttribute(name = "Version", required = true)
    protected BigDecimal version;

    public static EvidenceRecordType build(List<EvidenceRecordDto> evidenceRecordDtoList, POID poidObj) {
        if(poidObj == null){
            throw new IllegalArgumentException("poidObj cannot be null");
        }
        if(evidenceRecordDtoList == null || evidenceRecordDtoList.isEmpty()) {
            throw new IllegalArgumentException("evidenceRecordDtoList cannot be null or empty");
        }
        // initialize all structures
        EvidenceRecordType evidenceRecordType = new EvidenceRecordType();
        evidenceRecordType.setVersion(BigDecimal.valueOf(1.0));
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

        DigestAlgorithm alg = DigestAlgorithm.forOID(poidObj.getDigestMethod());
        digestMethodType.setAlgorithm(alg.getUri());
        archiveTimeStampChain.setDigestMethod(digestMethodType);

        //set canonicalization method : set fixed object
        CanonicalizationMethodType canonicalizationMethodType = new CanonicalizationMethodType();
        canonicalizationMethodType.setAlgorithm(ALGO_ID_C14N_OMIT_COMMENTS); // is this correct ?
        archiveTimeStampChain.setCanonicalizationMethod(canonicalizationMethodType);

        HashTreeType hashTreeType = new HashTreeType();
        List<HashTreeType.Sequence> hashTreeTypeSequenceList = hashTreeType.getSequence();

        boolean isGroup = false;
        List<Digest> digestList = poidObj.getPo().getDigestList().getDigests();

//        if (digestList.size() > 1) { //we have a group
//            //we need to sort digestList according to the binary order
//            isGroup = true;
//            digestList.sort(new Comparator<Digest>() {
//                @Override
//                public int compare(Digest o1, Digest o2) {
//                    return BinaryOrderComparator.compareBytes(o1.getDigest(), o2.getDigest());
//                }
//            });
//            //fill up the first level (sequence) of the hash tree
//            HashTreeType.Sequence sequence = new HashTreeType.Sequence();
//            sequence.setOrder(sequenceOrder);
//            sequenceOrder++;
//            List<byte[]> digestValues = sequence.getDigestValue();
//            for (Digest digest : digestList) {
//                digestValues.add(Base64.getEncoder().encode(digest.getDigest()));
//            }
//            //add the sequence
//            hashTreeTypeSequenceList.add(sequence);
//        }

        /////////////////////////////////////////////////////////////////////////////////////////////////
        //doing the first iteration
        if (digestList.size() > 1) { //we have a group
            //we need to sort digestList according to the binary order
            isGroup = true;
            digestList.sort(new Comparator<Digest>() {
                @Override
                public int compare(Digest o1, Digest o2) {
                    return BinaryOrderComparator.compareBytes(o1.getDigest(), o2.getDigest());
                }
            });
        }
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
        ///////////////////////////////////////////////////////////////////////////////////////

        Long currParent = evidenceRecordDtoList.get(0).getParent();
        long currTreeId = evidenceRecordDtoList.get(0).getTreeId();
        int currTreeCounter = 0;

        sequence = new HashTreeType.Sequence(); //changed here
        List<byte[]> digestsToSort = new ArrayList<>();
        digestValues = sequence.getDigestValue(); //changed here

        ArchiveTimeStampType archiveTimeStampType = new ArchiveTimeStampType();

        //do first iteration
        //need to capture BRANCHING_FACTOR elements and not just BRANCHING_FACTOR - 1

        boolean prevIsRoot = false;

        for (int index = 0; index < evidenceRecordDtoList.size(); index++) {
            //we iterate on each value
            EvidenceRecordDto evidenceRecordDto = evidenceRecordDtoList.get(index);
            if (evidenceRecordDto.getTreeId() == currTreeId) {
                currTreeCounter++;
            } else {
                currTreeId = evidenceRecordDto.getTreeId();
                currTreeCounter = 1;
            }

            if (evidenceRecordDto.getInTreeNum() == 0) { //root node
                if (currTreeCounter == 1 && !evidenceRecordDto.isStart()) { // leaf node at the same time //TODO change here if single group ?
                    digestsToSort.add(evidenceRecordDto.getNodeValue());
                    //digestValues.add(Base64.getEncoder().encode(evidenceRecordDto.getNodeValue()));
                }

                byte[] tsbytes = evidenceRecordDto.getTimestamp();// is this the right conversion?
                try {
                    TimestampToken timeStampToken = new TimestampToken(tsbytes, TimestampType.CONTENT_TIMESTAMP);
                    byte[] encoded = timeStampToken.getTimeStamp().getEncoded("DER");

                    TimeStampType timeStampType = new TimeStampType();
                    TimeStampType.TimeStampToken timestampToken = new TimeStampType.TimeStampToken();
                    timestampToken.setType("RFC3161");
                    List<Object> content = timestampToken.getContent();
                    String s = new String(Base64.getEncoder().encode(encoded), StandardCharsets.UTF_8);
                    content.add(s); //TODO check if this is right
                    timeStampType.setTimeStampToken(timestampToken);

                    if (!evidenceRecordDto.isStart()) {
                        sequence.setOrder(sequenceOrder);
                        digestsToSort.sort(new BinaryOrderComparator());
                        for (byte[] digest : digestsToSort) {
                            digestValues.add(Base64.getEncoder().encode(digest));
                        }
                        hashTreeTypeSequenceList.add(sequence);
                    }


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
                        digestsToSort.clear();
                        digestValues = sequence.getDigestValue();

                        sequenceOrder = 1;

                        //set current parent
                        currParent = evidenceRecordDto.getParent(); // necessary ?
                    }
                    prevIsRoot = true;
                } catch (TSPException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (CMSException e) {
                    e.printStackTrace();
                }
            } else { //non-root node
                if (!evidenceRecordDto.isStart()){ //changed here
                    EvidenceRecordDto next = evidenceRecordDtoList.get(index + 1);
                    if (next.isStart()) {
                        next = evidenceRecordDtoList.get(index + 2);
                    }
                    currParent = evidenceRecordDto.getParent();
                    digestsToSort.add(evidenceRecordDto.getNodeValue());
                    //byte[] t = Base64.getEncoder().encode(evidenceRecordDto.getNodeValue());
                    //digestValues.add(t);
                    //System.out.println("Added the following b64 encoded value "+ new String(t,StandardCharsets.UTF_8));

                    //if the next one is start, we need to check the parent of the next to next one

//                    if (next.getParent() == null || (next.getParent().longValue() != currParent.longValue() && next.getInTreeNum() != 0) || prevIsRoot) {
                    if ((next.getParent() != null && next.getParent().longValue() != currParent.longValue() && next.getInTreeNum() != 0) || prevIsRoot) {
                        sequence.setOrder(sequenceOrder);
                        digestsToSort.sort(new BinaryOrderComparator());
                        for (byte[] digest : digestsToSort) {
                            digestValues.add(Base64.getEncoder().encode(digest));
                        }
                        hashTreeTypeSequenceList.add(sequence);
                        sequence = new HashTreeType.Sequence();
                        digestsToSort.clear();
                        digestValues = sequence.getDigestValue();

                        sequenceOrder++;
                    }


                    prevIsRoot = false;


                }
            }
        }
        archiveTimeStampChains.add(archiveTimeStampChain);
        evidenceRecordType.setArchiveTimeStampSequence(archiveTimeStampSequenceType);
        return evidenceRecordType;
    }

    public EncryptionInfo getEncryptionInformation() {
        return encryptionInformation;
    }

    /**
     * Sets the value of the encryptionInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link EncryptionInfo }
     *     
     */
    public void setEncryptionInformation(EncryptionInfo value) {
        this.encryptionInformation = value;
    }

    /**
     * Gets the value of the supportingInformationList property.
     * 
     * @return
     *     possible object is
     *     {@link SupportingInformationType }
     *     
     */
    public SupportingInformationType getSupportingInformationList() {
        return supportingInformationList;
    }

    /**
     * Sets the value of the supportingInformationList property.
     * 
     * @param value
     *     allowed object is
     *     {@link SupportingInformationType }
     *     
     */
    public void setSupportingInformationList(SupportingInformationType value) {
        this.supportingInformationList = value;
    }

    /**
     * Gets the value of the archiveTimeStampSequence property.
     * 
     * @return
     *     possible object is
     *     {@link ArchiveTimeStampSequenceType }
     *     
     */
    public ArchiveTimeStampSequenceType getArchiveTimeStampSequence() {
        return archiveTimeStampSequence;
    }

    /**
     * Sets the value of the archiveTimeStampSequence property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArchiveTimeStampSequenceType }
     *     
     */
    public void setArchiveTimeStampSequence(ArchiveTimeStampSequenceType value) {
        this.archiveTimeStampSequence = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getVersion() {
        if (version == null) {
            return new BigDecimal("1.0");
        } else {
            return version;
        }
    }

    /**
     * Sets the value of the version property.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setVersion(BigDecimal value) {
        this.version = value;
    }

}
