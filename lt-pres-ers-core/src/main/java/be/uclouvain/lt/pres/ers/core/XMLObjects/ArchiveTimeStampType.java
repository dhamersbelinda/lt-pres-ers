
package be.uclouvain.lt.pres.ers.core.XMLObjects;

import be.uclouvain.lt.pres.ers.core.XMLObjects.Attributes;
import be.uclouvain.lt.pres.ers.core.XMLObjects.HashTreeType;
import be.uclouvain.lt.pres.ers.core.XMLObjects.TimeStampType;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for ArchiveTimeStampType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArchiveTimeStampType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HashTree" type="{urn:ietf:params:xml:ns:ers}HashTreeType" minOccurs="0"/>
 *         &lt;element name="TimeStamp" type="{urn:ietf:params:xml:ns:ers}TimeStampType"/>
 *         &lt;element name="Attributes" type="{urn:ietf:params:xml:ns:ers}Attributes" minOccurs="0"/>
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
@XmlType(name = "ArchiveTimeStampType", namespace = "urn:ietf:params:xml:ns:ers", propOrder = {
    "hashTree",
    "timeStamp",
    "attributes"
})
public class ArchiveTimeStampType {

    @XmlElement(name = "HashTree", namespace = "urn:ietf:params:xml:ns:ers")
    protected HashTreeType hashTree;
    @XmlElement(name = "TimeStamp", namespace = "urn:ietf:params:xml:ns:ers", required = true)
    protected TimeStampType timeStamp;
    @XmlElement(name = "Attributes", namespace = "urn:ietf:params:xml:ns:ers")
    protected Attributes attributes;
    @XmlAttribute(name = "Order", required = true)
    protected int order;

    /**
     * Gets the value of the hashTree property.
     * 
     * @return
     *     possible object is
     *     {@link HashTreeType }
     *     
     */
    public HashTreeType getHashTree() {
        return hashTree;
    }

    /**
     * Sets the value of the hashTree property.
     * 
     * @param value
     *     allowed object is
     *     {@link HashTreeType }
     *     
     */
    public void setHashTree(HashTreeType value) {
        this.hashTree = value;
    }

    /**
     * Gets the value of the timeStamp property.
     * 
     * @return
     *     possible object is
     *     {@link TimeStampType }
     *     
     */
    public TimeStampType getTimeStamp() {
        return timeStamp;
    }

    /**
     * Sets the value of the timeStamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeStampType }
     *     
     */
    public void setTimeStamp(TimeStampType value) {
        this.timeStamp = value;
    }

    /**
     * Gets the value of the attributes property.
     * 
     * @return
     *     possible object is
     *     {@link Attributes }
     *     
     */
    public Attributes getAttributes() {
        return attributes;
    }

    /**
     * Sets the value of the attributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Attributes }
     *     
     */
    public void setAttributes(Attributes value) {
        this.attributes = value;
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
