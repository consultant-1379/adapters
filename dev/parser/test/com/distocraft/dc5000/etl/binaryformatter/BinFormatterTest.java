package com.distocraft.dc5000.etl.binaryformatter;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * @author ejarsok
 *
 */

public class BinFormatterTest {

  @Test
  public void testGetClasses() {
    /* Calling the tested method */
    HashMap hm = BinFormatter.getClasses();
    
    String expected = "true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true";
    String actual = hm.containsKey("bit") + "," + hm.containsKey("tinyint") + "," + hm.containsKey("smallint") + "," + hm.containsKey("int")
                     + "," + hm.containsKey("integer") + "," + hm.containsKey("unsigned int") + "," + hm.containsKey("bigint") 
                     + "," + hm.containsKey("unsigned bigint") + "," + hm.containsKey("float") + "," + hm.containsKey("double")
                       + "," + hm.containsKey("char") + "," + hm.containsKey("varchar") + "," + hm.containsKey("binary")
                        + "," + hm.containsKey("varbinary") + "," + hm.containsKey("date") + "," + hm.containsKey("time")
                        + "," + hm.containsKey("datetime") + "," + hm.containsKey("numeric") + "," + hm.containsKey("decimal");
    
    assertEquals(expected, actual);
  }

}
