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

import java.io.*;
import java.sql.*;

import edu.psu.citeseerx.domain.*;
import edu.psu.citeseerx.utility.FileNamingUtils;

public class FileSysDAOImpl implements FileSysDAO {

    protected RepositoryMap repMap;
    
    public FileSysDAOImpl() {
        try {
            repMap = new RepositoryMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }  //- FileSysDAO
    
    
    public Document getDocFromXML(String repID, String relPath)
    throws IOException {

        String path = repMap.buildFilePath(repID, relPath);
        FileInputStream in = new FileInputStream(path);

        Document doc = new Document();
        doc.fromXML(in);

        in.close();
        return doc;
        
    }  //- getDocFromXML
    
    
    private static final String DEF_GET_VERSION_BY_NUM_QUERY =
        "select name, repositoryID, path, deprecated, spam from "+
        "paperVersions where paperid=? and version=?";
        
    public Document getDocVersion(String doi, int version, Connection con)
    throws SQLException, IOException {
        
        String repID, path;
        String name = null;
        boolean deprecated = false;
        boolean spam = false;
        
        if (version > 0) {
            PreparedStatement stmt =
                con.prepareStatement(DEF_GET_VERSION_BY_NUM_QUERY);
            stmt.setString(1, doi);
            stmt.setInt(2, version);

            repID = null;
            path = null;
        
            ResultSet rs = stmt.executeQuery();
            if (rs.first()) {
                name = rs.getString("name");
                repID = rs.getString("repositoryID");
                path = rs.getString("path");
                deprecated = rs.getBoolean("deprecated");
                spam = rs.getBoolean("spam");
            } else {
                return null;
            }
        } else {
            version = 0;
            repID = getRepositoryID(doi, con);
            path = FileNamingUtils.buildXMLPath(doi);
        }
        
        Document doc = getDocFromXML(repID, path);
        doc.setVersion(version);
        doc.setVersionName(name);
        doc.setVersionRepID(repID);
        doc.setVersionPath(path);
        doc.setVersionDeprecated(deprecated);
        doc.setVersionSpam(spam);
        
        return doc;
        
    }  //- getVersion


    private static final String DEF_GET_REPID_QUERY =
        "select repositoryID from fileInfo where id=?";
    
    public String getRepositoryID(String doi, Connection con)
    throws SQLException {

        PreparedStatement stmt = con.prepareStatement(DEF_GET_REPID_QUERY);
        stmt.setString(1, doi);
        ResultSet rs = stmt.executeQuery();
        String repid = null;
        if (rs.first()) {
            repid = rs.getString("repositoryID");
        }
        rs.close();
        stmt.close();
        return repid;
        
    }  //- getRepositoryID
    
        
    private static final String DEF_GET_VERSION_BY_NAME_QUERY =
        "select version, repositoryID, path, deprecated, spam from "+
        "paperVersions where paperid=? and name=?";
    
    public Document getDocVersion(String doi, String name, Connection con)
    throws SQLException, IOException {

        PreparedStatement stmt =
            con.prepareStatement(DEF_GET_VERSION_BY_NAME_QUERY);
        stmt.setString(1, doi);
        stmt.setString(2, name);

        int version = -1;
        String repID = null;
        String path = null;
        boolean deprecated = false;
        boolean spam = false;
        
        ResultSet rs = stmt.executeQuery();
        if (rs.first()) {
            version = rs.getInt("version");
            repID = rs.getString("repositoryID");
            path = rs.getString("path");
            deprecated = rs.getBoolean("deprecated");
            spam = rs.getBoolean("spam");
        } else {
            return null;
        }
        
        Document doc = getDocFromXML(repID, path);
        doc.setVersion(version);
        doc.setVersionName(name);
        doc.setVersionRepID(repID);
        doc.setVersionPath(path);
        doc.setVersionDeprecated(deprecated);
        doc.setVersionSpam(spam);
        
        return doc;
        
    }  //- getVersion
    
    
    public void writeXML(Document doc) throws IOException {

        String doi = doc.getDatum(Document.DOI_KEY, Document.UNENCODED);
        
        DocumentFileInfo finfo = doc.getFileInfo();
        String repID = finfo.getDatum(DocumentFileInfo.REP_ID_KEY,
                DocumentFileInfo.UNENCODED);
        String relPath = FileNamingUtils.buildXMLPath(doi);
        String path = repMap.buildFilePath(repID, relPath);
        
        FileOutputStream out = new FileOutputStream(path);
        doc.toXML(out, Document.INCLUDE_SYS_DATA);
        out.close();
        
    }  //- writeXML
    
    
    public void writeVersion(Document doc) throws IOException {

        String repID = doc.getVersionRepID();
        String relPath = doc.getVersionPath();
        String path = repMap.buildFilePath(repID, relPath);
        
        FileOutputStream out = new FileOutputStream(path);
        doc.toXML(out, Document.INCLUDE_SYS_DATA);
        out.close();
        
    }  //- writeVersion
    
    
    public FileInputStream getFileInputStream(String doi, String repID,
            String type) throws IOException {
        
        String dir = FileNamingUtils.getDirectoryFromDOI(doi);
        String fn = doi+"."+type;
        String relPath = dir+System.getProperty("file.separator")+fn;
        String path = repMap.buildFilePath(repID, relPath);
        
        return new FileInputStream(path);
        
    }  //- getFileInputStream
    
    
}  //- class FileSysDAOImpl
