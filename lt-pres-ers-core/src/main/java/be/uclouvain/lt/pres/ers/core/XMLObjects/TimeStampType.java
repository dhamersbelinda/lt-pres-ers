
package be.uclouvain.lt.pres.ers.core.XMLObjects;

import org.w3c.dom.Element;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for TimeStampType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TimeStampType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TimeStampToken">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;any processContents='lax' maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="Type" use="required" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="CryptographicInformationList" type="{urn:ietf:params:xml:ns:ers}CryptographicInformationType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TimeStampType", namespace = "urn:ietf:params:xml:ns:ers", propOrder = {
    "timeStampToken",
    "cryptographicInformationList"
})
public class TimeStampType {

    @XmlElement(name = "TimeStampToken", namespace = "urn:ietf:params:xml:ns:ers", required = true)
    protected TimeStampToken timeStampToken;
    @XmlElement(name = "CryptographicInformationList", namespace = "urn:ietf:params:xml:ns:ers")
    protected CryptographicInformationType cryptographicInformationList;

    /**
     * Gets the value of the timeStampToken property.
     * 
     * @return
     *     possible object is
     *     {@link TimeStampToken }
     *     
     */
    public TimeStampToken getTimeStampToken() {
        return timeStampToken;
    }

    /**
     * Sets the value of the timeStampToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeStampToken }
     *     
     */
    public void setTimeStampToken(TimeStampToken value) {
        this.timeStampToken = value;
    }

    /**
     * Gets the value of the cryptographicInformationList property.
     * 
     * @return
     *     possible object is
     *     {@link CryptographicInformationType }
     *     
     */
    public CryptographicInformationType getCryptographicInformationList() {
        return cryptographicInformationList;
    }

    /**
     * Sets the value of the cryptographicInformationList property.
     * 
     * @param value
     *     allowed object is
     *     {@link CryptographicInformationType }
     *     
     */
    public void setCryptographicInformationList(CryptographicInformationType value) {
        this.cryptographicInformationList = value;
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
     *         &lt;any processContents='lax' maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *       &lt;attribute name="Type" use="required" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
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
    public static class TimeStampToken {

        @XmlMixed
        @XmlAnyElement(lax = true)
        protected List<Object> content;
        @XmlAttribute(name = "Type", required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "NMTOKEN")
        protected String type;

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
         * {@link Element }
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

        /**
         * Gets the value of the type property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getType() {
            return type;
        }

        /**
         * Sets the value of the type property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setType(String value) {
            this.type = value;
        }

    }

}
