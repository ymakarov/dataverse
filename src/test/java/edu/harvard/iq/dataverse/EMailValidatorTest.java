package edu.harvard.iq.dataverse;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class EMailValidatorTest {

    @Test
    public void testIsValidEmailAddress() {
        assertEquals(false, EMailValidator.isValidEmailAddress(null));
        assertEquals(false, EMailValidator.isValidEmailAddress(""));
        assertEquals(true, EMailValidator.isValidEmailAddress("pete@mailinator.com"));
        assertEquals(false, EMailValidator.isValidEmailAddress("pete1@mailinator.com;pete2@mailinator.com"));
    }

}
