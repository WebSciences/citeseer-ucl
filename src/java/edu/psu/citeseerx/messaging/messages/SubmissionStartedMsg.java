package edu.psu.citeseerx.messaging.messages;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import edu.psu.citeseerx.messaging.JMSSender;

public class SubmissionStartedMsg extends SubmissionNotification {

    public SubmissionStartedMsg(MapMessage msg) throws JMSException {
        super(JOBSTARTED, msg);
    }
    
    public SubmissionStartedMsg(JMSSender sender) throws JMSException {
        super(JOBSTARTED, sender);
    }
    
    public SubmissionStartedMsg(JMSSender sender, String jobID, String url,
            int status) throws JMSException {
        super(JOBSTARTED, sender, jobID, url, status);
    }
}
