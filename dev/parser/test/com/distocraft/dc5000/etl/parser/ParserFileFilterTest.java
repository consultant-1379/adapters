package com.distocraft.dc5000.etl.parser;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for ParserFileFilter. Keeps count of accepted and rejected files.
 * 
 * @author EJAAVAH
 * 
 */
public class ParserFileFilterTest {

    private static ParserFileFilter objUnderTest;

    private static File existingTestFile;

    private static File nonExistingTestFile;
    
    private static String fileFilter = "(\\S+\\.(txt|doc|xml))";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        /* Files to be used in tests */
        existingTestFile = new File(System.getProperty("user.dir"), "testFileForParserFileFilter.txt");
        nonExistingTestFile = new File(System.getProperty("user.dir"), "nonExistantFile.txt");
        existingTestFile.deleteOnExit();
        try {
            final PrintWriter pw = new PrintWriter(new FileWriter(existingTestFile));
            pw.print("Test file for ParserFileFilter test class");
            pw.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws Exception {

        /* Cleaning up after test */
        objUnderTest = null;
    }

    /**
     * Testing accept() method when filecountlimit is exceeded.
     * 
     * @throws Exception
     */
    @Test
    public void testDontAccept() throws Exception {

        /* Initializing and calling tested object */
        objUnderTest = new ParserFileFilter(-1, 0,fileFilter);
        assertEquals(false + ", 1", objUnderTest.accept(existingTestFile) + ", " + objUnderTest.filesRejected);
    }

    /**
     * Testing accept() method when check type is 0.
     * 
     * @throws Exception
     */
    @Test
    public void testAcceptWithCheckTypeZero() throws Exception {

        /* Initializing and calling tested object */
        objUnderTest = new ParserFileFilter(2, 0, fileFilter);
        assertEquals(true + ", 1", objUnderTest.accept(existingTestFile) + ", " + objUnderTest.filesAccepted);
    }

    /**
     * Testing accept() method when check type is 1.
     * 
     * @throws Exception
     */
    @Test
    public void testAcceptWithCheckTypeOne() throws Exception {

        /* Initializing and calling tested object */
        objUnderTest = new ParserFileFilter(2, 1, fileFilter);
        final String actual = objUnderTest.accept(existingTestFile) + ", " + objUnderTest.filesAccepted + ", "
                + objUnderTest.accept(nonExistingTestFile) + ", " + objUnderTest.filesRejected;
        assertEquals(true + ", 1, " + true + ", 1", actual);
    }

    /**
     * Testing accept() method when check type is 2.
     * 
     * @throws Exception
     */
    @Test
    public void testAcceptWithCheckTypeTwo() throws Exception {

        /* Initializing and calling tested object */
        objUnderTest = new ParserFileFilter(2, 2, fileFilter);
        final String actual = objUnderTest.accept(existingTestFile) + ", " + objUnderTest.filesAccepted + ", "
                + objUnderTest.accept(nonExistingTestFile) + ", " + objUnderTest.filesRejected;
        assertEquals(true + ", 1, " + true + ", 1", actual);
    }

    /**
     * Testing accept() method when check type is 3.
     * 
     * @throws Exception
     */
    @Test
    public void testAcceptWithCheckTypeThree() throws Exception {

        /* Initializing and calling tested object */
        objUnderTest = new ParserFileFilter(2, 3, fileFilter);
        final String actual = objUnderTest.accept(existingTestFile) + ", " + objUnderTest.filesAccepted + ", "
                + objUnderTest.accept(nonExistingTestFile) + ", " + objUnderTest.filesRejected;
        assertEquals(true + ", 1, " + true + ", 1", actual);
    }

    /**
     * Testing accept() method with check type other than 0, 1, 2 or 3.
     * 
     * @throws Exception
     */
    @Test
    public void testWithCheckTypeOther() throws Exception {

        /* Initializing and calling tested object */
        objUnderTest = new ParserFileFilter(2, 7, fileFilter);
        assertEquals(true + ", 1", objUnderTest.accept(existingTestFile) + ", " + objUnderTest.filesAccepted);
    }

    /**
     * Testing accept() method with null file object.
     * 
     * @throws Exception
     */
    @Test
    public void testAcceptWithNullFile() throws Exception {

        /* Initializing and calling tested object */
        objUnderTest = new ParserFileFilter(2, 1, fileFilter);
        try {
            objUnderTest.accept(null);
            fail("Test failed - NullPointerException expected as the file object was null!");
        } catch (final NullPointerException npe) {
            // Test Passed
        } catch (final Exception e) {
            fail("Test failed - Unexpected exception thrown!\n" + e);
        }
    }
}
