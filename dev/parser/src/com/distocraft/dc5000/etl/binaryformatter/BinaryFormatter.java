package com.distocraft.dc5000.etl.binaryformatter;

import java.nio.ByteBuffer;

public class BinaryFormatter extends BinFormatter {

  @Override
  /**
   * binaryspesific format
   */
  public byte[] doFormat(String value, int ... sizeDetails) throws Exception {

    byte[] ret = new byte[sizeDetails[0] + 1];

    //TODO
    /*
    if (value == null || value.length() == 0){
      for(int i = 0 ; i < sizeDetails[0]; i++) {
        ret.put((byte)0);
      }
      
      intArr[sizeDetails[0]] = 1;
      return intArr;
    }
    
    for (int i = 0; i < sizeDetails[0]; i++) {
      if (i < value.length())
        intArr[i] = value.charAt(i);
      else
        break;
    }*/
    
    return ret;
  }
}
