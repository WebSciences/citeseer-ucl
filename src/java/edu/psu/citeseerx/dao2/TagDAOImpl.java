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
package edu.psu.citeseerx.dao2;

import java.util.List;

import java.sql.*;
import javax.sql.DataSource;

import org.springframework.context.ApplicationContextException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;

import edu.psu.citeseerx.domain.Tag;

public class TagDAOImpl extends JdbcDaoSupport implements TagDAO {

    private GetTags getTags;
    private TagExists tagExists;
    private InsertTag insertTag;
    private IncrementTag incrementTag;
    private DecrTag decrTag;
    private DeleteTag deleteTag;
    private TagAtZero tagAtZero;
    
    protected void initDao() throws ApplicationContextException {
        initMappingSqlQueries();
    } //- initDao
    
    
    protected void initMappingSqlQueries() throws ApplicationContextException {
        getTags = new GetTags(getDataSource());
        tagExists = new TagExists(getDataSource());
        insertTag = new InsertTag(getDataSource());
        incrementTag = new IncrementTag(getDataSource());
        decrTag = new DecrTag(getDataSource());
        deleteTag = new DeleteTag(getDataSource());
        tagAtZero = new TagAtZero(getDataSource());
    }
    

    public void addTag(String paperid, String tag) throws DataAccessException {
        if (tagExists.run(paperid, tag)) {
            incrementTag.run(paperid, tag);
        } else {
            insertTag.run(paperid, tag);
        }
    }
    
    public void deleteTag(String paperid, String tag)
    throws DataAccessException {
        decrTag.run(paperid, tag);
        if (tagAtZero.run(paperid, tag)) {
            deleteTag.run(paperid, tag);
        }
    }
    
    public List getTags(String doi) throws DataAccessException {
        return getTags.run(doi);
    }

    
    private static final String DEF_GET_TAGS_QUERY =
        "select tag, count from tags where paperid=? order by count desc";
    
    private class GetTags extends MappingSqlQuery {
        
        public GetTags(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_TAGS_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
            Tag tag = new Tag();
            tag.setTag(rs.getString(1));
            tag.setCount(rs.getInt(2));
            return tag;
        }
        
        public List run(String doi) {
            return execute(doi);
        }
    }
        
    
    private static final String DEF_GET_TAG_QUERY =
        "select count from tags where paperid=? and tag=?";
    
    private class TagExists extends MappingSqlQuery {
        
        public TagExists(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_TAG_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt(1);
        }
        
        public boolean run(String doi, String tag) {
            Object[] params = new Object[] { doi, tag };
            List list = execute(params);
            return !list.isEmpty();
        }
    }
    
    private class TagAtZero extends MappingSqlQuery {
        
        public TagAtZero(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_TAG_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt(1);
        }
        
        public boolean run(String doi, String tag) {
            Object[] params = new Object[] { doi, tag };
            List list = execute(params);
            if (list.isEmpty()) {
                return false;
            } else {
                int count = ((Integer)list.get(0)).intValue();
                return count <= 0;
            }
        }
    }
    
    
    private static final String DEF_INS_TAG_STMT =
        "insert into tags values (NULL, ?, ?, 1)";

    private class InsertTag extends SqlUpdate {
        
        public InsertTag(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INS_TAG_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(String doi, String tag) {
            Object[] params = new Object[] { doi, tag };
            return update(params);
        }
    }
    
    
    private static final String DEF_INCR_TAG_STMT =
        "update tags set count=count+1 where paperid=? and tag=?";
    
    private class IncrementTag extends SqlUpdate {
        
        public IncrementTag(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INCR_TAG_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(String doi, String tag) {
            Object[] params = new Object[] { doi, tag };
            return update(params);
        }
    }
    
    
    private static final String DEF_DECR_TAG_STMT =
        "update tags set count=count-1 where paperid=? and tag=?";
    
    private class DecrTag extends SqlUpdate {
        
        public DecrTag(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_DECR_TAG_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(String doi, String tag) {
            Object[] params = new Object[] { doi, tag };
            return update(params);
        }
    }
    
    
    private static final String DEF_DEL_TAG_STMT =
        "delete from tags where paperid=? and tag=?";
    
    private class DeleteTag extends SqlUpdate {
        
        public DeleteTag(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_DEL_TAG_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(String doi, String tag) {
            Object[] params = new Object[] { doi, tag };
            return update(params);
        }
    }    
    
}  //- class TagDAOImpl
