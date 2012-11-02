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
package edu.psu.citeseerx.domain;

import java.util.*;
import java.io.*;

import org.jdom.*;
import org.jdom.input.*;

import edu.psu.citeseerx.utility.SafeText;

/**
 * SourceableDataObject container for document metadata.  Supported data keys
 * include:
 * <ul>
 * <li>DOI_KEY - the ID of this document.</li>
 * <li>TITLE_KEY</li>
 * <li>ABSTRACT_KEY</li>
 * <li>YEAR_KEY</li>
 * <li>VENUE_KEY</li>
 * <li>VEN_TYPE_KEY</li>
 * <li>PAGES_KEY</li>
 * <li>VOL_KEY</li>
 * <li>NUM_KEY</li>
 * <li>PUBLISHER_KEY</li>
 * <li>PUBADDR_KEY</li>
 * <li>TECH_KEY</li>
 * </ul>
 *
 * @author Isaac Councill
 * @version $Rev: 668 $ $Date: 2008-07-27 15:25:51 -0400 (Sun, 27 Jul 2008) $
 */
public class Document extends SourceableDataObject
implements Clusterable, XMLSerializable, XMLTagAttrConstants, Versionable {

    public static final String DOC_ROOT         = "document";
    
    public static final String DOI_KEY          = "doi";
    public static final String TITLE_KEY        = "title";
    public static final String ABSTRACT_KEY     = "abstract";
    public static final String YEAR_KEY         = "year";
    public static final String VENUE_KEY        = "venue";
    public static final String VEN_TYPE_KEY     = "venType";
    public static final String PAGES_KEY        = "pages";
    public static final String VOL_KEY          = "volume";
    public static final String NUM_KEY          = "number";
    public static final String PUBLISHER_KEY    = "publisher";
    public static final String PUBADDR_KEY      = "pubAddress";
    public static final String TECH_KEY         = "tech";
    public static final String KEYWORDS_KEY     = "keywords";
    public static final String AUTHORS_KEY      = "authors";
    public static final String CITES_KEY        = "citations";
    public static final String ACKS_KEY         = "acknowledgments";
    public static final String FILEINFO_KEY     = "fileInfo";

    protected static final String[] fieldArray =
    {
        CLUST_KEY,TITLE_KEY,ABSTRACT_KEY,YEAR_KEY,VENUE_KEY,VEN_TYPE_KEY,
        PAGES_KEY,VOL_KEY,NUM_KEY,PUBLISHER_KEY,PUBADDR_KEY,TECH_KEY,
        KEYWORDS_KEY,AUTHORS_KEY,CITES_KEY,ACKS_KEY,FILEINFO_KEY
    };
    
    
    protected int version = 0;
    
    public int getVersion() {
        return version;
    }
    
    public void setVersion(int version) {
        this.version = version;
    }
    
    
    protected String versionName;
    
    public String getVersionName() {
        return versionName;
    }
    
    public void setVersionName(String name) {
        versionName = name;
    }
    
    
    protected Date versionTime;
    
    public Date getVersionTime() {
        return versionTime;
    }
    
    public void setVersionTime(Date versionTime) {
        this.versionTime = versionTime;
    }
    
    
    protected String versionRepID;
    
    public String getVersionRepID() {
        return versionRepID;
    }
    
    public void setVersionRepID(String repID) {
        versionRepID = repID;
    }
    
    
    protected String versionPath;
    
    public String getVersionPath() {
        return versionPath;
    }
    
    public void setVersionPath(String path) {
        versionPath = path;
    }
    
    
    protected boolean versionDeprecated = false;
    
    public boolean isDeprecatedVersion() {
        return versionDeprecated;
    }
    
    public void setVersionDeprecated(boolean isDeprecated) {
        versionDeprecated = isDeprecated;
    }
    
    
    protected boolean versionSpam = false;
    
    public boolean isSpamVersion() {
        return versionSpam;
    }
    
    public void setVersionSpam(boolean isSpam) {
        versionSpam = isSpam;
    }
    
    
    protected boolean reindex = true;
    protected boolean isPublic = true;
    
    public boolean flaggedForIndexing() {
        return reindex;
    }
    
    public void setIndexFlag(boolean flag) {
        reindex = flag;
    }
    
    public boolean isPublic() {
        return isPublic;
    }
    
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
    
    
    private int ncites = 0;
    
    public int getNcites() {
        return ncites;
    }
    
    public void setNcites(int ncites) {
        this.ncites = ncites;
    }
    
    
    private int selfCites = 0;
    
    public int getSelfCites() {
        return selfCites;
    }
    
    public void setSelfCites(int selfCites) {
        this.selfCites = selfCites;
    }
    
    
    public Document() {
        super();
        for (int i=0; i<privateFieldData.length; i++) {
            addPrivateField(privateFieldData[i]);
        }
    }
    
    
    public Long getClusterID() {
        String clustID = data.get(CLUST_KEY);
        if (clustID == null) {
            return new Long(0);
        } else {
            return Long.parseLong(clustID);
        }
        
    }  //- getClusterID

    public void setClusterID(Long id) {
        data.put(CLUST_KEY, id.toString());
    }
    
    public boolean isClustered() {
        return data.containsKey(CLUST_KEY);
    }
    
    
    protected DocumentFileInfo fileInfo = new DocumentFileInfo();
    
    public DocumentFileInfo getFileInfo() {
        return fileInfo;
    }
    
    public void setFileInfo(DocumentFileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }
    

    protected List<Author> authors     = new ArrayList<Author>();
    protected List<Keyword> keywords    = new ArrayList<Keyword>();
    protected List<Citation> citations = new ArrayList<Citation>();
    protected List<Acknowledgment> acknowledgments =
        new ArrayList<Acknowledgment>();
    protected List<Tag> tags = new ArrayList<Tag>();
    
    public List<Author> getAuthors() {
        return authors;
    }
    
    public void addAuthor(Author author) {
        authors.add(author);
    }
    
    public void setAuthors(ArrayList<Author> authors) {
        this.authors = authors;
    }
    
    public List<Keyword> getKeywords() {
        return keywords;
    }
    
    public void addKeyword(Keyword keyword) {
        keywords.add(keyword);
    }
    
    public void setKeywords(ArrayList<Keyword> keywords) {
        this.keywords = keywords;
    }
    
    public List<Citation> getCitations() {
        return citations;
    }
    
    public void addCitation(Citation citation) {
        citations.add(citation);
    }
    
    public List<Acknowledgment> getAcknowledgments() {
        return acknowledgments;
    }
    
    public void addAcknowledgment(Acknowledgment ack) {
        acknowledgments.add(ack);
    }
    
    public List<Tag> getTags() {
        return tags;
    }
    
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
    
    
    public String toXML(boolean sysData) {
        StringBuffer xml = new StringBuffer();
        buildXML(xml, sysData);
        return xml.toString();
        
    }  //- toXML
    
    
    public void toXML(OutputStream out, boolean sysData) throws IOException {
        StringBuffer xml = new StringBuffer();
        buildXML(xml, sysData);
        out.write(xml.toString().getBytes("utf-8"));

        
    }  //- toXML(OutputStream)
    
    
    public void fromXML(InputStream xmlin) throws IOException {
        SAXBuilder builder = new SAXBuilder();
        try {
            org.jdom.Document doc = builder.build(xmlin);
            Element root = doc.getRootElement();
            fromXML(root);
            
        } catch (JDOMException e) {
            e.printStackTrace();
        }
        
    }  //- fromXML
    
    
    public void fromXML(Element root) throws JDOMException {
        if (!root.getName().equals(DOC_ROOT)) {
            throw new JDOMException("Invalid root \'"+root.getName()+
                    "\', expected \'"+DOC_ROOT+"\'");
        }
        setDatum(DOI_KEY, root.getAttributeValue(ID_ATTR));
        List rootChildren = root.getChildren();
        for (Iterator it = rootChildren.iterator(); it.hasNext(); ) {
            Element child = (Element)it.next();
            if (child.getName().equals(AUTHORS_KEY)) {
                List authorElts = child.getChildren();
                for (Iterator authiter = authorElts.iterator();
                authiter.hasNext(); ) {
                    Element authElt = (Element)authiter.next();
                    Author author = new Author();
                    author.fromXML(authElt);
                    addAuthor(author);
                }
                continue;
            }
            if (child.getName().equals(CITES_KEY)) {
                List citeElts = child.getChildren();
                String src = child.getAttributeValue(SRC_ATTR);
                if (src != null) {
                    setSource(CITES_KEY, src);
                }
                for (Iterator citeiter = citeElts.iterator();
                citeiter.hasNext(); ) {
                    Element citeElt = (Element)citeiter.next();
                    Citation citation = new Citation();
                    citation.fromXML(citeElt);
                    addCitation(citation);
                }
                continue;
            }
            if (child.getName().equals(KEYWORDS_KEY)) {
                List keywordElts = child.getChildren();
                for (Iterator keyiter = keywordElts.iterator();
                keyiter.hasNext(); ) {
                    Element keyElt = (Element)keyiter.next();
                    Keyword keyword = new Keyword();
                    keyword.fromXML(keyElt);
                    addKeyword(keyword);
                }
                continue;
            }
            if (child.getName().equals(ACKS_KEY)) {
                List ackElts = child.getChildren();
                for (Iterator ackiter = ackElts.iterator();
                ackiter.hasNext(); ) {
                    Element ackElt = (Element)ackiter.next();
                    Acknowledgment ack = new Acknowledgment();
                    ack.fromXML(ackElt);
                    addAcknowledgment(ack);
                }
                continue;
            }
            if (child.getName().equals(FILEINFO_KEY)) {
                DocumentFileInfo fileInfo = new DocumentFileInfo();
                fileInfo.fromXML(child);
                setFileInfo(fileInfo);
                continue;
            }
            String key = child.getName();
            String src = child.getAttributeValue(SRC_ATTR);
            String val = SafeText.decodeHTMLSpecialChars(child.getValue());
            setDatum(key, val);
            if (src != null) {
                setSource(key, src);
            }
        }
        
    }  //- fromXML(Element)
    
    
    public void buildXML(StringBuffer xml, boolean sysData) {
        xml.append("<"+DOC_ROOT+" "+ID_ATTR+"=\""+
                getDatum(DOI_KEY, UNENCODED)+"\">");
        for (int i=0; i<fieldArray.length; i++) {
            String field = fieldArray[i];
            if (!sysData && privateFields.containsKey(field)) {
                continue;
            }
            if (field.equals(AUTHORS_KEY)) {
                xml.append("<"+AUTHORS_KEY+">");
                for (Iterator<Author> it = authors.iterator(); it.hasNext(); ) {
                    it.next().buildXML(xml, sysData);
                }
                xml.append("</"+AUTHORS_KEY+">");
                continue;
            }
            if (field.equals(CITES_KEY) && !citations.isEmpty()) {
                if (hasSourceData(CITES_KEY)) {
                    xml.append("<"+CITES_KEY+" "+SRC_ATTR+"=\""+
                            getSource(CITES_KEY)+"\">");
                } else {
                    xml.append("<"+CITES_KEY+">");
                }
                for (Iterator<Citation> it = citations.iterator();
                it.hasNext(); ) {
                    it.next().buildXML(xml, sysData);
                }
                xml.append("</"+CITES_KEY+">");
                continue;
            }
            if (field.equals(ACKS_KEY) && !acknowledgments.isEmpty()) {
                xml.append("<"+ACKS_KEY+">");
                for (Iterator<Acknowledgment> it = acknowledgments.iterator();
                it.hasNext(); ) {
                    it.next().buildXML(xml, sysData);
                }
                xml.append("</"+ACKS_KEY+">");
                continue;
            }
            if (field.equals(KEYWORDS_KEY)) {
                xml.append("<"+KEYWORDS_KEY+">");
                for (Iterator<Keyword> it = keywords.iterator();
                it.hasNext(); ) {
                    it.next().buildXML(xml, sysData);
                }
                xml.append("</"+KEYWORDS_KEY+">");
                continue;
            }
            if (field.equals(FILEINFO_KEY) && fileInfo != null) {
                fileInfo.buildXML(xml, sysData);
                continue;
            }
            if (getDatum(field, ENCODED) == null) {
                continue;
            }
            if (hasSourceData(field)) {
                xml.append("<"+field+" "+SRC_ATTR+"=\""+getSource(field)+"\">");
            } else {
                xml.append("<"+field+">");
            }
            xml.append(getDatum(field, ENCODED));
            xml.append("</"+field+">");
        }
        xml.append("</"+DOC_ROOT+">");

    }  //- buildXML
    
    
    public boolean sameAuthors(Document doc) {
        if (getAuthors().size() != doc.getAuthors().size()) {
            return false;
        }
        for (int i=0; i<getAuthors().size(); i++) {
            if (!getAuthors().get(i).equals(doc.getAuthors().get(i))) {
                return false;
            }
        }
        return true;
        
    }  //- sameAuthors
    
    
}  //- class Document
