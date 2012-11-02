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

public class LegacyIDDAOImpl extends JdbcDaoSupport implements LegacyIDDAO {
    
    private GetNew getNew;
    private InsertMapping insertMapping;
    
    protected void initDao() throws ApplicationContextException {
        initMappingSqlQueries();
    } //- initDao
    
    
    protected void initMappingSqlQueries() throws ApplicationContextException {
        getNew = new GetNew(getDataSource());
        insertMapping = new InsertMapping(getDataSource());
    }
    
    public String getNewID(int legacyID) throws DataAccessException {
        return getNew.run(legacyID);
    }  //- getNewID

    public void insertLegacyIDMapping(String csxID, int legacyID)
    throws DataAccessException {
        insertMapping.run(csxID, legacyID);
    }  //- insertMapping

    
    private static final String DEF_GET_NEWID_QUERY =
        "select paperid from legacyIDMap where legacyid=?";
    
    private class GetNew extends MappingSqlQuery {
        
        public GetNew(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_NEWID_QUERY);
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        }
        
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString(1);
        }
        
        public String run(int legacyID) {
            List list = execute(legacyID);
            if (list.isEmpty()) {
                return null;
            } else {
                return (String)list.get(0);
            }
        }
    }
        
    
    private static final String DEF_INSERT_MAPPING_STMT =
        "insert into legacyIDMap values (NULL, ?, ?)";
    
    private class InsertMapping extends SqlUpdate {
        
        public InsertMapping(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_MAPPING_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        }
        
        public int run(String csxID, int legacyID) {
            Object[] params = new Object[] { csxID, new Integer(legacyID) };
            return update(params);
        }
    }
    
}  //- class LegacyIDDAOImpl
