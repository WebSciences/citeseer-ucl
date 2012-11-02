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
package edu.psu.citeseerx.dao.admin;

import java.sql.*;

public class AdminDAOImpl implements AdminDAO {

    private static final String bannerName = "BANNER";
    
    private static final String DEF_INS_BANNER_STMT =
        "insert into textSources values (?, ?)";
    
    private static final String DEF_SET_BANNER_STMT =
        "update textSources set content=? where name=?";
    
    public void setBanner(String banner, Connection con) throws SQLException {
        
        String priorBanner = getBanner(con);
        PreparedStatement stmt = null;
        
        if (priorBanner != null) {
            stmt = con.prepareStatement(DEF_SET_BANNER_STMT);
            stmt.setString(1, banner);
            stmt.setString(2, bannerName);
        } else {
            stmt = con.prepareStatement(DEF_INS_BANNER_STMT);
            stmt.setString(1, bannerName);
            stmt.setString(2, banner);
        }
        try {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { stmt.close(); } catch (Exception e) { }
        }

    }  //- setBanner
    
    
    private static final String DEF_GET_BANNER_QUERY =
        "select content from textSources where name=?";
    
    public String getBanner(Connection con) throws SQLException {

        PreparedStatement stmt = con.prepareStatement(DEF_GET_BANNER_QUERY);
        ResultSet rs = null;
        try {
            stmt.setString(1, bannerName);
            rs = stmt.executeQuery();
            String banner = "";
            if (rs.first()) {
                String bannerStr = rs.getString("content");
                if (bannerStr != null) {
                    banner = bannerStr;
                }
            } else {
                return null;
            }
            return banner;
            
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { rs.close(); } catch (Exception e) { }
            try { stmt.close(); } catch (Exception e) { }
        }

    }  //- getBanner
    
}  //- class AdminDAO
