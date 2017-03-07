package edu.harvard.iq.dataverse.util.xml;

import java.io.IOException;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.xml.sax.SAXException;

public class XmlValidatorTest {

    private static final Logger logger = Logger.getLogger(XmlValidatorTest.class.getCanonicalName());

    @Test
    public void testValidateXml() throws IOException, SAXException {
        String dir = "src/test/java/edu/harvard/iq/dataverse/export/ddi/";

        System.out.println("Attempting to validate a DDI 2.0 file...");
        assertEquals(true, XmlValidator.validateXml(dir + "dataset-finch1-ddi-2.0.xml", dir + "Version2-0.xsd"));

        System.out.println("Attempting to validate a DDI 2.5 file...");
        // codebook.xsd comes from http://www.ddialliance.org/Specification/DDI-Codebook/2.5/XMLSchema/codebook.xsd
        /**
         * @todo Make the DDI 2.5 format we export pass validation. See
         * https://github.com/IQSS/dataverse/issues/3648
         */
        assertEquals(true, XmlValidator.validateXml(dir + "dataset-finch1.xml", dir + "codebook.xsd"));
    }

}
