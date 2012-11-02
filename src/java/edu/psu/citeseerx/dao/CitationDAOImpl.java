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
import java.util.*;
import edu.psu.citeseerx.domain.Citation;

public class CitationDAOImpl implements CitationDAO {

    private static final String DEF_GET_CITES_QUERY =
        "select id, cluster, authors, title, venue, venueType, year, " +
        "pages, editors, publisher, pubAddress, volume, number, tech, " +
        "raw, paperid from citations where paperid=?";


    public List<Citation> getCitations(String doi, boolean getContexts,
            Connection con) throws SQLException {

        ArrayList<Citation> citations = new ArrayList<Citation>();
        
        PreparedStatement stmt = con.prepareStatement(DEF_GET_CITES_QUERY);
        stmt.setString(1, doi);
        ResultSet crs = stmt.executeQuery();
        while (crs.next()) {
            Citation citation = mapCitation(crs);

            if (getContexts) {
                List<String> contexts =
                    getContexts(Long.parseLong(
                            citation.getDatum(Citation.DOI_KEY,
                                    Citation.UNENCODED)), con);
                for (Iterator<String> it = contexts.iterator();
                it.hasNext(); ) {
                    citation.addContext(it.next());
                }
            }
            citations.add(citation);
        }
        crs.close();
        stmt.close();
        
        return citations;
        
    }  //- getCitations
    
    
    private static final String DEF_GET_CITES_CLUST_QUERY =
        "select id, cluster, authors, title, venue, venueType, year, " +
        "pages, editors, publisher, pubAddress, volume, number, tech, " +
        "raw, paperid from citations where cluster=?";
    
    public List<Citation> getCitationsForCluster(Long clusterid, Connection con)
    throws SQLException {
        
        ArrayList<Citation> citations = new ArrayList<Citation>();
        
        PreparedStatement stmt =
            con.prepareStatement(DEF_GET_CITES_CLUST_QUERY);
        stmt.setLong(1, clusterid);
        ResultSet crs = stmt.executeQuery();
        while (crs.next()) {
            Citation citation = mapCitation(crs);
            citations.add(citation);
        }
        crs.close();
        stmt.close();
        
        return citations;
        
    }  //- getCitationsForCluster
    
    
    private static final String DEF_GET_CITE_QUERY =
        "select id, cluster, authors, title, venue, venueType, year, " +
        "pages, editors, publisher, pubAddress, volume, number, tech, " +
        "raw, paperid from citations where id=?";

    public Citation getCitation(long id, Connection con) throws SQLException {

        Citation citation = null;
        PreparedStatement stmt = con.prepareStatement(DEF_GET_CITE_QUERY);
        stmt.setLong(1, id);
        ResultSet crs = stmt.executeQuery();
        if(crs.first()) {
            citation = mapCitation(crs);
        }
        crs.close();
        stmt.close();

        return citation;
        
    }  //- getCitation
    
    
    private static final String DEF_GET_CITE_RANGE_QUERY =
        "select id, cluster, authors, title, venue, venueType, year, " +
        "pages, editors, publisher, pubAddress, volume, number, tech, " +
        "raw, paperid from citations where id>=? order by id limit ?";

    public List<Citation> getCitations(long startID, int n,
            Connection con) throws SQLException {

        ArrayList<Citation> citations = new ArrayList<Citation>();
        
        PreparedStatement stmt = con.prepareStatement(DEF_GET_CITE_RANGE_QUERY);
        stmt.setLong(1, startID);
        stmt.setInt(2, n);
        ResultSet crs = stmt.executeQuery();
        while (crs.next()) {
            Citation citation = mapCitation(crs);
            citations.add(citation);
        }
        crs.close();
        stmt.close();
        
        return citations;
        
    }  //- getCitations
    
    
    private static Citation mapCitation(ResultSet rs) throws SQLException {
        Citation citation = new Citation();
        
        citation.setDatum(Citation.DOI_KEY, rs.getString("id"));
        citation.setClusterID(rs.getLong("cluster"));
        
        String[] authors = rs.getString("authors").split(",");
        for (int i=0; i<authors.length; i++) {
            citation.addAuthorName(authors[i]);
        }
        
        citation.setDatum(Citation.TITLE_KEY, rs.getString("title"));
        citation.setDatum(Citation.VENUE_KEY, rs.getString("venue"));
        citation.setDatum(Citation.VEN_TYPE_KEY,
                rs.getString("venueType"));
        citation.setDatum(Citation.YEAR_KEY, rs.getString("year"));
        citation.setDatum(Citation.PAGES_KEY, rs.getString("pages"));
        citation.setDatum(Citation.EDITORS_KEY, rs.getString("editors"));
        citation.setDatum(Citation.PUBLISHER_KEY,
                rs.getString("publisher"));
        citation.setDatum(Citation.PUB_ADDR_KEY,
                rs.getString("pubAddress"));
        citation.setDatum(Citation.VOL_KEY, rs.getString("volume"));
        citation.setDatum(Citation.NUMBER_KEY, rs.getString("number"));
        citation.setDatum(Citation.TECH_KEY, rs.getString("tech"));
        citation.setDatum(Citation.RAW_KEY, rs.getString("raw"));
        citation.setDatum(Citation.PAPERID_KEY, rs.getString("paperid"));
        
        return citation;
        
    }  //- mapCitation
    
    
    /* cluster, authors, title, venue, venuetype, year, pages, editors, 
     * publisher, pubAddress, volume, number, tech, raw, paperid */
    private static final String DEF_INSERT_CITE_QUERY =
        "insert into citations values (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
        "?, ?, ?, ?, ?, ?)";
        

    public void insertCitation(String doi, Citation citation,
            Connection con) throws SQLException {
        
        citation.setDatum(Citation.PAPERID_KEY, doi);
        PreparedStatement stmt = con.prepareStatement(DEF_INSERT_CITE_QUERY);
        
        StringBuffer authorbuf = new StringBuffer();
        List<String> authors = citation.getAuthorNames();
        for (Iterator<String> ait = authors.iterator(); ait.hasNext(); ) {
            authorbuf.append(ait.next());
            if (ait.hasNext()) {
                authorbuf.append(",");
            }
        }

        stmt.setLong(1, citation.getClusterID());
        stmt.setString(2, authorbuf.toString());
        stmt.setString(3, citation.getDatum(Citation.TITLE_KEY,
                Citation.UNENCODED));
        stmt.setString(4, citation.getDatum(Citation.VENUE_KEY,
                Citation.UNENCODED));
        stmt.setString(5, citation.getDatum(Citation.VEN_TYPE_KEY,
                Citation.UNENCODED));

        String year = citation.getDatum(Citation.YEAR_KEY,
                Citation.UNENCODED);
        if (year != null) {
            stmt.setInt(6, Integer.parseInt(year));
        } else { 
            stmt.setNull(6, Types.INTEGER);
        }
        
        stmt.setString(7, citation.getDatum(Citation.PAGES_KEY,
                Citation.UNENCODED));
        stmt.setString(8, citation.getDatum(Citation.EDITORS_KEY,
                Citation.UNENCODED));
        stmt.setString(9, citation.getDatum(Citation.PUBLISHER_KEY,
                Citation.UNENCODED));
        stmt.setString(10, citation.getDatum(Citation.PUB_ADDR_KEY,
                Citation.UNENCODED));

        String vol = citation.getDatum(Citation.VOL_KEY, Citation.UNENCODED);
        if (vol != null) {
            stmt.setInt(11, Integer.parseInt(vol));
        } else {
            stmt.setNull(11, Types.INTEGER);
        }
        
        String num = citation.getDatum(Citation.NUMBER_KEY, Citation.UNENCODED);
        if (num != null) {
            stmt.setInt(12, Integer.parseInt(num));
        } else {
            stmt.setNull(12, Types.INTEGER);
        }

        stmt.setString(13, citation.getDatum(Citation.TECH_KEY,
                Citation.UNENCODED));
        stmt.setString(14, citation.getDatum(Citation.RAW_KEY,
                Citation.UNENCODED));
        stmt.setString(15, doi);

        stmt.executeUpdate();
        
        ResultSet ids = stmt.getGeneratedKeys();
        if (ids.first()) {
            long cid = new Long(ids.getLong(1));
            citation.setDatum(Citation.DOI_KEY, Long.toString(cid));
            insertContexts(cid, citation.getContexts(), con);
        } else {
            System.err.println("No citation ID generated!");
        }
        ids.close();
        stmt.close();
        
    }  //- insertCitations
    
    
    /* id, citationid, context */
    private static final String DEF_INSERT_CONTEXT_QUERY =
        "insert into citationContexts values (NULL, ?, ?)";

    
    public void insertContexts(Long cid, List<String> contexts,
            Connection con) throws SQLException {
        
        PreparedStatement stmt = con.prepareStatement(DEF_INSERT_CONTEXT_QUERY);
        
        for (Iterator<String> it = contexts.iterator(); it.hasNext(); ) {
            stmt.setLong(1, cid);
            stmt.setString(2, it.next());
            stmt.executeUpdate();
        }
        stmt.close();
        
    }  //- insertCitationContexts
    
    
    private static final String DEF_GET_CONTEXTS_QUERY =
        "select context from citationContexts where citationid=?";
    
    
    public List<String> getContexts(Long citationID, Connection con)
    throws SQLException {

        ArrayList<String> contexts = new ArrayList<String>();

        PreparedStatement stmt = con.prepareStatement(DEF_GET_CONTEXTS_QUERY);  
        stmt.setLong(1, citationID);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            contexts.add(rs.getString("context"));
        }
        rs.close();
        stmt.close();
        
        return contexts;
        
    }  //- getContexts
    
    
    private static final String DEF_SET_CLUSTER_QUERY =
        "update citations set cluster=? where id=?";
    
    
    public void setCluster(Citation citation, Long clusterID, Connection con)
    throws SQLException {
        PreparedStatement stmt = con.prepareStatement(DEF_SET_CLUSTER_QUERY);
        stmt.setLong(1, clusterID);
        stmt.setLong(1, Long.parseLong(
                citation.getDatum(Citation.DOI_KEY, Citation.UNENCODED)));
        stmt.executeUpdate();
        stmt.close();
        
    }  //- setCluster
    
    
    private static final String DEF_DEL_CITATIONS_QUERY =
        "delete from citations where paperid=?";
    
    
    public void deleteCitations(String doi, Connection con)
    throws SQLException {
        PreparedStatement stmt = con.prepareStatement(DEF_DEL_CITATIONS_QUERY);
        stmt.setString(1, doi);
        stmt.executeUpdate();
        stmt.close();
        
    }  //- deleteCitations

    
    private static final String DEF_DEL_CITATION_QUERY = 
        "delete from citations where id=?";
    
    
    public void deleteCitation(Long citationID, Connection con)
    throws SQLException {
        PreparedStatement stmt = con.prepareStatement(DEF_DEL_CITATION_QUERY);
        stmt.setLong(1, citationID);
        stmt.executeUpdate();
        stmt.close();
        
    }  //- deleteCitation
    
    
    private static final String DEF_DEL_CONTEXTS_QUERY =
        "delete from citationContexts where citationid=?";
    
    
    public void deleteContexts(Long citationID, Connection con)
    throws SQLException {
        PreparedStatement stmt = con.prepareStatement(DEF_DEL_CONTEXTS_QUERY);
        stmt.setLong(1, citationID);
        stmt.executeUpdate();
        stmt.close();
        
    }  //- deleteContexts
    

}  //- class CitationDAOImpl
