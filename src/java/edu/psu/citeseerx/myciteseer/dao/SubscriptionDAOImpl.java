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

import edu.psu.citeseerx.myciteseer.domain.Account;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.context.ApplicationContextException;
import org.springframework.dao.*;

import java.sql.*;

import javax.sql.*;

import java.util.List;

public class SubscriptionDAOImpl extends JdbcDaoSupport
implements SubscriptionDAO {

    private InsertMonitor insertMonitor;
    private DeleteMonitor deleteMonitor;
    private GetMonitorMapping getMonitor;
    private GetMonitorsMapping getMonitors;
    private GetUsersMonitoring getUsersMonitoring;
    
    protected void initDao() throws ApplicationContextException {
        initMappingSqlQueries();
    }
    
    protected void initMappingSqlQueries() {
        insertMonitor = new InsertMonitor(this.getDataSource());
        deleteMonitor = new DeleteMonitor(this.getDataSource());
        getMonitor = new GetMonitorMapping(this.getDataSource());
        getMonitors = new GetMonitorsMapping(this.getDataSource());
        getUsersMonitoring = new GetUsersMonitoring(this.getDataSource());
    }
    
    
    public void addMonitor(Account account, String paperid)
    throws DataAccessException {
        List ids = getMonitor.run(account, paperid);
        if (ids.isEmpty()) {
            insertMonitor.run(account, paperid);
        }
    }
    
    
    public void deleteMonitor(Account account, String paperid)
    throws DataAccessException {
        deleteMonitor.run(account, paperid);
    }
    
    
    public List getMonitors(Account account)
    throws DataAccessException {
        return getMonitors.run(account);
    }
    
    
    public List getUsersMonitoring(String paperid) {
        return getUsersMonitoring.run(paperid);
    }
    
    private static final String DEF_INS_MONITOR_STMT =
        "insert into monitors values (NULL, ?, ?)";
    
    protected class InsertMonitor extends SqlUpdate {
        public InsertMonitor(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_INS_MONITOR_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(Account account, String paperid) {
            Object[] params = new Object[] {
                    account.getUsername(),
                    paperid };
            return update(params);
        }
        
    }  //- class InsertMonitor
    
    
    private static final String DEF_GET_MONITOR_QUERY =
        "select id from monitors where userid=? and paperid=?";
    
    protected class GetMonitorMapping extends MappingSqlQuery {
        
        public GetMonitorMapping(DataSource ds) {
            super(ds, DEF_GET_MONITOR_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        protected Long mapRow(ResultSet rs, int rownum) throws SQLException {
            return rs.getLong("id");
        }

        public List run(Account account, String paperid) {
            Object[] params = new Object[] { account.getUsername(), paperid };
            return execute(params);
        }
        
    }  //- class GetMonitorMapping

    
    private static final String DEF_DEL_MONITOR_STMT =
        "delete from monitors where userid=? and paperid=?";
    
    protected class DeleteMonitor extends SqlUpdate {
        public DeleteMonitor(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_DEL_MONITOR_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(Account account, String paperid) {
            Object[] params = new Object[] {
                    account.getUsername(),
                    paperid };
            return update(params);
        }
        
    }  //- class DeleteMonitor
    
    
    private static final String DEF_GET_MONITORS_QUERY =
        "select paperid from monitors where userid=? order by paperid";
    
    protected class GetMonitorsMapping extends MappingSqlQuery {
        
        public GetMonitorsMapping(DataSource ds) {
            super(ds, DEF_GET_MONITORS_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        protected String mapRow(ResultSet rs, int rownum) throws SQLException {
            return rs.getString("paperid");
        }

        public List run(Account account) {
            return execute(account.getUsername());
        }        

    }  //- class GetMonitorsMapping
    
    
    private static final String DEF_GET_USERS_MONITORING_QUERY =
        "select userid from monitors where paperid=?";
    
    protected class GetUsersMonitoring extends MappingSqlQuery {
        
        public GetUsersMonitoring(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_GET_USERS_MONITORING_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        protected String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString("userid");
        }
        
        public List run(String paperid) {
            return execute(paperid);
        }
        
    }  //- class GetUsersMonitoring

    
}  //- class SubscriptionDAOImpl
