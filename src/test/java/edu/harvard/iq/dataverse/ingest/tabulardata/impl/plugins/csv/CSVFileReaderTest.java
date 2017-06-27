/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dataverse.ingest.tabulardata.impl.plugins.csv;

import edu.harvard.iq.dataverse.DataTable;
import edu.harvard.iq.dataverse.datavariable.DataVariable.VariableInterval;
import edu.harvard.iq.dataverse.datavariable.DataVariable.VariableType;
import edu.harvard.iq.dataverse.dataaccess.TabularSubsetGenerator;
import edu.harvard.iq.dataverse.ingest.tabulardata.TabularDataIngest;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author oscardssmith
 */
public class CSVFileReaderTest {

    private static final Logger logger = Logger.getLogger(CSVFileReaderTest.class.getCanonicalName());

    /**
     * Test CSVFileReader with a hellish CSV containing everything nasty I could
     * think of to throw at it.
     *
     */
    @Test
    public void testRead() {
        String testFile = "src/test/java/edu/harvard/iq/dataverse/ingest/tabulardata/impl/plugins/csv/IngestCSV.csv";
        String[] expResult = {"-199	\"hello\"	2013-04-08 13:14:23	2013-04-08 13:14:23	2017-06-20	\"2017/06/20\"	0.0	1	\"2\"	\"823478788778713\"",
            "2	\"Sdfwer\"	2013-04-08 13:14:23	2013-04-08 13:14:23	2017-06-20	\"1100/06/20\"	Inf	2	\"NaN\"	\",1,2,3\"",
            "0	\"cjlajfo.\"	2013-04-08 13:14:23	2013-04-08 13:14:23	2017-06-20	\"3000/06/20\"	-Inf	3	\"inf\"	\"\\casdf\"",
            "-1	\"Mywer\"	2013-04-08 13:14:23	2013-04-08 13:14:23	2017-06-20	\"06-20-2011\"	3.141592653	4	\"4.8\"	\" \\\"\\\"  \"",
            "266128	\"Sf\"	2013-04-08 13:14:23	2013-04-08 13:14:23	2017-06-20	\"06-20-1917\"	0	5	\"Inf+11\"	\"\"",
            "0.0	\"werxc\"	2013-04-08 13:14:23	2013-04-08 13:14:23	2017-06-20	\"03/03/1817\"	123	6.000001	\"11-2\"	\"\\\"\\\"adf\\0\\na\\tdsf\\\"\\\"\"",
            "-2389	\"Dfjl\"	2013-04-08 13:14:23	2013-04-08 13:14:72	2017-06-20	\"2017-03-12\"	NaN	2	\"nap\"	\"💩⌛👩🏻■\""};
        BufferedReader result = null;
        try (BufferedInputStream stream = new BufferedInputStream(
                new FileInputStream(testFile))) {
            CSVFileReader instance = new CSVFileReader(new CSVFileReaderSpi());
            result = new BufferedReader(new FileReader(instance.read(stream, null).getTabDelimitedFile()));
        } catch (IOException ex) {
            fail("" + ex);
        }

        String foundLine = null;
        assertNotNull(result);
        for (String expLine : expResult) {
            try {
                foundLine = result.readLine();
            } catch (IOException ex) {
                fail();
            }
            if (!expLine.equals(foundLine)) {
                logger.info("expected: " + expLine);
                logger.info("found : " + foundLine);
            }
            assertEquals(expLine, foundLine);
        }

    }

    /*
     * This test will read the CSV File From Hell, above, then will inspect
     * the DataTable object produced by the plugin, and verify that the
     * individual DataVariables have been properly typed.
     */
    @Test
    public void testVariables() {
        String testFile = "src/test/java/edu/harvard/iq/dataverse/ingest/tabulardata/impl/plugins/csv/IngestCSV.csv";

        String[] expectedVariableNames = {"ints", "Strings", "Times", "Not quite Times", "Dates", "Not quite Dates", "Numbers", "Not quite Ints", "Not quite Numbers", "\"Column that hates you.\""};


        VariableType[] expectedVariableTypes = {VariableType.NUMERIC, VariableType.CHARACTER, VariableType.CHARACTER, VariableType.CHARACTER, VariableType.CHARACTER,
                                                VariableType.CHARACTER, VariableType.NUMERIC, VariableType.NUMERIC, VariableType.CHARACTER, VariableType.CHARACTER};

        VariableInterval[] expectedVariableIntervals = {VariableInterval.CONTINUOUS, VariableInterval.DISCRETE, VariableInterval.DISCRETE, VariableInterval.DISCRETE, VariableInterval.DISCRETE,
                                                        VariableInterval.DISCRETE, VariableInterval.CONTINUOUS, VariableInterval.CONTINUOUS, VariableInterval.DISCRETE, VariableInterval.DISCRETE};

        String[] expectedVariableFormatCategories = {null, null, "time", "time", "date", null, null, null, null, null};

        String[] expectedVariableFormats = {null, null, "yyyy-MM-dd HH:mm:ss",  "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", null, null, null, null, null};

        Long expectedNumberOfCases = 7L; // aka the number of lines in the TAB file produced by the ingest plugin

        DataTable result = null;
        try (BufferedInputStream stream = new BufferedInputStream(
                new FileInputStream(testFile))) {
            CSVFileReader instance = new CSVFileReader(new CSVFileReaderSpi());
            result = instance.read(stream, null).getDataTable();
        } catch (IOException ex) {
            fail("" + ex);
        }

        assertNotNull(result);

        assertNotNull(result.getDataVariables());

        assertEquals(result.getVarQuantity(), new Long(result.getDataVariables().size()));

        if (result.getVarQuantity() != expectedVariableTypes.length) {
            logger.info("number of variables expected: "+expectedVariableTypes.length);
            logger.info("number of variables produced: "+result.getVarQuantity());
        }

        assertEquals(result.getVarQuantity(), new Long(expectedVariableTypes.length));


        if (!expectedNumberOfCases.equals(result.getCaseQuantity())) {
            logger.info("number of observations expected: "+expectedNumberOfCases);
            logger.info("number of observations produced: "+result.getCaseQuantity());
        }

        assertEquals(expectedNumberOfCases, result.getCaseQuantity());

        // OK, let's go through the individual variables:

        for (int i = 0; i < result.getVarQuantity(); i++) {

            if (!expectedVariableNames[i].equals(result.getDataVariables().get(i).getName())) {
                logger.info("variable "+i+", name expected: "+expectedVariableNames[i]);
                logger.info("variable "+i+", name produced: "+result.getDataVariables().get(i).getName());
            }

            assertEquals(expectedVariableNames[i], result.getDataVariables().get(i).getName());

           if (!expectedVariableTypes[i].equals(result.getDataVariables().get(i).getType())) {
               logger.info("variable "+i+", type expected: "+expectedVariableTypes[i].toString());
               logger.info("variable "+i+", type produced: "+result.getDataVariables().get(i).getType().toString());
           }

           assertEquals(expectedVariableTypes[i], result.getDataVariables().get(i).getType());

           if (!expectedVariableIntervals[i].equals(result.getDataVariables().get(i).getInterval())) {
               logger.info("variable "+i+", interval expected: "+expectedVariableIntervals[i].toString());
               logger.info("variable "+i+", interval produced: "+result.getDataVariables().get(i).getInterval().toString());
           }

           assertEquals(expectedVariableIntervals[i], result.getDataVariables().get(i).getInterval());

           if ((expectedVariableFormatCategories[i] != null && !expectedVariableFormatCategories[i].equals(result.getDataVariables().get(i).getFormatCategory()))
                   || (expectedVariableFormatCategories[i] == null && result.getDataVariables().get(i).getFormatCategory() != null)) {
               logger.info("variable "+i+", format category expected: "+expectedVariableFormatCategories[i]);
               logger.info("variable "+i+", format category produced: "+result.getDataVariables().get(i).getFormatCategory());
           }

           assertEquals(expectedVariableFormatCategories[i], result.getDataVariables().get(i).getFormatCategory());

           if ((expectedVariableFormats[i] != null && !expectedVariableFormats[i].equals(result.getDataVariables().get(i).getFormat()))
                   || (expectedVariableFormats[i] == null && result.getDataVariables().get(i).getFormat() != null)) {
               logger.info("variable "+i+", format expected: "+expectedVariableFormats[i]);
               logger.info("variable "+i+", format produced: "+result.getDataVariables().get(i).getFormat());
           }

           assertEquals(expectedVariableFormats[i], result.getDataVariables().get(i).getFormat());
        }        
    }
    
    /* 
     * This test will read a CSV file, then attempt to subset 
     * the resulting tab-delimited file and verify that the individual variable vectors 
     * are legit. 
    */
    
    @Test
    public void testSubset() {
        String testFile = "src/test/java/edu/harvard/iq/dataverse/ingest/tabulardata/impl/plugins/csv/election_precincts.csv";
        Long expectedNumberOfVariables = 13L;
        Long expectedNumberOfCases = 24L; // aka the number of lines in the TAB file produced by the ingest plugin
        
        TabularDataIngest ingestResult = null; 
        
        File generatedTabFile = null;
        DataTable generatedDataTable = null; 
        
        try (BufferedInputStream stream = new BufferedInputStream(
                new FileInputStream(testFile))) {
            CSVFileReader instance = new CSVFileReader(new CSVFileReaderSpi());
            
            ingestResult = instance.read(stream, null);
            
            generatedTabFile = ingestResult.getTabDelimitedFile();
            generatedDataTable = ingestResult.getDataTable();
        } catch (IOException ex) {
            fail("" + ex);
        }
        
        assertNotNull(generatedDataTable);
        
        assertNotNull(generatedDataTable.getDataVariables());
                
        assertEquals(generatedDataTable.getVarQuantity(), new Long(generatedDataTable.getDataVariables().size()));
        
        if (generatedDataTable.getVarQuantity() != expectedNumberOfVariables) {
            logger.info("number of variables expected: "+expectedNumberOfVariables);
            logger.info("number of variables produced: "+generatedDataTable.getVarQuantity());
        }
        
        assertEquals(generatedDataTable.getVarQuantity(), new Long(expectedNumberOfVariables));
        
        
        if (!expectedNumberOfCases.equals(generatedDataTable.getCaseQuantity())) {
            logger.info("number of observations expected: "+expectedNumberOfCases);
            logger.info("number of observations produced: "+generatedDataTable.getCaseQuantity());
        }
        
        assertEquals(expectedNumberOfCases, generatedDataTable.getCaseQuantity());
        
        // And now let's try and subset the individual vectors 
        
        // First, the "continuous" vectors (we should be able to read these as Double[]):
        
        Set<Integer> floatColumns = new HashSet<Integer>(Arrays.asList(2,9,10,11));
        
        Double[][] floatVectors = {
                                   {1.0, 3.0, 4.0, 6.0, 7.0, 8.0, 11.0, 12.0, 76.0, 77.0, 77.0, 77.0, 77.0, 77.0, 77.0, 77.0, 77.0, 77.0, 77.0, 77.0, 77.0, 77.0, 77.0, 77.0},
                                   {2.5025000201E10, 2.5025081001E10, 2.5025000701E10, 2.5025050901E10, 2.50250406E10, 2.5025000502E10, 2.5025040401E10, 2.50251009E10, 1111111.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0},
                                   {2.50250502002E11, 2.50250502003E11, 2.50250501013E11, 2.50250408011E11, 2.50250503001E11, 2.50250103001E11, 2.50250406002E11, 2.50250406001E11, 1111111.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0},
                                   {2.50251011024001E14, 2.50251011013003E14, 2.50251304041007E14, 2.50251011013006E14, 2.50251010016E14, 2.50251011024002E14, 2.50251001005004E14, 2.50251002003002E14, 1111111.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0, 4444444.0}
                                  };
        
        int vectorCount = 0; 
        for (int i : floatColumns) {
            // We'll be subsetting the column vectors one by one, re-opening the 
            // file each time. Inefficient - but we don't care here. 
            
            System.out.println("Verifying double float column "+i);
            
            if (generatedDataTable.getDataVariables().get(i).isIntervalContinuous()) {
                FileInputStream generatedTabInputStream = null;
                try {
                    generatedTabInputStream = new FileInputStream(generatedTabFile);
                } catch (IOException ioex) {
                    fail("Failed to open generated tab-delimited file for reading" + ioex);
                }
                
                Double[] columnVector = TabularSubsetGenerator.subsetDoubleVector(generatedTabInputStream, i, generatedDataTable.getCaseQuantity().intValue());
                
                assertArrayEquals(floatVectors[vectorCount++], columnVector);

                //System.out.print("double["+i+"] = {");
                //for (int j = 0; j < expectedNumberOfCases; j++) {
                //    System.out.print(columnVector[j]+", ");
                //}
                //System.out.println("};");
            } else {
                fail("Column "+i+" was not properly processed as \"continuous\"");
            }
        }
        
        // Discrete Numerics (aka, integers):
        
        Set<Integer> integerColumns = new HashSet<Integer>(Arrays.asList(1,4,6,7,8,12));
        
        Long[][] longVectors = {
                                {1L, 3L, 4L, 6L, 7L, 8L, 11L, 12L, 76L, 77L, 77L, 77L, 77L, 77L, 77L, 77L, 77L, 77L, 77L, 77L, 77L, 77L, 77L, 77L},
                                {1L, 2L, 3L, 4L, 5L, 11L, 13L, 15L, 19L, 19L, 19L, 19L, 19L, 19L, 19L, 19L, 19L, 19L, 19L, 19L, 19L, 19L, 19L, 19L},
                                {85729227L, 85699791L, 640323976L, 85695847L, 637089796L, 637089973L, 85695001L, 85695077L, 1111111L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L},
                                {205871733L, 205871735L, 205871283L, 258627915L, 257444575L, 205871930L, 260047422L, 262439738L, 1111111L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L},
                                {205871673L, 205871730L, 205871733L, 205872857L, 258627915L, 257444584L, 205873413L, 262439738L, 1111111L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L},
                                {2109L, 2110L, 2111L, 2120L, 2121L, 2115L, 2116L, 2122L, 11111L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L, 4444444L},
                               };
        
        vectorCount = 0; 
        
        for (int i : integerColumns) {
        //for (int i = 0; i < expectedNumberOfVariables; i++) {
            System.out.println("Verifying integer column "+i);
            if (generatedDataTable.getDataVariables().get(i).isIntervalDiscrete()
                    && generatedDataTable.getDataVariables().get(i).isTypeNumeric()) {
                
                FileInputStream generatedTabInputStream = null;
                try {
                    generatedTabInputStream = new FileInputStream(generatedTabFile);
                } catch (IOException ioex) {
                    fail("Failed to open generated tab-delimited file for reading" + ioex);
                }
                
                Long[] columnVector = TabularSubsetGenerator.subsetLongVector(generatedTabInputStream, i, generatedDataTable.getCaseQuantity().intValue());
                
                //System.out.print("long["+i+"] = {");
                //for (int j = 0; j < expectedNumberOfCases; j++) {
                //    System.out.print(columnVector[j]+"L, ");
                //}
                //System.out.println("};");
                
                assertArrayEquals(longVectors[vectorCount++], columnVector);
            } else {
                fail("Column "+i+" was not properly processed as \"discrete numeric\"");
            }
        }
        
        // And finally, Strings: 
        
        Set<Integer> stringColumns = new HashSet<Integer>(Arrays.asList(0,3,5));
        
        String[][] stringVectors = {
                                     {"Dog", "Squirrel", "Antelope", "Zebra", "Lion", "Gazelle", "Cat", "Giraffe", "Cat", "Donkey", "Donkey", "Donkey", "Donkey", "Donkey", "Donkey", "Donkey", "Donkey", "Donkey", "Donkey", "Donkey", "Donkey", "Donkey", "Donkey", "Donkey"},
                                     {"East Boston", "Charlestown", "South Boston", "Bronx", "Roslindale", "Mission Hill", "Jamaica Plain", "Hyde Park", "Fenway/Kenmore", "Queens", "Queens", "Queens", "Queens", "Queens", "Queens", "Queens", "Queens", "Queens", "Queens", "Queens", "Queens", "Queens", "Queens", "Queens"},
                                     {"2-06", "1-09", "1-1A", "1-1B", "2-04", "3-05", "1-1C", "1-10A", "41-10A", "41-10A", "41-10A", "41-10A", "41-10A", "41-10A", "41-10A", "41-10A", "41-10A", "41-10A", "41-10A", "41-10A", "41-10A", "41-10A", "41-10A", "41-10A", }
                                   };
        
        vectorCount = 0; 
        
        for (int i : stringColumns) {
        //for (int i = 0; i < expectedNumberOfVariables; i++) {
            System.out.println("Verifying character column "+i);
            if (generatedDataTable.getDataVariables().get(i).isTypeCharacter()) {
                
                FileInputStream generatedTabInputStream = null;
                try {
                    generatedTabInputStream = new FileInputStream(generatedTabFile);
                } catch (IOException ioex) {
                    fail("Failed to open generated tab-delimited file for reading" + ioex);
                }
                
                String[] columnVector = TabularSubsetGenerator.subsetStringVector(generatedTabInputStream, i, generatedDataTable.getCaseQuantity().intValue());
                
                //System.out.print("String["+i+"] = {");
                //for (int j = 0; j < expectedNumberOfCases; j++) {
                //    System.out.print("\""+columnVector[j]+"\", ");
                //}
                //System.out.println("};");
                
                assertArrayEquals(stringVectors[vectorCount++], columnVector);
            } else {
                fail("Column "+i+" was not properly processed as a character vector");
            }
        }
        
        assertTrue(true);
    }

    /*
    @Test
    public void testHardRead() {
        String testFile = "src/test/java/edu/harvard/iq/dataverse/ingest/tabulardata/impl/plugins/csv/posts_all.csv";
        String expFile = "src/test/java/edu/harvard/iq/dataverse/ingest/tabulardata/impl/plugins/csv/posts_all.tab";
        BufferedReader result = null;
        BufferedReader expected = null;
        try (BufferedInputStream stream = new BufferedInputStream(
                new FileInputStream(testFile))) {
            CSVFileReader instance = new CSVFileReader(new CSVFileReaderSpi());
            result = new BufferedReader(new FileReader(instance.read(stream, null).getTabDelimitedFile()));
            expected = new BufferedReader(new FileReader(new File(expFile)));
        } catch (IOException ex) {
            fail("" + ex);
        }

        String foundLine = null;
        String expLine = null;
        assertNotNull(result);
        assertNotNull(expected);
        int line = 0;
        while (true) {
            try {
                expLine = expected.readLine();
                foundLine = result.readLine();
            } catch (IOException ex) {
                fail();
            }
            if (!expLine.equals(foundLine)) {
                logger.info("on line:" + line);
                logger.info("expected: " + expLine);
                logger.info("found : " + foundLine);
            }
            assertEquals(expLine, foundLine);
            line++;
        }

    }*/
    
    /**
     * Tests CSVFileReader with a CSV with one more column than header. Tests
     * CSVFileReader with a null CSV.
     */
    @Test
    public void testBrokenCSV() {
        String brokenFile = "src/test/java/edu/harvard/iq/dataverse/ingest/tabulardata/impl/plugins/csv/BrokenCSV.csv";
        try {
            new CSVFileReader(new CSVFileReaderSpi()).read(null, null);
            fail("IOException not thrown on null csv");
        } catch (NullPointerException ex) {
            String expMessage = null;
            assertEquals(expMessage, ex.getMessage());
        } catch (IOException ex) {
            String expMessage = "Stream can't be null.";
            assertEquals(expMessage, ex.getMessage());
        }
        try (BufferedInputStream stream = new BufferedInputStream(
                new FileInputStream(brokenFile))) {
            new CSVFileReader(new CSVFileReaderSpi()).read(stream, null);
            fail("IOException was not thrown when collumns do not align.");
        } catch (IOException ex) {
            String expMessage = "Reading mismatch, line 3 of the Data file: 6 delimited values expected, 4 found.";
            assertEquals(expMessage, ex.getMessage());
        }
    }
}
