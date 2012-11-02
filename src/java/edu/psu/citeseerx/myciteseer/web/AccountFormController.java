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
package edu.psu.citeseerx.myciteseer.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.*;

import com.google.code.kaptcha.Constants;

import org.springframework.dao.*;
import org.springframework.security.providers.dao.SaltSource;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.validation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.*;
import edu.psu.citeseerx.myciteseer.web.mail.MailManager;

public class AccountFormController extends SimpleFormController {

    private MyCiteSeerFacade myciteseer;
    
    public AccountFormController() {
        setSessionForm(true);
        setValidateOnBinding(false);
        setCommandName("accountForm");
        setFormView("EditAccountForm");
    }
    
    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    }
    
    
    private MailManager mailManager;
    
    public void setMailManager(MailManager mailManager) {
        this.mailManager = mailManager;
    }
    
    
    protected Object formBackingObject(HttpServletRequest request)
            throws Exception {
        Account account = MCSUtils.getLoginAccount();
        if (account != null) {
            return new AccountForm(account);
        } else {
            return new AccountForm();
        }
        
    }  //- formBackingObject
    
    
    protected void onBindAndValidate(HttpServletRequest request,
            Object command, BindException errors) throws Exception {
        
        AccountForm accountForm = (AccountForm) command;
        Account account = accountForm.getAccount();
        
        errors.setNestedPath("account");
        getValidator().validate(account, errors);
        errors.setNestedPath("");
        
        if (accountForm.isNewAccount()) {
            account.setStatus("OK");
            
            String captcha = accountForm.getCaptcha();
            String captchaTarget = (String)request.getSession()
                    .getAttribute(Constants.KAPTCHA_SESSION_KEY);
            if (!captchaTarget.equals(captcha)) {
                errors.rejectValue("captcha", "BAD_CAPTCHA_RESPONSE",
                	"Incorrect captcha response.");
            }

            ValidationUtils.rejectIfEmpty(errors, "account.username",
                    "USER_ID_REQUIRED", "User ID is required.");
            if (account.getPassword() == null ||
                    account.getPassword().length() < 1 ||
                    !account.getPassword().equals(
                            accountForm.getRepeatedPassword())) {
                errors.reject("PASSWORD_MISMATCH",
                        "Passwords did not match or were not provided. " +
                        "Matching passwords are required.");
            }
        }
        /*else if (account.getPassword() != null &&
                account.getPassword().length() > 0) {
            if (!account.getPassword().equals(
                    accountForm.getRepeatedPassword())) {
                errors.reject("PASSWORD_MISMATCH",
                        "Passwords did not match.  " +
                        "Matching passwords are required.");
            }
        }*/
        
    }  //- onBindAndValidate
    
    
    protected Map referenceData(HttpServletRequest request) throws Exception {
        Map model = new HashMap();
        return model;
        
    }  //- referenceData
    
    
    protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object command,
            BindException errors) throws Exception {
        
        onBindAndValidate(request, command, errors);
        if (errors.hasErrors()) {
            return showForm(request, response, errors);
        }
        
        AccountForm accountForm = (AccountForm)command;
        
        try {
            if (accountForm.isNewAccount()) {
                String rawPassword = accountForm.getAccount().getPassword();
                if (rawPassword.length() < MCSConstants.MIN_PASSWD_LENGTH) {
                    throw new BadPasswordException("Password too short");
                }
                encodePassword(accountForm.getAccount());
                this.myciteseer.insertAccount(accountForm.getAccount());
                sendAccountActivationMessage(request, accountForm);
                String ticket = request.getParameter("ticket");
                if (ticket != null) {
                    myciteseer.deleteInvitationTicket(ticket);
                }
//                RedirectUtils.redirectAcegiLogin(request, response,
//                        accountForm.getAccount().getUsername(),
//                        rawPassword);
            } else {
                this.myciteseer.updateAccount(accountForm.getAccount());
            }
        } catch (DataIntegrityViolationException ex) {
            errors.rejectValue("account.username", "USER_ID_ALREADY_EXISTS",
                    "User ID already exists: choose a different ID.");
            return showForm(request, response, errors);
        } catch (BadPasswordException e) {
            errors.rejectValue("account.password", "BAD_PASSWORD",
                    "Supplied password is too short.");
            return showForm(request, response, errors);
        } catch (Exception e) {
            errors.rejectValue("account.username", "UNKNOWN_ERROR",
                    "An error occurred during the processing of " +
                    "your request.  Please try again later.");
            return showForm(request, response, errors);
        }
        /*
        UserSession userSession = new UserSession(
                this.myciteseer.getAccount(
                        accountForm.getAccount().getUsername()));
        request.getSession().setAttribute("userSession", userSession);
        */
        return super.onSubmit(request, response, command, errors);
        
    }  //- onSubmit
    
    
    private void sendAccountActivationMessage(HttpServletRequest request,
            AccountForm accountForm) {
        
        String username = accountForm.getAccount().getUsername();
        String code = request.getSession().getId();
        
        myciteseer.storeActivationCode(username, code);
        
        StringBuffer urlBuffer = request.getRequestURL();
        int lastSlash = urlBuffer.lastIndexOf("/");
        urlBuffer = new StringBuffer(urlBuffer.substring(0, lastSlash));
        urlBuffer.append("/activateAccount");
        urlBuffer.append("?id=");
        urlBuffer.append(username);
        urlBuffer.append("&recv=");
        urlBuffer.append(code);
        
        mailManager.sendAccountActivationMessage(
                accountForm.getAccount().getEmail(), urlBuffer.toString());
        
    }  //- sendAccountActivationMessage
    
    
    private SaltSource saltSource;
    
    public void setSaltSource(SaltSource saltSource) {
        this.saltSource = saltSource;
    }
    
    private PasswordEncoder passwordEncoder;
    
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    private void encodePassword(Account account) {
        Object salt = saltSource.getSalt(account);
        String encodedPassword =
            passwordEncoder.encodePassword(account.getPassword(), salt);
        account.setPassword(encodedPassword);

    }  //- encodePassword
    
}  //- class AccountFormController


class BadPasswordException extends RuntimeException {
    public BadPasswordException(String msg) {
        super(msg);
    }
}
