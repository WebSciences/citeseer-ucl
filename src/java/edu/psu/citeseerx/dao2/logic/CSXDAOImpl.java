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
package edu.psu.citeseerx.dao2.logic;

import org.springframework.dao.DataAccessException;

import java.util.*;
import java.io.IOException;
import java.io.FileInputStream;

import edu.psu.citeseerx.domain.*;
import edu.psu.citeseerx.utility.FileNamingUtils;
import edu.psu.citeseerx.dao2.*;

public class CSXDAOImpl implements CSXDAO {

    private AckDAO ackDAO;
    private AdminDAO adminDAO;
    private AuthorDAO authDAO;
    private CitationDAO citeDAO;
    private CiteChartDAO citeChartDAO;
    private DocumentDAO docDAO;
    private FileDAO fileDAO;
    private FileSysDAO fileSysDAO;
    private HubDAO hubDAO;
    private KeywordDAO keywordDAO;
    private LegacyIDDAO legacyIDDAO;
    private TagDAO tagDAO;
    private VersionDAO versionDAO;
    
    public void setAckDAO(AckDAO ackDAO) {
        this.ackDAO = ackDAO;
    }

    public void setAdminDAO(AdminDAO adminDAO) {
        this.adminDAO = adminDAO;
    }

    public void setAuthDAO(AuthorDAO authDAO) {
        this.authDAO = authDAO;
    }

    public void setCiteChartDAO(CiteChartDAO citeChartDAO) {
        this.citeChartDAO = citeChartDAO;
    }

    public void setCiteDAO(CitationDAO citeDAO) {
        this.citeDAO = citeDAO;
    }

    public void setDocDAO(DocumentDAO docDAO) {
        this.docDAO = docDAO;
    }

    public void setFileDAO(FileDAO fileDAO) {
        this.fileDAO = fileDAO;
    }

    public void setFileSysDAO(FileSysDAO fileSysDAO) {
        this.fileSysDAO = fileSysDAO;
    }

    public void setHubDAO(HubDAO hubDAO) {
        this.hubDAO = hubDAO;
    }

    public void setKeywordDAO(KeywordDAO keywordDAO) {
        this.keywordDAO = keywordDAO;
    }

    public void setLegacyIDDAO(LegacyIDDAO legacyIDDAO) {
        this.legacyIDDAO = legacyIDDAO;
    }

    public void setTagDAO(TagDAO tagDAO) {
        this.tagDAO = tagDAO;
    }

    public void setVersionDAO(VersionDAO versionDAO) {
        this.versionDAO = versionDAO;
    }

    
    ///////////////////////////////////////////////////////
    //  CSX Operations                               
    ///////////////////////////////////////////////////////

    /**
     * Inserts a basic document record into the database.  If importing
     * a full document for the first time, this method must be called
     * before importDocument due to foreign key constraints on the document
     * id field.  This is not necessary on some RDMS, but it is for
     * MySQL/InnoDB, for example.  If you want fully transactional document
     * imports, use a DB that supports deferred key checks and add a method
     * that does everything at once.
     * @param doc
     * @throws DataAccessException
     */
    public void insertDocumentEntry(Document doc) throws DataAccessException {
        docDAO.insertDocument(doc);
    }
    
    
    /**
     * Imports full document data into the repository.  This method handles
     * not only DB imports but writes base version info the the file system
     * as well.
     * NOTE: insertDocumentEntry MUST be called first!
     * @param doc
     * @throws DataAccessException
     * @throws IOException
     */
    public void importDocument(Document doc)
    throws DataAccessException, IOException {
        String doi = doc.getDatum(Document.DOI_KEY);
        
        docDAO.insertDocumentSrc(doc);
        
        DocumentFileInfo finfo = doc.getFileInfo();
        for (String url : finfo.getUrls()) {
            hubDAO.insertUrl(doi, url);
        }
        for (Hub hub : finfo.getHubs()) {
            for (String url : finfo.getUrls()) {
                hubDAO.addHubMapping(hub, url, doi);
            }
        }
        for (CheckSum sum : finfo.getCheckSums()) {
            sum.setDOI(doi);
            fileDAO.insertChecksum(sum);
        }
        
        for (Iterator<Author> it = doc.getAuthors().iterator();
        it.hasNext(); ) {
            authDAO.insertAuthor(doi, it.next());
        }

        for (Iterator<Citation> it = doc.getCitations().iterator();
        it.hasNext(); ) {
            citeDAO.insertCitation(doi, it.next());
        }

        for (Iterator<Acknowledgment> it =
            doc.getAcknowledgments().iterator(); it.hasNext(); ) {
            ackDAO.insertAcknowledgment(doi, it.next());
        }

        for (Iterator<Keyword> it = doc.getKeywords().iterator();
        it.hasNext(); ) {
            keywordDAO.insertKeyword(doi, it.next());
        }
            
        for (Iterator<Tag> it = doc.getTags().iterator(); it.hasNext(); ) {
            tagDAO.addTag(doi, it.next().getTag());
        }

        fileSysDAO.writeXML(doc);

    }  //- importDocument


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
     * @throws DataAccessException
     */
    public Document getDocumentFromDB(String doi, boolean getCitations,
            boolean getContexts, boolean getSource, boolean getAcks,
            boolean getKeywords, boolean getTags) throws DataAccessException {
        
        Document doc = docDAO.getDocument(doi, getSource);

        if (doc == null) {
            return null;
        }

        DocumentFileInfo finfo = doc.getFileInfo();
        List urls = hubDAO.getUrls(doi);
        for (Object o : urls) {
            finfo.addUrl((String)o);
        }
        
        List authors = authDAO.getDocAuthors(doi, getSource);
        for (Iterator it = authors.iterator(); it.hasNext(); ) {
                doc.addAuthor((Author)it.next());
        }

        if (getCitations) {
            List citations =
                citeDAO.getCitations(doi, getContexts);
            for (Iterator it = citations.iterator(); it.hasNext(); ) {
                doc.addCitation((Citation)it.next());
            }
        }
        if (getAcks) {
            List acks = ackDAO.getAcknowledgments(doi, getContexts, getSource);
            for (Iterator it = acks.iterator(); it.hasNext(); ) {
                doc.addAcknowledgment((Acknowledgment)it.next());
            }
        }
        if (getKeywords) {
            List keywords = keywordDAO.getKeywords(doi, getSource);
            for (Iterator it = keywords.iterator(); it.hasNext(); ) {
                doc.addKeyword((Keyword)it.next());
            }
        }
        if (getTags) {
            List tags = tagDAO.getTags(doi);
            doc.setTags(tags);
        }

        return doc;
        
    }  //- getDocumentFromDB
    
    
    /**
     * Gets a document object with all the core document metadata, with
     * options to retrieve citation contexts and provenance data.
     * @param doi
     * @param getContexts
     * @param getSource
     * @return
     * @throws DataAccessException
     */
    public Document getDocumentFromDB(String doi, boolean getContexts,
            boolean getSource) throws DataAccessException {

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
     * @throws DataAccessException
     */
    public Document getDocumentFromDB(String doi) throws DataAccessException {
        
        return getDocumentFromDB(doi, false, false, false, false, false, false);
    }


    public Document getDocumentFromXML(String doi)
    throws DataAccessException, IOException {
        Document doc = docDAO.getDocument(doi, false);
        String repID = doc.getFileInfo().getDatum(DocumentFileInfo.REP_ID_KEY);
        String relPath = FileNamingUtils.buildXMLPath(doi);
        return fileSysDAO.getDocFromXML(repID, relPath);

    }  //- getDocumentFromXML

    
    public void updateDocumentData(Document doc)
    throws DataAccessException, IOException {
        updateDocumentData(doc, true, true, true, true);
    }
    
    
    public void updateDocumentData(Document doc, boolean updateAuthors,
            boolean updateCitations,
            boolean updateAcknowledgements, boolean updateKeywords)
    throws DataAccessException, IOException {
        
        String doi = doc.getDatum(Document.DOI_KEY);

        docDAO.updateDocument(doc);
        //fileDAO.updateFileInfo(doi, doc.getFileInfo(), con);

        if (updateAuthors) {
            authDAO.deleteAuthors(doi);
            for (Iterator<Author> it = doc.getAuthors().iterator();
            it.hasNext(); ) {
                authDAO.insertAuthor(doi, it.next());
            }
        }

        if (updateCitations) {
            citeDAO.deleteCitations(doi);
            for (Iterator<Citation> it = doc.getCitations().iterator();
            it.hasNext(); ) {
                citeDAO.insertCitation(doi, it.next());
            }
        }

        if (updateAcknowledgements) {
            ackDAO.deleteAcknowledgments(doi);
            for (Iterator<Acknowledgment> it =
                doc.getAcknowledgments().iterator(); it.hasNext(); ) {
                ackDAO.insertAcknowledgment(doi, it.next());
            }
        }
        
        if (updateKeywords) {
            keywordDAO.deleteKeywords(doi);
            for (Iterator<Keyword> it = doc.getKeywords().iterator();
            it.hasNext(); ) {
                keywordDAO.insertKeyword(doi, it.next());
            }
        }

    }  //- updateDocumentData
    
    

    ///////////////////////////////////////////////////////
    //  Acknowledgment DAO                               
    ///////////////////////////////////////////////////////


    public void deleteAcknowledgment(Long ackID) throws DataAccessException {
        ackDAO.deleteAcknowledgment(ackID);
    }  //- deleteAcknowledgment


    public void deleteAcknowledgments(String doi) throws DataAccessException {
        ackDAO.deleteAcknowledgments(doi);
    }  //- deleteAcknowledgments


    public void deleteAckContexts(Long ackID) throws DataAccessException {
        ackDAO.deleteAckContexts(ackID);
    }  //- deleteAckContexts


    public List getAcknowledgments(String doi, boolean getContexts,
            boolean getSource) throws DataAccessException {
        return ackDAO.getAcknowledgments(doi, getContexts, getSource);
    }  //- getAcknowledgments


    public List getAckContexts(Long ackID) throws DataAccessException {
        return ackDAO.getAckContexts(ackID);
    }  //- getAckContexts


    public void insertAcknowledgment(String doi, Acknowledgment ack)
    throws DataAccessException {
        ackDAO.insertAcknowledgment(doi, ack);
    }  //- insertAcknowledgment


    public void insertAckContexts(Long ackID, List<String> contexts)
    throws DataAccessException {
        ackDAO.insertAckContexts(ackID, contexts);
    }  //- insertAckContexts


    public void setAckCluster(Acknowledgment ack, Long clusterID)
    throws DataAccessException {
        ackDAO.setAckCluster(ack, clusterID);
    }  //- setAckCluster


    public void updateAcknowledgment(Acknowledgment ack)
    throws DataAccessException {
        ackDAO.updateAcknowledgment(ack);
    }  //- updateAcknowledgment


    ///////////////////////////////////////////////////////
    //  Author DAO                               
    ///////////////////////////////////////////////////////



    public List getDocAuthors(String docID, boolean getSource)
    throws DataAccessException {
        return authDAO.getDocAuthors(docID, getSource);
    }  //- getdocAuthors


    public void insertAuthor(String docID, Author auth)
    throws DataAccessException {
        authDAO.insertAuthor(docID, auth);
    }  //- insertAuthor


    public void updateAuthor(Author auth) throws DataAccessException {
        authDAO.updateAuthor(auth);
    }  //- updateAuthor


    public void setAuthCluster(Author auth, Long clusterID)
    throws DataAccessException {
        authDAO.setAuthCluster(auth, clusterID);
    }  //- setAuthCluster


    public void deleteAuthors(String docID) throws DataAccessException {
        authDAO.deleteAuthors(docID);
    }  //- deleteAuthors


    public void deleteAuthor(Long authorID) throws DataAccessException {
        authDAO.deleteAuthor(authorID);
    }  //- deleteAuthor



    ///////////////////////////////////////////////////////
    //  Citation DAO                               
    ///////////////////////////////////////////////////////


    public List getCitations(String docID, boolean getContexts)
    throws DataAccessException {
        return citeDAO.getCitations(docID, getContexts);
    }  //- getCitations
    
    
    public List getCitations(long startID, int n) throws DataAccessException {
        return citeDAO.getCitations(startID, n);
    }  //- getCitations
    
    
    public List getCitationsForCluster(Long clusterid)
    throws DataAccessException {
        return citeDAO.getCitationsForCluster(clusterid);
    }  //- getCitationsForCluster
    
    
    public Citation getCitation(long id) throws DataAccessException {
        return citeDAO.getCitation(id);
    }  //- getCitation


    public void insertCitations(String DOI, List<Citation> citations)
    throws DataAccessException {
        for (Iterator<Citation> it = citations.iterator(); it.hasNext(); ) {
            citeDAO.insertCitation(DOI, it.next());
        }
    }  //- insertCitations
    
    
    public void insertCitation(String DOI, Citation citation)
    throws DataAccessException {
        citeDAO.insertCitation(DOI, citation);
    }


    public List getCiteContexts(Long citationID)
    throws DataAccessException {
        return citeDAO.getCiteContexts(citationID);
    }  //- getCitationContexts


    public void insertCiteContexts(Long citationID, List<String> contexts)
    throws DataAccessException {
        citeDAO.insertCiteContexts(citationID, contexts);
    }  //- insertCitationContexts


    public void setCiteCluster(Citation citation, Long clusterID)
    throws DataAccessException {
        citeDAO.setCiteCluster(citation, clusterID);
    }  //- setCitationCluster


    public void deleteCitations(String DOI) throws DataAccessException {
        citeDAO.deleteCitations(DOI);
    }  //- deleteCitations


    public void deleteCitation(Long citationID) throws DataAccessException {
        citeDAO.deleteCitation(citationID);
    }  //- deleteCitation


    public void deleteCiteContexts(Long citationID)
    throws DataAccessException {
        citeDAO.deleteCiteContexts(citationID);
    }  //- deleteCitationContexts
    
    /* (non-Javadoc)
	 * @see edu.psu.citeseerx.dao2.CitationDAO#getNumberOfCitationsRecords()
	 */
	public Integer getNumberOfCitationsRecords() throws DataAccessException {
		return citeDAO.getNumberOfCitationsRecords();
	} //- getNumberOfCitationsRecords


    ///////////////////////////////////////////////////////
    //  Document DAO                               
    ///////////////////////////////////////////////////////

	public Document getDocument(String doi, boolean getSource)
    throws DataAccessException {
        return docDAO.getDocument(doi, getSource);
    }
    
    public void updateDocument(Document doc) throws DataAccessException {
        docDAO.updateDocument(doc);
    }
    
    public void insertDocument(Document doc) throws DataAccessException {
        docDAO.insertDocument(doc);
    }
    
    public void insertDocumentSrc(Document doc) throws DataAccessException {
        docDAO.insertDocumentSrc(doc);
    }
    
    public void setDocPublic(Document doc, boolean isPublic)
    throws DataAccessException {
        docDAO.setDocPublic(doc, isPublic);
    }  //- setPublic


    public void setDocCluster(Document doc, Long clusterID)
    throws DataAccessException {
        docDAO.setDocCluster(doc, clusterID);
    }  //- setDocCluster
    
    
    public void setDocNcites(Document doc, int ncites)
    throws DataAccessException {
        docDAO.setDocNcites(doc, ncites);
    }  //- setNcites


    public Integer getNumberOfDocumentRecords() throws DataAccessException {
        return docDAO.getNumberOfDocumentRecords();
    }  //- getNumberOfDocumentRecords
    
    
    public List getDOIs(String start, int amount) throws DataAccessException {
        return docDAO.getDOIs(start, amount);
    }  //- getDOIs

    /* (non-Javadoc)
	 * @see edu.psu.citeseerx.dao2.DocumentDAO#getSetDOIs(java.util.Date, java.util.Date, java.lang.String, int)
	 */
	public List<DOIInfo> getSetDOIs(Date start, Date end, String prev, 
			int amount) throws DataAccessException {
		return docDAO.getSetDOIs(start, end, prev, amount);
	} //- getSetDOIs


    public Integer getSetDOICount(Date start, Date end, String prev) 
    throws DataAccessException {
    	return docDAO.getSetDOICount(start, end, prev);
    } //- getSetDOICount

    ///////////////////////////////////////////////////////
    //  File DAO                               
    ///////////////////////////////////////////////////////


	public void insertChecksum(CheckSum checksum) throws DataAccessException {
        fileDAO.insertChecksum(checksum);
    }  //- insertChecksum
    
    public void insertChecksums(String doi, List<CheckSum> checksums) {
        fileDAO.insertChecksums(doi, checksums);
    }  //- insertChecksums
    
    public List getChecksums(String sha1) throws DataAccessException {
        return fileDAO.getChecksums(sha1);
    }  //- getChecksums
    
    
    public void deleteChecksums(String doi) throws DataAccessException {
        fileDAO.deleteChecksums(doi);
    }
    
    public void updateChecksums(String doi, List<CheckSum> checksums)
    throws DataAccessException {
        fileDAO.updateChecksums(doi, checksums);
    }
    
    public List getChecksumsForDocument(String doi) {
        return fileDAO.getChecksumsForDocument(doi);
    }


    ///////////////////////////////////////////////////////
    //  Keyword DAO                               
    ///////////////////////////////////////////////////////


    public List getKeywords(String docID, boolean getSource)
    throws DataAccessException {
        return keywordDAO.getKeywords(docID, getSource);
    }  //- getKeywords


    public void insertKeyword(String docID, Keyword keyword)
    throws DataAccessException {
        keywordDAO.insertKeyword(docID, keyword);
    }  //- insertKeywords


    public void updateKeyword(String docID, Keyword keyword)
    throws DataAccessException {
        keywordDAO.updateKeyword(docID, keyword);
    }  //- updateKeyword
    
    
    public void deleteKeyword(String docID, Keyword keyword)
    throws DataAccessException {
        keywordDAO.deleteKeyword(docID, keyword);
    }  //- deleteKeyword
    
    
    public void deleteKeywords(String docID) throws DataAccessException {
        keywordDAO.deleteKeywords(docID);
    }  //- deleteKeywords


    ///////////////////////////////////////////////////////
    //  UserCorrection DAO                               
    ///////////////////////////////////////////////////////



    ///////////////////////////////////////////////////////
    //  Version DAO                               
    ///////////////////////////////////////////////////////


    public void setVersion(String doi, int version)
    throws DataAccessException, IOException {
        
        Document doc = fileSysDAO.getDocVersion(doi, version);
        updateDocumentData(doc);
        versionDAO.deprecateVersionsAfter(doi, doc.getVersion());
        
    }  //- setVersion


    public void setVersion(String doi, String name)
    throws DataAccessException, IOException {

        Document doc = fileSysDAO.getDocVersion(doi, name);
        versionDAO.deprecateVersionsAfter(doi, doc.getVersion());
        updateDocumentData(doc);
        
    }  //- setVersion
    
    
    public boolean insertVersion(Document doc)
    throws DataAccessException, IOException {
        
        versionDAO.insertVersion(doc);
        fileSysDAO.writeVersion(doc);
        return true;
        
    }  //- createNewVersion
    
    
    public void setVersionName(String doi, int version, String name)
    throws DataAccessException {
        versionDAO.setVersionName(doi, version, name);
    }  //- setVersionName


    public void setVersionSpam(String doi, int version, boolean isSpam)
    throws DataAccessException {
        versionDAO.setVersionSpam(doi, version, isSpam);
    }  //- setSpam
    
    
    public void deprecateVersion(String doi, int version)
    throws DataAccessException {
        versionDAO.deprecateVersion(doi, version);
    }
    
    public void deprecateVersionsAfter(String doi, int version)
    throws DataAccessException {
        versionDAO.deprecateVersionsAfter(doi, version);
    }
    
    public void createNewVersion(Document doc) throws DataAccessException {
        versionDAO.createNewVersion(doc);
    }
    
    public void insertCorrection(String userid, String paperid, int version)
    throws DataAccessException {
        versionDAO.insertCorrection(userid, paperid, version);
    }
    
    public String getCorrector(String paperid, int version)
    throws DataAccessException {
        return versionDAO.getCorrector(paperid, version);
    }


    ///////////////////////////////////////////////////////
    //  FileSys DAO                               
    ///////////////////////////////////////////////////////


    public Document getDocVersion(String doi, int version)
    throws DataAccessException, IOException {
        return fileSysDAO.getDocVersion(doi, version);
    }  //- getDocVersion


    public Document getDocVersion(String doi, String name)
    throws DataAccessException, IOException {
        return fileSysDAO.getDocVersion(doi, name);
    }  //- getDocVersion
    
    
    public FileInputStream getFileInputStream(String doi, String repID,
            String type) throws IOException {
        return fileSysDAO.getFileInputStream(doi, repID, type);
    }
    
    public void writeXML(Document doc) throws IOException {
        fileSysDAO.writeXML(doc);
    }
    
    public void writeVersion(Document doc) throws IOException {
        fileSysDAO.writeVersion(doc);
    }
    
    public Document getDocFromXML(String repID, String relPath)
    throws IOException {
        return fileSysDAO.getDocFromXML(repID, relPath);
    }
    
    public List<String> getFileTypes(String doi, String repID)
    throws IOException {
        return fileSysDAO.getFileTypes(doi, repID);
    }

    public String getRepositoryID(String doi) {
        return fileSysDAO.getRepositoryID(doi);
    }
    
    ///////////////////////////////////////////////////////
    //  Hub DAO                               
    ///////////////////////////////////////////////////////

    
    /**
     * Insert a new hub record.  If a hub already exists in the database
     * with a url that matches the one supplied, the hub will be updated
     * will the new values.  For this reason, ONLY INSERT HUBS THAT HAVE
     * ALL FIELDS POPULATED using this method.  Otherwise, data can be lost.
     * If you want a non-destructive hub insert, see addHubMapping.
     * @param hub
     * @throws DataAccessException
     */
    public long insertHub(Hub hub) throws DataAccessException {
        Hub existingHub = hubDAO.getHub(hub.getUrl());
        if (existingHub == null) {
            return hubDAO.insertHub(hub);
        } else {
            hubDAO.updateHub(hub);
            return 0;
        }
    }  //- insertHub
    
    
    public void addHubMapping(Hub hub, String url, String doi)
    throws DataAccessException {
        hubDAO.addHubMapping(hub, url, doi);
    }  //- addHubMapping
    
    
    public List getHubs(String doi) throws DataAccessException {
        return hubDAO.getHubs(doi);
    }  //- getHubs
    
    
    public List getHubsForUrl(String url) throws DataAccessException {
        return hubDAO.getHubsForUrl(url);
    }

    
    public Hub getHub(String url) throws DataAccessException {
        return hubDAO.getHub(url);
    }
    
    
    public void updateHub(Hub hub) throws DataAccessException {
        hubDAO.updateHub(hub);
    }
    
    public void insertHubMapping(long urlID, long hubID)
    throws DataAccessException {
        hubDAO.insertHubMapping(urlID, hubID);
    }
    
    
    public long insertUrl(String doi, String url) {
        return hubDAO.insertUrl(doi, url);
    }
    
    
    public List getUrls(String doi) {
        return hubDAO.getUrls(doi);
    }
    

    ///////////////////////////////////////////////////////
    //  CiteChart DAO                               
    ///////////////////////////////////////////////////////


    public boolean checkChartUpdateRequired(String doi)
    throws DataAccessException {
        return citeChartDAO.checkChartUpdateRequired(doi);
    }  //- chartUpdateRequired
    
    
    public void insertChartUpdate(String doi, int lastNcites)
    throws DataAccessException {
        citeChartDAO.insertChartUpdate(doi, lastNcites);
    }  //- insertChartUpdate
    
    
    ///////////////////////////////////////////////////////
    //  Legacy ID DAO                               
    ///////////////////////////////////////////////////////

    
    public String getNewID(int legacyID) throws DataAccessException {
        return legacyIDDAO.getNewID(legacyID);
    }  //- getNewID
    
    
    public void insertLegacyIDMapping(String csxID, int legacyID)
    throws DataAccessException {
        legacyIDDAO.insertLegacyIDMapping(csxID, legacyID);
    }  //- insertLegacyIDMapping


    ///////////////////////////////////////////////////////
    //  Tag DAO                               
    ///////////////////////////////////////////////////////

    
    public void addTag(String paperid, String tag) throws DataAccessException {
        tagDAO.addTag(paperid, tag);
    }  //- addTag
    
    
    public void deleteTag(String paperid, String tag) throws DataAccessException {
        tagDAO.deleteTag(paperid, tag);
    }  //- deleteTag
    
    
    public List getTags(String paperid) throws DataAccessException {
        return tagDAO.getTags(paperid);
    }  //- getTags
    
    
    ///////////////////////////////////////////////////////
    //  Admin DAO                               
    ///////////////////////////////////////////////////////

        
    public void setBanner(String banner) throws DataAccessException {
        adminDAO.setBanner(banner);
    }  //- setBanner
    

    public String getBanner() throws DataAccessException {
        return adminDAO.getBanner();
    }  //- getBanner    
    
}  //- class CSXDAOImpl
