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

public class AdminDAOImpl extends JdbcDaoSupport implements AdminDAO {

    private static final String bannerName = "BANNER";

    private InsertBanner insertBanner;
    private UpdateBanner updateBanner;
    private GetBanner getBanner;
    
    protected void initDao() throws ApplicationContextException {
        initMappingSqlQueries();
    } //- initDao
    
    
    protected void initMappingSqlQueries() throws ApplicationContextException {
        insertBanner = new InsertBanner(getDataSource());
        updateBanner = new UpdateBanner(getDataSource());
        getBanner = new GetBanner(getDataSource());
    }

    
    public void setBanner(String banner) throws DataAccessException {
        String priorBanner = getBanner();
        if (priorBanner == null) {
            System.out.println("banner1");
            insertBanner.run(banner);
        } else {
            System.out.println("banner2");
            updateBanner.run(banner);
        }
    }
    
    public String getBanner() throws DataAccessException {
        return getBanner.run();
    }
    
    
    private static final String DEF_INS_BANNER_STMT =
        "insert into textSources values (?, ?)";
    
    private class InsertBanner extends SqlUpdate {

        public InsertBanner(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INS_BANNER_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(String banner) {
            Object[] params = new Object[] { bannerName, banner };
            return update(params);
        }
    }
    
    
    private static final String DEF_SET_BANNER_STMT =
        "update textSources set content=? where name=?";
    
    private class UpdateBanner extends SqlUpdate {
        
        public UpdateBanner(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_SET_BANNER_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(String banner) {
            Object[] params = new Object[] { banner, bannerName };
            return update(params);
        }
    }
    
    
    private static final String DEF_GET_BANNER_QUERY =
        "select content from textSources where name=?";
    
    private class GetBanner extends MappingSqlQuery {
        
        public GetBanner(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_BANNER_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            String str = rs.getString(1);
            if (str == null) {
                str = "";
            }
            return str;
        }
        
        public String run() {
            List list = execute(bannerName);
            if (list.isEmpty()) {
                return null;
            } else {
                return (String)list.get(0);
            }
        }
    }
        
}  //- class AdminDAO
