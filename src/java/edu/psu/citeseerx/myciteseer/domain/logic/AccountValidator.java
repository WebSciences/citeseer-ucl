/*
 * Copyright 2007 Penn State University
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.psu.citeseerx.myciteseer.domain.logic;

import java.lang.reflect.Method;
import java.util.Locale;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.net.URL;
import java.net.MalformedURLException;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;

import org.springframework.validation.*;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

/**
 * Account creation/editing form validation utility.
 *
 * @author Isaac Councill
 *
 */
public class AccountValidator implements Validator {

    private MyCiteSeerFacade myciteseer;

    private MessageSource messageSource;

    public boolean supports(Class clazz) {
        return Account.class.isAssignableFrom(clazz);
    }


    public void validate(Object obj, Errors errors) {

        Account account = (Account)obj;

        validateFirstName(account.getFirstName(), errors);
        validateMiddleName(account.getMiddleName(), errors);
        validateLastName(account.getLastName(), errors);
        validateEmail(account.getEmail(), errors);
        validateAffiliation1(account.getAffiliation1(), errors);
        validateWebPage(account.getWebPage(), errors);
        validateCountry(account.getCountry(), errors);

    }  //- validate


    public void validateUsername(String username, Errors errors) {

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username",
                "USERNAME_REQUIRED",
        "Username is required.");
        if (errors.getAllErrors().size() == 0) {
            try {
                Account existingAccount = myciteseer.getAccount(username);
                if (existingAccount != null) {
                    errors.rejectValue("username", "USERNAME_UNIQUE_REQUIRED",
                    "This user ID is already taken.");
                }
            } catch (Exception e) { /* ignore */ }
        }

    }  //- validateUsername


    public void validateEmail(String email, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email",
                "EMAIL_REQUIRED", "Email address is required.");
        if (errors.getAllErrors().size() > 0) {
            return;
        }        
        try {
            InternetAddress address = new InternetAddress(email);
            if (!hasNameAndDomain(email)) {
                errors.rejectValue("email", "VALID_EMAIL_REQUIRED",
                "Invalid email address");
            }
        } catch (AddressException e) {
            errors.rejectValue("email", "VALID_EMAIL_REQUIRED",
            "Invalid email address");            
        }
        if (errors.getAllErrors().size() > 0) {
            return;
        }
        try {
            Account loginAccount = MCSUtils.getLoginAccount();
            Account existingAccount = myciteseer.getAccountByEmail(email);
            if (existingAccount != null) {
                if ((loginAccount == null) ||
                        !loginAccount.getUsername().equals(
                                existingAccount.getUsername())) {
                    errors.rejectValue("email", "EMAIL_UNIQUE_REQUIRED",
                    "This email address is already in use.");                    
                }
            }
        } catch (Exception e) { /* ignore */ }
        
    }  //- validateEmail
    
    
    private static boolean hasNameAndDomain(String emailAddress) {
        String[] tokens = emailAddress.split("@");
        return (tokens.length == 2 &&
                tokens[0].length() > 0 &&
                tokens[1].length() > 0);
        
    }  //- hasNameAndDomain


    public void validateFirstName(String firstName, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,
                "firstName",
                "FIRST_NAME_REQUIRED",
                "First name is required.");
    }


    public void validateMiddleName(String firstName, Errors errors) {}


    public void validateLastName(String lastName, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,
                "lastName",
                "LAST_NAME_REQUIRED",
                "Last name is required.");
    }


    public void validateAffiliation1(String affiliation1, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,
                "affiliation1",
                "AFFILIATION_REQUIRED", "Affiliation is required.");
    }
    
    
    public void validateWebPage(String webPage, Errors errors) {
        if (webPage == null || webPage.length() <= 0) {
            return;
        }
        try {
            URL url = new URL(webPage);
        } catch (MalformedURLException e) {
            errors.rejectValue("webPage", "INVALID_URL",
                    "Invalid url.  Did you include the protocol " +
                    "(e.g., http://)?");
        }
        
    }  //- validateWebPage
    
    
    public void validateCountry(String country, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,
                "country", "COUNTRY_REQUIRED", "Country is required.");
    }


    public String getInputFieldValidationMessage(String formInputId,
            String formInputValue) {
        String validationMessage = "";

        try {
            Object formBackingObject = new Account();
            Errors errors = new BindException(formBackingObject, "command");

            formInputId = formInputId.split("\\.")[1];
            String capitalizedFormInputId = StringUtils.capitalize(formInputId);

            String accountMethodName = "set" + capitalizedFormInputId;
            Class setterArgs[] = new Class[] { String.class };
            Method accountMethod =
                formBackingObject.getClass().getMethod(accountMethodName,
                        setterArgs);
            accountMethod.invoke(formBackingObject,
                    new Object[] { formInputValue });

            String validationMethodName = "validate" + capitalizedFormInputId;
            Class validationArgs[] = new Class[] { String.class, Errors.class };
            Method validationMethod = getClass().getMethod(validationMethodName,
                    validationArgs);
            validationMethod.invoke(this,
                    new Object[] { formInputValue, errors });

            validationMessage = getValidationMessage(errors, formInputId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return validationMessage;

    }  //- getInputFieldValidationMessage


    protected String getValidationMessage(Errors errors, String fieldName) {

        String message = "";
        FieldError fieldError = errors.getFieldError(fieldName);

        if (fieldError != null) {
            message = messageSource.getMessage(fieldError.getCode(), null,
                    "This field is invalid",
                    Locale.ENGLISH);
        }

        return message;

    }  //- getValidationMessage


    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    }

}  //- class AccountValidator
