package com.ericsson.eniq.etl.bcd;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.distocraft.dc5000.etl.parser.Main;

public class FdnTest extends BaseMock {
	Fdn objectUnderTest;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		objectUnderTest = new Fdn(true,true);
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public final void testHandle() {
		objectUnderTest.handle("SubNetwork=ONRM_RootMo_R", 0);
		assertTrue("Result for FDN at depth 0 is wrong.", objectUnderTest.getFdn().equalsIgnoreCase("SubNetwork=ONRM_RootMo"));

		objectUnderTest.handle("SubNetwork=RNC01", 1);
		assertTrue("Result for FDN at depth 1 is wrong.", objectUnderTest.getFdn().equalsIgnoreCase("SubNetwork=ONRM_RootMo,SubNetwork=RNC01"));

		objectUnderTest.handle("MeContext=RNC01", 2);
		assertTrue("Result for FDN at depth 2 is wrong.", objectUnderTest.getFdn().equalsIgnoreCase("SubNetwork=ONRM_RootMo,SubNetwork=RNC01,MeContext=RNC01"));

		objectUnderTest.handle("ManagedElement=1", 3);
		assertTrue("Result for FDN at depth 3 is wrong.", objectUnderTest.getFdn().equalsIgnoreCase("SubNetwork=ONRM_RootMo,SubNetwork=RNC01,MeContext=RNC01,ManagedElement=1"));

		objectUnderTest.handle("vsDataEquipment=1", 4);
		assertTrue("Result for FDN at depth 4 is wrong.", objectUnderTest.getFdn().equalsIgnoreCase("SubNetwork=ONRM_RootMo,SubNetwork=RNC01,MeContext=RNC01,ManagedElement=1,Equipment=1"));

		objectUnderTest.handle("vsDataSpDevicePool=1", 5);
		assertTrue("Result for FDN at depth 5 is wrong.", objectUnderTest.getFdn().equalsIgnoreCase("SubNetwork=ONRM_RootMo,SubNetwork=RNC01,MeContext=RNC01,ManagedElement=1,Equipment=1,SpDevicePool=1"));

		objectUnderTest.handle("vsDataPdrDevice=1", 6);
		assertTrue("Result for FDN at depth 6 is wrong.", objectUnderTest.getFdn().equalsIgnoreCase("SubNetwork=ONRM_RootMo,SubNetwork=RNC01,MeContext=RNC01,ManagedElement=1,Equipment=1,SpDevicePool=1,PdrDevice=1"));

		objectUnderTest.handle("vsDataIpEthPacketDataRouter=1", 7);
		assertTrue("Result for FDN at depth 7 is wrong.", objectUnderTest.getFdn().equalsIgnoreCase("SubNetwork=ONRM_RootMo,SubNetwork=RNC01,MeContext=RNC01,ManagedElement=1,Equipment=1,SpDevicePool=1,PdrDevice=1,IpEthPacketDataRouter=1"));
		
		objectUnderTest.handle("vsDataPacketDataRouter=1", 7);
		assertTrue("Result for FDN at depth 7 is wrong.", objectUnderTest.getFdn().equalsIgnoreCase("SubNetwork=ONRM_RootMo,SubNetwork=RNC01,MeContext=RNC01,ManagedElement=1,Equipment=1,SpDevicePool=1,PdrDevice=1,PacketDataRouter=1"));
		
		objectUnderTest.handle("vsDataSpDevicePool=2", 5);
		assertTrue("Result for FDN at depth 5 is wrong.", objectUnderTest.getFdn().equalsIgnoreCase("SubNetwork=ONRM_RootMo,SubNetwork=RNC01,MeContext=RNC01,ManagedElement=1,Equipment=1,SpDevicePool=2"));
	}

}
