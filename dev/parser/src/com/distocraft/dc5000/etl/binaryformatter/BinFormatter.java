package com.distocraft.dc5000.etl.binaryformatter;

import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * This is superclass of all the binary formatters. Purpose of this class is to
 * provide interface to create datatype spesific instance of subclass of this
 * class. Sublasses are named as [<datatype>"Formatter"]
 * 
 * Subclasses represent each datatype in sybase iq database: bit, tinyint,
 * smallint, int/unsigned int, bigint/insigned bigint, float, double,
 * char/varchar, binary/varbinary
 * 
 * @author etogust
 */
public abstract class BinFormatter {

  /**
   * Formatters
   */
  private static HashMap<String, BinFormatter> CLASSES = null;

  /**
   * This method takes string value as parameter and returns intarray
   * representing the value as bytes which can be used in binary load files for
   * sybase IQ database.
   * 
   * @param value
   *          Value in database as String
   * @param sizedetails
   *          Int value telling the possible size and possible precision of the
   *          column
   * @return
   * @throws Exception
   *           Exception is thrown if given String value cannot be parsed
   */
  public abstract byte[] doFormat(String value, int... sizeDetails) throws Exception;

  /**
   * Gets the static CLASSES HashMap, which stores information about which class
   * is used to which dadtatype
   * 
   * @return HashMap<String, String>
   */
  public static HashMap<String, BinFormatter> getClasses() {

    if (CLASSES == null) {

      CLASSES = new HashMap<String, BinFormatter>();

      CLASSES.put("bit", new BitFormatter());
      CLASSES.put("tinyint", new TinyintFormatter());
      CLASSES.put("smallint", new SmallintFormatter());
      CLASSES.put("int", new IntFormatter());
      CLASSES.put("integer", new IntFormatter());
      CLASSES.put("unsigned int", new UnsignedintFormatter());
      CLASSES.put("bigint", new BigintFormatter());
      CLASSES.put("unsigned bigint", new UnsignedbigintFormatter());
      CLASSES.put("float", new FloatFormatter());
      // CLASSES.put("double", new DoubleFormatter());
      CLASSES.put("char", new CharFormatter());
      CLASSES.put("varchar", new VarcharFormatter());
      CLASSES.put("binary", new BinaryFormatter());
      CLASSES.put("varbinary", new VarbinaryFormatter());
      CLASSES.put("date", new DateFormatter());
      CLASSES.put("time", new TimeFormatter());
      CLASSES.put("datetime", new DatetimeFormatter());
      CLASSES.put("numeric", new NumDecFormatter());
      CLASSES.put("decimal", new NumDecFormatter());

    }

    return CLASSES;
  }

  static byte[] intToByteArray(int value, byte[] b) {
    for (int i = 0; i < 4; i++) {
      int offset = (b.length - 1 - i) * 8;
      b[i] = (byte) ((value >>> offset) & 0xFF);
    }
    return b;
  }
  
  static int int_swap (int value) {
    int b1 = (value >>  0) & 0xff;
    int b2 = (value >>  8) & 0xff;
    int b3 = (value >> 16) & 0xff;
    int b4 = (value >> 24) & 0xff;

    return b1 << 24 | b2 << 16 | b3 << 8 | b4 << 0;
  }

}
