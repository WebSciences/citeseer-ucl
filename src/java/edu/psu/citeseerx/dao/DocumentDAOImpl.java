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
import java.util.List;
import java.util.ArrayList;
import edu.psu.citeseerx.domain.*;


public class DocumentDAOImpl implements DocumentDAO {

    private static final String DEF_GET_DOC_QUERY =
        "select id, version, cluster, title, abstract, year, venue, " +
        "venueType, pages, volume, number, publisher, pubAddress, tech, " +
        "reindex, public, ncites, versionName from papers where id=?";

    private static final String DEF_GET_DOC_SRC_QUERY =
        "select title, abstract, year, venue, venueType, pages, volume, " +
        "number, publisher, pubAddress, tech, citations from " +
        "papers_versionShadow where id=?";


    public Document getDocument(String id, boolean getSource, Connection con)
    throws SQLException {
        Document doc = new Document();

        PreparedStatement dstmt = con.prepareStatement(DEF_GET_DOC_QUERY);
        dstmt.setString(1, id);
        ResultSet drs = dstmt.executeQuery();
        if (drs.first()) {
            doc.setDatum(Document.DOI_KEY, drs.getString("id"));
            doc.setVersion(drs.getInt("version"));
            doc.setClusterID(drs.getLong("cluster"));
            doc.setDatum(Document.TITLE_KEY, drs.getString("title"));
            doc.setDatum(Document.ABSTRACT_KEY, drs.getString("abstract"));
            doc.setDatum(Document.YEAR_KEY, drs.getString("year"));
            doc.setDatum(Document.VENUE_KEY, drs.getString("venue"));
            doc.setDatum(Document.VEN_TYPE_KEY, drs.getString("venueType"));
            doc.setDatum(Document.PAGES_KEY, drs.getString("pages"));
            doc.setDatum(Document.VOL_KEY, drs.getString("volume"));
            doc.setDatum(Document.NUM_KEY, drs.getString("number"));
            doc.setDatum(Document.PUBLISHER_KEY, drs.getString("publisher"));
            doc.setDatum(Document.PUBADDR_KEY, drs.getString("pubAddress"));
            doc.setDatum(Document.TECH_KEY, drs.getString("tech"));
            doc.setNcites(drs.getInt("ncites"));
            doc.setVersionName(drs.getString("versionName"));

            doc.setIndexFlag(drs.getBoolean("reindex"));
            doc.setPublic(drs.getBoolean("public"));

        } else {
            return null;
        }
        if (getSource) {
            PreparedStatement sstmt =
                con.prepareStatement(DEF_GET_DOC_SRC_QUERY);
            sstmt.setString(1, id);
            ResultSet srs = sstmt.executeQuery();
            if (srs.first()) {
                doc.setSource(Document.TITLE_KEY, srs.getString("title"));
                doc.setSource(Document.ABSTRACT_KEY, srs.getString("abstract"));
                doc.setSource(Document.YEAR_KEY, srs.getString("year"));
                doc.setSource(Document.VENUE_KEY, srs.getString("venue"));
                doc.setSource(Document.VEN_TYPE_KEY,
                        srs.getString("venueType"));
                doc.setSource(Document.PAGES_KEY, srs.getString("pages"));
                doc.setSource(Document.VOL_KEY, srs.getString("volume"));
                doc.setSource(Document.NUM_KEY, srs.getString("number"));
                doc.setSource(Document.PUBLISHER_KEY,
                        srs.getString("publisher"));
                doc.setSource(Document.PUBADDR_KEY,
                        srs.getString("pubAddress"));
                doc.setSource(Document.TECH_KEY, srs.getString("tech"));
                doc.setSource(Document.CITES_KEY, srs.getString("citations"));
            }
            sstmt.close();
        }
        dstmt.close();

        return doc;

    }  //- getDocument


    /* id, cluster, title, abstract, year, venue, venueType, pages,
     * volume, number, publisher, pubAddress, tech, reindex, public */
    private static final String DEF_INSERT_DOC_QUERY =
        "insert into papers values (?, ?, ?, ?, ?, ?, ?, ?, ?," +
        " ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /* id, cluster, title, abstract, year, venue, venueType, pages,
     * volume, number, publisher, pubAddress, tech */
    private static final String DEF_INSERT_DOC_SRC_QUERY =
        "insert into papers_versionShadow values (?, ?, ?, ?, ?, ?, ?, ?, " +
        "?, ?, ?, ?, ?)";


    public void insertDocument(Document doc, Connection con)
    throws SQLException {

        PreparedStatement stmt = con.prepareStatement(DEF_INSERT_DOC_QUERY);
        stmt.setString(1, doc.getDatum(Document.DOI_KEY));
        stmt.setInt(2, doc.getVersion());
        stmt.setLong(3, doc.getClusterID());
        stmt.setString(4, doc.getDatum(Document.TITLE_KEY));
        stmt.setString(5, doc.getDatum(Document.ABSTRACT_KEY));

        String year = doc.getDatum(Document.YEAR_KEY);
        if (year != null) {
            stmt.setInt(6, Integer.parseInt(year));
        } else {
            stmt.setNull(6, Types.INTEGER);
        }

        stmt.setString(7, doc.getDatum(Document.VENUE_KEY));
        stmt.setString(8, doc.getDatum(Document.VEN_TYPE_KEY));
        stmt.setString(9, doc.getDatum(Document.PAGES_KEY));

        String vol = doc.getDatum(Document.VOL_KEY);
        if (vol != null) {
            stmt.setInt(10, Integer.parseInt(vol));
        } else {
            stmt.setNull(10, Types.INTEGER);
        }

        String num = doc.getDatum(Document.NUM_KEY);
        if (num != null) {
            stmt.setInt(11, Integer.parseInt(num));
        } else {
            stmt.setNull(11, Types.INTEGER);
        }

        stmt.setString(12, doc.getDatum(Document.PUBLISHER_KEY));
        stmt.setString(13, doc.getDatum(Document.PUBADDR_KEY));
        stmt.setString(14, doc.getDatum(Document.TECH_KEY));
        stmt.setBoolean(15, doc.flaggedForIndexing());
        stmt.setBoolean(16, doc.isPublic());
        stmt.setInt(17, doc.getNcites());
        stmt.setString(18, doc.getVersionName());

        stmt.executeUpdate();
        con.commit();

        if (doc.hasSourceData()) {
            PreparedStatement sstmt =
                con.prepareStatement(DEF_INSERT_DOC_SRC_QUERY);
            sstmt.setString(1, doc.getDatum(Document.DOI_KEY));
            sstmt.setString(2, doc.getSource(Document.TITLE_KEY));
            sstmt.setString(3, doc.getSource(Document.ABSTRACT_KEY));
            sstmt.setString(4, doc.getSource(Document.YEAR_KEY));
            sstmt.setString(5, doc.getSource(Document.VENUE_KEY));
            sstmt.setString(6, doc.getSource(Document.VEN_TYPE_KEY));
            sstmt.setString(7, doc.getSource(Document.PAGES_KEY));
            sstmt.setString(8, doc.getSource(Document.VOL_KEY));
            sstmt.setString(9, doc.getSource(Document.NUM_KEY));
            sstmt.setString(10, doc.getSource(Document.PUBLISHER_KEY));
            sstmt.setString(11, doc.getSource(Document.PUBADDR_KEY));
            sstmt.setString(12, doc.getSource(Document.TECH_KEY));
            sstmt.setString(13, doc.getSource(Document.CITES_KEY));

            sstmt.executeUpdate();
        }

    }  //- insertDocument


    private static final String DEF_UPDATE_DOC_QUERY =
        "update papers set version=?, cluster=?, title=?, abstract=?, " +
        "year=?, venue=?, venueType=?, pages=?, volume=?, number=?, " +
        "publisher=?, pubAddress=?, tech=?, reindex=?, public=?, " +
        "ncites=?, versionName=? where id=?";

    private static final String DEF_UPDATE_DOC_SRC_QUERY =
        "update papers_versionShadow set title=?, abstract=?, " +
        "year=?, venue=?, venueType=?, pages=?, volume=?, number=?, " +
        "publisher=?, pubAddress=?, tech=?, citations=? where id=?";


    public void updateDocument(Document doc, Connection con)
    throws SQLException {

        PreparedStatement stmt = con.prepareStatement(DEF_UPDATE_DOC_QUERY);
        stmt.setInt(1, doc.getVersion());
        stmt.setLong(2, doc.getClusterID());
        stmt.setString(3, doc.getDatum(Document.TITLE_KEY));
        stmt.setString(4, doc.getDatum(Document.ABSTRACT_KEY));

        String year = doc.getDatum(Document.YEAR_KEY);
        if (year != null) {
            stmt.setInt(5, Integer.parseInt(year));
        } else {
            stmt.setNull(5, Types.INTEGER);
        }

        stmt.setString(6, doc.getDatum(Document.VENUE_KEY));
        stmt.setString(7, doc.getDatum(Document.VEN_TYPE_KEY));
        stmt.setString(8, doc.getDatum(Document.PAGES_KEY));

        String vol = doc.getDatum(Document.VOL_KEY);
        if (vol != null) {
            stmt.setInt(9, Integer.parseInt(vol));
        } else {
            stmt.setNull(9, Types.INTEGER);
        }

        String num = doc.getDatum(Document.NUM_KEY);
        if (num != null) {
            stmt.setInt(10, Integer.parseInt(num));
        } else {
            stmt.setNull(10, Types.INTEGER);
        }

        stmt.setString(11, doc.getDatum(Document.PUBLISHER_KEY));
        stmt.setString(12, doc.getDatum(Document.PUBADDR_KEY));
        stmt.setString(13, doc.getDatum(Document.TECH_KEY));
        stmt.setBoolean(14, doc.flaggedForIndexing());
        stmt.setBoolean(15, doc.isPublic());
        stmt.setInt(16, doc.getNcites());
        stmt.setString(17, doc.getVersionName());
        stmt.setString(18, doc.getDatum(Document.DOI_KEY));

        stmt.executeUpdate();

        if (doc.hasSourceData()) {
            PreparedStatement sstmt =
                con.prepareStatement(DEF_UPDATE_DOC_SRC_QUERY);
            sstmt.setString(1, doc.getSource(Document.TITLE_KEY));
            sstmt.setString(2, doc.getSource(Document.ABSTRACT_KEY));
            sstmt.setString(3, doc.getSource(Document.YEAR_KEY));
            sstmt.setString(4, doc.getSource(Document.VENUE_KEY));
            sstmt.setString(5, doc.getSource(Document.VEN_TYPE_KEY));
            sstmt.setString(6, doc.getSource(Document.PAGES_KEY));
            sstmt.setString(7, doc.getSource(Document.VOL_KEY));
            sstmt.setString(8, doc.getSource(Document.NUM_KEY));
            sstmt.setString(9, doc.getSource(Document.PUBLISHER_KEY));
            sstmt.setString(10, doc.getSource(Document.PUBADDR_KEY));
            sstmt.setString(11, doc.getSource(Document.TECH_KEY));
            sstmt.setString(12, doc.getSource(Document.CITES_KEY));
            sstmt.setString(13, doc.getDatum(Document.DOI_KEY));

            sstmt.executeUpdate();
        }

    }  //- updateDocument


    private static final String DEF_SET_PUBLIC_QUERY =
        "update papers set public=? where id=?";


    public void setPublic(Document doc, boolean isPublic, Connection con)
    throws SQLException {
        PreparedStatement stmt = con.prepareStatement(DEF_SET_PUBLIC_QUERY);
        stmt.setBoolean(1, isPublic);
        stmt.setString(2, doc.getDatum(Document.DOI_KEY));
        stmt.executeUpdate();
        stmt.close();

    }  //- setPublic


    private static final String DEF_SET_REINDEX_QUERY =
        "update papers set reindex=? where id=?";


    public void setIndexFlag(Document doc, boolean reindex, Connection con)
    throws SQLException {
        PreparedStatement stmt = con.prepareStatement(DEF_SET_REINDEX_QUERY);
        stmt.setBoolean(1, reindex);
        stmt.setString(2, doc.getDatum(Document.DOI_KEY));
        stmt.executeUpdate();
        stmt.close();

    }  //- setIndexFlag


    private static final String DEF_SET_CLUSTER_QUERY =
        "update papers set cluster=? where id=?";


    public void setCluster(Document doc, Long clusterID, Connection con)
    throws SQLException {
        PreparedStatement stmt = con.prepareStatement(DEF_SET_CLUSTER_QUERY);
        stmt.setLong(1, clusterID);
        stmt.setString(2, doc.getDatum(Document.DOI_KEY));
        stmt.executeUpdate();
        stmt.close();

    }  //- setCluster
    
    
    private static final String DEF_SET_CITES_STMT =
        "update papers set ncites=? where id=?";
    
    public void setNcites(Document doc, int ncites, Connection con)
    throws SQLException {
        PreparedStatement stmt = con.prepareStatement(DEF_SET_CITES_STMT);
        try {
            stmt.setInt(1, ncites);
            stmt.setString(2, doc.getDatum(Document.DOI_KEY));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { stmt.close(); } catch (Exception e) {}
        }
        
    }  //- setNcites


    private static final String DEF_GET_DOCS_TO_INDEX_QUERY =
        "select id from papers where reindex=1 limit ?";

    public List<String> getDocsToBeIndexed(int num, Connection con)
    throws SQLException {

        ArrayList<String> ids = new ArrayList<String>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = con.prepareStatement(DEF_GET_DOCS_TO_INDEX_QUERY);
            stmt.setInt(1, num);
            rs = stmt.executeQuery();
            while(rs.next()) {
                ids.add(rs.getString("id"));
            }
            return ids;
            
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { rs.close(); } catch (Exception e) { }
            try { stmt.close(); } catch (Exception e) { }
        }

    }  //- getDocsToBeIndexed


    private static final String DEF_COUNT_DOCUMENTS_QUERY =
        "select count(id) from papers where reindex=0";

    public int getNumberOfDocumentRecords(Connection con) throws SQLException {
        PreparedStatement stmt =
            con.prepareStatement(DEF_COUNT_DOCUMENTS_QUERY);
        ResultSet rs = stmt.executeQuery();
        int n;
        if (rs.first()) {
            n = rs.getInt(1);
        } else {
            throw new SQLException("no document count could be obtained");
        }
        rs.close();
        stmt.close();
        
        return n;

    }  //- getNumberOfDocumentRecords
    
    
    private static final String DEF_GET_DOIS_QUERY =
        "select id from papers where id>? order by id asc limit ?";
    
    public List<String> getDOIs(String start, int amount, Connection con)
    throws SQLException {
        
        List<String> dois = new ArrayList<String>();
        PreparedStatement stmt = con.prepareStatement(DEF_GET_DOIS_QUERY);
        stmt.setString(1, start);
        stmt.setInt(2, amount);
        ResultSet rs = stmt.executeQuery();
        while(rs.next()) {
            dois.add(rs.getString("id"));
        }
        rs.close();
        stmt.close();
        
        return dois;
        
    }  //- getDOIs


}  //- class DocumentDAOImpl
