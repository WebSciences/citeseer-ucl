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

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.io.IOException;
import java.io.FileInputStream;

import edu.psu.citeseerx.domain.*;
import edu.psu.citeseerx.utility.FileNamingUtils;
import edu.psu.citeseerx.dao.admin.*;

public class CSXDAO {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        con.setAutoCommit(false);
        return con;
    }


    private static final String DEF_READ_LOCK_QUERY =
        "lock tables papers, papers_versions";

    private static final String DEF_READ_UNLOCK_QUERY =
        "unlock tables";


    public void loadDocument(Document doc) throws SQLException, IOException {

        Connection con = getConnection();

        String doi = doc.getDatum(Document.DOI_KEY);

        try {

            docDAO.insertDocument(doc, con);
            
            fileDAO.insertFileInfo(doi, doc.getFileInfo(), con);
            
            List<Hub> hubs = doc.getFileInfo().getHubs();
            for (Hub hub : hubs) {
                hubDAO.addHubMapping(hub, doi, con);
            }

            for (Iterator<Author> it = doc.getAuthors().iterator();
            it.hasNext(); ) {
                authDAO.insertAuthor(doi, it.next(), con);
            }

            for (Iterator<Citation> it = doc.getCitations().iterator();
            it.hasNext(); ) {
                citeDAO.insertCitation(doi, it.next(), con);
            }

            for (Iterator<Acknowledgment> it =
                doc.getAcknowledgments().iterator(); it.hasNext(); ) {
                ackDAO.insertAcknowledgment(doi, it.next(), con);
            }

            for (Iterator<Keyword> it = doc.getKeywords().iterator();
            it.hasNext(); ) {
                keywordDAO.insertKeyword(doi, it.next(), con);
            }
            
            for (Iterator<Tag> it = doc.getTags().iterator(); it.hasNext(); ) {
                tagDAO.addTag(doi, it.next().getTag(), con);
            }

            fileSysDAO.writeXML(doc);

        } catch (SQLException e) {
            con.rollback();
            con.close();
            throw(e);
        } catch (IOException e) {
            con.rollback();
            con.close();
            throw(e);
        }

        con.commit();
        con.close();

    }  //- loadDocument


    /**
     * Main method to retrieve a Document object from the database.  A great
     * deal of flexibility is included in specifying which data to retrieve.
     * This is useful because every "false" parameter saves a joining SQL
     * query from being performed.
     * @param doi
     * @param getCitations
     * @param getContexts
     * @param getSource
     * @param getAcks
     * @param getKeywords
     * @param getTags
     * @return
     * @throws SQLException
     */
    public Document getDocumentFromDB(String doi, boolean getCitations,
            boolean getContexts, boolean getSource, boolean getAcks,
            boolean getKeywords, boolean getTags) throws SQLException {
        
        Connection con = getConnection();
        //Statement lock_st = con.createStatement();
        //lock_st.execute(DEF_READ_LOCK_QUERY);

        Document doc = docDAO.getDocument(doi, getSource, con);

        try {

            if (doc == null) {
                return null;
            }

            DocumentFileInfo finfo = fileDAO.getFileInfo(doi, con);
            doc.setFileInfo(finfo);

            List<Author> authors = authDAO.getDocAuthors(doi, getSource, con);
            for (Iterator<Author> it = authors.iterator(); it.hasNext(); ) {
                doc.addAuthor(it.next());
            }

            if (getCitations) {
                List<Citation> citations =
                    citeDAO.getCitations(doi, getContexts, con);
                for (Iterator<Citation> it = citations.iterator();
                        it.hasNext(); ) {
                    doc.addCitation(it.next());
                }
            }
            if (getAcks) {
                List<Acknowledgment> acks =
                    ackDAO.getAcknowledgments(doi, getContexts, getSource, con);
                for (Iterator<Acknowledgment> it = acks.iterator();
                        it.hasNext(); ) {
                    doc.addAcknowledgment(it.next());
                }
            }
            if (getKeywords) {
                List<Keyword> keywords =
                    keywordDAO.getKeywords(doi, getSource, con);
                for (Iterator<Keyword> it = keywords.iterator();
                        it.hasNext(); ) {
                    doc.addKeyword(it.next());
                }
            }
            if (getTags) {
                List<Tag> tags = tagDAO.getTags(doi, con);
                doc.setTags(tags);
            }

        } catch (SQLException e) {
            //lock_st.execute(DEF_READ_UNLOCK_QUERY);
            //lock_st.close();
            con.close();
            throw(e);
        }

        //lock_st.execute(DEF_READ_UNLOCK_QUERY);
        //lock_st.close();
        con.close();

        return doc;
        
    }  //- getDocumentFromDB
    
    
    /**
     * Gets a document object with all the core document metadata, with
     * options to retrieve citation contexts and provenance data.
     * @param doi
     * @param getContexts
     * @param getSource
     * @return
     * @throws SQLException
     */
    public Document getDocumentFromDB(String doi, boolean getContexts,
            boolean getSource) throws SQLException {

        return getDocumentFromDB(doi, true, getContexts, getSource,
                true, true, true);

    }  //- getDocumentFromDB
    
    
    /**
     * Convenience method for getting a minimal document object with only
     * core metadata, including authors.  This is the equivalent of calling
     * the fully parameterized getDocumentsFromDB method with all false
     * boolean parameters, and requires only 1 primary key lookup on the
     * papers table, and 2 joined lookups on the authors and fileInfo tables.
     * @param doi
     * @return
     * @throws SQLException
     */
    public Document getDocumentFromDB(String doi) throws SQLException {
        
        return getDocumentFromDB(doi, false, false, false, false, false, false);
    }


    public Document getDocumentFromXML(String doi)
    throws SQLException, IOException {
        
        Connection con = getConnection();
        try {
            DocumentFileInfo finfo = fileDAO.getFileInfo(doi, con);
            String repID = finfo.getDatum(DocumentFileInfo.REP_ID_KEY);
            String relPath = FileNamingUtils.buildXMLPath(doi);
            return fileSysDAO.getDocFromXML(repID, relPath);
        } catch (SQLException e) {
            throw(e);
        } catch (IOException e) {
            throw(e);
        } finally {
            try { con.close(); } catch (Exception e) { }
        }

    }  //- getDocumentFromXML

    
    public void updateDocument(Document doc) throws SQLException, IOException {
        
        Connection con = getConnection();
        
        try {
            updateDocumentImpl(doc, con);
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw(e);
        } catch (IOException e) {
            con.rollback();
            throw(e);
        } finally {
            try { con.close(); } catch (Exception e) { }
        }
        
    }  //- updateDocument
    
    
    protected void updateDocumentImpl(Document doc, Connection con)
    throws SQLException, IOException {
        
        String doi = doc.getDatum(Document.DOI_KEY);

        docDAO.updateDocument(doc, con);
        //fileDAO.updateFileInfo(doi, doc.getFileInfo(), con);

        authDAO.deleteAuthors(doi, con);
        for (Iterator<Author> it = doc.getAuthors().iterator();
        it.hasNext(); ) {
            authDAO.insertAuthor(doi, it.next(), con);
        }

        //citeDAO.deleteCitations(doi, con);
        //for (Iterator<Citation> it = doc.getCitations().iterator();
        //it.hasNext(); ) {
        //    citeDAO.insertCitation(doi, it.next(), con);
        //}

        ackDAO.deleteAcknowledgments(doi, con);
        for (Iterator<Acknowledgment> it =
            doc.getAcknowledgments().iterator(); it.hasNext(); ) {
            ackDAO.insertAcknowledgment(doi, it.next(), con);
        }

        keywordDAO.deleteKeywords(doi, con);
        for (Iterator<Keyword> it = doc.getKeywords().iterator();
        it.hasNext(); ) {
            keywordDAO.insertKeyword(doi, it.next(), con);
        }

        //doc = null;
        //Document newDoc = getDocumentFromDB(doi, true, true);
        //fileSysDAO.writeXML(newDoc);
        
    }  //- updateDocumentImpl
    
    

    ///////////////////////////////////////////////////////
    //  Acknowledgment DAO                               
    ///////////////////////////////////////////////////////

    private AckDAO ackDAO = new AckDAOImpl();


    public void deleteAcknowledgment(Long ackID) throws SQLException {
        Connection con = getConnection();
        try {
            ackDAO.deleteAcknowledgment(ackID, con);
        } catch (SQLException e) {
            con.rollback();
            con.close();
            throw(e);
        }
        con.commit();
        con.close();

    }  //- deleteAcknowledgment


    public void deleteAcknowledgments(String doi) throws SQLException {
        Connection con = getConnection();
        try {
            ackDAO.deleteAcknowledgments(doi, con);
        } catch (SQLException e) {
            con.rollback();
            con.close();
            throw(e);
        }
        con.commit();
        con.close();

    }  //- deleteAcknowledgments


    public void deleteAckContexts(Long ackID) throws SQLException {
        Connection con = getConnection();
        try {
            ackDAO.deleteContexts(ackID, con);
        } catch (SQLException e) {
            con.rollback();
            con.close();
            throw(e);
        }
        con.commit();
        con.close();

    }  //- deleteAckContexts


    public List<Acknowledgment> getAcknowledgments(String doi,
            boolean getContexts, boolean getSource) throws SQLException {
        Connection con = getConnection();
        List<Acknowledgment> acks =
            ackDAO.getAcknowledgments(doi, getContexts, getSource, con);
        con.close();
        return acks;

    }  //- getAcknowledgments


    public List<String> getAckContexts(Long ackID) throws SQLException {
        Connection con = getConnection();
        List<String> contexts = ackDAO.getContexts(ackID, con);
        con.close();
        return contexts;

    }  //- getAckContexts


    public void insertAcknowledgment(String doi, Acknowledgment ack)
    throws SQLException {
        Connection con = getConnection();
        try {
            ackDAO.insertAcknowledgment(doi, ack, con);
        } catch (SQLException e) {
            con.rollback();
            con.close();
            throw(e);
        }
        con.commit();
        con.close();

    }  //- insertAcknowledgment


    public void insertAckContexts(Long ackID, List<String> contexts)
    throws SQLException {
        Connection con = getConnection();
        try {
            ackDAO.insertContexts(ackID, contexts, con);
        } catch (SQLException e) {
            con.rollback();
            con.close();
            throw(e);
        }
        con.commit();
        con.close();

    }  //- insertAckContexts


    public void setAckCluster(Long clusterID, Acknowledgment ack)
    throws SQLException {
        Connection con = getConnection();
        try {
            ackDAO.setCluster(ack, clusterID, con);
        } catch (SQLException e) {
            con.rollback();
            con.close();
            throw(e);
        }
        con.commit();
        con.close();

    }  //- setAckCluster


    public void updateAcknowledgment(Acknowledgment ack) throws SQLException {
        Connection con = getConnection();
        try {
            ackDAO.updateAcknowledgment(ack, con);
        } catch (SQLException e) {
            con.rollback();
            con.close();
            throw(e);
        }
        con.commit();
        con.close();

    }  //- updateAcknowledgment


    ///////////////////////////////////////////////////////
    //  Author DAO                               
    ///////////////////////////////////////////////////////

    private AuthorDAO authDAO = new AuthorDAOImpl();


    public List<Author> getDocAuthors(String docID, boolean getSource)
    throws SQLException {
        Connection con = getConnection();
        List<Author> authors = authDAO.getDocAuthors(docID, getSource, con);
        con.close();
        return authors;

    }  //- getdocAuthors


    public void insertAuthor(String docID, Author auth) throws SQLException {
        Connection con = getConnection();
        try {
            authDAO.insertAuthor(docID, auth, con);
        } catch (SQLException e) {
            con.rollback();
            con.close();
            throw(e);
        }
        con.commit();
        con.close();

    }  //- insertAuthor


    public void updateAuthor(Author auth) throws SQLException {
        Connection con = getConnection();
        try {
            authDAO.updateAuthor(auth, con);
        } catch (SQLException e) {
            con.rollback();
            con.close();
            throw(e);
        }
        con.commit();
        con.close();

    }  //- updateAuthor


    public void setAuthCluster(Author auth, Long clusterID)
    throws SQLException {
        Connection con = getConnection();
        try {
            authDAO.setCluster(auth, clusterID, con);
        } catch (SQLException e) {
            con.rollback();
            con.close();
            throw(e);
        }
        con.commit();
        con.close();

    }  //- setAuthCluster


    public void deleteAuthors(String docID) throws SQLException {
        Connection con = getConnection();
        try {
            authDAO.deleteAuthors(docID, con);
        } catch (SQLException e) {
            con.rollback();
            con.close();
            throw(e);
        }
        con.commit();
        con.close();

    }  //- deleteAuthors


    public void deleteAuthor(Long authorID) throws SQLException {
        Connection con = getConnection();
        try {
            authDAO.deleteAuthor(authorID, con);
        } catch (SQLException e) {
            con.rollback();
            con.close();
            throw(e);
        }
        con.commit();
        con.close();

    }  //- deleteAuthor



    ///////////////////////////////////////////////////////
    //  Citation DAO                               
    ///////////////////////////////////////////////////////

    private CitationDAO citeDAO = new CitationDAOImpl();


    public List<Citation> getCitations(String docID, boolean getContexts)
    throws SQLException {
        Connection con = getConnection();
        List<Citation> citations = null;
        try {
            citations = citeDAO.getCitations(docID, getContexts, con);
        } catch (SQLException e) {
            throw(e);
        } finally {
            con.close();
        }
        return citations;

    }  //- getCitations
    
    
    public List<Citation> getCitations(long startID, int n)
    throws SQLException {
        Connection con = getConnection();
        List<Citation> citations = null;
        try {
            citations = citeDAO.getCitations(startID, n, con);
        } catch (SQLException e) {
            throw(e);
        } finally {
            con.close();
        }
        return citations;
        
    }  //- getCitations
    
    
    public List<Citation> getCitationsForCluster(Long clusterid)
    throws SQLException {
        Connection con = getConnection();
        List<Citation> citations = null;
        try {
            citations = citeDAO.getCitationsForCluster(clusterid, con);
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { con.close(); } catch (Exception e) { }
        }
        return citations;
        
    }  //- getCitationsForCluster
    
    
    public Citation getCitation(long id) throws SQLException {
        Connection con = getConnection();
        Citation citation = null;
        try {
            citation = citeDAO.getCitation(id, con);
        } catch (SQLException e) {
            throw(e);
        } finally {
            con.close();
        }
        return citation;
        
    }  //- getCitation


    public void insertCitations(String DOI, List<Citation> citations)
    throws SQLException {
        Connection con = getConnection();
        try {
            for (Iterator<Citation> it = citations.iterator(); it.hasNext(); ) {
                citeDAO.insertCitation(DOI, it.next(), con);
            }
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw(e);
        } finally {
            con.close();
        }

    }  //- insertCitation


    public List<String> getCitationContexts(Long citationID)
    throws SQLException {
        Connection con = getConnection();
        List<String> contexts = null;
        try {
            contexts = citeDAO.getContexts(citationID, con);
        } catch (SQLException e) {
            throw(e);
        } finally {
            con.close();
        }
        return contexts;

    }  //- getCitationContexts


    public void insertCitationContexts(Long citationID, List<String> contexts)
    throws SQLException {
        Connection con = getConnection();
        try {
            citeDAO.insertContexts(citationID, contexts, con);
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw(e);
        } finally {
            con.close();
        }

    }  //- insertCitationContexts


    public void setCitationCluster(Citation citation, Long clusterID)
    throws SQLException {
        Connection con = getConnection();
        try {
            citeDAO.setCluster(citation, clusterID, con);
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw(e);
        } finally {
            con.close();
        }

    }  //- setCitationCluster


    public void deleteCitations(String DOI) throws SQLException {
        Connection con = getConnection();
        try {
            citeDAO.deleteCitations(DOI, con);
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw(e);
        } finally {
            con.close();
        }

    }  //- deleteCitations


    public void deleteCitation(Long citationID) throws SQLException {
        Connection con = getConnection();
        try {
            citeDAO.deleteCitation(citationID, con);
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw(e);
        } finally {
            con.close();
        }

    }  //- deleteCitation


    public void deleteCitationContexts(Long citationID) throws SQLException {
        Connection con = getConnection();
        try {
            citeDAO.deleteContexts(citationID, con);
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw(e);
        } finally {
            con.close();
        }

    }  //- deleteCitationContexts


    ///////////////////////////////////////////////////////
    //  Document DAO                               
    ///////////////////////////////////////////////////////

    private DocumentDAO docDAO = new DocumentDAOImpl();


    public void setIndexFlag(Document doc, boolean reindex)
    throws SQLException {
        Connection con = getConnection();
        try {
            docDAO.setIndexFlag(doc, reindex, con);
            con.commit();
        } catch (SQLException e) {
            try { con.rollback(); } catch (Exception exc) {}
            throw(e);
        } finally {
            try { con.close(); } catch (Exception e) {}            
        }

    }  //- setIndexFlag


    public void setPublic(Document doc, boolean isPublic) throws SQLException {
        Connection con = getConnection();
        try {
            docDAO.setPublic(doc, isPublic, con);
            con.commit();
        } catch (SQLException e) {
            try { con.rollback(); } catch (Exception exc) {}
            throw(e);
        } finally {
            try { con.close(); } catch (Exception e) {}
        }

    }  //- setPublic


    public void setDocCluster(Document doc, Long clusterID)
    throws SQLException {
        Connection con = getConnection();
        try {
            docDAO.setCluster(doc, clusterID, con);
            con.commit();
        } catch (SQLException e) {
            try { con.rollback(); } catch (Exception exc) {}
            throw(e);
        } finally {
            try { con.close(); } catch (Exception e) {}
        }

    }  //- setDocCluster
    
    
    public void setNcites(Document doc, int ncites) throws SQLException {
        Connection con = getConnection();
        try {
            docDAO.setNcites(doc, ncites, con);
            con.commit();
        } catch (SQLException e) {
            try { con.rollback(); } catch (Exception exc) {}
            throw(e);
        } finally {
            try { con.close(); } catch (Exception e) {}
        }
        
    }  //- setNcites


    public List<String> getDocsToBeIndexed(int numDocs) throws SQLException {
        Connection con = getConnection();
        try {
            return docDAO.getDocsToBeIndexed(numDocs, con);
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { con.close(); } catch (Exception e) {}
        }

    }  //- getDocsToBeIndexed
    
    
    public int getNumberOfDocumentRecords() throws SQLException {
        Connection con = getConnection();
        try {
            return docDAO.getNumberOfDocumentRecords(con);
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { con.close(); } catch (Exception e) {}
        }

    }  //- getNumberOfDocumentRecords
    
    
    public List<String> getDOIs(String start, int amount) throws SQLException {
        Connection con = getConnection();
        try {
            return docDAO.getDOIs(start, amount, con);
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { con.close(); } catch (Exception e) {}
        }
        
    }  //- getDOIs


    ///////////////////////////////////////////////////////
    //  File DAO                               
    ///////////////////////////////////////////////////////

    private FileDAO fileDAO = new FileDAOImpl();

    public DocumentFileInfo getFileInfo(String docID) throws SQLException {
        Connection con = getConnection();
        DocumentFileInfo fileInfo = fileDAO.getFileInfo(docID, con);
        con.close();
        return fileInfo;

    }  //- getFileInfo


    public void updateFileInfo(String docID, DocumentFileInfo fileInfo)
    throws SQLException {
        Connection con = getConnection();
        try {
            fileDAO.updateFileInfo(docID, fileInfo, con);
        } catch (SQLException e) {
            con.rollback();
            con.close();
            throw(e);
        }
        con.commit();
        con.close();

    }  //- updateFileInfo


    public void insertChecksum(CheckSum checksum) throws SQLException {
        Connection con = getConnection();
        try {
            fileDAO.insertChecksum(checksum, con);
        } catch (SQLException e) {
            con.rollback();
            con.close();
            throw(e);
        }
        con.commit();
        con.close();

    }  //- insertChecksum
    
    
    public List<CheckSum> getChecksums(String sha1) throws SQLException {
        Connection con = getConnection();
        List<CheckSum> checkSums = fileDAO.getChecksums(sha1, con);
        con.close();
        return checkSums;
        
    }  //- getChecksums


    ///////////////////////////////////////////////////////
    //  Keyword DAO                               
    ///////////////////////////////////////////////////////

    private KeywordDAO keywordDAO = new KeywordDAOImpl();

    public List<Keyword> getKeywords(String docID, boolean getSource)
    throws SQLException {
        Connection con = getConnection();
        List<Keyword> keywords = keywordDAO.getKeywords(docID, getSource, con);
        con.close();
        return keywords;

    }  //- getKeywords


    public void insertKeyword(String docID, Keyword keyword)
    throws SQLException {
        Connection con = getConnection();
        try {
            keywordDAO.insertKeyword(docID, keyword, con);
        } catch (SQLException e) {
            con.rollback();
            con.close();
            throw(e);
        }
        con.commit();
        con.close();

    }  //- insertKeywords


    public void updateKeyword(String docID, Keyword keyword)
    throws SQLException {
        Connection con = getConnection();
        try {
            keywordDAO.updateKeyword(docID, keyword, con);
        } catch (SQLException e) {
            con.rollback();
            con.close();
            throw(e);
        }
        con.commit();
        con.close();

    }  //- updateKeyword
    
    
    public void deleteKeyword(String docID, Keyword keyword)
    throws SQLException {
        Connection con = getConnection();
        try {
            keywordDAO.deleteKeyword(docID, keyword, con);
        } catch (SQLException e) {
            con.rollback();
            con.close();
            throw(e);
        }
        con.commit();
        con.close();
        
    }  //- deleteKeyword
    
    
    public void deleteKeywords(String docID) throws SQLException {
        Connection con = getConnection();
        try {
            keywordDAO.deleteKeywords(docID, con);
        } catch (SQLException e) {
            con.rollback();
            con.close();
            throw(e);
        }
        con.commit();
        con.close();
        
    }  //- deleteKeywords


    ///////////////////////////////////////////////////////
    //  UserCorrection DAO                               
    ///////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////
    //  Version DAO                               
    ///////////////////////////////////////////////////////

    private VersionDAO versionDAO = new VersionDAOImpl();


    public void setVersion(String doi, int version)
    throws SQLException, IOException {

        Connection con = getConnection();

        try {
            Document doc = fileSysDAO.getDocVersion(doi, version, con);
            updateDocumentImpl(doc, con);
            versionDAO.deprecateVersionsAfter(doi, doc.getVersion(), con);
        
        } catch (SQLException e) {
            con.rollback();
            con.close();
            throw(e);            
        } catch (IOException e) {
            con.rollback();
            con.close();
            throw(e);
        }
        
        con.commit();
        con.close();
        
    }  //- setVersion


    public void setVersion(String doi, String name)
    throws SQLException, IOException {

        Connection con = getConnection();

        try {
            Document doc = fileSysDAO.getDocVersion(doi, name, con);
            versionDAO.deprecateVersionsAfter(doi, doc.getVersion(), con);
            updateDocumentImpl(doc, con);
        
        } catch (SQLException e) {
            con.rollback();
            con.close();
            throw(e);            
        } catch (IOException e) {
            con.rollback();
            con.close();
            throw(e);
        }
        
        con.commit();
        con.close();
        
    }  //- setVersion
    
    
    public void insertVersion(Document doc)
    throws SQLException, IOException {
        
        Connection con = getConnection();
        try {
            versionDAO.insertVersion(doc, con);
            fileSysDAO.writeVersion(doc);
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw(e);                        
        } catch (IOException e) {
            con.rollback();
            throw(e);                        
        } finally {
            try { con.close(); } catch (Exception e) { }
        }
        
    }  //- createNewVersion
    
    
    public void setVersionName(String doi, int version, String name)
    throws SQLException {
        
        Connection con = getConnection();
        try {
            versionDAO.setVersionName(doi, version, name, con);
        } catch (SQLException e){
            con.rollback();
            con.close();
            throw(e);
        }
        con.commit();
        con.close();
        
    }  //- setVersionName


    public void setSpam(String doi, int version, boolean isSpam)
    throws SQLException {
        Connection con = getConnection();
        try {
            versionDAO.setSpam(doi, version, isSpam, con);
        } catch (SQLException e) {
            con.rollback();
            con.close();
            throw(e);
        }
        con.commit();
        con.close();
        
    }  //- setSpam


    ///////////////////////////////////////////////////////
    //  FileSys DAO                               
    ///////////////////////////////////////////////////////

    private FileSysDAO fileSysDAO = new FileSysDAOImpl();


    public Document getDocVersion(String doi, int version)
    throws SQLException, IOException {
        Connection con = getConnection();
        Document doc = fileSysDAO.getDocVersion(doi, version, con);
        con.close();
        return doc;

    }  //- getDocVersion


    public Document getDocVersion(String doi, String name)
    throws SQLException, IOException {
        Connection con = getConnection();
        Document doc = fileSysDAO.getDocVersion(doi, name, con);
        con.close();
        return doc;

    }  //- getDocVersion
    
    
    public FileInputStream getFileInputStream(String doi, String repID,
            String type) throws IOException {
        return fileSysDAO.getFileInputStream(doi, repID, type);
    }

    
    ///////////////////////////////////////////////////////
    //  Hub DAO                               
    ///////////////////////////////////////////////////////

    private HubDAO hubDAO = new HubDAOImpl();
    
    /**
     * Insert a new hub record.  If a hub already exists in the database
     * with a url that matches the one supplied, the hub will be updated
     * will the new values.  For this reason, ONLY INSERT HUBS THAT HAVE
     * ALL FIELDS POPULATED using this method.  Otherwise, data can be lost.
     * If you want a non-destructive hub insert, see addHubMapping.
     * @param hub
     * @throws SQLException
     */
    public void insertHub(Hub hub) throws SQLException {
        Connection con = getConnection();
        Hub existingHub = hubDAO.getHub(hub.getUrl(), con);
        if (existingHub == null) {
            hubDAO.insertHub(hub, con);
        } else {
            hubDAO.updateHub(hub, con);
        }
        con.commit();
        con.close();
        
    }  //- insertHub
    
    
    public void addHubMapping(Hub hub, String doi) throws SQLException {
        Connection con = getConnection();
        hubDAO.addHubMapping(hub, doi, con);
        con.commit();
        con.close();
        
    }  //- addHubMapping
    
    
    public List<Hub> getHubs(String doi) throws SQLException {
        Connection con = getConnection();
        List<Hub> hubs = hubDAO.getHubs(doi, con);
        con.close();
        return hubs;
        
    }  //- getHubs


    ///////////////////////////////////////////////////////
    //  CiteChart DAO                               
    ///////////////////////////////////////////////////////

    private CiteChartDAO citeChartDAO = new CiteChartDAOImpl();
    
    public boolean chartUpdateRequired(String doi) throws SQLException {
        Connection con = getConnection();
        try {
            return citeChartDAO.checkChartUpdateRequired(doi, con);
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { con.close(); } catch (Exception e) { }
        }
        
    }  //- chartUpdateRequired
    
    
    public void insertChartUpdate(String doi, int lastNcites)
    throws SQLException {
        Connection con = getConnection();
        try {
            citeChartDAO.insertChartUpdate(doi, lastNcites, con);
            con.commit();
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { con.close(); } catch (Exception e) { }
        }
        
    }  //- insertChartUpdate
    
    
    ///////////////////////////////////////////////////////
    //  Legacy ID DAO                               
    ///////////////////////////////////////////////////////

    private LegacyIDDAO legacyIDDAO = new LegacyIDDAOImpl();
    
    public String getNewID(int legacyID) throws SQLException {
        Connection con = getConnection();
        try {
            String newID = legacyIDDAO.getNewID(legacyID, con);
            return newID;
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { con.close(); } catch (Exception e) { }
        }
        
    }  //- getNewID
    
    
    public void insertLegacyIDMapping(String csxID, int legacyID)
    throws SQLException {
        Connection con = getConnection();
        try {
            legacyIDDAO.insertMapping(csxID, legacyID, con);
            con.commit();
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { con.close(); } catch (Exception e) { }
        }
        
    }  //- insertLegacyIDMapping


    ///////////////////////////////////////////////////////
    //  Tag DAO                               
    ///////////////////////////////////////////////////////

    private TagDAO tagDAO = new TagDAOImpl();
    
    public void addTag(String paperid, String tag) throws SQLException {
        Connection con = getConnection();
        try {
            tagDAO.addTag(paperid, tag, con);
            Document doc = getDocumentFromDB(paperid);
            docDAO.setIndexFlag(doc, true, con);
            con.commit();
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { con.close(); } catch (Exception e) { }
        }
        
    }  //- addTag
    
    
    public void deleteTag(String paperid, String tag) throws SQLException {
        Connection con = getConnection();
        try {
            tagDAO.deleteTag(paperid, tag, con);
            Document doc = getDocumentFromDB(paperid);
            docDAO.setIndexFlag(doc, true, con);
            con.commit();
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { con.close(); } catch (Exception e) { }
        }
        
    }  //- deleteTag
    
    
    public List<Tag> getTags(String paperid) throws SQLException {
        Connection con = getConnection();
        try {
            return tagDAO.getTags(paperid, con);
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { con.close(); } catch (Exception e) { }
        }
        
    }  //- getTags
    
    
    ///////////////////////////////////////////////////////
    //  Admin DAO                               
    ///////////////////////////////////////////////////////

    private AdminDAO adminDAO = new AdminDAOImpl();
    
    public void setBanner(String banner) throws SQLException {
        Connection con = getConnection();
        try {
            adminDAO.setBanner(banner, con);
            con.commit();
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { con.close(); } catch (Exception e) { }
        }
        
    }  //- setBanner
    
    public String getBanner() throws SQLException {
        Connection con = getConnection();
        try {
            return adminDAO.getBanner(con);
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { con.close(); } catch (Exception e) { }
        }
        
    }  //- getBanner
    
    
}  //- class CSXDAOImpl
