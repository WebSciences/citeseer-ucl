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

import edu.psu.citeseerx.domain.Keyword;

import java.util.List;
import java.sql.*;
import javax.sql.DataSource;

import org.springframework.context.ApplicationContextException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.GeneratedKeyHolder;

public class KeywordDAOImpl extends JdbcDaoSupport implements KeywordDAO {

    private GetKeywords getKeywords;
    private GetKeySrc getKeySrc;
    private InsertKeyword insertKeyword;
    private InsertKeySrc insertKeySrc;
    private UpdateKeyword updateKeyword;
    private UpdateKeySrc updateKeySrc;
    private DeleteKeyword deleteKeyword;
    private DeleteKeywords deleteKeywords;
    
    protected void initDao() throws ApplicationContextException {
        initMappingSqlQueries();
    } //- initDao
    
    
    protected void initMappingSqlQueries() throws ApplicationContextException {
        getKeywords = new GetKeywords(getDataSource());
        getKeySrc = new GetKeySrc(getDataSource());
        insertKeyword = new InsertKeyword(getDataSource());
        insertKeySrc = new InsertKeySrc(getDataSource());
        updateKeyword = new UpdateKeyword(getDataSource());
        updateKeySrc = new UpdateKeySrc(getDataSource());
        deleteKeyword = new DeleteKeyword(getDataSource());
        deleteKeywords = new DeleteKeywords(getDataSource());        
    }
    
    
    public List getKeywords(String doi, boolean getSource)
    throws DataAccessException {
        List keywords = getKeywords.run(doi);
        if (getSource) {
            for (Object o : keywords) {
                Keyword keyword = (Keyword)o;
                keyword.setSource(Keyword.KEYWORD_KEY, getKeySrc.run(keyword));
            }
        }
        return keywords;
    }

    
    public void insertKeyword(String doi, Keyword keyword)
    throws DataAccessException {
        insertKeyword.run(doi, keyword);
        if (keyword.hasSourceData()) {
            insertKeySrc.run(keyword);
        }
    }  //- insertKeyword

    
    public void updateKeyword(String doi, Keyword keyword)
    throws DataAccessException {
        updateKeyword.run(doi, keyword);
        if (keyword.hasSourceData()) {
            updateKeySrc.run(keyword);
        }
    }  //- updateKeyword

    
    public void deleteKeyword(String doi, Keyword keyword)
    throws DataAccessException {
        deleteKeyword.run(doi, keyword);
    }  //- deleteKeyword

    
    public void deleteKeywords(String doi) throws DataAccessException {
        deleteKeywords.run(doi);
    }  //- deleteKeywords
    
    
    private static final String DEF_GET_KEYWORD_QUERY =
        "select id, keyword from keywords where paperid=?";

    private class GetKeywords extends MappingSqlQuery {
        
        public GetKeywords(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_KEYWORD_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public Keyword mapRow(ResultSet rs, int rowNum) throws SQLException {
            Keyword keyword = new Keyword();
            keyword.setDatum(Keyword.DOI_KEY, rs.getString(1));
            keyword.setDatum(Keyword.KEYWORD_KEY, rs.getString(2));
            return keyword;
        }
        
        public List run(String doi) {
            return execute(doi);
        }
    }
    
    
    private static final String DEF_GET_KEYWORD_SRC_QUERY =
        "select keyword from keywords_versionShadow where id=?";

    private class GetKeySrc extends MappingSqlQuery {
        
        public GetKeySrc(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_KEYWORD_SRC_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString(1);
        }
        
        public String run(Keyword keyword) {
            List list =
                execute(Long.parseLong(keyword.getDatum(Keyword.DOI_KEY)));
            if (list.isEmpty()) {
                return null;
            } else {
                return (String)list.get(0);
            }
        }
    }
        
    
    /* id, keyword, paperid */
    private static final String DEF_INSERT_KEYWORD_QUERY =
        "insert into keywords values (NULL, ?, ?)";
    
    private class InsertKeyword extends SqlUpdate {
        
        public InsertKeyword(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_KEYWORD_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            setReturnGeneratedKeys(true);
            compile();
        }
        
        public int run(String doi, Keyword keyword) {
            Object[] params = new Object[] {
                keyword.getDatum(Keyword.KEYWORD_KEY), doi
            };
            KeyHolder holder = new GeneratedKeyHolder();
            int n = update(params, holder);
            keyword.setDatum(Keyword.DOI_KEY,
                    Long.toString(holder.getKey().longValue()));
            return n;
        }
    }
    
    /* id, keyword */
    private static final String DEF_INSERT_KEYWORD_SRC_QUERY =
        "insert into keywords_versionShadow values (?, ?)";

    private class InsertKeySrc extends SqlUpdate {
        
        public InsertKeySrc(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_KEYWORD_SRC_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(Keyword keyword) {
            Object[] params = new Object[] {
                    Long.parseLong(keyword.getDatum(Keyword.DOI_KEY)),
                    keyword.getSource(Keyword.KEYWORD_KEY)
            };
            return update(params);
        }
    }
        
    
    private static final String DEF_UPDATE_KEYWORD_QUERY =
        "update keywords set keyword=?, paperid=? where id=?";
    
    private class UpdateKeyword extends SqlUpdate {
        
        public UpdateKeyword(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_UPDATE_KEYWORD_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
        public int run(String doi, Keyword keyword) {
            Object[] params = new Object[] {
                keyword.getDatum(Keyword.KEYWORD_KEY), doi,
                keyword.getDatum(Keyword.DOI_KEY)
            };
            return update(params);
        }
    }
    
    
    private static final String DEF_UPDATE_KEYWORD_SRC_QUERY =
        "update keywords_versionShadow set keyword=? where id=?";

    private class UpdateKeySrc extends SqlUpdate {
        
        public UpdateKeySrc(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_KEYWORD_SRC_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
        public int run(Keyword keyword) {
            Object[] params = new Object[] {
                    keyword.getSource(Keyword.KEYWORD_KEY),
                    Long.parseLong(keyword.getDatum(Keyword.DOI_KEY))
            };
            return update(params);
        }
    }
        
    
    private static final String DEF_DELETE_KEYWORD_QUERY =
        "delete from keywords where keyword=? and paperid=?";
    
    private class DeleteKeyword extends SqlUpdate {
        
        public DeleteKeyword(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_DELETE_KEYWORD_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(String doi, Keyword keyword) {
            Object[] params = new Object[] {
                keyword.getDatum(Keyword.KEYWORD_KEY), doi
            };
            return update(params);
        }
    }
        
    
    private static final String DEF_DELETE_KEYWORDS_QUERY =
        "delete from keywords where paperid=?";
    
    private class DeleteKeywords extends SqlUpdate {
        
        public DeleteKeywords(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_DELETE_KEYWORDS_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(String doi) {
            return update(doi);
        }
    }
    
}  //- class KeywordDAOImpl
