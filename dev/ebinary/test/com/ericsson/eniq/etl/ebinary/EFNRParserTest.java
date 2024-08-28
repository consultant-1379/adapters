package com.ericsson.eniq.etl.ebinary;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

import com.distocraft.dc5000.common.Properties;
import com.distocraft.dc5000.etl.parser.Main;
import com.distocraft.dc5000.etl.parser.MeasurementFile;
import com.distocraft.dc5000.etl.parser.MeasurementFileImpl;
import com.distocraft.dc5000.etl.parser.ParseSession;
import com.distocraft.dc5000.etl.parser.ParserDebugger;
import com.distocraft.dc5000.etl.parser.SourceFile;
import com.distocraft.dc5000.repository.cache.DataFormatCache;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;

import ssc.rockfactory.RockFactory;



@RunWith(JMock.class)
public class EFNRParserTest {

	private Mockery context = new JUnit4Mockery();
	
	private Mockery concreteContext = new JUnit4Mockery(){{
		setImposteriser(ClassImposteriser.INSTANCE);
	}};
	
	private Main mainMock = concreteContext.mock(Main.class);
	private SourceFile sourceFileMock = concreteContext.mock(SourceFile.class);
	private MeasurementFile measurementFileMock = context.mock(MeasurementFile.class);

	@SuppressWarnings("static-access")
	@Test
	public void testParse() {
		File file = new File("C:\\eniq\\data\\pmdata\\FNRFOA0012");
		final String techPack = "INTF_EFNRParser";
		final String setType = "Adaptor";
		final String setName = "test";
		final String wn = "w01234948638438";
		Properties prop = null;
		RockFactory rf = null;
		RockFactory reprf    = null;
		ParseSession pSession = null;
		ParserDebugger debugger = null;
		Logger log = null;
		
//	SourceFile sFile = new SourceFile(file, prop, rf, reprf, pSession, debugger, false, log);

		try {
			final FileInputStream fis = new FileInputStream(file);
			DataFormatCache dfCache = DataFormatCache.getCache();
			concreteContext.checking(new Expectations() {{
//				allowing(mainMock).nextSourceFile(); 
//				will(returnValue(sourceFileMock));
//				oneOf(mainMock).preParse(sourceFileMock);
				allowing(sourceFileMock).getProperty(with(any(String.class)), with("C:\\eniq\\sw\\conf\\efnrParserConf"));
				will(returnValue("C:\\eniq\\sw\\conf\\efnrParserConf"));
				allowing(sourceFileMock).getProperty(with(any(String.class)), with("20<A13>-<A15>-<A17> <A19>:<A21>:00"));
				will(returnValue("20<A13>-<A15>-<A17> <A19>:<A21>:00"));
				allowing(sourceFileMock).getFileInputStream();
				will(returnValue(fis));
				allowing(sourceFileMock).getProperty("interfaceName");
				will(returnValue("interfaceName"));
				allowing(sourceFileMock).getProperty("rowstatus", "ROWSTATUS");
				will(returnValue("ROWSTATUS"));
				allowing(sourceFileMock).getProperty("suspected", "SUSPECTED");
				will(returnValue("SUSPECTED"));
				allowing(sourceFileMock).getProperty("debug", "");
				will(returnValue("false"));
//				oneOf(mainMock).createMeasurementFile(with(sourceFileMock), 
//						with(any(String.class)), 
//						with(techPack), 
//						with(setType), 
//						with(setName), with(any(Logger.class)));
//				will(returnValue(measurementFileMock));
			}});
		} catch (Exception e) {
			fail("Could not create Mock, "+e.getMessage());
		}

		EFNRParser fnrParser = new EFNRParser();
		assertNotNull(fnrParser);
		
		
		fnrParser.init(mainMock, techPack, setType, setName, wn);
		try {
			fnrParser.parse(sourceFileMock, techPack, setType, setName);
		} catch (Exception e) {
			fail("Failed");
		}
	}

	@Test
	public void testStatus() {
		fail("Not yet implemented");
	}

	@Test
	public void testRun() {
		fail("Not yet implemented");
	}

	@Test
	public void testFormatDateTimeID() {
		fail("Not yet implemented");
	}

}
