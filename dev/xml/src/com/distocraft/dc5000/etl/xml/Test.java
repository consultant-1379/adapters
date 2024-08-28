package com.distocraft.dc5000.etl.xml;

import java.io.File;
import java.io.FileInputStream;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class Test extends DefaultHandler {

  public static void main(final String[] args) {

    try {
      final Test t = new Test();
      final XMLReader xmlReader = new org.apache.xerces.parsers.SAXParser();
      xmlReader.setContentHandler(t);
      xmlReader.setErrorHandler(t);

      xmlReader.parse(new InputSource(new FileInputStream(new File(args[0]))));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Event handlers
   */
  public void startDocument() {
    System.out.println("Start document");
  }

  public void endDocument() throws SAXException {
    System.out.println("End document");
  }

  public void startElement(final String uri, final String name, final String qName, final Attributes atts) throws SAXException {
    System.out.println("Start: \"" + uri + "\" \"" + name + "\" \"" + qName + "\" Attributes " + atts.getLength());
  }

  public void endElement(final String uri, final String name, final String qName) throws SAXException {
    System.out.println("End:   \"" + uri + "\" \"" + name + "\" \"" + qName + "\"");
  }

  public void characters(final char ch[], final int start, final int length) {
    
    String charValue = "";
    
    for (int i = start; i < start + length; i++) {
      // If no control char
      if (ch[i] != '\\' && ch[i] != '\n' && ch[i] != '\r' && ch[i] != '\t') {
        charValue += ch[i];
      }
    }
    
    charValue = charValue.trim();
    
    if(charValue.length() > 0) {
      System.out.println("Chars: \""+charValue.trim()+"\"");
    }
    
  }

}
