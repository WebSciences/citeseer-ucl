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

/*

	Hacked in by: Pradeep Teregowda 08-18-2008 
*/

package edu.psu.citeseerx.web;

import java.io.IOException;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.AddressException;

import java.net.URL;
import java.net.MalformedURLException;

import java.util.*;
import java.util.logging.*;

public class SimpleDocSubmit implements Controller {
    
    
    /** Value to be supplied with the "rt" URL parameter.  Value is "view" */
    public static final String VIEW_TYPE = "view";

    /** Value to be supplied with the "rt" URL parameter.  Value is "send" */
    public static final String SEND_TYPE = "send";

    private String fileName = "";
    private static HashSet<String> reqTypes = new HashSet<String>();
    private static Logger logger;
    private static FileHandler fh;
	
    static {
        reqTypes.add(VIEW_TYPE);
        reqTypes.add(SEND_TYPE);        
    }
    
    private String defaultEmail = "you@your.org";
    private String defaultURL = "http://yourwebsite.org";
    
    public void setDefaultEmail(String demail) {
        this.defaultEmail = demail;
    }
    
    public void setDefaultURL(String durl) {
        this.defaultURL = durl;
    }
   
    public void setLogFile(String logFile) {
        this.fileName = logFile;
    }
 
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        
        String addr    = request.getParameter("email");
        String submitURL    = request.getParameter("url");
        String reqType = request.getParameter("rt");
        Account account = MCSUtils.getLoginAccount();
        
        if (addr == null && account != null) {
        	addr = account.getEmail();
        }
	else if (addr == null && account == null) {
        	addr = defaultEmail;
        }

        if (submitURL == null) submitURL = defaultURL;
  
      	if (reqType == null || !reqTypes.contains(reqType)) reqType = VIEW_TYPE;
        
        Map model = new HashMap();
        model.put("email", addr);
        model.put("url", submitURL);

        Boolean error = false;
        String errMsg = "";
        
	if(submitURL == defaultURL) {
		error = true;
		errMsg = "Please enter a valid document link";
	}


        if (!isValidEmailAddress(addr)) {
            error = true;
            errMsg = "Please enter a valid e-mail address";
        }

	if(!isURLValid(submitURL)) {
	    error = true;
	    errMsg = "Please enter a valid document link";
	}

        model.put("error", error);
        model.put("errMsg", errMsg);


        if (reqType != null && reqType.equals(SEND_TYPE)) {
            storeSubmissioninLog(model);
            error = (Boolean)model.get("error");
        }

        if (!error && reqType.equals(SEND_TYPE)) {
            return new ModelAndView("submitSent", null);
        } else {
            return new ModelAndView("submit", model);
        }
        
    }  //- handleRequest
   
    private static boolean isValidEmailAddress(String emailAddress) {
	// a null string is invalid
         if ( emailAddress == null ) return false;

    // a string without an @ symbol is an invalid email address
    	if ( emailAddress.indexOf("@") < 0 ) return false;

    // a string without an @ symbol is an invalid email address
    	if ( emailAddress.indexOf(".") < 0 ) return false;

    	if ( lastEmailFieldTwoCharsOrMore(emailAddress) == false ) return false;

    	try
    	{
      		InternetAddress internetAddress = new InternetAddress(emailAddress);
      		return true;
    	}
    	catch (AddressException ae)
   	{
      		return false;
    	}
    } //- email validation from http://www.devdaily.com/java/junit/node18.shtml
 
   
    private static boolean lastEmailFieldTwoCharsOrMore(String emailAddress) {
  	StringTokenizer st = new StringTokenizer(emailAddress,".");
  	String lastToken = null;
  	while ( st.hasMoreTokens() )
  	{
    		lastToken = st.nextToken();
  	}

  	if ( lastToken.length() >= 2 )
  	{
    		return true;
  	}
  	else
  	{
    		return false;
  	}
    } //- email validation (lastEmail...) from http://www.devdaily.com/java/junit/node18.shtml

	
    private static boolean isURLValid(String inURL) {
	 try {
		URL canformURL = new URL(inURL);
	 }
	 catch (MalformedURLException m) {
		return false;
	 }
	 return true;
    }
 
    protected void storeSubmissioninLog(Map model) {
	/* 
		Log the message ... we will look at it later
	*/
	logger = Logger.getLogger("edu.psu.citeseerx.web.SimpleDocSubmit");
	try {
		fh = new FileHandler(fileName, true);
		logger.addHandler(fh);
		logger.info((String)model.get("email")+":"+(String)model.get("url"));
		fh.close();
	}
	catch (Exception exc) {
		model.put("error", true);
		model.put("errMsg", "Submissions is not currently active, please try again later");
        	// model.put("errMsg", "Store Error"+exc.getMessage());
	};
       	 
    }  //- storeSubmissioninLog

}  //- class SimpleDocSubmit
