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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.*;

import org.springframework.security.providers.dao.SaltSource;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import edu.psu.citeseerx.myciteseer.web.mail.MailManager;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.web.utils.RandomStringGenerator;

public class ForgottenAccountController implements Controller {

    private MyCiteSeerFacade myciteseer;
    
    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    }
    
    
    private MailManager mailManager;
    
    public void setMailManager(MailManager mailManager) {
        this.mailManager = mailManager;
    }
    
    
    private SaltSource saltSource;
    
    public void setSaltSource(SaltSource saltSource) {
        this.saltSource = saltSource;
    }
    
    private PasswordEncoder passwordEncoder;
    
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        
        Map model = new HashMap();
        Boolean error = false;
        String errMsg = "";
        
        String email = request.getParameter("email");
        if (email == null) {
            return new ModelAndView("forgotAccount", model);
        }
        Account account = myciteseer.getAccountByEmail(email);
        if (account == null) {
            error = true;
            errMsg = "No account matched the email address you entered";
        }
        if (!error) {
            String password = RandomStringGenerator.randomString(8, 12);
            account.setPassword(password);
            encodePassword(account);

            myciteseer.changePassword(account);
            mailManager.sendForgottenAccountMessage(email, account.getUsername(),
                    password);
        }
        model.put("error", error);
        model.put("errMsg", errMsg);
        model.put("email", email);
        
        return new ModelAndView("forgotAccountResult", model);
        
    }  //- handleRequest
    
    
    private void encodePassword(Account account) {
        Object salt = saltSource.getSalt(account);
        String encodedPassword =
            passwordEncoder.encodePassword(account.getPassword(), salt);
        account.setPassword(encodedPassword);

    }  //- encodePassword
    
}
