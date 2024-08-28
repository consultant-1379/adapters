package com.distocraft.dc5000.etl.binaryformatter;

import java.nio.ByteBuffer;

public class BitFormatter extends BinFormatter {

  @Override
  /**
   * Bit spesific format
   */
  public byte[] doFormat(String value, int ... sizeDetails) throws Exception {
    byte[] ret = new byte[2];
    
    //TODO
    /*
    if (value == null || value.length() == 0)
      return new int[] { 0, 0 };

    byte retByte = Byte.parseByte(value);
    return new int[] { retByte, 0 };
    */
    
    return ret;
  }

}
