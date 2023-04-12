
package be.uclouvain.lt.pres.ers.core.XMLObjects;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ArchiveTimeStampSequenceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArchiveTimeStampSequenceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ArchiveTimeStampChain" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="DigestMethod" type="{urn:ietf:params:xml:ns:ers}DigestMethodType"/>
 *                   &lt;element name="CanonicalizationMethod" type="{urn:ietf:params:xml:ns:ers}CanonicalizationMethodType"/>
 *                   &lt;element name="ArchiveTimeStamp" type="{urn:ietf:params:xml:ns:ers}ArchiveTimeStampType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="Order" use="required" type="{urn:ietf:params:xml:ns:ers}OrderType" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArchiveTimeStampSequenceType", namespace = "urn:ietf:params:xml:ns:ers", propOrder = {
    "archiveTimeStampChain"
})
public class ArchiveTimeStampSequenceType {

    @XmlElement(name = "ArchiveTimeStampChain", namespace = "urn:ietf:params:xml:ns:ers", required = true)
    protected List<ArchiveTimeStampChain> archiveTimeStampChain;

    /**
     * Gets the value of the archiveTimeStampChain property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the archiveTimeStampChain property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArchiveTimeStampChain().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ArchiveTimeStampChain }
     * 
     * 
     */
    public List<ArchiveTimeStampChain> getArchiveTimeStampChain() {
        if (archiveTimeStampChain == null) {
            archiveTimeStampChain = new ArrayList<ArchiveTimeStampChain>();
        }
        return this.archiveTimeStampChain;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="DigestMethod" type="{urn:ietf:params:xml:ns:ers}DigestMethodType"/>
     *         &lt;element name="CanonicalizationMethod" type="{urn:ietf:params:xml:ns:ers}CanonicalizationMethodType"/>
     *         &lt;element name="ArchiveTimeStamp" type="{urn:ietf:params:xml:ns:ers}ArchiveTimeStampType" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *       &lt;attribute name="Order" use="required" type="{urn:ietf:params:xml:ns:ers}OrderType" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "digestMethod",
        "canonicalizationMethod",
        "archiveTimeStamp"
    })
    public static class ArchiveTimeStampChain {

        @XmlElement(name = "DigestMethod", namespace = "urn:ietf:params:xml:ns:ers", required = true)
        protected DigestMethodType digestMethod;
        @XmlElement(name = "CanonicalizationMethod", namespace = "urn:ietf:params:xml:ns:ers", required = true)
        protected CanonicalizationMethodType canonicalizationMethod;
        @XmlElement(name = "ArchiveTimeStamp", namespace = "urn:ietf:params:xml:ns:ers", required = true)
        protected List<ArchiveTimeStampType> archiveTimeStamp;
        @XmlAttribute(name = "Order", required = true)
        protected int order;

        /**
         * Gets the value of the digestMethod property.
         * 
         * @return
         *     possible object is
         *     {@link DigestMethodType }
         *     
         */
        public DigestMethodType getDigestMethod() {
            return digestMethod;
        }

        /**
         * Sets the value of the digestMethod property.
         * 
         * @param value
         *     allowed object is
         *     {@link DigestMethodType }
         *     
         */
        public void setDigestMethod(DigestMethodType value) {
            this.digestMethod = value;
        }

        /**
         * Gets the value of the canonicalizationMethod property.
         * 
         * @return
         *     possible object is
         *     {@link CanonicalizationMethodType }
         *     
         */
        public CanonicalizationMethodType getCanonicalizationMethod() {
            return canonicalizationMethod;
        }

        /**
         * Sets the value of the canonicalizationMethod property.
         * 
         * @param value
         *     allowed object is
         *     {@link CanonicalizationMethodType }
         *     
         */
        public void setCanonicalizationMethod(CanonicalizationMethodType value) {
            this.canonicalizationMethod = value;
        }

        /**
         * Gets the value of the archiveTimeStamp property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the archiveTimeStamp property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getArchiveTimeStamp().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ArchiveTimeStampType }
         * 
         * 
         */
        public List<ArchiveTimeStampType> getArchiveTimeStamp() {
            if (archiveTimeStamp == null) {
                archiveTimeStamp = new ArrayList<ArchiveTimeStampType>();
            }
            return this.archiveTimeStamp;
        }

        /**
         * Gets the value of the order property.
         * 
         */
        public int getOrder() {
            return order;
        }

        /**
         * Sets the value of the order property.
         * 
         */
        public void setOrder(int value) {
            this.order = value;
        }

    }

}
