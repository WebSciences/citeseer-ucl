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
package edu.psu.citeseerx.messaging.messages;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import edu.psu.citeseerx.messaging.JMSSender;

/**
 * Wrapper for messages that provide updates regarding the status
 * of submissions after the ingestion phase.
 * 
 * @author Isaac Councill
 *
 */
public class IngestStatusMsg extends SubmissionNotification {

    public IngestStatusMsg(MapMessage msg) throws JMSException {
        super(INGESTSTATUS, msg);
    }
    
    public IngestStatusMsg(JMSSender sender) throws JMSException {
        super(INGESTSTATUS, sender);
    }
    
    public IngestStatusMsg(JMSSender sender, String jobID, String url,
            int status) throws JMSException {
        super(INGESTSTATUS, sender, jobID, url, status);
    }
    
    
    protected static final String DOI_LABEL = "doi";
    
    public String getDOI() throws JMSException {
        return msg.getString(DOI_LABEL);
    }
    
    public void setDOI(String doi) throws JMSException {
        msg.setString(DOI_LABEL, doi);
    }
    
    
}  //- class IngestStatusMsg 
