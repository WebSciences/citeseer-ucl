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
package edu.psu.citeseerx.dao;

import java.sql.*;

public class LegacyIDDAOImpl implements LegacyIDDAO {
    
    private static final String DEF_GET_NEWID_QUERY =
        "select paperid from legacyIDMap where legacyid=?";
    
    public String getNewID(int legacyID, Connection con) throws SQLException {
        
        PreparedStatement stmt = con.prepareStatement(DEF_GET_NEWID_QUERY);
        stmt.setInt(1, legacyID);
        ResultSet rs = stmt.executeQuery();
        
        String newID = null;
        if (rs.first()) {
            newID = rs.getString("paperid");
        }
        rs.close();
        stmt.close();
        
        return newID;
        
    }  //- getNewID
    
    
    private static final String DEF_INSERT_MAPPING_STMT =
        "insert into legacyIDMap values (NULL, ?, ?)";
    
    public void insertMapping(String csxID, int legacyID, Connection con)
    throws SQLException {
        
        PreparedStatement stmt = con.prepareStatement(DEF_INSERT_MAPPING_STMT);
        stmt.setString(1, csxID);
        stmt.setInt(2, legacyID);
        stmt.executeUpdate();
        stmt.close();
        
    }  //- insertMapping

}  //- class LegacyIDDAOImpl
