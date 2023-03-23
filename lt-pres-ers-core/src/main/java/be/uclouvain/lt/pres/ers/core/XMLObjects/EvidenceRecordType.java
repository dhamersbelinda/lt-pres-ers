
package be.uclouvain.lt.pres.ers.core.XMLObjects;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;


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

    @XmlElement(name = "EncryptionInformation", namespace = "urn:ietf:params:xml:ns:ers")
    protected EncryptionInfo encryptionInformation;
    @XmlElement(name = "SupportingInformationList", namespace = "urn:ietf:params:xml:ns:ers")
    protected SupportingInformationType supportingInformationList;
    @XmlElement(name = "ArchiveTimeStampSequence", namespace = "urn:ietf:params:xml:ns:ers", required = true)
    protected ArchiveTimeStampSequenceType archiveTimeStampSequence;
    @XmlAttribute(name = "Version", required = true)
    protected BigDecimal version;

    /**
     * Gets the value of the encryptionInformation property.
     * 
     * @return
     *     possible object is
     *     {@link EncryptionInfo }
     *     
     */
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
