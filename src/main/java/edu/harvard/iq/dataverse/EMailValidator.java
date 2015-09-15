/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dataverse;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.validator.routines.EmailValidator;

/**
 *
 * @author skraffmi
 */
public class EMailValidator implements ConstraintValidator<ValidateEmail, String> {

    @Override
    public void initialize(ValidateEmail constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        
        return isEmailValid(value, context);

        
    }
    
    public static boolean isEmailValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            //we'll let someone else decide if it's required
            return true;
        }
        /**
         * @todo Why are we validating the *trimmed* value but (presumably)
         * persisting the value that is *not* trimmed? Shouldn't any trimming
         * happen *before* the value is passed to this method?
         */
        boolean isValid = isValidEmailAddress(value.trim());
        if (!isValid) {
            context.buildConstraintViolationWithTemplate(value + " is not a valid email address.").addConstraintViolation();
            return false;
        }
        return true;
    }

    public static boolean isValidEmailAddress(String value) {
        return EmailValidator.getInstance().isValid(value);
    }
}
