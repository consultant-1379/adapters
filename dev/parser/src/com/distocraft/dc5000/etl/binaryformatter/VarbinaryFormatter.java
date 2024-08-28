package com.distocraft.dc5000.etl.binaryformatter;

import java.nio.ByteBuffer;

public class VarbinaryFormatter extends BinFormatter {

  @Override
  /**
   * varbinaryspesific format
   */
  public byte[] doFormat(String value, int ... sizeDetails) throws Exception {
    
    //TODO
    /*
    int[] intArr = new int[sizeDetails[0] + 1];
    for (int i = 0; i < intArr.length; i++)
      intArr[i] = 0;
      
    if (value == null || value.length() == 0){
      intArr[sizeDetails[0]] = 1;
      return intArr;
    }
    
    for (int i = 0; i < sizeDetails[0]; i++) {
      if (i < value.length())
        intArr[i] = value.charAt(i);
      else
        break;
    }
    */
    
    return new byte[0];
  }
}
