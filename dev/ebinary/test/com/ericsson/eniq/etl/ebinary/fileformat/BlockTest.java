package com.ericsson.eniq.etl.ebinary.fileformat;

import static org.junit.Assert.*;

import java.util.Properties;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class BlockTest {

	private Mockery context = new JUnit4Mockery();
	
	private static final String SEP = System.getProperty("file.separator");
	@Test
	public void testBlock() {
	    String propertiesPath = "C:"+SEP+"test";
	    Properties properties = new Properties();
	    int[] content = {1,2,3,4};
		try {
			Block block = new Block(propertiesPath, properties, content);
			assertNotNull(block.getSimpleValues());
		} catch (Exception e) {
			fail("Got Exception: "+e.getMessage());
		}
	}
}
