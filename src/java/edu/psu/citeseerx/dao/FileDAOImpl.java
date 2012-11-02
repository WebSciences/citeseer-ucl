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
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;
import edu.psu.citeseerx.domain.DocumentFileInfo;
import edu.psu.citeseerx.domain.CheckSum;

public class FileDAOImpl implements FileDAO {

    private static final String DEF_GET_FILEINFO_QUERY =
        "select crawlDate, url, repositoryID, conversionTrace " +
        "from fileInfo where id=?";

    private static final String DEF_GET_CHECKSUMS_QUERY =
        "select sha1, fileType from checksum where paperid=?";

    public DocumentFileInfo getFileInfo(String doi, Connection con)
    throws SQLException {

        DocumentFileInfo finfo = new DocumentFileInfo();
        
        PreparedStatement fstmt = con.prepareStatement(DEF_GET_FILEINFO_QUERY);
        fstmt.setString(1, doi);

        ResultSet frs = fstmt.executeQuery();
        if (frs.first()) {
            finfo.setDatum(DocumentFileInfo.CRAWL_DATE_KEY,
                    DateFormat.getDateInstance().format(
                            frs.getTimestamp("crawlDate")));
            finfo.addUrl(frs.getString("url"));
            finfo.setDatum(DocumentFileInfo.REP_ID_KEY,
                    frs.getString("repositoryID"));
            finfo.setDatum(DocumentFileInfo.CONV_TRACE_KEY,
                    frs.getString("conversionTrace"));
        }
        frs.close();
        fstmt.close();
        
        PreparedStatement cstmt = con.prepareStatement(DEF_GET_CHECKSUMS_QUERY);
        cstmt.setString(1, doi);
        
        ResultSet crs = cstmt.executeQuery();
        while(crs.next()) {
            CheckSum checksum = new CheckSum();
            checksum.setSha1(crs.getString("sha1"));
            checksum.setFileType(crs.getString("fileType"));
            finfo.addCheckSum(checksum);
        }
        crs.close();
        cstmt.close();
        
        return finfo;
        
    }  //- getFileInfo
    
    
    /* id, crawldate, url, parentUrl, sha1, repID, filePath,
     * bodyFilePath, citeFilePath, conversionTrace */
    private static final String DEF_INSERT_FILEINFO_QUERY =
        "insert into fileInfo values (?, ?, ?, ?, ?)";
        

    public void insertFileInfo(String doi, DocumentFileInfo finfo,
            Connection con) throws SQLException {
        
        PreparedStatement stmt =
            con.prepareStatement(DEF_INSERT_FILEINFO_QUERY);
        
        stmt.setString(1, doi);
        java.util.Date crawlDate = null;
        try {
            crawlDate = DateFormat.getDateInstance().parse(
                    finfo.getDatum(DocumentFileInfo.CRAWL_DATE_KEY,
                            DocumentFileInfo.UNENCODED));
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            /* that's ok - we'll use current_timestamp */
            crawlDate = new java.util.Date(System.currentTimeMillis());
        }
        stmt.setTimestamp(2, new Timestamp(crawlDate.getTime()));
        stmt.setString(3, finfo.getUrls().get(0));
        stmt.setString(4, finfo.getDatum(DocumentFileInfo.REP_ID_KEY,
                DocumentFileInfo.UNENCODED));
        stmt.setString(5, finfo.getDatum(DocumentFileInfo.CONV_TRACE_KEY,
                DocumentFileInfo.UNENCODED));
        
        stmt.executeUpdate();
        stmt.close();
        
        for (Iterator<CheckSum> it = finfo.getCheckSums().iterator();
        it.hasNext(); ) {
            CheckSum checksum = it.next();
            checksum.setDOI(doi);
            insertChecksum(checksum, con);
        }
        
    }  //- insertFileInfo
    

    private static final String DEF_UPDATE_FILEINFO_QUERY =
        "update fileInfo set crawlDate=?, url=?, repositoryID=?, " +
        "conversionTrace=? where id=?";

    
    public void updateFileInfo(String doi, DocumentFileInfo finfo,
            Connection con) throws SQLException {

        PreparedStatement stmt =
            con.prepareStatement(DEF_UPDATE_FILEINFO_QUERY);
        
        java.util.Date crawlDate = null;
        try {
            crawlDate = DateFormat.getDateInstance().parse(
                    finfo.getDatum(DocumentFileInfo.CRAWL_DATE_KEY,
                            DocumentFileInfo.UNENCODED));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        stmt.setTimestamp(1, new Timestamp(crawlDate.getTime()));
        stmt.setString(2, finfo.getUrls().get(0));
        stmt.setString(3, finfo.getDatum(DocumentFileInfo.REP_ID_KEY,
                DocumentFileInfo.UNENCODED));
        stmt.setString(4, finfo.getDatum(DocumentFileInfo.CONV_TRACE_KEY,
                DocumentFileInfo.UNENCODED));
        stmt.setString(5, doi);

        stmt.executeUpdate();
        stmt.close();
        
        deleteChecksums(doi, con);
        for (Iterator<CheckSum> it = finfo.getCheckSums().iterator();
        it.hasNext(); ) {
            CheckSum checksum = it.next();
            insertChecksum(checksum, con);
        }
        
    }  //- updateFileInfo
    
    
    private static final String DEF_GET_CHECKSUM_QUERY =
        "select paperid, fileType from checksum where sha1=?";

    
    public List<CheckSum> getChecksums(String sha1, Connection con)
    throws SQLException {
        
        PreparedStatement stmt = con.prepareStatement(DEF_GET_CHECKSUM_QUERY);
        stmt.setString(1, sha1);
        
        ArrayList<CheckSum> checksums = new ArrayList<CheckSum>();
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            CheckSum checksum = new CheckSum();
            checksum.setDOI(rs.getString("paperid"));
            checksum.setFileType(rs.getString("fileType"));
            checksum.setSha1(sha1);
            checksums.add(checksum);
        }
        rs.close();
        stmt.close();
        
        return checksums;
        
    }  //- getDOIbyChecksum
    
    
    /* sha1, paperid, fileType */
    private static final String DEF_INSERT_CHECKSUM_QUERY =
        "insert into checksum values (?, ?, ?)";


    public void insertChecksum(CheckSum checksum, Connection con)
    throws SQLException {
        
        PreparedStatement stmt =
            con.prepareStatement(DEF_INSERT_CHECKSUM_QUERY);
        stmt.setString(1, checksum.getSha1());
        stmt.setString(2, checksum.getDOI());
        stmt.setString(3, checksum.getFileType());
        
        stmt.executeUpdate();
        stmt.close();
        
    }  //- insertChecksum
    
    
    private static final String DEF_DEL_CHECKSUM_QUERY =
        "delete from checksum where paperid=?";
    
    public void deleteChecksums(String doi, Connection con)
    throws SQLException {
        
        PreparedStatement stmt = con.prepareStatement(DEF_DEL_CHECKSUM_QUERY);
        stmt.setString(1, doi);
        stmt.executeUpdate();
        stmt.close();
        
    }  //- deleteChecksums
    
    
}  //- class FileDAOImpl
