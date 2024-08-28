package com.ericsson.eniq.etl.ebinary.fileformat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;


public class DataBlockTest {

	@Test
	public void createNewDataBank(){
		DataBlock db = new DataBlock();
		assertNotNull(db);
	}
	
	@Test
	public void InWhatRowBankCanThisNewKeyBeAdded(){
		String key = "C0001";
		String instructions = "0, 1, Integer, 0";
		
		SubDataBlock bank1 = new SubDataBlock();
		bank1.addKey("C0001", "0");
		bank1.addKey("C0002", "0");
		bank1.addKey("C0003_0", "0");
		bank1.addKey("C0004_0", "0");

		SubDataBlock bank2 = new SubDataBlock();
		bank2.addKey("C0021", "1");
		bank2.addKey("C0022", "1");
		bank2.addKey("C0023_0", "1");
		bank2.addKey("C0024_0", "1");

		DataBlock db = new DataBlock();
		db.addKey(key, instructions);
		db.addKey("C0009", instructions);
		db.add(bank1);
		db.add(bank2);
	}
	
	@Test
	public void addKeyToMultipleDataBlocks_1(){
		DataBlock db = new DataBlock();
		String key = "C0001";
		String instructions = "0, 1, Integer, 1+2";
		db.addKey(key, instructions);
		assertEquals(2, db.size());
	}

	@Test
	public void addKeyToMultipleDataBlocks_2(){
		DataBlock db = new DataBlock();
		db.addKey("C0001", "0, 1, Integer, 1+2");
		db.addKey("C0002", "0, 1, Integer, 1+2");
		db.addKey("C0003", "0, 1, Integer, 2");
		db.addKey("C0004", "0, 1, Integer, 1");
		assertEquals(2, db.size());
		
		SubDataBlock rb1 = new SubDataBlock();
		rb1.addKey("C0001", "1");
		rb1.addKey("C0002");
		rb1.addKey("C0004");
		SubDataBlock rb2 = new SubDataBlock();
		rb2.addKey("C0001", "2");
		rb2.addKey("C0002");
		rb2.addKey("C0003");
		DataBlock expected = new DataBlock();
		expected.add(rb1);
		expected.add(rb2);
		
		assertEquals(expected, db);
	}

	@Test
	public void addLCSDataBlock(){
		DataBlock db = new DataBlock();
		
		db.addKey("C0691", 		 "0, 1, Integer, 1");
		db.addKey("C0692_0",  	 "0, 1, Integer, 1");
		db.addKey("C0699_0_0", 	 "0, 1, Integer, 1"); 
		db.addKey("C0700_0_0_0", "0, 1, Integer, 1"); 
		db.addKey("C0700_0_0_1", "0, 1, Integer, 1"); 
		
		db.addKey("C0710_0", 	 "0, 1, Integer, 2"); 
		db.addKey("C0717_0_0", 	 "0, 1, Integer, 2"); 
		db.addKey("C0718_0_0_0", "0, 1, Integer, 2"); 
		db.addKey("C0718_0_0_1", "0, 1, Integer, 2"); 
		
		db.addKey("C0731_0",   "0, 1, Integer, 3"); 
		db.addKey("C0736_0_0", "0, 1, Integer, 3");  
		db.addKey("C0736_0_1", "0, 1, Integer, 3");  
		
		db.addKey("C0741_0", "0, 1, Integer, 1+2+3");  
		db.addKey("C0742_0", "0, 1, Integer, 1+2+3");
		
//		db.addKey("C0743_0", "0, 1, Integer, 1");
//		db.addKey("C0743_1", "0, 1, Integer, 1");
//		db.addKey("C0744_0", "0, 1, Integer, 1+2+3");

		SubDataBlock subDataBlock1 = new SubDataBlock();
		SubDataBlock subDataBlock2 = new SubDataBlock();
		SubDataBlock subDataBlock3 = new SubDataBlock();
		
		Row row1 = new Row();
		row1.addKey("C0691");
		row1.addKey("C0692_0");
		row1.addKey("C0699_0_0"); 
		row1.addKey("C0700_0_0_0"); 
		row1.addKey("C0741_0");
		row1.addKey("C0742_0");  
		
		Row row2 = new Row();
		row2.addKey("C0691");
		row2.addKey("C0692_0");
		row2.addKey("C0699_0_0"); 
		row2.addKey("C0700_0_0_1"); 
		row2.addKey("C0741_0");
		row2.addKey("C0742_0");  

		Row row3 = new Row();
		row3.addKey("C0710_0");
		row3.addKey("C0717_0_0");
		row3.addKey("C0718_0_0_0"); 
		row3.addKey("C0741_0");
		row3.addKey("C0742_0");  

		Row row4 = new Row();
		row4.addKey("C0710_0");
		row4.addKey("C0717_0_0");
		row4.addKey("C0718_0_0_1"); 
		row4.addKey("C0741_0");
		row4.addKey("C0742_0");  

		Row row5 = new Row();
		row5.addKey("C0731_0");
		row5.addKey("C0736_0_0");
		row5.addKey("C0741_0");
		row5.addKey("C0742_0");  

		Row row6 = new Row();
		row6.addKey("C0731_0");
		row6.addKey("C0736_0_1");
		row6.addKey("C0741_0");
		row6.addKey("C0742_0");  

		subDataBlock1.add(row1);
		subDataBlock1.add(row2);
		subDataBlock2.add(row3);
		subDataBlock2.add(row4);
		subDataBlock3.add(row5);
		subDataBlock3.add(row6);
		
		DataBlock expected = new DataBlock();
		expected.add(subDataBlock1);
		expected.add(subDataBlock2);
		expected.add(subDataBlock3);

		System.out.println(db.toString());
		
		assertEquals(expected, db);
	
	}

	@Test
	public void LCSDataBlock(){
		//This tests real data taken from the LCS Data block.
		System.out.println("addkey_8 - 32 Rows");
		DataBlock bank = new DataBlock();
		String[] key = {"C0691", "C0692_0", "C0693_0", "C0695_0", "C0696_0_0", "C0697_0_0", 
				"C0699_0_0", "C0700_0_0_0", "C0708_0_0_0", "C0709_0_0_0", "C0700_0_0_1", "C0708_0_0_1", 
				"C0709_0_0_1", "C0700_0_0_2", "C0708_0_0_2", "C0709_0_0_2", "C0700_0_0_3", "C0708_0_0_3", 
				"C0709_0_0_3", "C0710_0", "C0711_0", "C0713_0", "C0714_0_0", "C0715_0_0", 
				"C0717_0_0", "C0718_0_0_0", "C0727_0_0_0", "C0718_0_0_1", "C0727_0_0_1", "C0718_0_0_2", 
				"C0727_0_0_2", "C0718_0_0_3", "C0727_0_0_3", "C0731_0", "C0732_0", "C0733_0", 
				"C0734_0_0", "C0735_0_0", "C0736_0_0", "C0734_0_1", "C0735_0_1", "C0736_0_1", 
				"C0741_0", "C0742_0", "C0743_0", "C0744_0", "C0745_0", "C0746_0"};

		long startTime = Calendar.getInstance().getTimeInMillis();
		for(int i = 0; i < key.length; i++){
			bank.addKey(key[i], "0, 1, Integer, 1");
		}
		long stopTime = Calendar.getInstance().getTimeInMillis();

		long duration = stopTime - startTime;
		System.out.println("The time taken to process LCS for one subscriber (42 keys) = "+duration+"ms");
		for(int i = 0; i < bank.size(); i++){
			System.out.println(i+" = "+bank.get(i).toString());
		}
		assertEquals(32, bank.flatten().size());
//		assertTrue(isUnique(bank));
	}

	/**
	 * This tests for duplicates in the data. The GPRS dataBlock
	 * uncovered a bug which generated duplicated data. This check 
	 * must pass.
	 */
	@Test
	public void GPRSDataBlock(){
		DataBlock db = new DataBlock();
		//ROW 1
		db.addKey("C0483", 	 "0, 1, Integer, 1");
		db.addKey("C0484", 	 "0, 1, Integer, 1");
		db.addKey("C0485", 	 "0, 1, Integer, 1");
		db.addKey("C0487", 	 "0, 1, Integer, 1");
		db.addKey("C0488_0", "0, 1, Integer, 1");
		db.addKey("C0489_0", "0, 1, Integer, 1");
		db.addKey("C0510_0", "0, 1, Integer, 1");
		db.addKey("C0512_0", "0, 1, Integer, 1");
		db.addKey("C0514_0", "0, 1, Integer, 1");
		db.addKey("C0515_0", "0, 1, Integer, 1");
		db.addKey("C0516_0", "0, 1, Integer, 1");
		
		//ROW 2
		db.addKey("C0488_1",	"0, 1, Integer, 1");
		db.addKey("C0489_1",    "0, 1, Integer, 1");
		db.addKey("C0490_1_0", 	"0, 1, Integer, 1");
		db.addKey("C0491_1_0", 	"0, 1, Integer, 1");
		db.addKey("C0492_1_0", 	"0, 1, Integer, 1");
		db.addKey("C0493_1_0", 	"0, 1, Integer, 1");
		db.addKey("C0510_1", 	"0, 1, Integer, 1");
		db.addKey("C0512_1", 	"0, 1, Integer, 1");
		db.addKey("C0514_1", 	"0, 1, Integer, 1");
		db.addKey("C0515_1", 	"0, 1, Integer, 1");
		db.addKey("C0516_1", 	"0, 1, Integer, 1");
		
		//ROW 3
		db.addKey("C0488_2", 	"0, 1, Integer, 1");
		db.addKey("C0489_2", 	"0, 1, Integer, 1");
		db.addKey("C0494_2_0", 	"0, 1, Integer, 1");
		db.addKey("C0496_2_0", 	"0, 1, Integer, 1");
		db.addKey("C0498_2_0", 	"0, 1, Integer, 1");
		db.addKey("C0500_2_0", 	"0, 1, Integer, 1");
		db.addKey("C0502_2_0", 	"0, 1, Integer, 1");
		db.addKey("C0504_2_0", 	"0, 1, Integer, 1");
		db.addKey("C0506_2_0", 	"0, 1, Integer, 1");
		db.addKey("C0508_2_0", 	"0, 1, Integer, 1");
		db.addKey("C0510_2", 	"0, 1, Integer, 1");
		db.addKey("C0512_2", 	"0, 1, Integer, 1");
		db.addKey("C0514_2", 	"0, 1, Integer, 1");
		db.addKey("C0515_2", 	"0, 1, Integer, 1");
		db.addKey("C0516_2", 	"0, 1, Integer, 1");
		
		System.out.println(db.toString());
		
		//ROW 1
		Row row0 = new Row();
		row0.addKey("C0483");
		row0.addKey("C0484");
		row0.addKey("C0485");
		row0.addKey("C0487");
		row0.addKey("C0488_0");
		row0.addKey("C0489_0");
		row0.addKey("C0510_0");
		row0.addKey("C0512_0");
		row0.addKey("C0514_0");
		row0.addKey("C0515_0");
		row0.addKey("C0516_0");

		
		//ROW 2
		Row row1 = new Row();
		row1.addKey("C0483");
		row1.addKey("C0484");
		row1.addKey("C0485");
		row1.addKey("C0487");
		row1.addKey("C0488_1");
		row1.addKey("C0489_1");
		row1.addKey("C0490_1_0");
		row1.addKey("C0491_1_0");
		row1.addKey("C0492_1_0");
		row1.addKey("C0493_1_0");
		row1.addKey("C0510_1");
		row1.addKey("C0512_1");
		row1.addKey("C0514_1");
		row1.addKey("C0515_1");
		row1.addKey("C0516_1");


		//ROW 3
		Row row2 = new Row();
		row2.addKey("C0483");
		row2.addKey("C0484");
		row2.addKey("C0485");
		row2.addKey("C0487");
		row2.addKey("C0488_2");
		row2.addKey("C0489_2");
		row2.addKey("C0494_2_0");
		row2.addKey("C0496_2_0");
		row2.addKey("C0498_2_0");
		row2.addKey("C0500_2_0");
		row2.addKey("C0502_2_0");
		row2.addKey("C0504_2_0");
		row2.addKey("C0506_2_0");
		row2.addKey("C0508_2_0");
		row2.addKey("C0510_2");
		row2.addKey("C0512_2");
		row2.addKey("C0514_2");
		row2.addKey("C0515_2");
		row2.addKey("C0516_2");

		//SubDataBlock
		SubDataBlock subDataBlock = new SubDataBlock();
		subDataBlock.add(row0);
		subDataBlock.add(row1);
		subDataBlock.add(row2);
		
		DataBlock expected = new DataBlock();
		expected.add(subDataBlock);

		assertEquals(expected, db);
	}
	@Test
	public void addGPRSDataBlock(){
		DataBlock db = new DataBlock();
		//ROW 1
		db.addKey("C0483", 	 "0, 1, Integer, 1");
		db.addKey("C0484",   "0, 1, Integer, 1");
		db.addKey("C0485",   "0, 1, Integer, 1");
		db.addKey("C0487", 	 "0, 1, Integer, 1"); 
		db.addKey("C0488_0", "0, 1, Integer, 1"); 

		//REPEAT, ROW 2
		db.addKey("C0488_1", "0, 1, Integer, 1");
		db.addKey("C0517_1_0", "0, 1, Integer, 1");
		
		//REPEAT, ROW 3
		db.addKey("C0488_2", "0, 1, Integer, 1");
		db.addKey("C0517_2_0", "0, 1, Integer, 1");
		//REPEAT, ROW 4
		db.addKey("C0517_2_1", "0, 1, Integer, 1");
		db.addKey("C0517_2_1_0", "0, 1, Integer, 1");
		
		//REPEAT, ROW 5
		db.addKey("C0488_3", "0, 1, Integer, 1");
		db.addKey("C0517_3_0", "0, 1, Integer, 1");
		//REPEAT, ROW 6
		db.addKey("C0517_3_1", "0, 1, Integer, 1");
		
		//ROW 1
		Row row1 = new Row();
		row1.addKey("C0483");
		row1.addKey("C0484");
		row1.addKey("C0485");
		row1.addKey("C0487");
		row1.addKey("C0488_0");
		//ROW 2
		Row row2 = new Row();
		row2.addKey("C0483");
		row2.addKey("C0484");
		row2.addKey("C0485");
		row2.addKey("C0487");
		row2.addKey("C0488_1");
		row2.addKey("C0517_1_0");
		//ROW 3
		Row row3 = new Row();
		row3.addKey("C0483");
		row3.addKey("C0484");
		row3.addKey("C0485");
		row3.addKey("C0487");
		row3.addKey("C0488_2");
		row3.addKey("C0517_2_0");
		// ROW 4
		Row row4 = new Row();
		row4.addKey("C0483");
		row4.addKey("C0484");
		row4.addKey("C0485");
		row4.addKey("C0487");
		row4.addKey("C0488_2");
		row4.addKey("C0517_2_1");
		row4.addKey("C0517_2_1_0");
		// ROW 5
		Row row5 = new Row();
		row5.addKey("C0483");
		row5.addKey("C0484");
		row5.addKey("C0485");
		row5.addKey("C0487");
		row5.addKey("C0488_3");
		row5.addKey("C0517_3_0");
		// ROW 6
		Row row6 = new Row();
		row6.addKey("C0483");
		row6.addKey("C0484");
		row6.addKey("C0485");
		row6.addKey("C0487");
		row6.addKey("C0488_3");
		row6.addKey("C0517_3_1");	
		
		//SubDataBlock
		SubDataBlock subDataBlock = new SubDataBlock();
		subDataBlock.add(row1);
		subDataBlock.add(row2);
		subDataBlock.add(row3);
		subDataBlock.add(row4);
		subDataBlock.add(row5);
		subDataBlock.add(row6);


		DataBlock expected = new DataBlock();
		expected.add(subDataBlock);

		assertEquals(expected, db);
	}

	@Test
	public void addSPAMDataBlock(){
		DataBlock db = new DataBlock();
		
		db.addKey("C0766", 	 "0, 1, Integer, 1");
		db.addKey("C0767",   "0, 1, Integer, 1");
		db.addKey("C0768",   "0, 1, Integer, 1");
		db.addKey("C0769", 	 "0, 1, Integer, 1"); 
		db.addKey("C0770_0", "0, 1, Integer, 1"); 
		db.addKey("C0778_0", "0, 1, Integer, 1"); 
		db.addKey("C0770_1", "0, 1, Integer, 1"); 
		db.addKey("C0778_1", "0, 1, Integer, 1"); 
		db.addKey("C0770_2", "0, 1, Integer, 1"); 
		db.addKey("C0778_2", "0, 1, Integer, 1"); 
		db.addKey("C0770_3", "0, 1, Integer, 1"); 
		db.addKey("C0778_3", "0, 1, Integer, 1"); 
		
		SubDataBlock rb0 = new SubDataBlock();
		rb0.addKey("C0766", "1");
		rb0.addKey("C0767");
		rb0.addKey("C0768");
		rb0.addKey("C0769"); 
		rb0.addKey("C0770_0"); 
		rb0.addKey("C0778_0"); 
		rb0.addKey("C0770_1"); 
		rb0.addKey("C0778_1"); 
		rb0.addKey("C0770_2"); 
		rb0.addKey("C0778_2"); 
		rb0.addKey("C0770_3"); 
		rb0.addKey("C0778_3"); 

		DataBlock expected = new DataBlock();
		expected.add(rb0);
		System.out.println(db.toString());
		assertEquals(expected, db);
	}

	@Test
	public void addKeyToMultipleDataBlocks_4(){
		DataBlock db = new DataBlock();
		db.addKey("C0000", "0, 1, Integer, 0");
		db.addKey("C0001", "0, 1, Integer, 1");
		db.addKey("C0002", "0, 1, Integer, 1");
		db.addKey("C0003", "0, 1, Integer, 2");
		db.addKey("C0004", "0, 1, Integer, 1+2");
		db.addKey("C0005_0", "0, 1, Integer, 0");

		
		SubDataBlock rb0 = new SubDataBlock();
		rb0.addKey("C0000", "0");
		rb0.addKey("C0005_0");
		SubDataBlock rb1 = new SubDataBlock();
		rb1.addKey("C0000");
		rb1.addKey("C0001", "1");
		rb1.addKey("C0002");
		rb1.addKey("C0004");
		rb1.addKey("C0005_0");
		SubDataBlock rb2 = new SubDataBlock();
		rb2.addKey("C0000");
		rb2.addKey("C0003", "2");
		rb2.addKey("C0004");
		rb2.addKey("C0005_0");
		DataBlock expected = new DataBlock();
		expected.add(rb0);
		expected.add(rb1);
		expected.add(rb2);
		
		assertEquals(expected, db);
		
	}

	@Test
	public void duplicateCheck_1(){
		//ROW 1
		Row row1 = new Row();
		row1.addKey("C0483");
		row1.addKey("C0484");
		row1.addKey("C0485");
		row1.addKey("C0487");
		row1.addKey("C0488_0");
		//ROW 2
		Row row2 = new Row();
		row2.addKey("C0483");
		row2.addKey("C0484");
		row2.addKey("C0485");
		row2.addKey("C0487");
		row2.addKey("C0488_1");
		row2.addKey("C0517_1_0");
		//SubDataBlock
		SubDataBlock subDataBlock = new SubDataBlock();
		subDataBlock.add(row1);
		subDataBlock.add(row2);
		
		DataBlock expected = new DataBlock();
		expected.add(subDataBlock);
		boolean answer = expected.duplicateCheck();
		assertFalse(answer);
	}
	
	
	@Test
	public void duplicateCheck_2(){
		//ROW 1
		Row row1 = new Row();
		row1.addKey("C0483");
		row1.addKey("C0484");
		row1.addKey("C0485");
		row1.addKey("C0487");
		row1.addKey("C0488_0");
		//ROW 2
		Row row2 = new Row();
		row2.addKey("C0483");
		row2.addKey("C0484");
		row2.addKey("C0485");
		row2.addKey("C0487");
		row2.addKey("C0488_1");
		row2.addKey("C0517_1_0");
		//ROW 1
		Row row3 = new Row();
		row3.addKey("C0483");
		row3.addKey("C0484");
		row3.addKey("C0485");
		row3.addKey("C0487");
		row3.addKey("C0488_0");
		//SubDataBlock
		SubDataBlock subDataBlock = new SubDataBlock();
		subDataBlock.add(row1);
		subDataBlock.add(row2);
		subDataBlock.add(row3);
		
		DataBlock expected = new DataBlock();
		expected.add(subDataBlock);
		boolean answer = expected.duplicateCheck();
		assertTrue(answer);
	}

	@Test
	public void duplicateCheck_3(){
		//ROW 1
		Row row1 = new Row();
		row1.addKey("C0483");
		row1.addKey("C0484");
		row1.addKey("C0485");
		row1.addKey("C0487");
		row1.addKey("C0488_0");
		//ROW 2
		Row row2 = new Row();
		row2.addKey("C0483");
		row2.addKey("C0484");
		row2.addKey("C0485");
		row2.addKey("C0487");
		row2.addKey("C0488_1");
		row2.addKey("C0517_1_0");
		//ROW 3
		Row row3 = new Row();
		row3.addKey("C0483");
		row3.addKey("C0484");
		row3.addKey("C0485");
		row3.addKey("C0487");
		row3.addKey("C0488_1");
		//ROW 4
		Row row4 = new Row();
		row4.addKey("C0483");
		row4.addKey("C0484");
		row4.addKey("C0485");
		row4.addKey("C0487");
		row4.addKey("C0488_1");
		row4.addKey("C0517_1_0");
		
		//SubDataBlock
		SubDataBlock subDataBlock = new SubDataBlock();
		subDataBlock.add(row1);
		subDataBlock.add(row2);
		subDataBlock.add(row3);
		subDataBlock.add(row4);
		
		DataBlock expected = new DataBlock();
		expected.add(subDataBlock);
		boolean answer = expected.duplicateCheck();
		assertTrue(answer);
	}

	@Test
	public void duplicateCheck_4(){
		//ROW 1
		Row row1 = new Row();
		row1.addKey("C0483");
		row1.addKey("C0484");
		row1.addKey("C0485");
		row1.addKey("C0487");
		row1.addKey("C0488_0");
		//ROW 2
		Row row2 = new Row();
		row2.addKey("C0483");
		row2.addKey("C0484");
		row2.addKey("C0485");
		row2.addKey("C0487");
		row2.addKey("C0488_1");
		row2.addKey("C0517_1_0");
		//ROW 3
		Row row3 = new Row();
		row3.addKey("C0483");
		row3.addKey("C0484");
		row3.addKey("C0485");
		row3.addKey("C0487");
		row3.addKey("C0488_1");
		//ROW 4
		Row row4 = new Row();
		row4.addKey("C0483");
		row4.addKey("C0484");
		row4.addKey("C0485");
		row4.addKey("C0487");
		row4.addKey("C0488_1");
		row4.addKey("C0517_1_0");
		
		//SubDataBlock
		SubDataBlock subDataBlock1 = new SubDataBlock();
		subDataBlock1.add(row1);
		subDataBlock1.add(row2);
		subDataBlock1.setId("1");

		//SubDataBlock
		SubDataBlock subDataBlock2 = new SubDataBlock();
		subDataBlock2.add(row3);
		subDataBlock2.add(row4);
		subDataBlock2.setId("1");
		
		DataBlock expected = new DataBlock();
		expected.add(subDataBlock1);
		expected.add(subDataBlock2);
		boolean answer = expected.duplicateCheck();
		assertTrue(answer);
	}
	@Test
	public void flattenTheData_1(){
		DataBlock db = new DataBlock();
		
		db.addKey("C0765", 	 "0, 1, Integer, 0");
		db.addKey("C0766", 	 "0, 1, Integer, 1");
		db.addKey("C0767",   "0, 1, Integer, 1");
		db.addKey("C0768",   "0, 1, Integer, 1");
		db.addKey("C0769", 	 "0, 1, Integer, 1"); 
		db.addKey("C0770_0", "0, 1, Integer, 1"); 
		db.addKey("C0778_0", "0, 1, Integer, 1"); 
		db.addKey("C0770_1", "0, 1, Integer, 1"); 
		db.addKey("C0778_1", "0, 1, Integer, 1"); 

		List<Row> rows = db.flatten();
		Row kw1 = new Row();
		kw1.addKey("C0765");
		kw1.addKey("C0766");
		kw1.addKey("C0767");
		kw1.addKey("C0768");
		kw1.addKey("C0769");
		kw1.addKey("C0770_0");
		kw1.addKey("C0778_0");
		Row kw2 = new Row();
		kw2.addKey("C0765");
		kw2.addKey("C0766");
		kw2.addKey("C0767");
		kw2.addKey("C0768");
		kw2.addKey("C0769");
		kw2.addKey("C0770_1");
		kw2.addKey("C0778_1");
		List<Row> expected = new ArrayList<Row>();
		expected.add(kw1);
		expected.add(kw2);
		
		assertEquals(expected, rows);
		
	}
	
	@Test
	public void flattenTheData_2(){

		//ROW 1
		Row row1 = new Row();
		row1.addKey("C0483");
		row1.addKey("C0484");
		row1.addKey("C0485");
		row1.addKey("C0487");
		row1.addKey("C0488_0");
		//ROW 2
		Row row2 = new Row();
		row2.addKey("C0483");
		row2.addKey("C0484");
		row2.addKey("C0485");
		row2.addKey("C0487");
		row2.addKey("C0488_1");
		row2.addKey("C0517_1_0");
		//ROW 3
		Row row3 = new Row();
		row3.addKey("C0483");
		row3.addKey("C0484");
		row3.addKey("C0485");
		row3.addKey("C0487");
		row3.addKey("C0488_1");
		//ROW 4
		Row row4 = new Row();
		row4.addKey("C0483");
		row4.addKey("C0484");
		row4.addKey("C0485");
		row4.addKey("C0487");
		row4.addKey("C0488_1");
		row4.addKey("C0517_1_0");

		List<Row> expected = new ArrayList<Row>();
		expected.add(row1);
		expected.add(row2);
		expected.add(row3);
		expected.add(row4);
		
		//SubDataBlock
		SubDataBlock subDataBlock1 = new SubDataBlock();
		subDataBlock1.add(row1);
		subDataBlock1.add(row2);
		subDataBlock1.setId("1");
		//SubDataBlock
		SubDataBlock subDataBlock2 = new SubDataBlock();
		subDataBlock2.add(row3);
		subDataBlock2.add(row4);
		subDataBlock2.setId("1");

		DataBlock input = new DataBlock();
		input.add(subDataBlock1);
		input.add(subDataBlock2);
		
		List<Row> rows = input.flatten();
		assertEquals(expected, rows);
	}

	/**
	 * This is a test to check that each row is unique. True if unique, 
	 * otherwise False is returned.
	 * @param bank
	 * @return
	 */
	private boolean isUnique(DataBlock bank){
			List<Row> rows = bank.flatten();
			for(int i = 0; i < rows.size(); i++){
				for(int j = i+1; j < rows.size(); j++){
					if(rows.get(i).equals(rows.get(j))){
						return true;
					}
				}
			}
			return false;
		}

}
