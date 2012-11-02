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
 * Wrapper for messages that indicate the status of a particular
 * component url of a submission.
 * 
 * @author Isaac Councill
 *
 */
public class UrlCrawledMsg extends SubmissionNotification {

    public UrlCrawledMsg(MapMessage msg) throws JMSException {
        super(URLCRAWLSTATUS, msg);
    }
    
    public UrlCrawledMsg(JMSSender sender) throws JMSException {
        super(URLCRAWLSTATUS, sender);
    }
    
    public UrlCrawledMsg(JMSSender sender, String jobID, String url,
            int status) throws JMSException {
        super(URLCRAWLSTATUS, sender, jobID, url, status);
    }
    
}  //- class UrlCrawledMsg
