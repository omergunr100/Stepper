//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.05.08 at 05:26:46 PM IDT 
//


package com.main.stepper.xml.generated.ex2;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded">
 *         &lt;element ref="{}ST-Continuation"/>
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
    "stContinuation"
})
@XmlRootElement(name = "ST-Continuations")
public class STContinuations {

    @XmlElement(name = "ST-Continuation", required = true)
    protected List<STContinuation> stContinuation;

    /**
     * Gets the value of the stContinuation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the stContinuation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSTContinuation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link STContinuation }
     * 
     * 
     */
    public List<STContinuation> getSTContinuation() {
        if (stContinuation == null) {
            stContinuation = new ArrayList<STContinuation>();
        }
        return this.stContinuation;
    }

}
