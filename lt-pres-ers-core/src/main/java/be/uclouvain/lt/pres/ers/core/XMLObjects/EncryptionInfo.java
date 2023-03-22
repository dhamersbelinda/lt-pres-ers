
package be.uclouvain.lt.pres.ers.core.XMLObjects;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for EncryptionInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EncryptionInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EncryptionInformationType" type="{urn:ietf:params:xml:ns:ers}ObjectIdentifier"/>
 *         &lt;element name="EncryptionInformationValue">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;any minOccurs="0"/>
 *                 &lt;/sequence>
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
@XmlType(name = "EncryptionInfo", namespace = "urn:ietf:params:xml:ns:ers", propOrder = {
    "encryptionInformationType",
    "encryptionInformationValue"
})
public class EncryptionInfo {

    @XmlElement(name = "EncryptionInformationType", namespace = "urn:ietf:params:xml:ns:ers", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String encryptionInformationType;
    @XmlElement(name = "EncryptionInformationValue", namespace = "urn:ietf:params:xml:ns:ers", required = true)
    protected EncryptionInformationValue encryptionInformationValue;

    /**
     * Gets the value of the encryptionInformationType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEncryptionInformationType() {
        return encryptionInformationType;
    }

    /**
     * Sets the value of the encryptionInformationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEncryptionInformationType(String value) {
        this.encryptionInformationType = value;
    }

    /**
     * Gets the value of the encryptionInformationValue property.
     * 
     * @return
     *     possible object is
     *     {@link EncryptionInformationValue }
     *     
     */
    public EncryptionInformationValue getEncryptionInformationValue() {
        return encryptionInformationValue;
    }

    /**
     * Sets the value of the encryptionInformationValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link EncryptionInformationValue }
     *     
     */
    public void setEncryptionInformationValue(EncryptionInformationValue value) {
        this.encryptionInformationValue = value;
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
     *         &lt;any minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "content"
    })
    public static class EncryptionInformationValue {

        @XmlMixed
        @XmlAnyElement(lax = true)
        protected List<Object> content;

        /**
         * Gets the value of the content property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the content property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getContent().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * {@link Object }
         * 
         * 
         */
        public List<Object> getContent() {
            if (content == null) {
                content = new ArrayList<Object>();
            }
            return this.content;
        }

    }

}
