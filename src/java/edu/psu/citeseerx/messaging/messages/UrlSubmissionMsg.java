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

import javax.jms.*;
import edu.psu.citeseerx.messaging.*;

/**
 * Wrapper for messages that are sent to the crawler to indicate a new
 * URLs that should be crawled.
 * 
 * @author Isaac Councill
 *
 */
public class UrlSubmissionMsg extends JMSMessage {

    public UrlSubmissionMsg(MapMessage msg) {
        super(msg);
    }
    
    public UrlSubmissionMsg(JMSSender sender) throws JMSException {
        super(sender);
    }
    
    public UrlSubmissionMsg(JMSSender sender, String jobID, String url,
            String description) throws JMSException {
        super(sender);
        setJobID(jobID);
        setUrl(url);
        setDescription(description);
    }
    
    
    protected final static String jobIDLabel = "JID";
    
    public String getJobID() throws JMSException {
        return msg.getString(jobIDLabel);
    }
    
    public void setJobID(String id) throws JMSException {
        msg.setString(jobIDLabel, id);
    }
    
    
    protected final static String urlLabel = "URL";
    
    public String getUrl() throws JMSException {
        return msg.getString(urlLabel);
    }
    
    public void setUrl(String url) throws JMSException {
        msg.setString(urlLabel, url);
    }
    
    
    protected final static String descLabel = "DESC";
    
    public String getDescription() throws JMSException {
        String desc = msg.getString(descLabel);
        if (desc != null) {
            return desc;
        }
        return "";
    }
    
    public void setDescription(String desc) throws JMSException {
        if (desc == null) {
            desc = "";
        }
        msg.setString(descLabel, desc);
    }
    
    public boolean validate() throws JMSException {
        return (msg.itemExists(jobIDLabel) &&
                msg.itemExists(urlLabel));
    }
    
}  //- class UrlSubmissionMsg
