package com.ericsson.eniq.etl.asn1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;

import junit.framework.TestCase;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

//import com.distocraft.dc5000.common.HelpClass;

/**
 * 
 * @author ejarsok
 * 
 */

public class ASN1HandlerTest {

  private static Method getOffset;
  
  private static Method getDec;
  
  private static Method tagForm;
  
  private static Method tagClass;
  
  private static Method lengthFormat;
  
  private static Method getLenght;
  
  private static Method tagID;
  
  private static Method next;
  
  private static Field buffer;
  
  private static Field fis;
  
  private static Field dis;
  
  private static Field nextByte;
  
  @BeforeClass
  public static void init() {
    try {
      getOffset = ASN1Handler.class.getDeclaredMethod("getOffset", null);
      getDec = ASN1Handler.class.getDeclaredMethod("getDec", null);
      tagForm = ASN1Handler.class.getDeclaredMethod("tagForm", null);
      tagClass = ASN1Handler.class.getDeclaredMethod("tagClass", null);      
      lengthFormat = ASN1Handler.class.getDeclaredMethod("lengthFormat", new Class[] {int.class});
      getLenght = ASN1Handler.class.getDeclaredMethod("getLenght", new Class[] {int.class});
      tagID = ASN1Handler.class.getDeclaredMethod("tagID", null);
      next = ASN1Handler.class.getDeclaredMethod("next", null);
      buffer = ASN1Handler.class.getDeclaredField("buffer");
      fis = ASN1Handler.class.getDeclaredField("fis");
      dis = ASN1Handler.class.getDeclaredField("dis");
      nextByte = ASN1Handler.class.getDeclaredField("nextByte");
      
      getOffset.setAccessible(true);
      getDec.setAccessible(true);
      tagForm.setAccessible(true);
      tagClass.setAccessible(true);
      lengthFormat.setAccessible(true);
      getLenght.setAccessible(true);
      tagID.setAccessible(true);
      next.setAccessible(true);
      buffer.setAccessible(true);
      fis.setAccessible(true);
      dis.setAccessible(true);
      nextByte.setAccessible(true);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("init() failed");
    }
  }
  
  @Test
  public void testGetOffset() {
    ASN1Handler ah = new ASN1Handler();
    
    try {
      Integer i = (Integer) getOffset.invoke(ah, null);
      
      assertEquals("0", i.toString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetOffset() failed");
    }  
  }
  
  @Test
  public void testGetDec() {
    ASN1Handler ah = new ASN1Handler();
    
    try {
      Byte B = (Byte) getDec.invoke(ah, null);
      byte b = -1;
      //org.junit.Assert.assertEquals(b, B);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testGetOffset() failed");
    }
  }
  
  @Test
  @Ignore
  public void testNext() {
    ASN1Handler ah = new ASN1Handler();
    String HomeDir = System.getProperty("user.home");
    HelpClass hc = new HelpClass();
    File x = hc.createFile(HomeDir, "testNext", "a");
    
    try {
      FileInputStream fis = new FileInputStream(x);
      DataInputStream ds = new DataInputStream(fis);
      dis.set(ah, ds);
      
      next.invoke(ah, null);
      
      byte b = (Byte) nextByte.get(ah);

      fis.close();
      x.delete();
      
      assertEquals(97, b);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testNext() failed");
    }
  }
  
  @Test
  @Ignore
  public void testNext2() {
    ASN1Handler ah = new ASN1Handler();
    String HomeDir = System.getProperty("user.home");
    HelpClass hc = new HelpClass();
    File x = hc.createFile(HomeDir, "testNext", "ab");
    
    try {
      FileInputStream fis = new FileInputStream(x);
      DataInputStream ds = new DataInputStream(fis);
      dis.set(ah, ds);
      
      for(int i = 0; i < 2; i++)
        next.invoke(ah, null);
      
      byte b = (Byte) nextByte.get(ah);

      fis.close();
      x.delete();
      
      assertEquals(98, b);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testNext2() failed");
    }
  }
  
  @Test
  public void testNext3() {
    ASN1Handler ah = new ASN1Handler();
    
    try {
      next.invoke(ah, null);
      fail("should'n execute this line, nullpointer exeption expected");
      
    } catch (Exception e) {
    }
  }
  
  @Test
  public void testTagForm() {
    ASN1Handler ah = new ASN1Handler();
    
    try {
      ah.nextByte = 32;
      Integer i = (Integer) tagForm.invoke(ah, null);
      
      assertEquals(1, i.intValue());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTagForm() failed");
    }
  }
  
  @Test
  public void testTagForm2() {
    ASN1Handler ah = new ASN1Handler();
    
    try {
      ah.nextByte = 1;
      Integer i = (Integer) tagForm.invoke(ah, null);
      
      assertEquals(2, i.intValue());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTagForm() failed");
    }
  }
  
  @Test
  public void testTagClass() {
    ASN1Handler ah = new ASN1Handler();
    
    try {
      ah.nextByte = (byte) 1;
      Integer i = (Integer) tagClass.invoke(ah, null);
      
      assertEquals(1, i.intValue());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTagForm() failed");
    }
  }
  
  @Test
  public void testTagClass2() {
    ASN1Handler ah = new ASN1Handler();
    
    try {
      ah.nextByte = (byte) 64;
      Integer i = (Integer) tagClass.invoke(ah, null);
      
      assertEquals(2, i.intValue());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTagForm() failed");
    }
  }
  
  @Test
  public void testTagClass3() {
    ASN1Handler ah = new ASN1Handler();
    
    try {
      ah.nextByte = (byte) 128;
      Integer i = (Integer) tagClass.invoke(ah, null);
      
      assertEquals(3, i.intValue());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTagForm() failed");
    }
  }
  
  @Test
  public void testTagClass4() {
    ASN1Handler ah = new ASN1Handler();
    
    try {
      ah.nextByte = (byte) 192;
      Integer i = (Integer) tagClass.invoke(ah, null);
      
      assertEquals(4, i.intValue());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTagForm() failed");
    }
  }
  
  @Test
  public void testLengthFormat() {
    ASN1Handler ah = new ASN1Handler();
    
    try {
      ah.nextByte = (byte) 1;
      Integer i = (Integer) lengthFormat.invoke(ah, new Object[] {2});
      
      assertEquals(1, i.intValue());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTagForm() failed");
    }
  }
  
  @Test
  public void testLengthFormat2() {
    ASN1Handler ah = new ASN1Handler();
    
    try {
      ah.nextByte = (byte) 128;
      Integer i = (Integer) lengthFormat.invoke(ah, new Object[] {2});
      
      assertEquals(3, i.intValue());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTagForm() failed");
    }
  }
  
  @Test
  public void testLengthFormat3() {
    ASN1Handler ah = new ASN1Handler();
    
    try {
      ah.nextByte = (byte) 128;
      Integer i = (Integer) lengthFormat.invoke(ah, new Object[] {1});
      
      assertEquals(2, i.intValue());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTagForm() failed");
    }
  }
  
  @Test
  public void testLengthFormat4() {
    ASN1Handler ah = new ASN1Handler();
    
    try {
      ah.nextByte = (byte) 192;
      Integer i = (Integer) lengthFormat.invoke(ah, new Object[] {1});
      
      assertEquals(3, i.intValue());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTagForm() failed");
    }
  }
  
  @Test
  public void testLengthFormat5() {
    ASN1Handler ah = new ASN1Handler();
    
    try {
      ah.nextByte = (byte) 1;
      Integer i = (Integer) lengthFormat.invoke(ah, new Object[] {1});
      
      assertEquals(1, i.intValue());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTagForm() failed");
    }
  }
  
  @Test
  public void testGetLenght() {
    ASN1Handler ah = new ASN1Handler();
    
    try{ 
      Integer i = (Integer)getLenght.invoke(ah, new Object[] {2});
      
      assertEquals(-1, i.intValue());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTagForm() failed");
    }
  }
  
  // TODO
  @Ignore
  public void testGetLenght2() {
    ASN1Handler ah = new ASN1Handler();
    
    try{ 
      Integer i = (Integer)getLenght.invoke(ah, new Object[] {3});
      
      assertEquals(-1, i.intValue());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTagForm() failed");
    }
  }
  
  @Test
  public void testGetLenght3() {
    ASN1Handler ah = new ASN1Handler();
    
    try{
      ah.nextByte = (byte) 127;
      Integer i = (Integer)getLenght.invoke(ah, new Object[] {1});
      
      assertEquals(127, i.intValue());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTagForm() failed");
    }
  }
  
  @Test
  public void testGetLenght4() {
    ASN1Handler ah = new ASN1Handler();
    
    try{
      ah.nextByte = (byte) 127;
      Integer i = (Integer)getLenght.invoke(ah, new Object[] {4});
      
    } catch (Exception e) {
      // Test passed
    }
  }
  
  @Test
  public void testTagID() {
    ASN1Handler ah = new ASN1Handler();
    
    try{
      ah.nextByte = (byte) 31;
      Integer i = (Integer)tagID.invoke(ah, null);
      
      assertEquals(31, i.intValue());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTagForm() failed");
    }
  }
  
  @Test
  public void testTagID2() {
    ASN1Handler ah = new ASN1Handler();
    
    try{
      ah.nextByte = (byte) 32;
      Integer i = (Integer)tagID.invoke(ah, null);
      
      assertEquals(0, i.intValue());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testTagForm() failed");
    }
  }
  
  @Test
  @Ignore
  public void testParse() {
    fail("Not yet implemented");
  }

  @Test
  @Ignore
  public void testInitInputStream() {
    ASN1Handler ah = new ASN1Handler();
    String homeDir = System.getProperty("user.home");
    HelpClass hc = new HelpClass();
    
    File x = hc.createFile(homeDir, "testInitFile", "Meaningles");
    DataInputStream ds = null;
    
    try {
      FileInputStream fi = new FileInputStream(x);
      ah.init(fi);
      
      ds = (DataInputStream) dis.get(ah);
      
      fi.close();
      x.delete();
      assertNotNull(ds);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testInitFile() failed");
    }
  }

  @Test
  @Ignore
  public void testInitFile() {
    ASN1Handler ah = new ASN1Handler();
    String homeDir = System.getProperty("user.home");
    HelpClass hc = new HelpClass();
    
    File x = hc.createFile(homeDir, "testInitFile", "Meaningles");
    DataInputStream ds = null;
    
    try {
      ah.init(x);
      
      ds = (DataInputStream) dis.get(ah);
      FileInputStream fi = (FileInputStream) fis.get(ah);
      
      fi.close();
      x.delete();
      assertNotNull(ds);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testInitFile() failed");
    }
  }

  @Test
  @Ignore
  public void testSetRulesFile() {
    fail("Not yet implemented");
  }

  @Test
  @Ignore
  public void testSetRulesString() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetBuffer() {
    ASN1Handler ah = new ASN1Handler();
    
    try {
      ah.setBuffer(100);
      
      int i = (Integer) buffer.get(ah);
      assertEquals(100, i);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testSetBuffer() failed");
    }
  }

  @Test
  public void testDoData() {
    ASN1Handler ah = new ASN1Handler();
    ASN1Rule ar = new ASN1Rule();
    ar.type = "boolean";
    
    byte[] b = new byte[] {0};
    
    try {
      assertEquals("false", ah.doData(ar, b));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testDoData() failed");
    }
  }
  
  @Test
  public void testDoData2() {
    ASN1Handler ah = new ASN1Handler();
    ASN1Rule ar = new ASN1Rule();
    ar.type = "boolean";
    
    byte[] b = new byte[] {1};
    
    try {
      assertEquals("true", ah.doData(ar, b));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testDoData() failed");
    }
  }
  
  @Test
  public void testDoData3() {
    ASN1Handler ah = new ASN1Handler();
    ASN1Rule ar = new ASN1Rule();
    ar.type = "INTEGER";

    byte[] b = new byte[] {9,9};
    try {
      assertEquals("2313", ah.doData(ar, b));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testDoData() failed");
    }
  }
  
  @Test
  public void testDoData4() {
    ASN1Handler ah = new ASN1Handler();
    ASN1Rule ar = new ASN1Rule();
    ar.type = "PrintableString";
    
    byte[] b = "text".getBytes();
    
    try {
      assertEquals("text", ah.doData(ar, b));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testDoData() failed");
    }
  }
  
  @Test
  public void testDoData5() {
    ASN1Handler ah = new ASN1Handler();
    ASN1Rule ar = new ASN1Rule();
    ar.type = "GraphicString";
    
    byte[] b = "text".getBytes();
    
    try {
      assertEquals("text", ah.doData(ar, b));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testDoData() failed");
    }
  }
  
  @Test
  public void testDoData6() {
    ASN1Handler ah = new ASN1Handler();
    ASN1Rule ar = new ASN1Rule();
    ar.type = "UTF8String";
    
    byte[] b = "text".getBytes();
    
    try {
      assertEquals("text", ah.doData(ar, b));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testDoData() failed");
    }
  }
  
  @Test
  public void testDoData7() {
    ASN1Handler ah = new ASN1Handler();
    ASN1Rule ar = new ASN1Rule();
    ar.type = "empty";
    
    byte[] b = "text".getBytes();
    
    try {
      assertEquals("text", ah.doData(ar, b));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testDoData() failed");
    }
  }
  
  @Test
  public void testDoDataBigInteger() {
    ASN1Handler ah = new ASN1Handler();
    ASN1Rule ar = new ASN1Rule();
    ar.type = "INTEGER";

    byte[] b = new byte[] {0, -1, -31, -6, 62};
    try {
      assertEquals("4292999742", ah.doData(ar, b));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testDoData() failed");
    }
  }

  @Test
  public void testDoReal() {
    ASN1Handler ah = new ASN1Handler();
    
    byte[] b = new byte[] {0,0,1};
    
    Double d = ah.doReal(b);
    assertEquals("1.0", d.toString());

  }

  @Test
  public void testDoBoolean() {
    ASN1Handler ah = new ASN1Handler();
    
    byte[] b = new byte[] {0};
    assertFalse("false expected", ah.doBoolean(b));
  }
  
  @Test
  public void testDoBoolean2() {
    ASN1Handler ah = new ASN1Handler();
    
    byte[] b = new byte[] {1};
    assertTrue("true expected", ah.doBoolean(b));
  }

  @Test
  public void testDoString() {
    ASN1Handler ah = new ASN1Handler();
    ASN1Rule ar = new ASN1Rule();
    ar.type = "utf8string";
    
    byte[] b = "text".getBytes();
    try {
      String s = ah.doString(ar, b);
      
      assertEquals("text", s);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testDoString() failed");
    }
  }
  
  @Test
  public void testDoString2() {
    ASN1Handler ah = new ASN1Handler();
    ASN1Rule ar = new ASN1Rule();
    ar.type = "nothing";
    
    byte[] b = "text".getBytes();
    try {
      String s = ah.doString(ar, b);
      
      assertEquals("text", s);
      
    } catch (Exception e) {
      e.printStackTrace();
      fail("testDoString() failed");
    }
  }

  /*public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(ASN1HandlerTest.class);
  }*/
}
