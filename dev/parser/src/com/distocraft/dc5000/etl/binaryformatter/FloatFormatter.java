package com.distocraft.dc5000.etl.binaryformatter;

import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

public class FloatFormatter extends BinFormatter {

  @Override
  /**
   * float spesific format
   */
  public byte[] doFormat(String value, int ... sizeDetails) throws Exception {
    byte[] ret = new byte[9];
    
    //TODO
    /*    
    double d = Double.parseDouble(value);
    ByteBuffer bb = MappedByteBuffer.allocate(8);
    bb.putDouble(d);
    int[] intArr = new int[9];
    for (int i = 0; i < intArr.length; i++) 
      intArr[i] = 0;
    
    if (value == null || value.length() == 0){
      intArr[8] = 1;
      return intArr;
    }
    
    int bytehelp = Integer.parseInt("01111111", 2);
    int sign = Integer.parseInt("10000000", 2);
    for (int i = 0; i < 8; i++) {
      int val = bb.get(i);
      int putVal = val & bytehelp;
      if (val < 0)
        putVal = putVal | sign;
      intArr[i] = putVal;
    }

    */

    return ret;
  }
}
