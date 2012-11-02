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
package edu.psu.citeseerx.myciteseer.dao;

import edu.psu.citeseerx.myciteseer.domain.*;
import edu.psu.citeseerx.utility.UrlStatusMappings;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.context.ApplicationContextException;

import java.sql.*;
import javax.sql.*;
import java.util.List;

public class SubmissionDAOImpl extends JdbcDaoSupport implements SubmissionDAO {
    
    protected UrlSubmittedMapping urlSubmittedMapping;
    protected InsertUrlSubmission insertUrlSubmission;
    protected InsertSubmissionComponent insertSubComponent;
    protected UpdateSubmissionComponent updateSubComponent;
    protected GetUrlSubmissionsMapping getUrlSubmissionsMapping;
    protected GetSubmissionComponentsMapping getSubComponentsMapping;
    protected GetSubmissionComponentMapping getSubComponentMapping;
    protected GetUrlSubmissionMapping getUrlSubmissionMapping;
    protected UpdateSubStatus updateSubStatus;

    protected void initDao() throws ApplicationContextException {
        initMappingSqlQueries();
    }

    
    protected void initMappingSqlQueries() {
        this.urlSubmittedMapping = new UrlSubmittedMapping(getDataSource());
        this.insertUrlSubmission = new InsertUrlSubmission(getDataSource());
        this.insertSubComponent =
            new InsertSubmissionComponent(getDataSource());
        this.updateSubComponent =
            new UpdateSubmissionComponent(getDataSource());
        this.getUrlSubmissionsMapping =
            new GetUrlSubmissionsMapping(getDataSource());
        this.getSubComponentsMapping =
            new GetSubmissionComponentsMapping(getDataSource());
        this.getSubComponentMapping =
            new GetSubmissionComponentMapping(getDataSource());
        this.getUrlSubmissionMapping =
            new GetUrlSubmissionMapping(getDataSource());
        this.updateSubStatus = new UpdateSubStatus(getDataSource());

    }
    
    
    public boolean isUrlAlreadySubmitted(String url, String username) {
        List submissions = urlSubmittedMapping.run(url, username);
        if (submissions.size() > 0) {
            System.err.println("already submitted true");
            return true;
        } else {
            System.err.println("already submitted false");
            return false;
        }
        
    }  //- urlAlreadySubmitted
    
    
    protected boolean isKnownComponent(SubmissionNotificationItem item) {
        List components =
            getSubComponentMapping.run(item.getJobID(), item.getURL());
        if (components.size() > 0) {
            return true;
        } else {
            return false;
        }
    }
    
    
    public void insertUrlSubmission(UrlSubmission submission) {
        insertUrlSubmission.run(submission);
    }
    
    
    public List getUrlSubmissions(String username) {
        return getUrlSubmissionsMapping.execute(username);
    }
    
    
    public UrlSubmission getUrlSubmission(String jobID) {
        List list = getUrlSubmissionMapping.execute(jobID);
        if (list.isEmpty()) {
            return null;
        }
        return (UrlSubmission)list.get(0);
    }
    
    
    public void insertSubmissionComponent(SubmissionNotificationItem item) {
        System.err.println("INSERTING COMPONENT");
        if (isKnownComponent(item)) {
            if (item.isSuccess()) {
                updateSubComponent.run(item);
            }
        } else {
            insertSubComponent.run(item);
        }
        
    }  //- insertSubmissionComponent
    
    
    public List getSubmissionComponents(String JID) {
        return getSubComponentsMapping.execute(JID);
    }
    
    
    public void updateJobStatus(String JID, int status) {
        updateSubStatus.run(JID, status);
    }

    
    public static final String DEF_INSERT_URL_SUBMISSION_STATEMENT =
        "insert into submissionJobs values " +
        "(?, ?, ?, 1, NULL, "+UrlStatusMappings.CSX_CRAWL_UNPROCESSED+
        ", CURRENT_TIMESTAMP)";

    protected class InsertUrlSubmission extends SqlUpdate {
        public InsertUrlSubmission(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_INSERT_URL_SUBMISSION_STATEMENT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(UrlSubmission submission) {
            Object[] params = new Object[] {
                    submission.getJobID(),
                    submission.getUsername(),
                    submission.getUrl() };
            return update(params);
        }
        
    }  //- class InsertUrlSubmission
    
    
    public static final String DEF_INSERT_SUB_COMPONENT_STATEMENT =
        "insert into submissionComponents values (NULL, ?, ?, ?, NULL, ?)";
    
    protected class InsertSubmissionComponent extends SqlUpdate {
        public InsertSubmissionComponent(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_INSERT_SUB_COMPONENT_STATEMENT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(SubmissionNotificationItem data) {
            Object[] params = new Object[] {
                    data.getJobID(),
                    data.getURL(),
                    new Integer(data.getStatus()),
                    data.getDID()
            };
            return update(params);
        }
        
    }  //- class InsertSubmissionComponent
    
    
    public static final String DEF_UPDATE_SUB_COMPONENT_STATEMENT =
        "update submissionComponents set status=?, DID=?, " +
        "time=current_timestamp where JID=? and URL=?";

    protected class UpdateSubmissionComponent extends SqlUpdate {
        public UpdateSubmissionComponent(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_UPDATE_SUB_COMPONENT_STATEMENT);
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(SubmissionNotificationItem data) {
            Object[] params = new Object[] {
                    new Integer(data.getStatus()),
                    data.getDID(),
                    data.getJobID(),
                    data.getURL()
            };
            return update(params);
        }
        
    }  //- class UpdateSubmissionComponent
    
    
    public static final String DEF_URL_ALREADY_SUBMITTED_QUERY =
        "select JID, UID, URL, depth, time, status, statusTime from "+
        "submissionJobs where UID=? and URL=?";

    protected class UrlSubmittedMapping extends MappingSqlQuery {
        
        public UrlSubmittedMapping(DataSource ds) {
            super(ds, DEF_URL_ALREADY_SUBMITTED_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        protected Object mapRow(ResultSet rs, int rownum) throws SQLException {
            UrlSubmission submission = new UrlSubmission();
            submission.setJobID(rs.getString("JID"));
            submission.setUsername(rs.getString("UID"));
            submission.setUrl(rs.getString("URL"));
            submission.setDepth(rs.getInt("depth"));
            submission.setTime(new Date(rs.getTimestamp("time").getTime()));
            submission.setStatus(rs.getInt("status"));
            submission.setStatusTime(
                    new Date(rs.getTimestamp("statusTime").getTime()));
            return submission;
            
        }  //- UserMapping.mapRow

        public List run(String url, String username) {
            System.err.println("user " + username + " url " + url);
            Object[] params = new Object[] { username, url };
            return execute(params);
        }
        
    }  //- class UrlSubmittedMapping
    
    
    public static final String DEF_GET_URL_SUBMISSIONS_QUERY =
        "select JID, UID, URL, depth, time, status, statusTime from " +
        "submissionJobs where UID=? order by time desc";

    protected class GetUrlSubmissionsMapping extends MappingSqlQuery {
        
        public GetUrlSubmissionsMapping(DataSource ds) {
            super(ds, DEF_GET_URL_SUBMISSIONS_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public Object mapRow(ResultSet rs, int rownum) throws SQLException {
            UrlSubmission submission = new UrlSubmission();
            submission.setJobID(rs.getString("JID"));
            submission.setUsername(rs.getString("UID"));
            submission.setUrl(rs.getString("URL"));
            submission.setDepth(rs.getInt("depth"));
            submission.setTime(new Date(rs.getTimestamp("time").getTime()));
            submission.setStatus(rs.getInt("status"));
            submission.setStatusTime(
                    new Date(rs.getTimestamp("statusTime").getTime()));
            return submission;
            
        }  //- GetUrlSubmissionsMapping.mapRow
        
    }  //- class GetUrlSubmissionsMapping
    
    
    public static final String DEF_GET_URL_SUBMISSION_QUERY =
        "select JID, UID, URL, depth, time, status, statusTime " +
        "from submissionJobs where JID=?";

    protected class GetUrlSubmissionMapping extends MappingSqlQuery {
        
        public GetUrlSubmissionMapping(DataSource ds) {
            super(ds, DEF_GET_URL_SUBMISSION_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public Object mapRow(ResultSet rs, int rownum) throws SQLException {
            UrlSubmission submission = new UrlSubmission();
            submission.setJobID(rs.getString("JID"));
            submission.setUsername(rs.getString("UID"));
            submission.setUrl(rs.getString("URL"));
            submission.setDepth(rs.getInt("depth"));
            submission.setTime(new Date(rs.getTimestamp("time").getTime()));
            submission.setStatus(rs.getInt("status"));
            submission.setStatusTime(
                    new Date(rs.getTimestamp("statusTime").getTime()));
            return submission;
            
        }  //- GetUrlSubmissionMapping.mapRow
        
    }  //- class GetUrlSubmissionMapping
    
    
    public static final String DEF_GET_SUB_COMPONENTS_QUERY =
        "select JID, URL, status, time, DID from submissionComponents " +
        "where JID=?";

    protected class GetSubmissionComponentsMapping extends MappingSqlQuery {
        
        public GetSubmissionComponentsMapping(DataSource ds) {
            super(ds, DEF_GET_SUB_COMPONENTS_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public Object mapRow(ResultSet rs, int rownum) throws SQLException {
            SubmissionNotificationItem item = new SubmissionNotificationItem();
            item.setJobID(rs.getString("JID"));
            item.setURL(rs.getString("URL"));
            item.setStatus(rs.getInt("status"));
            item.setTime(rs.getTimestamp("time").getTime());
            item.setDID(rs.getString("DID"));
            return item;
            
        }  //- GetSubmissionComponentsMapping.maprow
        
    }  //- class GetSubmissionComponentsMapping
    
    
    public static final String DEF_GET_SUB_COMPONENT_QUERY =
        "select id from submissionComponents where JID=? and URL=?";

    protected class GetSubmissionComponentMapping extends MappingSqlQuery {
        
        public GetSubmissionComponentMapping(DataSource ds) {
            super(ds, DEF_GET_SUB_COMPONENT_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public Object mapRow(ResultSet rs, int rownum) throws SQLException {
            return new Integer(rs.getInt("id"));   
        }
        
        
        public List run(String JID, String URL) {
            Object[] params = new Object[] { JID, URL };
            return execute(params);
        }
        
    }  //- class GetSubmissionComponentMapping
    
    
    public static final String DEF_UPDATE_SUB_STATUS =
        "update submissionJobs set status=? where JID=?";
    
    protected class UpdateSubStatus extends SqlUpdate {
        
        public UpdateSubStatus(DataSource ds) {
            super(ds, DEF_UPDATE_SUB_STATUS);
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(String JID, int status) {
            return update(new Object[] { new Integer(status), JID });
        }
        
    }  //- class UpdateSubStatus
    
    
}  //- class SubmissionDAOImpl
