package com.ericsson.eniq.etl.ebinary.fileformat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.math.BigInteger;

import org.junit.Test;


public class RowTest {

	@Test
	public void createKeyRow(){
		Row row = new Row();
		assertNotNull(row);
	}
	
	@Test
	public void toStringTest(){
		Row row = new Row();
		String key = "C0001";
		row.addKey(key);
		row.addKey("C0002");
		row.addKey("C0003");
		row.addKey("C0004");
		String output = row.toString();
		String expected = "[C0001, C0002, C0003, C0004]";
		assertEquals(expected, output);
	}
		
	@Test
	public void getInsertAfterRow_1(){
		Row row = new Row();
		row.addKey("C00021");
		row.addKey("C0002");
		row.addKey("C0003");
		row.addKey("C0004_0");
		String key = "C0004_1";

		Row expectedRow = new Row();
		expectedRow.addKey("C00021");
		expectedRow.addKey("C0002");
		expectedRow.addKey("C0003");

		Row newRow = row.getInsertAfter(key);
		assertEquals(expectedRow, newRow);
	}
	
	@Test
	public void getInsertAfterRow_2(){
		Row row = new Row();
		row.addKey("C00021");
		row.addKey("C0002");
		row.addKey("C0003");
		row.addKey("C0004_0");
		String key = "C0005_0";

		Row expectedRow = new Row();
		expectedRow.addKey("C00021");
		expectedRow.addKey("C0002");
		expectedRow.addKey("C0003");
		expectedRow.addKey("C0004_0");

		Row newRow = row.getInsertAfter(key);
		assertEquals(expectedRow, newRow);
	}
	
	@Test
	public void isInRange(){
		Row row = new Row();
		row.addKey("C00021");
		boolean inRange = false;
		row.setActive(inRange);
		boolean answer = row.isActive();
		assertFalse(answer);
	}
	
	@Test
	public void testWhatToDoWhenAddingKey_1(){
		Row row = new Row();
		row.addKey("C0001");
		String key = "C0002";

		assertEquals(BlockENum.WRITE_KEY, row.handleKey(key));
	}
	@Test
	public void testWhatToDoWhenAddingKey_2(){
		Row row = new Row();
		row.addKey("C0001_0");
		String key = "C0002_0_0";

		assertEquals(BlockENum.WRITE_KEY, row.handleKey(key));
	}

	@Test
	public void testWhatToDoWhenAddingKey_3(){
		Row row = new Row();
		row.addKey("C0001_0");
		String key = "C0002";

		assertEquals(BlockENum.RESET_RANGE_WRITE_KEY, row.handleKey(key));
	}
	@Test
	public void testWhatToDoWhenAddingKey_4(){
		Row row = new Row();
		row.addKey("C0001_0");
		String key = "C0001_1";

		assertEquals(BlockENum.COPY_RANGE_WRITE_KEY, row.handleKey(key));
	}

	@Test
	public void testWhatToDoWhenAddingKey_5(){
		Row row = new Row();
		row.addKey("C0001_1");
		row.addKey("C0002_1_0");
		String key = "C0001_2";

		assertEquals(BlockENum.COPY_RANGE_WRITE_KEY, row.handleKey(key));
	}

	@Test
	public void getTheSubdataBlockIdFromInstructions_1(){
		Row row = new Row();
		String subdataBlockId = row.getSubdataBlockId("0, 8, Binary, 0+1");
		assertEquals("0+1", subdataBlockId);
	}

	@Test
	public void getTheSubdataBlockIdFromInstructions_2(){
		Row row = new Row();
		String subdataBlockId = row.getSubdataBlockId("0, 8, Binary");
		assertEquals("0", subdataBlockId);
	}
}
