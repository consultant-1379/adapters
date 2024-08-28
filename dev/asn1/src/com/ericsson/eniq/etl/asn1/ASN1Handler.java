package com.ericsson.eniq.etl.asn1;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ASN1Handler {

  int buffer = 100000;

  final static int PRINTABLESTRING = 19;

  final static int GRAPHICSTRING = 25;

  final static int UTF8STRING = 12;

  final static int END = 0;

  final static int SEQUENCE = 16;

  final static int UNIVERSAL = 1;

  final static int APPLICATION = 2;

  final static int CONTEXSPESIFIC = 3;

  final static int PRIVATE = 4;

  final static int SHORTDEFINEDLENGTH = 1;

  final static int INDEFINEDLENGTH = 2;

  final static int LONGDEFINEDLENGTH = 3;

  final static int CONSTRUCTED = 1;

  final static int PRIMITIVE = 2;

  FileInputStream fis;

  DataInputStream dis;

  List data = new ArrayList();

  byte nextByte = -1;

  boolean eof = false;

  List sequenceEnds = new ArrayList();

  int offset = 0;

  int bytePointer = 0;

  byte[] bytes;

  int bytesFromFile = 0;

  private ASN1 asn1Handler = null;

  ASN1RuleHandler asn1Rules = null;

  private int getOffset() {
    return offset;
  }

  private byte getDec() {
    return nextByte;
  }

  private void next() throws Exception {
    
    if (bytesFromFile == 0) {

      bytes = new byte[buffer];

      try {
        int bytesRead = -1;

        if (dis.available() > 0) {

          bytesRead = dis.read(bytes, 0, buffer);
        }
        if (bytesRead == -1) {

          eof = true;
          offset++;
          
        } else {

          bytesFromFile = bytesRead;
          bytePointer = 0;

          nextByte = bytes[bytePointer];
          bytePointer++;
          bytesFromFile--;

          if (offset > 0) {
            offset++;
          }

        }
      } catch (Exception e) {
        throw new Exception(e);
      }
    
    } else {
      
      nextByte = bytes[bytePointer];
      bytePointer++;
      bytesFromFile--;
      offset++;
    }
  }

  private int tagForm() {

    // bitmask 0010000
    final int i = getDec() & 32;
    if (i == 0) {
      return PRIMITIVE;
    } else {
      return CONSTRUCTED;
    }
  }

  private int tagClass() {

    if (((getDec() & 128) == 0) && ((getDec() & 64) == 0)) {
      return UNIVERSAL;
    } else if (((getDec() & 128) == 0) && ((getDec() & 64) != 0)) {
      return APPLICATION;
    } else if (((getDec() & 128) != 0) && ((getDec() & 64) == 0)) {
      return CONTEXSPESIFIC;
    } else {
      return PRIVATE;
    }
  }

  private int lengthFormat(final int tagForm) {

    if (tagForm == PRIMITIVE && !((getDec() & 128) != 0)) {
      return SHORTDEFINEDLENGTH;
    } else if (tagForm == PRIMITIVE && ((getDec() & 128) != 0)) {
      return LONGDEFINEDLENGTH;
    } else if (tagForm == CONSTRUCTED && ((getDec() & 128) != 0)) {

      // bitMaks 01111111
      final int i = getDec() & 127;
      if (i == 0) {
        return INDEFINEDLENGTH;
      } else {
        return LONGDEFINEDLENGTH;
      }
    } else {
      return SHORTDEFINEDLENGTH;
    }
  }

  private int getLenght(final int lengthFormat) throws Exception {

    if (lengthFormat == INDEFINEDLENGTH) {
      return -1;
    } else if (lengthFormat == LONGDEFINEDLENGTH) {
   
      int v=0;
      // bitmask 01111111
      final int lenghtOfLength = getDec() & 127;
      for (int i = lenghtOfLength-1; i >= 0 ; i--) {
        next();
        v+=(getDec()&255)<<(i*8);
      }
      
      return v;
      
    } else if (lengthFormat == SHORTDEFINEDLENGTH) {
      return getDec() & 127;
    } else {
      throw new Exception("Invalid length format encountered.");
    }
  }

  private int tagID() {

    // bitmask 00011111
    return getDec() & 31;
  }

  public ASN1Handler(ASN1 asn1) {
    asn1Handler = asn1;
  }

  public ASN1Handler() {

  }

  public void parse() throws Exception {

    next();

    while (true) {

      while (!sequenceEnds.isEmpty() && ((Integer) sequenceEnds.get(0)).intValue() == getOffset()) {
        sequenceEnds.remove(0);
        asn1Handler.seqEnd(tagID(), asn1Rules.endSequence());
      }

      if (eof) {
        asn1Handler.eof();
        break;
      }
      final int tagForm = tagForm();

      if (tagID() == END && tagClass() == UNIVERSAL) {

        next();
        asn1Handler.seqEnd(tagID(), asn1Rules.endSequence());
        next();
        continue;

      } else if (tagForm() == PRIMITIVE && tagClass() == CONTEXSPESIFIC) {

        // PRIMITIVE

        final int tagid = tagID();

        next();

        final int length = getDec();

        byte[] result = new byte[length];
        for (int ii = 0; ii < length; ii++) {
          next();
          result[ii] = getDec();
        }

        asn1Handler.primitive(tagid, "N/A", result, asn1Rules.primitive(tagid));
        next();
        continue;

      } else if (tagForm() == CONSTRUCTED && tagClass() == CONTEXSPESIFIC) {

        //

        // SEQUENCE START
        final int tagid = tagID();
        next();
        final int length = getLenght(lengthFormat(tagForm));

        if (length > 0) {
          sequenceEnds.add(new Integer(getOffset() + length + 1));
          Collections.sort(sequenceEnds);
        }

        // if empty sequence (=0) do not trigger seqStart
        // if (getDec() != 0) {
        if (length != 0) {
          asn1Handler.seqStart(tagid, length, asn1Rules.startSequence());
        }

        next();
        continue;

      } else if (tagID() == SEQUENCE) {

        // SEQUENCE START
        final int tagid = tagID();
        next();
        final int length = getLenght(lengthFormat(tagForm));

        if (length > 0) {
          sequenceEnds.add(new Integer(getOffset() + length + 1));
          Collections.sort(sequenceEnds);
        }
        asn1Handler.seqStart(tagid, length, asn1Rules.startSequence());
        next();
        continue;

      } else {

        final int tagid = tagID();
        next();

        final int length = (int) getDec();

        byte[] result = new byte[length];
        for (int ii = 0; ii < length; ii++) {
          next();
          result[ii] = getDec();
        }

        asn1Handler.primitive(tagid, "N/A", result, asn1Rules.primitive(tagid));
        next();
        continue;
      }
    }
  }

  public void init(final InputStream f) throws Exception {
    fis = (FileInputStream) f;
    dis = new DataInputStream(fis);
  }

  public void init(final File file) throws Exception {
    fis = new FileInputStream(file);
    dis = new DataInputStream(fis);
  }

  public void setRules(final File file) throws Exception {
    asn1Rules = new ASN1RuleHandler();
    asn1Rules.createRules(file);
  }

  public void setRules(final String rules) throws Exception {
    asn1Rules = new ASN1RuleHandler();
    asn1Rules.createRules(rules);
  }

  public void setBuffer(final int buffer) throws Exception {
    this.buffer = buffer;
  }

  public String doData(final ASN1Rule rule, final byte[] data) throws Exception {
    if (rule.type.equalsIgnoreCase("BOOLEAN")) {
      return "" + doBoolean(data);
    } else if (rule.type.equalsIgnoreCase("INTEGER")) {
      return "" + new BigInteger(data);
    } else if (rule.type.equalsIgnoreCase("REAL")) {
      return "" + doReal(data);
    } else if (rule.type.equalsIgnoreCase("PrintableString")) {
      return doString(rule, data);
    } else if (rule.type.equalsIgnoreCase("GraphicString")) {
      return doString(rule, data);
    } else if (rule.type.equalsIgnoreCase("UTF8String")) {
      return doString(rule, data);
    } else {
      return doString(rule, data);
    }
  }

  public double doReal(final byte[] data) {

    final byte[] bytes = (byte[]) data;

    int sign = bytes[0] & 64;
    if (sign == 0) {
      sign = 1;
    } else {
      sign = -1;
    }

    int base = bytes[0] & 48;
    if (base == 0) {
      base = 2;
    } else if (base == 16) {
      base = 8;
    } else if (base == 32) {
      base = 16;
    }

    final int scaleFactor = bytes[0] & 12;
    int exponentLength = bytes[0] & 3;
    int start = 1;

    if (exponentLength == 0) {
      exponentLength = 1;
    } else if (exponentLength == 1) {
      exponentLength = 2;
    } else if (exponentLength == 2) {
      exponentLength = 1;
    } else if (exponentLength == 3) {
      exponentLength = bytes[2];
      start = 2;
    }

    int exponent = 0;
    int ii = (exponentLength - 1) * 8;
    for (int i = start; i < start + exponentLength; i++) {
      exponent = (bytes[i] << ii);
      ii -= 8;
    }

    long mantissa = 0;

    for (int i = start + 1; i < bytes.length; i++) {
      mantissa = (mantissa << 8) | (bytes[i] & 255);
    }

    return sign * mantissa * Math.pow(2, scaleFactor) * Math.pow(base, exponent);
  }

  public boolean doBoolean(final byte[] data) {

    final byte[] bytes = (byte[]) data;

    if (bytes[0] == 0) {
      return false;
    }
    return true;

  }

  public String doString(final ASN1Rule rule, final byte[] data) throws Exception {

    if (rule.type.equalsIgnoreCase("UTF8String")) {

      final byte[] bytes = (byte[]) data;
      return new String(bytes, 0, bytes.length, "UTF8");

    } else {

      final byte[] bytes = (byte[]) data;
      return new String(bytes, 0, bytes.length);

    }

  }
}
