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
import javax.sql.DataSource;
import org.json.*;

import edu.psu.citeseerx.domain.*;
import edu.psu.citeseerx.citeinf.*;

public class CiteClusterDAOImpl implements CiteClusterDAO {

    private DataSource dataSource;
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    
    public Long clusterDocument(List<String> keys, Document doc)
    throws SQLException, JSONException {
        
        Connection con = dataSource.getConnection();
        try {
            Long cid = clusterDocument(keys, doc, con);
            con.commit();
            return cid;
        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException exc) { }
            throw(e);
        } catch (JSONException e) {
            try { con.rollback(); } catch (SQLException exc) { }
            throw(e);
        } finally {
            try { con.close(); } catch (SQLException e) { }
        }

    }  //- clusterDocument
        
    
    public Long clusterDocument(List<String> keys, Document doc,
            Connection con) throws SQLException, JSONException {
        
        String keyFound = null;
        List<Long> cids = new ArrayList<Long>();

        if (!keys.isEmpty()) {
            Long cid = getClusterID(keys.get(0), con);
            if (cid != null) {
                // Only test the first (best) key for documents
                keyFound = keys.get(0);
                cids.add(cid);
            }
        }

        if (cids.size() == 0) {
            Long cid = insertCluster(doc, keys, con);
            cids.add(cid);
            insertDocument(doc, cid, con);
        } else {
            Long cid = cids.get(0);
            for (String key : keys) {
                if (!key.equalsIgnoreCase(keyFound)) {
                    insertKeyMapping(key, cid, con);
                }
            }
            insertDocument(doc, cid, con);
        }
        
        for (Citation citation : doc.getCitations()) {
            clusterCitation(citation.getKeys(), citation, cids.get(0), con);
        }
        
        return cids.get(0);
        
    }  //- clusterDocument
    
    
    private void clusterCitation(List<String> keys, Citation citation,
            Long docCID, Connection con)
    throws SQLException, JSONException {
        
        if (keys.isEmpty()) {
            return;
        }
        
        String keyFound = null;
        List<Long> cids = new ArrayList<Long>();

        for (String key : keys) {
            Long cid = getClusterID(key, con);
            if (cid != null) {
                keyFound = key;
                cids.add(cid);
            }
        }
        
        String context = null;
        if (!citation.getContexts().isEmpty()) {
            context = citation.getContexts().get(0);
        }
        
        if (cids.size()==0) {
            Long cid = insertCluster(false, keys, con);
            insertCitation(citation, cid, con);
            insertGraphMapping(docCID, cid, context, con);
        } else if (cids.size() > 1) {
            for (String key : keys) {
                Long cid = getClusterID(key, con);
                if (cid != null) {
                    insertCitation(citation, cid, con);
                    insertGraphMapping(docCID, cid, context, con);
                    break;
                }
            }
        } else if (cids.size() == 1) {
            Long cid = cids.get(0);
            for (String key : keys) {
                if (!key.equals(keyFound)) {
                    insertKeyMapping(key, cid, con);
                }
            }
            insertCitation(citation, cid, con);
            insertGraphMapping(docCID, cid, context, con);
        }
        
    }  //- clusterCitation
    
    
    private static final String DEF_GET_GRAPH_MAPPING_QUERY =
        "select id, firstContext from citegraph where citing=? and cited=?";
    
    private static final String DEF_INSERT_GRAPH_MAPPING_STMT =
        "insert into citegraph values (NULL, ?, ?, ?)";
        
    private void insertGraphMapping(Long citing, Long cited, String context,
            Connection con) throws SQLException {
        
        if (citing == null || cited == null) {
            System.err.println("Warning: tried to insert null graph mapping!");
            return;
        }
        
        PreparedStatement stmt = null;
        PreparedStatement insert = null;
        ResultSet rs = null;

        try {
            stmt = con.prepareStatement(DEF_GET_GRAPH_MAPPING_QUERY);
            stmt.setLong(1, citing);
            stmt.setLong(2, cited);
            rs = stmt.executeQuery();
        
            if (rs.first()) {
                Long mapid = rs.getLong("id");
                String prevContext = rs.getString("firstContext");
                if (prevContext == null || prevContext.equals("")) {
                    updateGraphContext(mapid, context, con);
                }
            } else {
                insert = con.prepareStatement(DEF_INSERT_GRAPH_MAPPING_STMT);
                insert.setLong(1, citing);
                insert.setLong(2, cited);
                insert.setString(3, context);
                insert.executeUpdate();
            }
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { stmt.close(); } catch (Exception e) {}
            try { rs.close(); } catch (Exception e) {}
        }
        
    }  //- insertGraphMapping
    
    
    private static final String DEF_UPDATE_GRAPH_CONTEXT_STMT =
        "update citegraph set firstContext=? where id=?";
    
    private void updateGraphContext(Long id, String context,
            Connection con) throws SQLException {
        
        PreparedStatement stmt = null;

        try {
            stmt = con.prepareStatement(DEF_UPDATE_GRAPH_CONTEXT_STMT);
            stmt.setString(1, context);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { stmt.close(); } catch (Exception e) {}
        }
        
    }  //- updateGraphContext
    
    
    private static final String DEF_GET_CITING_QUERY =
        "select clusters.id, cauth, ctitle, cvenue, cyear, cpages, " +
        "cpublisher, cvol, cnum, ctech, incollection, " +
        "size, firstContext from clusters, citegraph where " +
        "citing=clusters.id and cited=? order by size desc limit ?";
    
    public List<ThinDoc> getCitingDocuments(Long clusterid,
            int start, int amount) throws SQLException {
        
        Connection con = dataSource.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        int limit = start+amount;
        
        try {
            stmt = con.prepareStatement(DEF_GET_CITING_QUERY);
            stmt.setLong(1, clusterid);
            stmt.setInt(2, limit);
            rs = stmt.executeQuery();
            
            ArrayList<ThinDoc> cited = new ArrayList<ThinDoc>();
            
            // spin until the correct number go by
            int spin = 0;
            while(++spin<=start && rs.next());
            
            while(rs.next()) {
                ThinDoc doc = new ThinDoc();
                doc.setCluster(rs.getLong(1));
                doc.setAuthors(rs.getString("cauth"));
                doc.setTitle(rs.getString("ctitle"));
                doc.setVenue(rs.getString("cvenue"));
                try { doc.setYear(rs.getInt("cyear")); } catch (Exception e) {}
                try { doc.setVol(rs.getInt("cvol")); } catch (Exception e) {}
                try { doc.setNum(rs.getInt("cnum")); } catch (Exception e) {}
                doc.setPages(rs.getString("cpages"));
                doc.setPublisher(rs.getString("cpublisher"));
                doc.setTech(rs.getString("ctech"));
                doc.setInCollection(rs.getBoolean("incollection"));
                doc.setNcites(rs.getInt("size"));
                doc.setSnippet(rs.getString("firstContext"));
                cited.add(doc);
            }
            return cited;
            
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { rs.close(); } catch (Exception e) { }
            try { stmt.close(); } catch (Exception e) { }
            try { con.close(); } catch (Exception e) { }
        }

    }  //- getCitingDocuments

    
    private static final String DEF_GET_CITED_QUERY =
        "select clusters.id, cauth, ctitle, cvenue, cyear, " +
        "cpages, cpublisher, cvol, cnum, ctech, incollection, " +
        "size, firstContext from clusters, citegraph where " +
        "cited=clusters.id and citing=? order by size desc limit ?";

    public List<ThinDoc> getCitedDocuments(Long clusterid,
            int start, int amount) throws SQLException {
        
        Connection con = dataSource.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        int limit = start+amount;
        
        try {
            stmt = con.prepareStatement(DEF_GET_CITED_QUERY);
            stmt.setLong(1, clusterid);
            stmt.setInt(2, limit);
            rs = stmt.executeQuery();
            
            ArrayList<ThinDoc> cited = new ArrayList<ThinDoc>();
            
            // spin until the correct number go by
            int spin = 0;
            while(++spin<=start && rs.next());
            
            while(rs.next()) {
                ThinDoc doc = new ThinDoc();
                doc.setCluster(rs.getLong(1));
                doc.setAuthors(rs.getString("cauth"));
                doc.setTitle(rs.getString("ctitle"));
                doc.setVenue(rs.getString("cvenue"));
                try { doc.setYear(rs.getInt("cyear")); } catch (Exception e) {}
                try { doc.setVol(rs.getInt("cvol")); } catch (Exception e) {}
                try { doc.setNum(rs.getInt("cnum")); } catch (Exception e) {}
                doc.setPages(rs.getString("cpages"));
                doc.setPublisher(rs.getString("cpublisher"));
                doc.setTech(rs.getString("ctech"));
                doc.setInCollection(rs.getBoolean("incollection"));
                doc.setNcites(rs.getInt("size"));
                doc.setSnippet(rs.getString("firstContext"));
                cited.add(doc);
            }
            return cited;
            
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { rs.close(); } catch (Exception e) { }
            try { stmt.close(); } catch (Exception e) { }
            try { con.close(); } catch (Exception e) { }
        }

    }  //- getCitedDocuments
    
    
    private static final String DEF_GET_CITING_CLUST_QUERY =
        "select citing from citegraph where cited=?";
    
    public List<Long> getCitingClusters(Long clusterid) throws SQLException {
        
        Connection con = dataSource.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(DEF_GET_CITING_CLUST_QUERY);
            stmt.setLong(1, clusterid);
            rs = stmt.executeQuery();
            
            List<Long> citing = new ArrayList<Long>();
            while(rs.next()) {
                citing.add(rs.getLong("citing"));
            }
            return citing;
            
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { rs.close(); } catch (Exception e) { }
            try { stmt.close(); } catch (Exception e) { }
            try { con.close(); } catch (Exception e) { }
        }
        
    }  //- getCitingClusters

    
    private static final String DEF_GET_CITED_CLUST_QUERY =
        "select cited from citegraph where citing=?";
    
    public List<Long> getCitedClusters(Long clusterid) throws SQLException {
        
        Connection con = dataSource.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(DEF_GET_CITED_CLUST_QUERY);
            stmt.setLong(1, clusterid);
            rs = stmt.executeQuery();
            
            List<Long> cited = new ArrayList<Long>();
            while(rs.next()) {
                cited.add(rs.getLong("cited"));
            }
            return cited;
            
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { rs.close(); } catch (Exception e) { }
            try { stmt.close(); } catch (Exception e) { }
            try { con.close(); } catch (Exception e) { }
        }
        
    }  //- getCitedClusters

    
    private static final String DEF_GET_TOP_CITING_CLUST_QUERY =
        "select citing from citegraph, clusters where cited=? " +
        "and citing=clusters.id order by size desc limit ?";
    
    public List<Long> getCitingClusters(Long clusterid, int amount)
    throws SQLException {
        
        Connection con = dataSource.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(DEF_GET_TOP_CITING_CLUST_QUERY);
            stmt.setLong(1, clusterid);
            stmt.setInt(2, amount);
            rs = stmt.executeQuery();
            
            List<Long> citing = new ArrayList<Long>();
            while(rs.next()) {
                citing.add(rs.getLong("citing"));
            }
            return citing;
            
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { rs.close(); } catch (Exception e) { }
            try { stmt.close(); } catch (Exception e) { }
            try { con.close(); } catch (Exception e) { }
        }
        
    }  //- getCitingClusters

    
    private static final String DEF_GET_TOP_CITED_CLUST_QUERY =
        "select cited from citegraph, clusters where citing=? " +
        "and cited=clusters.id order by size desc limit ?";
    
    public List<Long> getCitedClusters(Long clusterid, int amount)
    throws SQLException {
        
        Connection con = dataSource.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(DEF_GET_TOP_CITED_CLUST_QUERY);
            stmt.setLong(1, clusterid);
            stmt.setInt(2, amount);
            rs = stmt.executeQuery();
            
            List<Long> cited = new ArrayList<Long>();
            while(rs.next()) {
                cited.add(rs.getLong("cited"));
            }
            return cited;
            
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { rs.close(); } catch (Exception e) { }
            try { stmt.close(); } catch (Exception e) { }
            try { con.close(); } catch (Exception e) { }
        }
        
    }  //- getCitedClusters
    
    public List<String> getCitingDocumentIDs(String paperid)
    throws SQLException {
     
        return null;
    }
    
    public List<String> getCitedDocumentIDs(String paperid)
    throws SQLException {
        
        return null;
    }
    
    public void deleteDocument(String paperid) throws SQLException {
        
    }
    
    
    private static final String DEF_INSERT_CLUSTER_STMT =
        "insert into clusters values (NULL, 0, ?, ?, ?, NULL, NULL, " +
        "NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 1)";

    
    private Long insertCluster(boolean inCollection, List<String> keys,
            Connection con) throws SQLException {
        
        Long cid = null;
        PreparedStatement stmt = null;
        ResultSet keySet = null;
        
        try {
            stmt = con.prepareStatement(DEF_INSERT_CLUSTER_STMT);
            stmt.setBoolean(1, inCollection);
            stmt.setString(2, null);
            stmt.setString(3, null);
            stmt.executeUpdate();

            keySet = stmt.getGeneratedKeys();
            if (keySet.first()) {
                cid = keySet.getLong(1);
            }
        
            for (String key : keys) {
                insertKeyMapping(key, cid, con);
            }

        } catch (SQLException e) {
            throw(e);
        } finally {
            try { keySet.close(); } catch (Exception e) {}
            try { stmt.close(); } catch (Exception e) {}
        }
                
        return cid;
        
    }  //- insertCluster

    
    private Long insertCluster(Document doc, List<String> keys,
            Connection con) throws SQLException {
        
        Long cid = null;
        PreparedStatement stmt = null;
        ResultSet keySet = null;
        
        StringBuffer authBuf = new StringBuffer();
        for (Iterator<Author> it = doc.getAuthors().iterator(); it.hasNext();) {
            String name = it.next().getDatum(Author.NAME_KEY, Author.UNENCODED);
            authBuf.append(name);
            if (it.hasNext()) {
                authBuf.append(",");
            }
        }
        
        String authors = authBuf.toString();
        String title = doc.getDatum(Document.TITLE_KEY, Document.UNENCODED);

        try {
            stmt = con.prepareStatement(DEF_INSERT_CLUSTER_STMT);
            stmt.setBoolean(1, true);
            stmt.setString(2, authors);
            stmt.setString(3, title);
            stmt.executeUpdate();
            
            keySet = stmt.getGeneratedKeys();
            if (keySet.first()) {
                cid = keySet.getLong(1);
            }
            if (!keys.isEmpty()) {
                insertKeyMapping(keys.get(0), cid, con);
            }
            
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { keySet.close(); } catch (Exception e) {}
            try { stmt.close(); } catch (Exception e) {}
        }
        
        return cid;
        
    }  //- insertCluster
    
    
    private static final String DEF_INSERT_DOC_STMT =
        "insert into papers values (?, ?)";
    
    private void insertDocument(Document doc, Long cid,
            Connection con) throws SQLException {
        
        PreparedStatement stmt = null;
        doc.setClusterID(cid);
        
        try {
            stmt = con.prepareStatement(DEF_INSERT_DOC_STMT);
            stmt.setString(1,
                    doc.getDatum(Document.DOI_KEY, Document.UNENCODED));
            stmt.setLong(2, cid);
            stmt.executeUpdate();
            setInCollection(cid, con);
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { stmt.close(); } catch (Exception e) {}
        }
                    
    }  //- insertDocument
    
    
    private static final String DEF_SET_INCOLLECTION_STMT =
        "update clusters set incollection=1 where id=?";
    
    private void setInCollection(Long cid, Connection con)
    throws SQLException {
        
        PreparedStatement stmt = null;
        
        try {
            stmt = con.prepareStatement(DEF_SET_INCOLLECTION_STMT);
            stmt.setLong(1, cid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { stmt.close(); } catch (Exception e) {}
        }
        
    }  //- setInCollection
    
    
    private static final String DEF_INSERT_CITATION_STMT =
        "insert into citations values (?, ?, ?)";
    
    private void insertCitation(Citation citation, Long cid,
            Connection con)
    throws SQLException, JSONException {
        
        citation.setClusterID(cid);
        PreparedStatement stmt = null;
        
        try {
            stmt = con.prepareStatement(DEF_INSERT_CITATION_STMT);
            Long id = Long.parseLong(
                    citation.getDatum(Citation.DOI_KEY));
            stmt.setLong(1, id);
            stmt.setLong(2, cid);
            stmt.setString(3, citation.getDatum(Citation.PAPERID_KEY));
            stmt.executeUpdate();
            
            updateObservations(citation, cid, con);
            incrementClusterSize(cid, con);
        } catch (SQLException e) {
            throw(e);
        } catch (JSONException e) {
            throw(e);
        } finally {
            try { stmt.close(); } catch (Exception e) {}
        }
        
    }  //- insertCitation
    
    
    private void updateObservations(Citation citation, Long cid,
            Connection con)
    throws SQLException, JSONException {
                
        StringBuffer authorBuf = new StringBuffer();
        for (Iterator<String> it = citation.getAuthorNames().iterator();
        it.hasNext(); ) {
            authorBuf.append(it.next());
            if (it.hasNext()) {
                authorBuf.append(",");
            }
        }
        
        String yearStr = citation.getDatum(Citation.YEAR_KEY);
        String volStr  = citation.getDatum(Citation.VOL_KEY);
        String numStr  = citation.getDatum(Citation.NUMBER_KEY);
        
        ThinDoc thinDoc = new ThinDoc();
        thinDoc.setAuthors(authorBuf.toString());
        thinDoc.setTitle(citation.getDatum(Citation.TITLE_KEY));
        thinDoc.setVenue(citation.getDatum(Citation.VENUE_KEY));
        thinDoc.setVentype(citation.getDatum(Citation.VEN_TYPE_KEY));
        thinDoc.setPages(citation.getDatum(Citation.PAGES_KEY));
        thinDoc.setPublisher(citation.getDatum(Citation.PUBLISHER_KEY));
        thinDoc.setTech(citation.getDatum(Citation.TECH_KEY));
        try {thinDoc.setYear(Integer.parseInt(yearStr));} catch (Exception e) {}
        try {thinDoc.setVol(Integer.parseInt(volStr));}   catch (Exception e) {}
        try {thinDoc.setNum(Integer.parseInt(numStr));}   catch (Exception e) {}
        thinDoc.setCluster(cid);
        
        ThinDoc cluster = getThinDoc(cid);
        JSONObject metadata = new JSONObject();
        if (cluster != null && cluster.getObservations() != null) {
            metadata = new JSONObject(cluster.getObservations());
        }
        boolean updated = InferenceBuilder.addObservation(thinDoc, metadata);

        updateCluster(thinDoc, updated, con);

    }  //- updateObservations
    
        
    private static final String DEF_GET_CLUSTERID_QUERY = 
        "select cid from keymap where ckey=?";
    
    private Long getClusterID(String key, Connection con) throws SQLException {

        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = con.prepareStatement(DEF_GET_CLUSTERID_QUERY);
            stmt.setString(1, key);
            rs = stmt.executeQuery();
            
            Long id = null;
            if (rs.first()) {
                id = rs.getLong("cid");
            }
            return id;

        } catch (SQLException e) {
            throw(e);
        } finally {
            try { stmt.close(); } catch (Exception e) {}
            try { rs.close(); } catch (Exception e) {}
        }
        
    }  //- getClusterID
    
    
    private static final String DEF_INSERT_KEYMAP_STMT =
        "insert into keymap values (NULL, ?, ?)";
    
    private void insertKeyMapping(String key, Long cid,
            Connection con) throws SQLException {

        PreparedStatement stmt = null;
        
        try {
            stmt = con.prepareStatement(DEF_INSERT_KEYMAP_STMT);
            stmt.setString(1, key);
            stmt.setLong(2, cid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { stmt.close(); } catch (Exception e) {}
        }
        
    }  //- insertKeyMapping
    
    
    private static final String DEF_INCR_CLUSTSIZE_STMT =
        "update clusters set size=size+1 where id=?";
    
    private void incrementClusterSize(Long cid, Connection con)
    throws SQLException {

        PreparedStatement stmt = null;
        
        try {
            stmt = con.prepareStatement(DEF_INCR_CLUSTSIZE_STMT);
            stmt.setLong(1, cid);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { stmt.close(); } catch (Exception e) {}
        }
        
    }  //- incrementClusterSize
    
    
    private static final String DEF_GET_CLUSTERS_IN_COLL_QUERY =
        "select id, size, cauth, ctitle, cvenue, cventype, cyear, " +
        "cpages, cpublisher, cvol, cnum, ctech from clusters " +
        "where id>? and incollection=1 limit ?";
    
    public List<ThinDoc> getClustersInCollection(Long start, int amount) 
    throws SQLException {
        
        Connection con = dataSource.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = con.prepareStatement(DEF_GET_CLUSTERS_IN_COLL_QUERY);
            stmt.setLong(1, start);
            stmt.setInt(2, amount);
            
            ArrayList<ThinDoc> docs = new ArrayList<ThinDoc>();
            
            rs = stmt.executeQuery();
            while (rs.next()) {

                Long cid     = rs.getLong("id");
                int size     = rs.getInt("size");
                String auths = rs.getString("cauth");
                String title = rs.getString("ctitle");
                String venue = rs.getString("cvenue");
                String vt    = rs.getString("cventype");
                Integer year = rs.getInt("cyear");
                String pages = rs.getString("cpages");
                String publ  = rs.getString("cpublisher");
                Integer vol  = rs.getInt("cvol");
                Integer num  = rs.getInt("cnum");
                String tech  = rs.getString("ctech");
                
                ThinDoc doc = new ThinDoc();
                
                doc.setCluster(cid);
                doc.setNcites(size);
                doc.setAuthors(auths);
                doc.setTitle(title);
                doc.setVenue(venue);
                doc.setVentype(vt);
                if (year != null) {
                    doc.setYear(year.intValue());
                }
                doc.setPages(pages);
                doc.setPublisher(publ);
                if (vol != null) {
                    doc.setVol(vol.intValue());
                }
                if (num != null) {
                    doc.setNum(num.intValue());
                }
                doc.setTech(tech);
                
                docs.add(doc);
                
            }
            return docs;
            
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { rs.close(); } catch (Exception e) { }
            try { stmt.close(); } catch (Exception e) { }
            try { con.close(); } catch (Exception e) { }
        }

    }  //- getClustersInCollection
    
    
    private static final String DEF_GET_PAPERIDS_BY_CLUSTER_QUERY =
        "select id from papers where cluster=?";
    
    public List<String> getPaperIDs(Long clusterid) throws SQLException {
        
        Connection con = dataSource.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = con.prepareStatement(DEF_GET_PAPERIDS_BY_CLUSTER_QUERY);
            stmt.setLong(1, clusterid);
            rs = stmt.executeQuery();
            
            ArrayList<String> dois = new ArrayList<String>();
            while(rs.next()) {
                String doi = rs.getString("id");
                dois.add(doi);
            }
            return dois;
            
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { rs.close(); } catch (Exception e) { }
            try { stmt.close(); } catch (Exception e) { }
            try { con.close(); } catch (Exception e) { }
        }
        
    }  //- getPaperIDs
    
    
    private static final String DEF_GET_CITEIDS_BY_CLUSTER_QUERY =
        "select id from citations where cluster=?";
    
    public List<Long> getCitationIDs(Long clusterid) throws SQLException {
        
        Connection con = dataSource.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = con.prepareStatement(DEF_GET_CITEIDS_BY_CLUSTER_QUERY);
            stmt.setLong(1, clusterid);
            rs = stmt.executeQuery();
            
            ArrayList<Long> ids = new ArrayList<Long>();
            while(rs.next()) {
                Long id = rs.getLong("id");
                ids.add(id);
            }
            return ids;
            
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { rs.close(); } catch (Exception e) { }
            try { stmt.close(); } catch (Exception e) { }
            try { con.close(); } catch (Exception e) { }
        }
        
    }  //- getPaperIDs
    
    private static final String DEF_GET_CLUSTER_QUERY =
        "select size, incollection, cauth, ctitle, cvenue, cventype, cyear, " +
        "cpages, cpublisher, cvol, cnum, ctech, observations " +
        "from clusters where id=?";
    
    public ThinDoc getThinDoc(Long clusterid) throws SQLException {
        
        Connection con = dataSource.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            stmt = con.prepareStatement(DEF_GET_CLUSTER_QUERY);
            stmt.setLong(1, clusterid);
            rs = stmt.executeQuery();
            
            ThinDoc doc = null;
            
            if (rs.first()) {
                doc = new ThinDoc();
                doc.setCluster(clusterid);
                doc.setTitle(rs.getString("ctitle"));
                doc.setAuthors(rs.getString("cauth"));
                doc.setVenue(rs.getString("cvenue"));
                doc.setVentype(rs.getString("cventype"));
                try { doc.setYear(rs.getInt("cyear")); } catch (Exception e) {}
                doc.setPages(rs.getString("cpages"));
                doc.setPublisher(rs.getString("cpublisher"));
                try { doc.setVol(rs.getInt("cvol")); } catch (Exception e) {}
                try { doc.setNum(rs.getInt("cnum")); } catch (Exception e) {}
                doc.setTech(rs.getString("ctech"));
                doc.setObservations(rs.getString("observations"));
                doc.setNcites(rs.getInt("size"));
                doc.setInCollection(rs.getBoolean("incollection"));   
            }

            return doc;

        } catch (SQLException e) {
            throw(e);
        } finally {
            try { rs.close(); } catch (Exception e) { }
            try { stmt.close(); } catch (Exception e) { }
            try { con.close(); } catch (Exception e) { }
        }
        
    }  //- getThinDoc
    
    
    private static final String DEF_UPDATE_CLUSTER_STMT =
        "update clusters set cauth=?, ctitle=?, " +
        "cvenue=?, cventype=?, cyear=?, cpages=?, cpublisher=?, cvol=?, " +
        "cnum=?, ctech=?, observations=?, reindex=?, updated=? where id=?";
    
    private static final String DEF_UPDATE_OBS_STMT =
        "update clusters set observations=? where id=?";
    
    public void updateCluster(ThinDoc cluster, boolean changed,
            Connection con) throws SQLException {
        
        PreparedStatement stmt = null;
        PreparedStatement obsStmt = null;
        
        try {
            if (changed) {
                stmt = con.prepareStatement(DEF_UPDATE_CLUSTER_STMT);
                stmt.setString(1, cluster.getAuthors());
                stmt.setString(2, cluster.getTitle());
                stmt.setString(3, cluster.getVenue());
                stmt.setString(4, cluster.getVentype());
                if (cluster.getYear() > 0) {
                    stmt.setInt(5, cluster.getYear());
                } else {
                    stmt.setNull(5, Types.INTEGER);
                }
                stmt.setString(6, cluster.getPages());
                stmt.setString(7, cluster.getPublisher());
                if (cluster.getVol() > 0) {
                    stmt.setInt(8, cluster.getVol());
                } else {
                    stmt.setNull(8, Types.INTEGER);
                }
                if (cluster.getNum() > 0) {
                    stmt.setInt(8, cluster.getNum());
                } else {
                    stmt.setNull(9, Types.INTEGER);
                }
                stmt.setString(10, cluster.getTech());
                stmt.setString(11, cluster.getObservations());
                stmt.setBoolean(12, changed);
                stmt.setBoolean(13, changed);
                stmt.setLong(14, cluster.getCluster());
                stmt.executeUpdate();
            }
            obsStmt = con.prepareStatement(DEF_UPDATE_OBS_STMT);
            obsStmt.setString(1, cluster.getObservations());
            obsStmt.setLong(2, cluster.getCluster());
            obsStmt.executeUpdate();
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { stmt.close();} catch (Exception e) {}
            try { obsStmt.close();} catch (Exception e) {}
        }

    }  //- updateCluster
    
    
    public void updateCluster(ThinDoc cluster, boolean changed)
    throws SQLException {
        
        Connection con = dataSource.getConnection();
        PreparedStatement stmt = null;
        try {
            if (changed) {
                stmt = con.prepareStatement(DEF_UPDATE_CLUSTER_STMT);
                stmt.setString(1, cluster.getAuthors());
                stmt.setString(2, cluster.getTitle());
                stmt.setString(3, cluster.getVenue());
                stmt.setString(4, cluster.getVentype());
                if (cluster.getYear() > 0) {
                    stmt.setInt(5, cluster.getYear());
                } else {
                    stmt.setNull(5, Types.INTEGER);
                }
                stmt.setString(6, cluster.getPages());
                stmt.setString(7, cluster.getPublisher());
                if (cluster.getVol() > 0) {
                    stmt.setInt(8, cluster.getVol());
                } else {
                    stmt.setNull(8, Types.INTEGER);
                }
                if (cluster.getNum() > 0) {
                    stmt.setInt(9, cluster.getNum());
                } else {
                    stmt.setNull(9, Types.INTEGER);
                }
                stmt.setString(10, cluster.getTech());
                stmt.setString(11, cluster.getObservations());
                stmt.setBoolean(12, changed);
                stmt.setBoolean(13, changed);
                stmt.setLong(14, cluster.getCluster());
            } else {
                stmt = con.prepareStatement(DEF_UPDATE_OBS_STMT);
                stmt.setString(1, cluster.getObservations());
                stmt.setLong(2, cluster.getCluster());
            }

            stmt.executeUpdate();
            con.commit();
            
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { stmt.close(); } catch (Exception e) {}
            try { con.close(); } catch (Exception e) {}
        }
        
    }  //- updateCluster
    
    
    private static final String DEF_GET_CLUSTERS_TO_INDEX_QUERY =
        "select id, size, incollection, cauth, ctitle, cvenue, cventype, " +
        "cyear, cpages, cpublisher, cvol, cnum, ctech from clusters " +
        "where reindex=1 limit ?";
    
    public List<ThinDoc> getClustersToBeIndexed(int amount)
    throws SQLException {
        
        Connection con = dataSource.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(DEF_GET_CLUSTERS_TO_INDEX_QUERY);
            stmt.setInt(1, amount);
            rs = stmt.executeQuery();
            List<ThinDoc> clusters = new ArrayList<ThinDoc>();
            while(rs.next()) {
                ThinDoc doc = new ThinDoc();
                doc.setCluster(rs.getLong("id"));
                doc.setNcites(rs.getInt("size"));
                doc.setInCollection(rs.getBoolean("incollection"));
                doc.setAuthors(rs.getString("cauth"));
                doc.setTitle(rs.getString("ctitle"));
                doc.setVenue(rs.getString("cvenue"));
                doc.setVentype(rs.getString("cventype"));
                try { doc.setYear(rs.getInt("cyear")); } catch (Exception e) {}
                doc.setPages(rs.getString("cpages"));
                doc.setPublisher(rs.getString("cpublisher"));
                try { doc.setVol(rs.getInt("cvol")); } catch (Exception e) {}
                try { doc.setNum(rs.getInt("cnum")); } catch (Exception e) {}
                doc.setTech(rs.getString("ctech"));
                clusters.add(doc);
            }
            return clusters;
            
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { rs.close(); } catch (Exception e) { }
            try { stmt.close(); } catch (Exception e) { }
            try { con.close(); } catch (Exception e) { }
        }
        
    }  //- getClustersToBeIndexed
    
    
    
    private static final String DEF_SET_CLUSTER_INDEX_FLAG_STMT =
        "update clusters set reindex=? where id=?";
    
    public void setClusterIndexFlag(Long clusterid, boolean reindex)
    throws SQLException {
        
        Connection con = dataSource.getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(DEF_SET_CLUSTER_INDEX_FLAG_STMT);
            stmt.setBoolean(1, reindex);
            stmt.setLong(2, clusterid);
            stmt.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            try { con.rollback(); } catch (Exception exc) { }
            throw(e);
        } finally {
            try { stmt.close(); } catch (Exception e) { }
            try { con.close(); } catch (Exception e) { }
        }
        
    }  //- setClusterIndexFlag
    
    
    private static final String DEF_GET_UPDATED_CLUSTERS_QUERY =
        "select id, size, incollection, cauth, ctitle, cvenue, cventype, " +
        "cyear, cpages, cpublisher, cvol, cnum, ctech from clusters " +
        "where updated=1 and incollection=1 and size>0 limit ?";

    public List<ThinDoc> getUpdatedClusters(int amount)
    throws SQLException {
        
        Connection con = dataSource.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(DEF_GET_UPDATED_CLUSTERS_QUERY);
            stmt.setInt(1, amount);
            rs = stmt.executeQuery();
            List<ThinDoc> clusters = new ArrayList<ThinDoc>();
            while(rs.next()) {
                ThinDoc doc = new ThinDoc();
                doc.setCluster(rs.getLong("id"));
                doc.setNcites(rs.getInt("size"));
                doc.setInCollection(rs.getBoolean("incollection"));
                doc.setAuthors(rs.getString("cauth"));
                doc.setTitle(rs.getString("ctitle"));
                doc.setVenue(rs.getString("cvenue"));
                doc.setVentype(rs.getString("cventype"));
                try { doc.setYear(rs.getInt("cyear")); } catch (Exception e) {}
                doc.setPages(rs.getString("cpages"));
                doc.setPublisher(rs.getString("cpublisher"));
                try { doc.setVol(rs.getInt("cvol")); } catch (Exception e) {}
                try { doc.setNum(rs.getInt("cnum")); } catch (Exception e) {}
                doc.setTech(rs.getString("ctech"));
                
                clusters.add(doc);
            }
            return clusters;
            
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { rs.close(); } catch (Exception e) { }
            try { stmt.close(); } catch (Exception e) { }
            try { con.close(); } catch (Exception e) { }
        }
        
    }  //- getUpdatedClusters
    
    
    private static final String DEF_SET_CLUSTER_UPDATED_FLAG_STMT =
        "update clusters set updated=? where id=?";
    
    public void setClusterUpdatedFlag(Long clusterid, boolean updated)
    throws SQLException {
        
        Connection con = dataSource.getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(DEF_SET_CLUSTER_UPDATED_FLAG_STMT);
            stmt.setBoolean(1, updated);
            stmt.setLong(2, clusterid);
            stmt.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            try { con.rollback(); } catch (Exception exc) { }
            throw(e);
        } finally {
            try { stmt.close(); } catch (Exception e) { }
            try { con.close(); } catch (Exception e) { }
        }
        
    }  //- setClusterUpdatedFlag
    
    
    private static final String DEF_GET_CONTEXT_QUERY =
        "select firstContext from citegraph where citing=? and cited=?";
    
    public String getContext(Long citing, Long cited) throws SQLException {
        
        Connection con = dataSource.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(DEF_GET_CONTEXT_QUERY);
            stmt.setLong(1, citing);
            stmt.setLong(2, cited);
            rs = stmt.executeQuery();

            String context = null;
            if (rs.first()) {
                context = rs.getString("firstContext");
            }
            return context;
            
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { rs.close(); } catch (Exception e) { }
            try { stmt.close(); } catch (Exception e) { }
            try { con.close(); } catch (Exception e) { }
        }
        
    }  //- getContext
    
    
    private static final String DEF_GET_MIN_CID_QUERY =
        "select min(id) from clusters";
    
    public Long getMinClusterID() throws SQLException {
        
        Connection con = dataSource.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(DEF_GET_MIN_CID_QUERY);
            rs = stmt.executeQuery();
            if (rs.first()) {
                return rs.getLong(1);
            } else {
                return null;
            }
            
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { rs.close(); } catch (SQLException e) {}
            try { stmt.close(); } catch (SQLException e) {}
            try { con.close(); } catch (SQLException e) {}
        }
        
    }  //- getMinClusterID
    
    
    private static final String DEF_GET_MAX_CID_QUERY =
        "select max(id) from clusters";
    
    public Long getMaxClusterID() throws SQLException {
        
        Connection con = dataSource.getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.prepareStatement(DEF_GET_MAX_CID_QUERY);
            rs = stmt.executeQuery();
            if (rs.first()) {
                return rs.getLong(1);
            } else {
                return null;
            }
            
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { rs.close(); } catch (SQLException e) {}
            try { stmt.close(); } catch (SQLException e) {}
            try { con.close(); } catch (SQLException e) {}
        }
        
    }  //- getMaxClusterID
    
    
}  //- class CiteClusterDAOImpl
