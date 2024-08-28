package com.ericsson.eniq.etl.asn1;

public interface ASN1 {

  void seqStart(int tagID,int length,String name) throws Exception;
  void seqEnd(int tagID,String name) throws Exception;
  void primitive(int tagID,String type, byte[] data,ASN1Rule rule) throws Exception;
  void eof() throws Exception;
    
}
