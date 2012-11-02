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

import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.DocumentFileInfo;
import edu.psu.citeseerx.utility.FileNamingUtils;

public class VersionDAOImpl implements VersionDAO {
    
    private static final String DEF_SET_VERSION_QUERY =
        "update papers set version=? where id=?";
    
    
    public void setVersion(String doi, int version, Connection con)
    throws SQLException {
        PreparedStatement stmt = con.prepareStatement(DEF_SET_VERSION_QUERY);
        stmt.setInt(1, version);
        stmt.setString(2, doi);
        stmt.executeUpdate();
        stmt.close();
        
    }  //- setVersion

    
    public boolean insertVersion(Document doc, Connection con)
    throws SQLException {
        
        String doi = doc.getDatum(Document.DOI_KEY);
        String vname = doc.getVersionName();
        int priorVersion = getVersionForName(vname, doi, con);
        
        if (priorVersion > 0) {
            DocumentFileInfo finfo = doc.getFileInfo();
            String vrepID = finfo.getDatum(DocumentFileInfo.REP_ID_KEY);
            String vpath = FileNamingUtils.buildVersionPath(doi, priorVersion); 

            doc.setVersion(priorVersion);
            doc.setVersionRepID(vrepID);
            doc.setVersionPath(vpath);

            return false;
        } else {
            createNewVersion(doc, con);
            return true;
        }
        
    }  //- insertVersion
    
    
    private static final String DEF_GET_MAX_VERSION_QUERY =
        "select max(version) from paperVersions where paperid=?";
        
    /* name, paperid, version, repositoryID, path */
    private static final String DEF_INSERT_VERSION_QUERY =
        "insert into paperVersions values (NULL, ?, ?, ?, ?, ?, 0, 0)";
        
    
    public void createNewVersion(Document doc, Connection con)
    throws SQLException {
        
        String doi = doc.getDatum(Document.DOI_KEY);
        
        PreparedStatement maxstmt = null;
        PreparedStatement insstmt = null;
        ResultSet rs = null;
        
        try {
            maxstmt = con.prepareStatement(DEF_GET_MAX_VERSION_QUERY);
            maxstmt.setString(1, doi);
            rs = maxstmt.executeQuery();
            
            int version = 1;
            if (rs.first()) {
                version = rs.getInt(1) + 1;
            }
        
            DocumentFileInfo finfo = doc.getFileInfo();
            String vrepID = finfo.getDatum(DocumentFileInfo.REP_ID_KEY);
            String vpath = FileNamingUtils.buildVersionPath(doi, version); 

            doc.setVersion(version);
            doc.setVersionRepID(vrepID);
            doc.setVersionPath(vpath);
        
            insstmt = con.prepareStatement(DEF_INSERT_VERSION_QUERY);
            insstmt.setString(1, doc.getVersionName());
            insstmt.setString(2, doi);
            insstmt.setInt(3, doc.getVersion());
            insstmt.setString(4, doc.getVersionRepID());
            insstmt.setString(5, doc.getVersionPath());
            insstmt.executeUpdate();

        } catch (SQLException e) {
            throw(e);
        } finally {
            try { rs.close(); }      catch (Exception e) { }
            try { maxstmt.close(); } catch (Exception e) { }
            try { insstmt.close(); } catch (Exception e) { }
        }

        
    }  //- createNewVersion
    
    
    private static final String DEF_SET_VNAME_QUERY =
        "update paperVersions set name=? where paperid=? and version=?";
    
    
    public void setVersionName(String doi, int version,
            String name, Connection con) throws SQLException {
        PreparedStatement stmt = con.prepareStatement(DEF_SET_VNAME_QUERY);
        stmt.setString(1, name);
        stmt.setString(2, doi);
        stmt.setInt(3, version);
        stmt.executeUpdate();
        stmt.close();
        
    }  //- setVersionName
    
    
    private static final String DEF_SET_DEPRECATED_QUERY =
        "update paperVersions set deprecated=? where paperid=? and version=?";
    
    
    public void deprecateVersion(String doi, int version, Connection con)
    throws SQLException {
        PreparedStatement stmt = con.prepareStatement(DEF_SET_DEPRECATED_QUERY);
        stmt.setBoolean(1, true);
        stmt.setString(2, doi);
        stmt.setInt(3, version);
        stmt.executeUpdate();
        stmt.close();
        
    }  //- deprecateVersion
    
    
    private static final String DEF_GET_VERSIONS_AFTER_QUERY =
        "select version from paperVersions where paperid=? and version>?";
    
    
    public void deprecateVersionsAfter(String doi, int version, Connection con)
    throws SQLException {
        PreparedStatement stmt =
            con.prepareStatement(DEF_GET_VERSIONS_AFTER_QUERY);
        stmt.setString(1, doi);
        stmt.setInt(2, version);
        ResultSet rs = stmt.executeQuery();
        
        while(rs.next()) {
            int v = rs.getInt("version");
            deprecateVersion(doi, v, con);
        }
        rs.close();
        stmt.close();
        
    }  //- deprecateVersionsAfter
    
    
    private static final String DEF_SET_SPAM_QUERY =
        "update paperVersions set spam=? where paperid=? and version=?";
    
    
    public void setSpam(String doi, int version, boolean isSpam,
            Connection con) throws SQLException {
        PreparedStatement stmt = con.prepareStatement(DEF_SET_SPAM_QUERY);
        stmt.setBoolean(1, isSpam);
        stmt.setString(2, doi);
        stmt.setInt(3, version);
        stmt.executeUpdate();
        stmt.close();
        
    }  //- setSpam
    
    
    private static final String DEF_GET_VERSION_FOR_NAME_QUERY =
        "select version from paperVersions where name=? and paperid=?";
    
    protected int getVersionForName(String name, String doi, Connection con)
    throws SQLException {
        
        if (name == null || doi == null) return -1;
        
        PreparedStatement stmt =
            con.prepareStatement(DEF_GET_VERSION_FOR_NAME_QUERY);
        ResultSet rs = null;
        try {
            stmt.setString(1, name);
            stmt.setString(2, doi);
            rs = stmt.executeQuery();
            if (rs.first()) {
                return rs.getInt("version");
            } else {
                return -1;
            }
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { rs.close(); } catch (Exception e) {}
            try { stmt.close(); } catch (Exception e) {}
        }
        
    }  //- getVersionForName

    
}  //- class VersionDAOImpl
