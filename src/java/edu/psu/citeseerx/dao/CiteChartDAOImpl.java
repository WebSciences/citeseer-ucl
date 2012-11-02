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

public class CiteChartDAOImpl implements CiteChartDAO {

    
    private static final String DEF_GET_NCITES_QUERY =
        "select ncites from papers where id=?";
    
    private static final String DEF_GET_UPDATE_QUERY =
        "select lastNcites from citecharts where id=?";
    
    public boolean checkChartUpdateRequired(String doi, Connection con)
    throws SQLException {
        
        PreparedStatement getUpdateStmt =
            con.prepareStatement(DEF_GET_UPDATE_QUERY);
        getUpdateStmt.setString(1, doi);
        ResultSet rs1 = getUpdateStmt.executeQuery();
        int lastNcites = 0;
        if (rs1.first()) {
            lastNcites = rs1.getInt("lastNcites");
        } else {
            rs1.close();
            getUpdateStmt.close();
            return true;
        }
        rs1.close();
        getUpdateStmt.close();
        
        PreparedStatement getNcitesStmt =
            con.prepareStatement(DEF_GET_NCITES_QUERY);
        getNcitesStmt.setString(1, doi);
        int ncites = 0;
        ResultSet rs2 = getNcitesStmt.executeQuery();
        if (rs2.first()) {
            ncites = rs2.getInt("ncites");
        }
        rs2.close();
        getNcitesStmt.close();
        if (ncites != lastNcites) {
            return true;
        } else {
            return false;
        }
        
    }  //- checkChartUpdateRequired
    
    
    private static final String DEF_INS_UPDATE_STMT =
        "insert into citecharts values (?, ?)";
    
    private static final String DEF_UPDATE_UPDATE_STMT =
        "update citecharts set lastNcites=? where id=?";
    
    public void insertChartUpdate(String doi, int lastNcites, Connection con)
    throws SQLException {
        
        PreparedStatement getUpdateStmt =
            con.prepareStatement(DEF_GET_UPDATE_QUERY);
        getUpdateStmt.setString(1, doi);
        ResultSet rs = getUpdateStmt.executeQuery();
        boolean isNew = true;
        if (rs.first()) {
            isNew = false;
        }
        rs.close();
        getUpdateStmt.close();
        
        PreparedStatement stmt = null;
        if (isNew) {
            stmt = con.prepareStatement(DEF_INS_UPDATE_STMT);
            stmt.setString(1, doi);
            stmt.setInt(2, lastNcites);
        } else {
            stmt = con.prepareStatement(DEF_UPDATE_UPDATE_STMT);
            stmt.setInt(1, lastNcites);
            stmt.setString(2, doi);
        }
        stmt.executeUpdate();
        stmt.close();
        
    }  //- insertChartUpdate
    
}  //- class CharUpdateDAOImpl
