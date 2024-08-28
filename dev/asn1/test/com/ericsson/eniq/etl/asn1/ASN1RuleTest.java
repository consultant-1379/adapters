package com.ericsson.eniq.etl.asn1;

import static org.junit.Assert.*;

import java.util.ArrayList;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

/**
 * 
 * @author ejarsok
 *
 */

public class ASN1RuleTest {

  @Test
  public void testCopy() {
    ASN1Rule asn = new ASN1Rule();
    asn.name = "Name";
    asn.internalType = "iType";
    asn.info = "Info";
    asn.type = "Type";      
    asn.isChoice = true;
    asn.choices = new ArrayList();
    asn.defaultValue = "Default";
    asn.optional = true;
    
    ASN1Rule copy = asn.copy();
    
    String expected = "Name iType Info Type true Default true";
    String actual = copy.name + " " + copy.internalType + " " + copy.info + " " + copy.type + " " + copy.isChoice + 
                    " " + copy.defaultValue + " " + copy.optional;
    
    assertEquals(expected, actual);
  }
  
  @Test 
  public void testToString() {
    ASN1Rule asn = new ASN1Rule();
    
    asn.name = "Name";
    asn.internalType = "iType";
    asn.type = "type";
    asn.defaultValue = "Default";
    
    asn.optional = true;
    
    String expected = "Name type (iType)OPTIONAL default:[Default]";
    String actual = asn.toString();
    
    assertEquals(expected, actual);
  }
  
  /*public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(ASN1RuleTest.class);
  }*/
}
