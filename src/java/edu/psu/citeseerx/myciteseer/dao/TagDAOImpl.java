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

public class TagDAOImpl extends JdbcDaoSupport implements TagDAO {

    private InsertTag insertTag;
    private DeleteTag deleteTag;
    private GetTagMapping getTagMapping;
    private GetTagsMapping getTagsMapping;
    private GetDoisMapping getDoisMapping;
    
    protected void initDao() throws ApplicationContextException {
        initMappingSqlQueries();
    }
    
    protected void initMappingSqlQueries() {
        insertTag = new InsertTag(this.getDataSource());
        deleteTag = new DeleteTag(this.getDataSource());
        getTagMapping = new GetTagMapping(this.getDataSource());
        getTagsMapping = new GetTagsMapping(this.getDataSource());
        getDoisMapping = new GetDoisMapping(this.getDataSource());
    }
    
    
    public void addTag(Account account, String doi, String tag)
    throws DataAccessException {
        List ids = getTagMapping.run(account, doi, tag);
        if (ids.isEmpty()) {
            insertTag.run(account, doi, tag);
        }
    }
    
    public void deleteTag(Account account, String doi, String tag)
    throws DataAccessException {
        deleteTag.run(account, doi, tag);
    }
    
    public List getTags(Account account) throws DataAccessException {
        return getTagsMapping.run(account);
    }
    
    public List getDoisForTag(Account account, String tag)
    throws DataAccessException {
        return getDoisMapping.run(account, tag);
    }
    
    
    private static final String DEF_INS_TAG_STMT =
        "insert into tags values (NULL, ?, ?, ?, NULL)";
    
    protected class InsertTag extends SqlUpdate {
        public InsertTag(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_INS_TAG_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(Account account, String paperid, String tag) {
            Object[] params = new Object[] {
                    account.getUsername(),
                    paperid,
                    tag };
            return update(params);
        }
        
    }  //- class InsertTag
    
    
    private static final String DEF_GET_TAG_QUERY =
        "select id from tags where userid=? and paperid=? and tag=?";
    
    protected class GetTagMapping extends MappingSqlQuery {
        
        public GetTagMapping(DataSource ds) {
            super(ds, DEF_GET_TAG_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        protected Long mapRow(ResultSet rs, int rownum) throws SQLException {
            return rs.getLong("id");
        }

        public List run(Account account, String paperid, String tag) {
            Object[] params = new Object[] {
                    account.getUsername(),
                    paperid,
                    tag  };
            return execute(params);
        }
        
    }  //- class GetTagMapping
    
    
    private static final String DEF_DEL_TAG_STMT =
        "delete from tags where userid=? and paperid=? and tag=?";
    
    protected class DeleteTag extends SqlUpdate {
        public DeleteTag(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_DEL_TAG_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(Account account, String paperid, String tag) {
            Object[] params = new Object[] {
                    account.getUsername(),
                    paperid,
                    tag };
            return update(params);
        }
        
    }  //- class DeleteTag
    
    
    private static final String DEF_GET_TAGS_QUERY =
        "select tag from tags where userid=?";
    
    protected class GetTagsMapping extends MappingSqlQuery {
        
        public GetTagsMapping(DataSource ds) {
            super(ds, DEF_GET_TAGS_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        protected String mapRow(ResultSet rs, int rownum) throws SQLException {
            return rs.getString("tag");
        }

        public List run(Account account) {
            return execute(account.getUsername());
        }
        
    }  //- class GetTagsMapping


    private static final String DEF_GET_DOIS_QUERY =
        "select paperid from tags where userid=? and tag=?";
    
    protected class GetDoisMapping extends MappingSqlQuery {
        
        public GetDoisMapping(DataSource ds) {
            super(ds, DEF_GET_DOIS_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        protected String mapRow(ResultSet rs, int rownum) throws SQLException {
            return rs.getString("paperid");
        }

        public List run(Account account, String tag) {
            Object[] params = new Object[] { account.getUsername(), tag };
            return execute(params);
        }
        
    }  //- class GetDoisMapping
    
    
}  //- class JdbcDaoSupport
