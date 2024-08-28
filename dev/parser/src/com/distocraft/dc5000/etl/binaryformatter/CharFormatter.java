package com.distocraft.dc5000.etl.binaryformatter;

import java.nio.ByteBuffer;

public class CharFormatter extends BinFormatter {

  @Override
  /**
   * char spesific format
   */
  public byte[] doFormat(String value, int... sizeDetails) throws Exception {
    byte[] ret = new byte[sizeDetails[0]+1];
    
    //TODO
    /*    
    int[] intArr = new int[sizeDetails[0] + 1];
    for (int i = 0; i < intArr.length - 1; i++)
      intArr[i] = 32;

    // empty string, not null
    intArr[intArr.length - 1] = 0;
    if (value == null || value.length() == 0) 
      return intArr;

    for (int i = 0; i < sizeDetails[0]; i++) {
      if (i < value.length())
        intArr[i] = value.charAt(i);
      else
        break;
    }
    */
    
    return ret;
  }
}
