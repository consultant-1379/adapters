/**
 * 
 */
package com.ericsson.eniq.etl.bcd;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.jmock.Expectations;
import org.jmock.api.Expectation;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.distocraft.dc5000.etl.parser.Main;
import com.distocraft.dc5000.etl.parser.MeasurementFile;
import com.distocraft.dc5000.etl.parser.MeasurementFileImpl;
import com.distocraft.dc5000.etl.parser.SourceFile;
import com.distocraft.dc5000.repository.cache.DataFormatCache;

/**
 * @author edeamai
 *
 */
public class BCDParserTest extends BaseMock {

	private static BCDParser objectUnderTest;
	private static Main mockedMainParser;
	private static AttributesImpl mockedAttributes;
	//private static Fdn mockedFdn;
	private static SourceFile mockedSourceFile;
	private MeasurementFileImpl mockedMeasFile;
	private DataFormatCache mockedDFC;
	
	Field sourceFile_Field;
	Field techPack_Field;
	Field setType_Field;
	Field setName_Field;
	Field workerName_Field;
	Field status_Field;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}

	@Before
	public void setUp() throws Exception {
		//Setting up the test object:
		objectUnderTest = new BCDParser();
		mockedMainParser = context.mock(Main.class);
		objectUnderTest.init(mockedMainParser, "x", "x", "x", "x");
		//Making some of the test object's private Fields accessible:
		techPack_Field = BCDParser.class.getDeclaredField("techPack");
		techPack_Field.setAccessible(true);
		setType_Field = BCDParser.class.getDeclaredField("setType");
		setType_Field.setAccessible(true);
		setName_Field = BCDParser.class.getDeclaredField("setName");
		setName_Field.setAccessible(true);
		workerName_Field = BCDParser.class.getDeclaredField("workerName");
		workerName_Field.setAccessible(true);
		status_Field = BCDParser.class.getDeclaredField("status");
		status_Field.setAccessible(true);
		
		//A mocked AttributesImpl object is needed for some of the tests
		mockedAttributes = context.mock(AttributesImpl.class);
		
		//mockedFdn = context.mock(Fdn.class);
		//objectUnderTest.fdn = mockedFdn;
		objectUnderTest.fdn = new Fdn(true, true);
		
		
		mockedSourceFile = context.mock(SourceFile.class);
		Field sourceFile_Field = BCDParser.class.getDeclaredField("sourceFile");
		sourceFile_Field.setAccessible(true);
		sourceFile_Field.set(objectUnderTest, mockedSourceFile);
		//Method addMeasurementFile_Method = SourceFile.class.getDeclaredMethod("addMeasurementFile", SourceFile.class);
		//addMeasurementFile_Method.setAccessible(true);
		
		//A mocked MeasurementFile is needed in one test.
		mockedMeasFile = context.mock(MeasurementFileImpl.class);
		
		mockedDFC = context.mock(DataFormatCache.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	

	@Test
	public final void testStartElement_bulkCmConfigDataFile() throws SAXException{
		
		objectUnderTest.startElement("configData.xsd", "bulkCmConfigDataFile", "bulkCmConfigDataFile", new AttributesImpl());
		
		assertTrue(objectUnderTest.elementStart.equalsIgnoreCase("bulkCmConfigDataFile"));
		assertTrue(null == objectUnderTest.elementParent);
		assertTrue(null == objectUnderTest.moId);
		assertTrue(null == objectUnderTest.moClass);
		assertTrue(objectUnderTest.fdnDepth == 0);
		assertTrue(null == objectUnderTest.parentMoClass);
		assertTrue(null == objectUnderTest.parentMoId);
		assertTrue(!objectUnderTest.collect);
	}
	
	@Test
	public final void testStartElement_fileHeader() throws SAXException{
		
		objectUnderTest.elementStart = "bulkCmConfigDataFile";
		objectUnderTest.docDataMap = new HashMap<String, String>();
		context.checking(new Expectations()
			{{	
					one(mockedAttributes).getValue("id"); will(returnValue(null));
					allowing(mockedAttributes).getLength(); will(returnValue(2));
					allowing(mockedAttributes).getLocalName(0); will(returnValue("vendorName"));
					allowing(mockedAttributes).getValue(0); will(returnValue("Ericsson"));
					allowing(mockedAttributes).getLocalName(1); will(returnValue("fileFormatVersion"));
					allowing(mockedAttributes).getValue(1); will(returnValue("32.615 V4.5"));
			}}
		);
		
		objectUnderTest.startElement("configData.xsd", "fileHeader", "fileHeader", mockedAttributes);
		
		assertTrue(objectUnderTest.elementStart.equalsIgnoreCase("fileHeader"));
		assertTrue(objectUnderTest.elementParent.equalsIgnoreCase("bulkCmConfigDataFile"));
		assertTrue(null == objectUnderTest.moId);
		assertTrue(null == objectUnderTest.moClass);
		assertTrue(objectUnderTest.fdnDepth == 0);
		assertTrue(null == objectUnderTest.parentMoClass);
		assertTrue(null == objectUnderTest.parentMoId);
		assertTrue(!objectUnderTest.collect);
		assertTrue(objectUnderTest.docDataMap.size()==2);
	}

	@Test
	public final void testStartElement_configData() throws SAXException{
		
		objectUnderTest.elementStart = "fileHeader";
		objectUnderTest.docDataMap = new HashMap<String, String>();
		objectUnderTest.docDataMap.put("vendorName", "Ericsson");
		objectUnderTest.docDataMap.put("fileFormatVersion", "32.615 V4.5");
		objectUnderTest.fdnDepth = -1;
		context.checking(new Expectations()
			{{	
					one(mockedAttributes).getValue("id"); will(returnValue(null));
					allowing(mockedAttributes).getLength(); will(returnValue(1));
					allowing(mockedAttributes).getLocalName(0); will(returnValue("dnPrefix"));
					allowing(mockedAttributes).getValue(0); will(returnValue("Undefined"));
			}}
		);
		
		objectUnderTest.startElement("configData.xsd", "configData", "configData", mockedAttributes);
		
		assertTrue(objectUnderTest.elementStart.equalsIgnoreCase("configData"));
		assertTrue(objectUnderTest.elementParent.equalsIgnoreCase("fileHeader"));
		assertTrue(null == objectUnderTest.moId);
		assertTrue(null == objectUnderTest.moClass);
		assertTrue(objectUnderTest.fdnDepth == -1);
		assertTrue(null == objectUnderTest.parentMoClass);
		assertTrue(null == objectUnderTest.parentMoId);
		assertTrue(!objectUnderTest.collect);
		assertTrue(objectUnderTest.docDataMap.size()==3);
	}

	@Test
	public final void testStartElement_SubNetwork() throws SAXException{
		
		objectUnderTest.elementStart = "configData";
		objectUnderTest.docDataMap = new HashMap<String, String>();
		objectUnderTest.fdnDepth = -1;
		objectUnderTest.dataMap = new HashMap<String, String>();
		context.checking(new Expectations()
			{{	
					one(mockedAttributes).getValue("id"); will(returnValue("ONRM_RootMo_R"));
					one(mockedAttributes).getValue("id"); will(returnValue("ONRM_RootMo_R"));
					//one(mockedFdn).handle("SubNetwork=ONRM_RootMo_R", 0); 
					//one(mockedFdn).getFdn(); will(returnValue("SubNetwork=ONRM_RootMo"));
			}}
		);
		
		objectUnderTest.startElement("genericNrm.xsd", "SubNetwork", "xn:SubNetwork", mockedAttributes);
		
		assertTrue(objectUnderTest.elementStart.equalsIgnoreCase("xn:SubNetwork"));
		assertTrue(objectUnderTest.elementParent.equalsIgnoreCase("configData"));
		assertTrue(objectUnderTest.moId.equalsIgnoreCase("ONRM_RootMo_R"));
		assertTrue(objectUnderTest.moClass.equalsIgnoreCase("SubNetwork"));
		assertTrue(objectUnderTest.fdnDepth == 0);
		assertTrue(objectUnderTest.parentMoClass.equalsIgnoreCase("SubNetwork"));
		assertTrue(objectUnderTest.parentMoId.equalsIgnoreCase("ONRM_RootMo_R"));
		assertTrue(!objectUnderTest.collect);
		assertTrue(objectUnderTest.docDataMap.size()==0);
		assertTrue(objectUnderTest.attributeDepth==0);
		
	}
	
	@Test
	public final void testStartElement_attributes() throws SAXException{
		
		objectUnderTest.elementStart = "xn:SubNetwork";
		objectUnderTest.docDataMap = new HashMap<String, String>();
		objectUnderTest.docDataMap.put("vendorName", "Ericsson");
		objectUnderTest.docDataMap.put("fileFormatVersion", "32.615 V4.5");
		objectUnderTest.docDataMap.put("dnPrefix", "Undefined");
		objectUnderTest.fdnDepth = 1;
		
		context.checking(new Expectations()
			{{	
					one(mockedAttributes).getValue("id"); will(returnValue(null));
					allowing(mockedAttributes).getLength(); will(returnValue(0));
			}}
		);
		
		objectUnderTest.startElement("genericNrm.xsd", "attributes", "xn:attributes", mockedAttributes);
		
		assertTrue(objectUnderTest.elementStart.equalsIgnoreCase("xn:attributes"));
		assertTrue(objectUnderTest.elementParent.equalsIgnoreCase("xn:SubNetwork"));
		assertTrue(null==objectUnderTest.moId);
		assertTrue(null==objectUnderTest.moClass);
		assertTrue(objectUnderTest.fdnDepth == 1);
		assertTrue(null==objectUnderTest.parentMoClass);
		assertTrue(null==objectUnderTest.parentMoId);
		assertTrue(objectUnderTest.collect);
		assertTrue(objectUnderTest.docDataMap.size()==3);
		assertTrue(objectUnderTest.attributeDepth==0);
		
	}

	@Test
	public final void testEndElement() throws Exception {
		/*
		<?xml version="1.0" encoding="UTF-8"?>
		<bulkCmConfigDataFile xmlns:un="utranNrm.xsd"
		    xmlns:es="EricssonSpecificAttributes.11.23.xsd"
		    xmlns:xn="genericNrm.xsd" xmlns:gn="geranNrm.xsd" xmlns="configData.xsd">
		    <fileHeader fileFormatVersion="32.615 V4.5" vendorName="Ericsson"/>
		 */
		objectUnderTest.startElement("configData.xsd", "bulkCmConfigDataFile", "bulkCmConfigDataFile", new AttributesImpl()); /*STARTELEMENT*/
		assertTrue(objectUnderTest.elementStart.equalsIgnoreCase("bulkCmConfigDataFile"));
		
		objectUnderTest.docDataMap = new HashMap<String, String>();
		context.checking(new Expectations()
			{{	
				one(mockedAttributes).getValue("id"); will(returnValue(null));
				one(mockedAttributes).getLength(); will(returnValue(2));
				one(mockedAttributes).getLocalName(0); will(returnValue("vendorName"));
				one(mockedAttributes).getValue(0); will(returnValue("Ericsson"));
				one(mockedAttributes).getLength(); will(returnValue(2));
				one(mockedAttributes).getLocalName(1); will(returnValue("fileFormatVersion"));
				one(mockedAttributes).getValue(1); will(returnValue("32.615 V4.5"));
				one(mockedAttributes).getLength(); will(returnValue(2));
			}}
		);
		objectUnderTest.startElement("configData.xsd", "fileHeader", "fileHeader", mockedAttributes); /*STARTELEMENT*/
		//context.assertIsSatisfied();
		assertTrue(objectUnderTest.elementParent.equalsIgnoreCase("bulkCmConfigDataFile"));
		assertTrue(objectUnderTest.elementStart.equalsIgnoreCase("fileHeader"));
		assertTrue(objectUnderTest.fdnDepth == 0);
		assertTrue(objectUnderTest.docDataMap.size()==2);
		
		//Last else - and inside the sub if
		objectUnderTest.endElement("configData.xsd", "fileHeader", "fileHeader"); 					/*ENDELEMENT*/
		assertTrue(objectUnderTest.elementEnd=="fileHeader");
		assertTrue(objectUnderTest.fdnDepth==-1);
		
		/*
			<configData dnPrefix="Undefined">
	        <xn:SubNetwork id="ONRM_RootMo_R">
	            <xn:SubNetwork id="RNC01">
	                <xn:attributes>
	                    <xn:userDefinedNetworkType>WCDMA</xn:userDefinedNetworkType>
	                    <xn:userLabel>RNC01</xn:userLabel>
	                </xn:attributes>
		*/
		//context = new JUnit4Mockery();
		//context.setImposteriser(ClassImposteriser.INSTANCE);
		//Expectations ex = new Expectations();
		context.checking(new Expectations()
			{{	
				one(mockedAttributes).getValue("id"); will(returnValue(null));
				one(mockedAttributes).getLength(); will(returnValue(1));
				one(mockedAttributes).getLocalName(0); will(returnValue("dnPrefix"));
				one(mockedAttributes).getValue(0); will(returnValue("Undefined"));
				one(mockedAttributes).getLength(); will(returnValue(1));
			}}
		);
		objectUnderTest.startElement("configData.xsd", "configData", "configData", mockedAttributes); /*STARTELEMENT*/
		assertTrue(objectUnderTest.docDataMap.size()==3);
		
		objectUnderTest.dataMap = new HashMap<String, String>();
		context.checking(new Expectations()
			{{	
					one(mockedAttributes).getValue("id"); will(returnValue("ONRM_RootMo_R"));
					one(mockedAttributes).getValue("id"); will(returnValue("ONRM_RootMo_R"));
					//one(mockedFdn).handle("SubNetwork=ONRM_RootMo_R", 0); 
					//one(mockedFdn).getFdn(); will(returnValue("SubNetwork=ONRM_RootMo"));
			}}
		);
		objectUnderTest.startElement("genericNrm.xsd", "SubNetwork", "xn:SubNetwork", mockedAttributes); /*STARTELEMENT*/
		assertTrue(objectUnderTest.moId.equalsIgnoreCase("ONRM_RootMo_R"));
		assertTrue(objectUnderTest.moClass.equalsIgnoreCase("SubNetwork"));
		assertTrue(objectUnderTest.parentMoClass.equalsIgnoreCase("SubNetwork"));
		assertTrue(objectUnderTest.parentMoId.equalsIgnoreCase("ONRM_RootMo_R"));
		
		context.checking(new Expectations()
			{{	
					one(mockedAttributes).getValue("id"); will(returnValue("RNC01"));
					one(mockedAttributes).getValue("id"); will(returnValue("RNC01"));
					//one(mockedFdn).handle("SubNetwork=RNC01", 1); 
					//one(mockedFdn).getFdn(); will(returnValue("SubNetwork=ONRM_RootMo,SubNetwork=RNC01"));
			}}
		);
		objectUnderTest.startElement("genericNrm.xsd", "SubNetwork", "xn:SubNetwork", mockedAttributes); /*STARTELEMENT*/
		//Don't need asserts as it's the same as previous run of startElement.
		
		//No mocking needed.
		objectUnderTest.startElement("genericNrm.xsd", "attributes", "xn:attributes", new AttributesImpl()); /*STARTELEMENT*/
		assertTrue(objectUnderTest.collect);
		
		//No mocking needed.
		objectUnderTest.startElement("genericNrm.xsd", "userDefinedNetworkType", "xn:userDefinedNetworkType", new AttributesImpl()); /*STARTELEMENT*/
		assertTrue(objectUnderTest.attributeDepth==1);
		
		//handleNonStruct - dataMap.put
		objectUnderTest.characters = new StringBuilder("WCDMA"); //This accounts for the running of the characters(..) method in BCDParser.
		objectUnderTest.endElement("genericNrm.xsd", "userDefinedNetworkType", "xn:userDefinedNetworkType");					/*ENDELEMENT*/
		assertTrue(objectUnderTest.attributeDepth==0);
		objectUnderTest.dataMap.containsKey("userDefinedNetworkType");
		objectUnderTest.dataMap.get("userDefinedNetworkType").equalsIgnoreCase("WCDMA");
		assertTrue(objectUnderTest.characters.length()==0);
		
		//No mocking needed.
		objectUnderTest.startElement("genericNrm.xsd", "userLabel", "xn:userLabel", new AttributesImpl()); 					/*STARTELEMENT*/
		//Don't need asserts as it's the same as previous run of startElement.
		
		//handleNonStruct - dataMap.put
		objectUnderTest.characters.append("RNC01"); //This accounts for the running of the characters(..) method in BCDParser.
		objectUnderTest.endElement("genericNrm.xsd", "userLabel", "xn:userLabel");											/*ENDELEMENT*/
		//Don't need asserts as it's the same as previous run of endElement.
		
		//attibutes - no write
		objectUnderTest.endElement("genericNrm.xsd", "attributes", "xn:attributes");						/*ENDELEMENT*/							
		assertTrue(null==objectUnderTest.vsDataClass);
		assertTrue(null==objectUnderTest.moId);
		assertTrue(!objectUnderTest.collect);
		assertTrue(objectUnderTest.attributeDepth==0);
		
		/*
					<xn:MeContext id="RNC01">
			        <xn:VsDataContainer id="RNC01">
			            <xn:attributes>
			                <xn:vsDataType>vsDataMeContext</xn:vsDataType>
			                <xn:vsDataFormatVersion>EricssonSpecificAttributes.11.23</xn:vsDataFormatVersion>
			                <es:vsDataMeContext>
                                <es:userLabel>RNC01</es:userLabel>
		*/          
		context.checking(new Expectations()
			{{	
				one(mockedAttributes).getValue("id"); will(returnValue("RNC01"));
				one(mockedAttributes).getValue("id"); will(returnValue("RNC01"));
				//exactly(2).of(mockedSourceFile).getProperty("interfaceName"); will(returnValue("INTF_DC_E_BULK_CM"));
				one(mockedSourceFile).getName(); will(returnValue("filename"));
				one(mockedSourceFile).getDir(); will(returnValue("directory"));
				//one(mockedFdn).handle("MeContext=RNC01", 2); 
				//one(mockedFdn).getFdn(); will(returnValue("SubNetwork=ONRM_RootMo,SubNetwork=RNC01,MeContext=RNC01"));
				exactly(2).of(mockedMeasFile).addData(with(any(HashMap.class)));
				one(mockedMeasFile).saveData();
			}}
		);
		objectUnderTest.vectorListSet = new HashMap<String, List<String>>();
		objectUnderTest.vectorMap = new LinkedHashMap<String, HashMap<String, String>>();
		objectUnderTest.openFiles = new HashMap<String, MeasurementFile>();
		objectUnderTest.openFiles.put("SubNetwork", mockedMeasFile);
		objectUnderTest.openFiles.put("SubNetwork_V", mockedMeasFile);
		objectUnderTest.elementFDNMap.put("other", Pattern.compile(objectUnderTest.otherPattern));
		objectUnderTest.startElement("genericNrm.xsd", "MeContext", "xn:MeContext", mockedAttributes); 		/*STARTELEMENT*/
		assertTrue(objectUnderTest.moId.equalsIgnoreCase("RNC01"));
		assertTrue(objectUnderTest.moClass.equalsIgnoreCase("MeContext"));
		assertTrue(objectUnderTest.fdnDepth==2);
		assertTrue(objectUnderTest.openFiles.containsKey("SubNetwork"));
		assertTrue(objectUnderTest.parentMoId.equalsIgnoreCase("RNC01"));
		assertTrue(objectUnderTest.parentMoClass.equalsIgnoreCase("MeContext"));
		
		context.checking(new Expectations()
			{{	
				one(mockedAttributes).getValue("id"); will(returnValue("RNC01"));
				one(mockedAttributes).getValue("id"); will(returnValue("RNC01"));
			}}
		);
		objectUnderTest.startElement("genericNrm.xsd", "VsDataContainer", "xn:VsDataContainer", mockedAttributes); 		/*STARTELEMENT*/
		assertTrue(objectUnderTest.moId.equalsIgnoreCase("RNC01"));
		assertTrue(objectUnderTest.moClass.equalsIgnoreCase("VsDataContainer"));
		assertTrue(objectUnderTest.fdnDepth == 3);
		
		objectUnderTest.startElement("genericNrm.xsd", "attributes", "xn:attributes", new AttributesImpl()); 		/*STARTELEMENT*/
		//No asserts needed. This has been tested above. It is run for setup purposes.
		
		objectUnderTest.startElement("genericNrm.xsd", "vsDataType", "xn:vsDataType", new AttributesImpl()); 		/*STARTELEMENT*/
		//No asserts needed. This has been tested above. It is run for setup purposes.
		
		//handleDataTypeTag - no marker
		//handleNonStruct
		objectUnderTest.characters = new StringBuilder("vsDataMeContext"); //This accounts for the running of the characters(..) method in BCDParser.
		objectUnderTest.endElement("genericNrm.xsd", "xvsDataType", "xn:vsDataType"); 								/*ENDELEMENT*/
		assertTrue(objectUnderTest.attributeDepth==0);
		assertTrue(objectUnderTest.vsDataClass.equalsIgnoreCase("MeContext"));
		assertTrue(objectUnderTest.characters.length()==0);
		objectUnderTest.dataMap.containsKey("vsDataType");
		objectUnderTest.dataMap.get("vsDataType").equalsIgnoreCase("MeContext");
		
		objectUnderTest.startElement("genericNrm.xsd", "vsDataFormatVersion", "xn:vsDataFormatVersion", new AttributesImpl()); 		/*STARTELEMENT*/
		//No asserts needed here. This has been tested above. It is run for setup purposes.
		
		//handleNonStruct - dataMap.put
		objectUnderTest.endElement("genericNrm.xsd", "vsDataFormatVersion", "xn:vsDataFormatVersion");
		//No asserts needed here. This has been tested above. It is run for setup purposes.
		
		objectUnderTest.startElement("genericNrm.xsd", "vsDataMeContext", "es:vsDataMeContext", new AttributesImpl()); 				/*STARTELEMENT*/
		assertTrue(objectUnderTest.attributeDepth==1);
		
		objectUnderTest.startElement("EricssonSpecificAttributes.11.23.xsd", "userLabel", "es:userLabel", new AttributesImpl()); 	/*STARTELEMENT*/
		//No asserts needed here. This has been tested above. It is run for setup purposes.
		
		objectUnderTest.characters.append("RNC01"); //This accounts for the running of the characters(..) method in BCDParser.
		objectUnderTest.endElement("EricssonSpecificAttributes.11.23.xsd", "userLabel", "xn:userLabel");								/*ENDELEMENT*/
		//No asserts needed here. This has been tested above. It is run for setup purposes.
		
		/*
					        </es:vsDataMeContext>
					    </xn:attributes>
					</xn:VsDataContainer>
					<xn:ManagedElement id="1">
					    <xn:attributes>		
		*/
		//handleNonStruct - dataMap.put
		objectUnderTest.endElement("EricssonSpecificAttributes.11.23.xsd", "vsDataMeContext", "es:vsDataMeContext");						/*ENDELEMENT*/
		assertTrue(objectUnderTest.attributeDepth==0);
		
		//:attributes - no write. handleNonStruct - dataMap.put
		objectUnderTest.endElement("xn:attributes", "attributes", "xn:attributes");												/*ENDELEMENT*/
		//No asserts needed here. This has been tested above. It is run for setup purposes.
		
		//Last else - but not inside the sub if
		objectUnderTest.endElement("genericNrm.xsd", "VsDataContainer", "xn:VsDataContainer");											/*ENDELEMENT*/
		assertTrue(objectUnderTest.fdnDepth == 2);
		
		
		
		context.checking(new Expectations()
			{{
				one(mockedAttributes).getValue("id"); will(returnValue("1"));
				one(mockedAttributes).getValue("id"); will(returnValue("1"));
				one(mockedSourceFile).getName(); will(returnValue("filename"));
				one(mockedSourceFile).getDir(); will(returnValue("directory"));
				exactly(2).of(mockedMeasFile).addData(with(any(HashMap.class)));
				one(mockedMeasFile).saveData();
				//one(mockedFdn).handle("ManagedElement=1", 3); 
				//one(mockedFdn).getFdn(); will(returnValue("SubNetwork=ONRM_RootMo,SubNetwork=RNC01,MeContext=RNC01,ManagedElement=1"));
			}}
		);
		objectUnderTest.openFiles.put("MeContext", mockedMeasFile);
		objectUnderTest.openFiles.put("MeContext_V", mockedMeasFile);
		objectUnderTest.startElement("genericNrm.xsd", "ManagedElement", "xn:ManagedElement", mockedAttributes); 			/*STARTELEMENT*/
		assertTrue(objectUnderTest.moId.equalsIgnoreCase("1"));
		assertTrue(objectUnderTest.moClass.equalsIgnoreCase("ManagedElement"));
		assertTrue(objectUnderTest.fdnDepth==3);
		//assertTrue(objectUnderTest.openFiles.containsKey("MeContext"));
		//assertTrue(objectUnderTest.vectorListSet.containsKey("MeContext"));
		//assertTrue(objectUnderTest.vectorListSet.containsKey("MeContext_V"));
		assertTrue(objectUnderTest.parentMoId.equalsIgnoreCase("1"));
		assertTrue(objectUnderTest.parentMoClass.equalsIgnoreCase("ManagedElement"));
		
		
		
		
		
		
		//Skipping....
		
		//The if statement for xn:managedElementType
		//handleNonStruct - dataMap.put
		/*objectUnderTest.characters.append("RNC");
		objectUnderTest.endElement("genericNrm.xsd", "managedElementType", "xn:managedElementType");
		assertTrue(objectUnderTest.elementType.equalsIgnoreCase("RNC"));*/
		
		//Skipping...
		
		
		
	}

	@Test
	public final void testInit() throws IllegalArgumentException, IllegalAccessException {
		objectUnderTest.init(mockedMainParser, "techPack", "setType", "setName", "workerName");
		assertTrue(techPack_Field.get(objectUnderTest)=="techPack");
		assertTrue(setType_Field.get(objectUnderTest)=="setType");
		assertTrue(setName_Field.get(objectUnderTest)=="setName");
		assertTrue(workerName_Field.get(objectUnderTest)=="workerName");
	}

	@Test
	public final void testParse() throws Exception {
		context.checking(new Expectations()
			{{	
				exactly(22).of(mockedSourceFile).getProperty(with(any(String.class)), with(any(String.class))); will(returnValue(new String("property_value")));
				
				exactly(6).of(mockedSourceFile).getProperty(with(any(String.class)), with(aNull(String.class))); will(returnValue(new String("(.+)")));
				exactly(6).of(mockedSourceFile).getName(); will(returnValue("filename"));
				
				one(mockedSourceFile).getProperty(with(any(String.class)), with(any(String.class))); will(returnValue(new String(".+/(.+)/.+/.+")));
				one(mockedSourceFile).getDir(); will(returnValue("a/ossid/b/c"));
				
				one(mockedSourceFile).getProperty("BCDParser.elementTypesSupported", ""); will(returnValue(new String("RNC,RBS,RXI")));
				one(mockedSourceFile).getProperty("BCDParser.elementTypesFDNProvided", ""); will(returnValue(new String("RNC")));
				one(mockedSourceFile).getProperty("BCDParser.FDNPattern.RNC", null); will(returnValue(new String("SubNetwork=(.+),SubNetwork=.+,MeContext=(.+),ManagedElement=.+")));
				
				one(mockedSourceFile).getFileInputStream();
			}}
		);
		objectUnderTest.parse(mockedSourceFile, "x", "x", "x");
	}
	
	
	@Test
	public final void testStartDocument() throws SAXException, IllegalArgumentException, IllegalAccessException {
		long time = Calendar.getInstance().getTimeInMillis();
		objectUnderTest.start = time - 5;   //Set it to 5 sec earlier
		objectUnderTest.startDocument();
		assertTrue(objectUnderTest.start >= time);
	}

	@Test
	public final void testEndDocument() throws Exception {
		context.checking(new Expectations()
			{{	
				one(mockedMeasFile).close();
			}}
		);
		
		long time = Calendar.getInstance().getTimeInMillis();
		objectUnderTest.stop = time - 5;   //Set it to 5 sec earlier
		objectUnderTest.openFiles = new HashMap<String, MeasurementFile>();
		objectUnderTest.openFiles.put("file", mockedMeasFile);
		objectUnderTest.endDocument();                    /*Method under test*/
		assertTrue(objectUnderTest.stop >= time);
	}

	/*@Test
	public final void testCharacters() {
		//objectUnderTest.characters(buf, offset, len)
	}*/

	@Test
	public final void testStatus() {
		assertTrue(objectUnderTest.status()==1);
	}

	@Test
	public final void testRun() throws Exception {
		context.checking(new Expectations()
			{{	
				
				exactly(22).of(mockedSourceFile).getProperty(with(any(String.class)), with(any(String.class))); will(returnValue(new String("property_value")));
				//These 2 expectations are specifically for the run() method:
				one(mockedMainParser).nextSourceFile(); will(returnValue(mockedSourceFile));
				one(mockedMainParser).preParse(mockedSourceFile);

				//The following 8 expectations are for inside the parse method (which is called by the run() method). They are the 
				//same expectations seen in testParse().
				exactly(6).of(mockedSourceFile).getProperty(with(any(String.class)), with(aNull(String.class))); will(returnValue(new String("(.+)")));
				exactly(6).of(mockedSourceFile).getName(); will(returnValue("filename"));
				one(mockedSourceFile).getProperty(with(any(String.class)), with(any(String.class))); will(returnValue(new String(".+/(.+)/.+/.+")));
				one(mockedSourceFile).getDir(); will(returnValue("a/ossid/b/c"));
				one(mockedSourceFile).getProperty("BCDParser.elementTypesSupported", ""); will(returnValue(new String("RNC,RBS,RXI")));
				one(mockedSourceFile).getProperty("BCDParser.elementTypesFDNProvided", ""); will(returnValue(new String("RNC")));
				one(mockedSourceFile).getProperty("BCDParser.FDNPattern.RNC", null); will(returnValue(new String("SubNetwork=(.+),SubNetwork=.+,MeContext=(.+),ManagedElement=.+")));
				one(mockedSourceFile).getFileInputStream();
				
				//These 2 expectations are specifically for the run() method:
				one(mockedMainParser).postParse(mockedSourceFile);
				one(mockedMainParser).finallyParse(mockedSourceFile);
				one(mockedMainParser).nextSourceFile(); will(returnValue(null));
			}}
		);
		objectUnderTest.run();
		assertTrue(objectUnderTest.status()==3);
	}

}
