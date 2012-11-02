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

import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.domain.Account;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import java.io.IOException;

import java.util.HashMap;

public class AccountActivationController implements Controller {

    private MyCiteSeerFacade myciteseer;
    
    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    }
    
    private String view;
    
    public void setView(String view) {
        this.view = view;
    }
    
    
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        
        String username = request.getParameter("id");
        String code = request.getParameter("recv");

        HashMap<String,String> model = new HashMap<String,String>();

        boolean valid = myciteseer.isValidActivationCode(username, code);
        if (valid) {
            Account account = myciteseer.getAccount(username);
            account.setEnabled(true);
            myciteseer.updateAccount(account);
            myciteseer.deleteActivationCode(username);
            model.put("username", account.getFirstName());
            model.put("success", "true");
        } else {
            model.put("success", "false");
        }
        
        return new ModelAndView(view, model);
        
    }  //- handleRequest
    
}  //- class AccountActivationController
