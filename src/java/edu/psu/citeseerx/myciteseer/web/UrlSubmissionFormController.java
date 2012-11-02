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

import java.util.*;

import javax.servlet.http.*;

import org.springframework.validation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import edu.psu.citeseerx.utility.*;
import edu.psu.citeseerx.myciteseer.domain.*;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.messaging.*;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

public class UrlSubmissionFormController extends SimpleFormController {

    private MyCiteSeerFacade myciteseer;
    
    public UrlSubmissionFormController() {
        setSessionForm(true);
        setValidateOnBinding(false);
        setCommandName("urlSubmissionForm");
        setFormView("UrlSubmissionPage");
    }
    
    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    }
    
    
    private SubmissionSender sender;
    
    public void setSubmissionSender(SubmissionSender sender) {
        this.sender = sender;
    }
    
    
    protected Object formBackingObject(HttpServletRequest request)
            throws Exception {
        UrlSubmissionForm form = new UrlSubmissionForm();
        form.setPreviousSubmissions(myciteseer.getUrlSubmissions(
                form.getUrlSubmission().getUsername()));
        return form;
        
    }  //- formBackingObject
    
    
    protected void onBindAndValidate(HttpServletRequest request,
            Object command, BindException errors) throws Exception {
        
        UrlSubmissionForm submissionForm = (UrlSubmissionForm)command;
        UrlSubmission submission = submissionForm.getUrlSubmission(); 
        
        errors.setNestedPath("urlSubmission");
        getValidator().validate(submission, errors);
        errors.setNestedPath("");
                
    }  //- onBindAndValidate
    
    
    protected Map referenceData(HttpServletRequest request) throws Exception {
        Map model = new HashMap();
        return model;
        
    }  //- referenceData
    
    
    protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object command,
            BindException errors) throws Exception {
        
        MCSConfiguration config = myciteseer.getConfiguration();
        if (!config.getUrlSubmissionsEnabled()) {
            return new ModelAndView("urlSubmissionsDisabled", null);
        }
        
        UrlSubmissionForm submissionForm = (UrlSubmissionForm)command;
        UrlSubmission submission = submissionForm.getUrlSubmission();
        Account account = MCSUtils.getLoginAccount();
        if (account != null) {
            String jid =
                CSXConstants.USER_SUBMISSION_PREFIX + account.getUsername() +
                System.currentTimeMillis();
            submission.setJobID(jid);
            submission.setUsername(account.getUsername());
            submission.setDepth(1);
        }
        myciteseer.insertUrlSubmission(submissionForm.getUrlSubmission());
        try {
            sender.sendMessage(submission);
        } catch (Exception e) {
            e.printStackTrace();
            errors.rejectValue("urlSubmission.url", "URL_SUBMISSION_ERROR",
                    "There was an error sending this URL to the crawler - "+
                    "please try again later.");
            return showForm(request, response, errors);
        }
        return super.onSubmit(request, response, command, errors);
        
    }  //- onSubmit

}  //- class UrlSubmissionFormController
