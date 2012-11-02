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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import org.jdom.Element;
import org.jdom.JDOMException;

import edu.psu.citeseerx.utility.SafeText;

/**
 * MappedDataObject container for document file information.
 * Supported data keys:
 * <ul>
 * <li>CRAWL_DATE_KEY - the date on which the document was crawled.</li>
 * <li>REP_ID_KEY - the repository ID for where the document files reside.</li>
 * <li>CONV_TRACE_KEY - the file conversion trace generated when extracting text from the file.</li>
 * </ul>
 *
 * @author Isaac Councill
 * @version $Rev: 668 $ $Date: 2008-07-27 15:25:51 -0400 (Sun, 27 Jul 2008) $
 */
public class DocumentFileInfo extends MappedDataObject
implements XMLSerializable, XMLTagAttrConstants {
    
    public static final String FILEINFO_ROOT    = "fileInfo";
    
    public static final String CRAWL_DATE_KEY   = "crawldate";
    public static final String REP_ID_KEY       = "repID";
    public static final String CONV_TRACE_KEY   = "conversionTrace";

    protected static final String[] fieldArray =
    {
        CRAWL_DATE_KEY,REP_ID_KEY,CONV_TRACE_KEY
    };
    
    protected static final String[] privateFieldData =
    {
        REP_ID_KEY
    };
    
    
    public DocumentFileInfo() {
        super();
        for (int i=0; i<privateFieldData.length; i++) {
            addPrivateField(privateFieldData[i]);
        }
    }
    
    
    protected ArrayList<String> urls = new ArrayList<String>();
    
    public void addUrl(String url) {
        urls.add(url);
    }
    
    public List<String> getUrls() {
        return urls;
    }
    

    protected ArrayList<CheckSum> checkSums = new ArrayList<CheckSum>();
    
    public void addCheckSum(CheckSum checkSum) {
        checkSums.add(checkSum);
    }
    
    public List<CheckSum> getCheckSums() {
        return checkSums;
    }
    
    
    protected ArrayList<Hub> hubs = new ArrayList<Hub>();
    
    public void addHub(Hub hub) {
        hubs.add(hub);
    }
    
    public List<Hub> getHubs() {
        return hubs;
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
    
        
    }  //- fromXML
    
    
    public void fromXML(Element root) throws JDOMException {
        if (!root.getName().equals(FILEINFO_ROOT)) {
            throw new JDOMException("Invalid root \'"+root.getName()+
                    "\', expected \'"+FILEINFO_ROOT+"\'");
        }
        HashMap<String,Object> fieldMap = new HashMap<String,Object>();
        for (int i=0; i<fieldArray.length; i++) {
            fieldMap.put(fieldArray[i], null);
        }
        
        List children = root.getChildren();
        for (Iterator it = children.iterator(); it.hasNext(); ) {
            Element child = (Element)it.next();
            String key = child.getName();
            
            if (fieldMap.containsKey(key)) {
                String val = SafeText.decodeHTMLSpecialChars(child.getValue());
                setDatum(key, val);
            }
            
            if (key.equals("checkSums")) {
                List checkSumElts = child.getChildren();
                for (Iterator cit = checkSumElts.iterator(); cit.hasNext(); ) {
                    Element checkSumElt = (Element)cit.next();
                    CheckSum checkSum = new CheckSum();
                    List cfields = checkSumElt.getChildren();
                    for (Iterator cfieldit = cfields.iterator();
                    cfieldit.hasNext(); ) {
                        Element cfield = (Element)cfieldit.next();
                        if (cfield.getName().equals("fileType")) {
                            checkSum.setFileType(cfield.getValue());
                        }
                        if (cfield.getName().equals("sha1")) {
                            checkSum.setSha1(cfield.getValue());
                        }
                    }
                    addCheckSum(checkSum);
                }
            }
            if (key.equals("urls")) {
                List urlElts = child.getChildren();
                for (Object o : urlElts) {
                    Element urlElt = (Element)o;
                    if (urlElt.getName().equals("url")) {
                        addUrl(SafeText.decodeHTMLSpecialChars(
                                urlElt.getValue()));
                    }
                }
            }
        }
        
    }  //- fromXML(Element)
    
    
    public void buildXML(StringBuffer xml, boolean sysData) {
        xml.append("<"+FILEINFO_ROOT+">");
        for (int i=0; i<fieldArray.length; i++) {
            String field = fieldArray[i];
            if (!sysData && privateFields.containsKey(field)) {
                continue;
            }
            if (getDatum(field, ENCODED) == null) {
                continue;
            }
            xml.append("<"+field+">");
            xml.append(getDatum(field, ENCODED));
            xml.append("</"+field+">");
        }
        if (!urls.isEmpty()) {
            xml.append("<urls>");
            for (String url : getUrls()) {
                xml.append("<url>");
                xml.append(SafeText.cleanXML(url));
                xml.append("</url>");
            }
            xml.append("</urls>");
        }
        if (!checkSums.isEmpty()) {
            xml.append("<checkSums>");
            for (Iterator<CheckSum> it = checkSums.iterator(); it.hasNext(); ) {
                CheckSum checkSum = it.next();
                xml.append("<checkSum>");
                xml.append("<fileType>");
                xml.append(checkSum.getFileType());
                xml.append("</fileType>");
                xml.append("<sha1>");
                xml.append(checkSum.getSha1());
                xml.append("</sha1>");
                xml.append("</checkSum>");
            }
            xml.append("</checkSums>");
        }
        xml.append("</"+FILEINFO_ROOT+">");

    }  //- buildXML
    
}  //- class DocumentFileInfo
