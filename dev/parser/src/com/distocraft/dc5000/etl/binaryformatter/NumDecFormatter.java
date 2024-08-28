package com.distocraft.dc5000.etl.binaryformatter;

public strictfp class NumDecFormatter extends BinFormatter {

  private SmallintFormatter smform = new SmallintFormatter();

  private IntFormatter inform = new IntFormatter();

  private BigintFormatter biform = new BigintFormatter();

  @Override
  public byte[] doFormat(final String value, final int... sizeDetails) throws Exception {

    if (sizeDetails.length < 2) {
      throw new Exception("Numeric/Decimal does not have information about size and precision");
    }

    final int size = sizeDetails[0];
    final int precision = sizeDetails[1];

    String[] valArr = null;
    if (value != null){
    	valArr = value.split("[\\.,]");
    }

    String parsedValue = null;

    if (value != null && value.length() > 0) {

      final StringBuilder number;
      if (valArr.length > 0 && valArr[0] != null) {
        number = new StringBuilder(valArr[0]);
      } else {
        number = new StringBuilder();
      }

      final StringBuilder fraction;
      if (valArr.length > 1 && valArr[1] != null) {
        fraction = new StringBuilder(valArr[1]);
      } else {
        fraction = new StringBuilder();
      }

      while (fraction.length() < precision) {
        fraction.append("0");
      }

      parsedValue = "" + number + fraction;

    }

    if (size <= 4) {
      return smform.doFormat(parsedValue, sizeDetails);
    } else if (size >= 5 && size <= 9) {
      return inform.doFormat(parsedValue, sizeDetails);
    } else if (size >= 10 && size <= 18) {
      return biform.doFormat(parsedValue, sizeDetails);
    } else {
      return specialFormat(parsedValue, size, precision);
    }

  }

  private byte[] specialFormat(final String value, final int size, final int precision) {
    
    final byte sign = value.charAt(0) == '-' ? (byte)0 : (byte)1; // sign 1 for +, 0 for -
    byte ndig = 0; // # digits

    final byte[] digits = new byte[71];
    for (int i = 0; i < digits.length; i++) {
      digits[i] = 0;
    }

    String integral = value.substring(0, size - precision);
    String fraction = value.substring(size - precision);

    integral = integral.replaceAll("^[-0\\+]*", "");
    fraction = fraction.replaceAll("0*$", "");

    while (fraction.length() % 4 != 0)
      fraction += "0";

    int intLeng = (int) Math.ceil(integral.length() / 4.0);
    int fracLeng = (int) Math.ceil(fraction.length() / 4.0);
    int[] digitArr = new int[intLeng + fracLeng];
    int excess = 80 - fracLeng;

    String str = fraction;
    int start = str.length() - 4;
    int len = 4;
    for (int i = 0; i < digitArr.length; i++) {

      if (start < 0) {
        len += start;
        start = 0;

        if (len <= 0) {
          str = integral;
          start = str.length() >= 4 ? str.length() - 4 : 0;
          len = str.length() >= 4 ? 4 : str.length();
        }
      }

      digitArr[i] = Integer.parseInt(str.substring(start, start + len));
      start -= 4;
    }

    digits[0] = sign;
    digits[1] = 0;
    digits[2] = 1;
    digits[3] = 0;

    if ((fraction + integral).equals(""))
      return digits;

    int byteHelp = Integer.parseInt("11111111", 2);
    digits[2] = (byte)(byteHelp & excess);
    digits[3] = 0;

    ndig = (byte)digitArr.length;
    digits[1] = ndig;

    byteHelp = Integer.parseInt("11111111", 2);
    int digitIndex = 0;
    int i = 4;
    for (i = 4; i < digits.length - 1; i += 2) {

      if (digitArr.length > digitIndex) {
        int digit = digitArr[digitIndex];
        digits[i + 1] = (byte)(digit & byteHelp);
        digit = digit >> 8;
        digits[i] = (byte)(digit & byteHelp);
        digitIndex++;
      }

    }

    return digits;

  }
}
