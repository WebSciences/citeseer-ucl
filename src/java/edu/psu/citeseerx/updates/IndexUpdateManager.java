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
package edu.psu.citeseerx.updates;

import java.sql.SQLException;
import java.io.*;

import java.util.*;
import java.net.*;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.dao2.logic.CiteClusterDAO;
import edu.psu.citeseerx.domain.*;
import edu.psu.citeseerx.utility.*;

/**
 * Utilities for updating a Solr index to be consistent with the csx_citegraph
 * cluster table within the storage backend.  This class reads in cluster
 * records and, if the cluster is marked to have a corresponding document
 * record within the citeseerx papers table, with read in the paper record
 * as well to create XML in the Solr update format and send the XML to
 * a Solr server.
 * <br><br>
 * The IndexUpdateManager maintains a timestamp of the last update within
 * the csx_citegraph database so that only records modified since the last
 * update will be processed.
 * <br><br>
 * Deletions are handled by reading in records marked for deletion within
 * the csx_citegraph deletions table.
 *
 * @author Isaac Councill
 * @version $Rev: 664 $ $Date: 2008-07-27 10:44:05 -0400 (Sun, 27 Jul 2008) $
 */
public class IndexUpdateManager {

    int maxTextLength = 28000;
    
    /**
     * Sets the maximum number of characters that will be indexed from
     * document full text.
     * @param maxTextLength (default 28000)
     */
    public void setMaxTextLength(int maxTextLength) {
        this.maxTextLength = maxTextLength;
    }
    
    
    private URL solrUpdateUrl;
    
    public void setSolrURL(String solrUpdateUrl) throws MalformedURLException {
        this.solrUpdateUrl = new URL(solrUpdateUrl);
    }
    
    
    private CSXDAO csxdao;
    
    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    }
    
    
    private CiteClusterDAO citedao;
    
    public void setCiteClusterDAO(CiteClusterDAO citedao) {
        this.citedao = citedao;
    }
    
    
    /**
     * Updates the index only for records that have corresponding document
     * records (document files within the CiteSeerX corpus).  This re-indexes
     * everything - not indexUpdateTime is recorded.
     * @throws IOException
     */
    public void indexInCollection() throws IOException {
        int counter = 0;
        int lastCommit = 0;
        Long lastID = new Long(0);
        
        ArrayList<ThinDoc> docsAdded = new ArrayList<ThinDoc>();
        
        boolean finished = false;

        while(true) {

            if (finished) {
                break;
            }
            List<ThinDoc> docs = new ArrayList<ThinDoc>();
            Date date = new Date(0);
            docs = citedao.getClustersInCollection(lastID, 1000);
            if (docs.isEmpty()) {
                break;
            }

            StringBuffer xmlBuffer = new StringBuffer();
            xmlBuffer.append("<add>");

            int nBatch = 0;
            
            for (ThinDoc doc : docs) {

                Long clusterid = doc.getCluster();
                lastID = clusterid;
                
                List<Long> cites = new ArrayList<Long>();
                List<Long> citedby = new ArrayList<Long>();                
                if (clusterid != null) {
                    cites = citedao.getCitedClusters(clusterid);
                    citedby = citedao.getCitingClusters(clusterid);
                }

                List<String> dois = citedao.getPaperIDs(clusterid);
                Document fullDoc = null;
                for (String doi : dois) {
                    fullDoc = csxdao.getDocumentFromDB(doi, false, false);
                    if (fullDoc.isPublic()) {
                        break;
                    }
                }
                if (fullDoc != null) {
                    fullDoc.setClusterID(clusterid);
                    fullDoc.setNcites(doc.getNcites());
                    buildDocEntry(fullDoc, cites, citedby, xmlBuffer);
                } else {
                    buildDocEntry(doc, cites, citedby, xmlBuffer);
                }
                docsAdded.add(doc);
                if (++nBatch>=200) {
                    xmlBuffer.append("</add>");
                    sendPost(xmlBuffer.toString());
                    xmlBuffer = new StringBuffer();
                    xmlBuffer.append("<add>");
                    nBatch = 0;
                }
                counter++;
                
            }
            if (nBatch>0) {
                xmlBuffer.append("</add>");
                sendPost(xmlBuffer.toString());
            }

            sendCommit();
            docsAdded.clear();
            lastCommit = counter;
            System.out.println("commit "+lastCommit);
        }
        sendOptimize();
        
    }  //- indexInCollection
    
    
    /**
     * Indexes all cluster records modified since the last update time.
     * @throws SQLException
     * @throws IOException
     */
    public void indexAll() throws SQLException, IOException {
        
        int counter = 0;
        int lastCommit = 0;
        Long lastID = new Long(0);
        Date lastUpdate = citedao.getLastIndexTime();
        Date currentTime = new Date(System.currentTimeMillis());
        
        ArrayList<ThinDoc> docsAdded = new ArrayList<ThinDoc>();
        
        while(true) {

            List<ThinDoc> docs = new ArrayList<ThinDoc>();
            Date date = new Date(0);
            docs = citedao.getClustersSinceTime(lastUpdate, lastID, 1000);
            if (docs.isEmpty()) {
                break;
            }

            StringBuffer xmlBuffer = new StringBuffer();
            xmlBuffer.append("<add>");

            int nBatch = 0;
            
            for (ThinDoc doc : docs) {

                Long clusterid = doc.getCluster();
                lastID = clusterid;
                
                List<Long> cites = new ArrayList<Long>();
                List<Long> citedby = new ArrayList<Long>();                
                if (clusterid != null) {
                    cites = citedao.getCitedClusters(clusterid);
                    citedby = citedao.getCitingClusters(clusterid);
                }

                if (doc.getInCollection()) {
                    List<String> dois = citedao.getPaperIDs(clusterid);
                    Document fullDoc = null;
                    for (String doi : dois) {
                        fullDoc = csxdao.getDocumentFromDB(doi, false, false);
                        if (fullDoc.isPublic()) {
                            break;
                        }
                    }
                    if (fullDoc != null) {
                        fullDoc.setClusterID(clusterid);
                        fullDoc.setNcites(doc.getNcites());
                        buildDocEntry(fullDoc, cites, citedby, xmlBuffer);
                    }
                    
                } else {
                    buildDocEntry(doc, cites, citedby, xmlBuffer);
                }
                docsAdded.add(doc);
                if (++nBatch>=200) {
                    xmlBuffer.append("</add>");
                    sendPost(xmlBuffer.toString());
                    xmlBuffer = new StringBuffer();
                    xmlBuffer.append("<add>");
                    nBatch = 0;
                }
                counter++;
                
            }
            if (nBatch>0) {
                xmlBuffer.append("</add>");
                sendPost(xmlBuffer.toString());
            }

            sendCommit();
            docsAdded.clear();
            lastCommit = counter;
            System.out.println("commit "+lastCommit);
        }
        
        processDeletions(citedao.getDeletions(currentTime));
        citedao.removeDeletions(currentTime);
        citedao.setLastIndexTime(currentTime);
        
        sendOptimize();
                
    }  //- indexAll
    
    
    /**
     * Builds a record in Solr update syntax corresponding to the
     * supplied parameters.
     * @param doc
     * @param cites
     * @param citedby
     * @param buffer
     * @throws IOException
     */
    private void buildDocEntry(Document doc, List<Long> cites,
            List<Long> citedby, StringBuffer buffer) throws IOException {

        String id = doc.getClusterID().toString();
        String doi = doc.getDatum(Document.DOI_KEY, Document.ENCODED);
        String title = doc.getDatum(Document.TITLE_KEY, Document.ENCODED);
        String venue = doc.getDatum(Document.VENUE_KEY, Document.ENCODED);
        String ventype = doc.getDatum(Document.VEN_TYPE_KEY, Document.ENCODED);
        String year = doc.getDatum(Document.YEAR_KEY, Document.ENCODED);
        String abs = doc.getDatum(Document.ABSTRACT_KEY, Document.ENCODED);
        String pages = doc.getDatum(Document.PAGES_KEY, Document.ENCODED);
        String publ = doc.getDatum(Document.PUBLISHER_KEY, Document.ENCODED);
        String vol = doc.getDatum(Document.VOL_KEY, Document.ENCODED);
        String num = doc.getDatum(Document.NUM_KEY, Document.ENCODED);
        String tech = doc.getDatum(Document.TECH_KEY, Document.ENCODED);
        String text = getText(doc);
        long vtime = (doc.getVersionTime() != null) ?
                doc.getVersionTime().getTime() : 0;
        
        int ncites = doc.getNcites();
        int scites = doc.getSelfCites();
        
        List<Keyword> keys = doc.getKeywords();
        ArrayList<String> keywords = new ArrayList<String>();
        for (Keyword key : keys) {
            keywords.add(key.getDatum(Keyword.KEYWORD_KEY, Keyword.ENCODED));
        }
        
        List<Author> authors = doc.getAuthors();
        ArrayList<String> authorNames = new ArrayList<String>();
        ArrayList<String> authorAffils = new ArrayList<String>();
        for (Author author  : authors) {
            String name = author.getDatum(Author.NAME_KEY, Author.ENCODED);
            String affil = author.getDatum(Author.AFFIL_KEY, Author.ENCODED);
            if (name != null) {
                authorNames.add(name);
            }
            if (affil != null) {
                authorAffils.add(affil);
            }
        }
        List<String> authorNorms = buildAuthorNorms(authorNames);
        
        String url = null;
        DocumentFileInfo finfo = doc.getFileInfo();
        if (finfo != null && finfo.getUrls().size() > 0) {
            url = SafeText.cleanXML(finfo.getUrls().get(0));
        }
        
        
        StringBuffer citesBuffer = new StringBuffer();
        for (Iterator<Long> cids = cites.iterator(); cids.hasNext(); ) {
            citesBuffer.append(cids.next());
            if (cids.hasNext()) {
                citesBuffer.append(" ");
            }
        }
        
        StringBuffer citedbyBuffer = new StringBuffer();
        for (Iterator<Long> cids = citedby.iterator(); cids.hasNext(); ) {
            citedbyBuffer.append(cids.next());
            if (cids.hasNext()) {
                citedbyBuffer.append(" ");
            }
        }
        
        buffer.append("<doc>");

        buffer.append("<field name=\"id\">");
        buffer.append(id);
        buffer.append("</field>");

        if (doi != null) {
            buffer.append("<field name=\"doi\">");
            buffer.append(doi);
            buffer.append("</field>");

            buffer.append("<field name=\"incol\">");
            buffer.append(1);
            buffer.append("</field>");
        } else {
            buffer.append("<field name=\"incol\">");
            buffer.append(0);
            buffer.append("</field>");
        }

        if (title != null) {
            buffer.append("<field name=\"title\">");
            buffer.append(title);
            buffer.append("</field>");
        }

        if (venue != null) {
            buffer.append("<field name=\"venue\">");
            buffer.append(venue);
            buffer.append("</field>");
        }

        if (ventype != null) {
            buffer.append("<field name=\"ventype\">");
            buffer.append(ventype);
            buffer.append("</field>");
        }

        if (abs != null) {
            buffer.append("<field name=\"abstract\">");
            buffer.append(abs);
            buffer.append("</field>");
        }
        
        if (pages != null) {
            buffer.append("<field name=\"pages\">");
            buffer.append(pages);
            buffer.append("</field>");
        }

        if (publ != null) {
            buffer.append("<field name=\"publisher\">");
            buffer.append(publ);
            buffer.append("</field>");
        }

        if (vol != null) {
            buffer.append("<field name=\"vol\">");
            buffer.append(vol);
            buffer.append("</field>");
        }

        if (num != null) {
            buffer.append("<field name=\"num\">");
            buffer.append(num);
            buffer.append("</field>");
        }

        if (tech != null) {
            buffer.append("<field name=\"tech\">");
            buffer.append(tech);
            buffer.append("</field>");
        }

        if (url != null) {
            buffer.append("<field name=\"url\">");
            buffer.append(url);
            buffer.append("</field>");
        }
        
        buffer.append("<field name=\"ncites\">");
        buffer.append(ncites);
        buffer.append("</field>");
        
        buffer.append("<field name=\"scites\">");
        buffer.append(scites);
        buffer.append("</field>");
        
        try {
            int year_i = Integer.parseInt(year);
            buffer.append("<field name=\"year\">");
            buffer.append(year_i);
            buffer.append("</field>");
        } catch (Exception e) { }
        
        for (String keyword : keywords) {
            buffer.append("<field name=\"keyword\">");
            buffer.append(keyword);
            buffer.append("</field>");
        }
        
        for (String name : authorNames) {
            buffer.append("<field name=\"author\">");
            buffer.append(name);
            buffer.append("</field>");
        }

        for (String norm : authorNorms) {
            buffer.append("<field name=\"authorNorms\">");
            buffer.append(norm);
            buffer.append("</field>");
        }

        for (String affil : authorAffils) {
            buffer.append("<field name=\"affil\">");
            buffer.append(affil);
            buffer.append("</field>");
        }
        
        for (Tag tag : doc.getTags()) {
            buffer.append("<field name=\"tag\">");
            buffer.append(SafeText.cleanXML(tag.getTag()));
            buffer.append("</field>");
        }
        if (text != null) {
            buffer.append("<field name=\"text\">");
            buffer.append(text);
            buffer.append("</field>");
        }
        
        buffer.append("<field name=\"cites\">");
        buffer.append(citesBuffer.toString());
        buffer.append("</field>");

        buffer.append("<field name=\"citedby\">");
        buffer.append(citedbyBuffer.toString());
        buffer.append("</field>");
        
        buffer.append("<field name=\"vtime\">");
        buffer.append(vtime);
        buffer.append("</field>");

        buffer.append("</doc>");

    }  //- buildDocEntry
    
    
    /**
     * Translates the supplied ThinDoc to a Document obejct and passes
     * control the the Document-based buildDocEntry method.
     * @param thinDoc
     * @param cites
     * @param citedby
     * @param buffer
     * @throws IOException
     */
    private void buildDocEntry(ThinDoc thinDoc, List<Long> cites,
            List<Long> citedby, StringBuffer buffer) throws IOException {

        Document doc = DomainTransformer.toDocument(thinDoc);
        buildDocEntry(doc, cites, citedby, buffer);
        
    }  //- buildDocEntry
    
    
    /**
     * Builds a list of author normalizations to create more flexible
     * author search.
     * @param names
     * @return
     */
    private static List<String> buildAuthorNorms(List<String> names) {

        HashSet<String> norms = new HashSet<String>();
        for (String name : names) {
            name = name.replaceAll("[^\\p{L} ]", "");
            StringTokenizer st = new StringTokenizer(name);
            String[] tokens = new String[st.countTokens()];
            int counter = 0;
            while(st.hasMoreTokens()) {
                tokens[counter] = st.nextToken();
                counter++;
            }
            norms.add(joinStringArray(tokens));
            
            if (tokens.length > 2) {

                String[] n1 = new String[tokens.length];
                for (int i=0; i<tokens.length; i++) {
                    if (i<tokens.length-1) {
                        n1[i] = Character.toString(tokens[i].charAt(0));
                    } else {
                        n1[i] = tokens[i];
                    }
                }
            
                String[] n2 = new String[tokens.length];
                for (int i=0; i<tokens.length; i++) {
                    if (i>0 && i<tokens.length-1) {
                        n2[i] = Character.toString(tokens[i].charAt(0));
                    } else {
                        n2[i] = tokens[i];
                    }
                }

                norms.add(joinStringArray(n1));
                norms.add(joinStringArray(n2));
            }
            
            if (tokens.length > 1) {

                String[] n3 = new String[2];
                n3[0] = tokens[0];
                n3[1] = tokens[tokens.length-1];
                
                String[] n4 = new String[2];
                n4[0] = Character.toString(tokens[0].charAt(0));
                n4[1] = tokens[tokens.length-1];
                
                norms.add(joinStringArray(n3));
                norms.add(joinStringArray(n4));
            }
        }
        ArrayList<String> normList = new ArrayList<String>();
        for (Iterator<String> it = norms.iterator(); it.hasNext(); ) {
            normList.add(it.next());
        }
        return normList;
        
    }  //- buildAuthorNorms
    
    
    private static String joinStringArray(String[] strings) {
        StringBuffer buffer = new StringBuffer();
        for (int i=0; i<strings.length; i++) {
            buffer.append(strings[i]);
            if (i<strings.length-1) {
                buffer.append(" ");
            }
        }
        return buffer.toString();
        
    }  //- joinStringArray
    
    
    /**
     * Fetches the full text of a document from the filesystem repository.
     * @param doc
     * @return
     * @throws IOException
     */
    private String getText(Document doc) throws IOException {
        
        String doi = doc.getDatum(Document.DOI_KEY);
        if (doi == null) {
            return null;
        }
        FileInputStream ins = null;
        BufferedReader reader = null;
        try {
            ins = csxdao.getFileInputStream(doi,
                    doc.getFileInfo().getDatum(DocumentFileInfo.REP_ID_KEY),
            "body");
        } catch (IOException e) { }
        if (ins == null) {
            ins = csxdao.getFileInputStream(doi,
                    doc.getFileInfo().getDatum(DocumentFileInfo.REP_ID_KEY),
            "txt"); 
        }
        try {
            reader = new BufferedReader(new InputStreamReader(ins, "UTF-8"));
            char[] buffer = new char[maxTextLength];
            int nread = reader.read(buffer, 0, buffer.length-1);
            if (nread == buffer.length) {
                for (int j=buffer.length-1; j>=0; j--) {
                    if (buffer[j] == ' ') {
                        break;
                    } else {
                        buffer[j] = ' ';
                    }
                }
            }
            String text = new String(buffer);
            text = SafeText.cleanXML(text);

            return text;
            
        } catch (IOException e) {
            throw(e);
        } finally {
            try { reader.close(); } catch (Exception e) { }
        }
            
    }  //- getText
    
    
    private void sendCommit() throws IOException {
        sendPost("<commit waitFlush=\"false\" waitSearcher=\"false\"/>");
    }
    
    private void sendOptimize() throws IOException {
        sendPost("<optimize/>");
    }
    
    private void sendPost(String str) throws IOException {

        HttpURLConnection conn =
            (HttpURLConnection)solrUpdateUrl.openConnection();
        try {
            conn.setRequestMethod("POST");
        } catch (ProtocolException e) { /* unlikely... */ }
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setAllowUserInteraction(false);
        conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
        
        Writer wr = new OutputStreamWriter(conn.getOutputStream());
        
        try {
            pipe(new StringReader(str), wr);
        } catch (IOException e) {
            throw(e);
        } finally {
            try { wr.close(); } catch (Exception e) { }
        }
        
        Reader reader = new InputStreamReader(conn.getInputStream());
        try {
            StringWriter output = new StringWriter();
            pipe(reader,output);
            checkExpectedResponse(output.toString());
        } catch (IOException e) {
            throw(e);
        } finally {
            try { reader.close(); } catch (Exception e) { }
        }
        
    }  //- sendPost
    
    
    private static void pipe(Reader reader, Writer writer) throws IOException {
        char[] buf = new char[1024];
        int read = 0;
        while ((read = reader.read(buf)) >= 0) {
            writer.write(buf, 0, read);
        }
        writer.flush();
        
    }  //- pipe
    
    
    private static final String expectedResponse =
        "<int name=\"status\">0</int>";
    
    private static void checkExpectedResponse(String response)
    throws IOException {
        if (response.indexOf(expectedResponse) < 0) {
            throw new IOException("Unexpected response from solr: "+response);
        }
    }
    
    
    private void processDeletions(List list) throws IOException {
        if (list.isEmpty()) {
            return;
        }
        for (Object o : list) {
            String del = "<delete><id>";
            del += (Long)o;
            del += "</id></delete>";
            sendPost(del);  // Have to send multiple posts due to Solr bug.
        }
        sendCommit();
        
    }  //- processDeletions
    
    
    /*
    public static void main(String[] args) throws Exception {
        
        DataSource dataSource = DBCPFactory.createDataSource("citeseerx");
        CSXDAO csxdao = new CSXDAO();
        csxdao.setDataSource(dataSource);
        
        DataSource cgDataSource = DBCPFactory.createDataSource("citegraph");
        CiteClusterDAO citedao = new CiteClusterDAOImpl();
        citedao.setDataSource(cgDataSource);

        DataSource cmDataSource = DBCPFactory.createDataSource("citemaster");
        CiteClusterDAO citemaster = new CiteClusterDAOImpl();
        citemaster.setDataSource(cmDataSource);

        IndexUpdateManager manager = new IndexUpdateManager();
        manager.setSolrURL("http://130.203.133.38:8983/solr/update");
        manager.setCSXDAO(csxdao);
        manager.setCiteClusterDAO(citemaster);
        manager.setCiteMaster(citemaster);
        manager.indexAll();
        
    }
    */
    
}  //- class IndexUpdateManager
