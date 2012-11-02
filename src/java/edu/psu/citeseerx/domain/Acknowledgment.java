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

import org.jdom.*;

import edu.psu.citeseerx.utility.SafeText;

/**
 * SourceableDataObject container for acknowledgement metadata.  Supported
 * data keys include:
 * <ul>
 * <li>DOI_KEY - the acknowledgement ID.</li>
 * <li>NAME_KEY - the name of the acknowledged entity.</li>
 * <li>ENT_TYPE_KEY - the entity class.</li>
 * <li>ACK_TYPE_KEY - the acknowledgement class.</li>
 * </ul>
 *
 * @author Isaac Councill
 * @version $Rev: 668 $ $Date: 2008-07-27 15:25:51 -0400 (Sun, 27 Jul 2008) $
 */
public class Acknowledgment extends SourceableDataObject
implements Clusterable, XMLSerializable, XMLTagAttrConstants {

    public static final String ACK_ROOT        = "acknowledgment";
    
    public static final String DOI_KEY         = "id";
    public static final String NAME_KEY        = "name";
    public static final String ENT_TYPE_KEY    = "entityType";
    public static final String ACK_TYPE_KEY    = "ackType";
    public static final String CONTEXT_KEY     = "contexts";
    
    protected final String[] fieldArray =
    {
            CLUST_KEY,NAME_KEY,ENT_TYPE_KEY,ACK_TYPE_KEY,CONTEXT_KEY
    };
    
    
    public Acknowledgment() {
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

    public void setClusterID(Long clusterID) {
        data.put(CLUST_KEY, clusterID.toString());
    }
    
    public boolean isClustered() {
        return data.containsKey(CLUST_KEY);
    }

    
    private ArrayList<String> contexts = new ArrayList<String>();

    public void addContext(String context) {
        contexts.add(context);
    }
    
    public List<String> getContexts() {
        return contexts;
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
        throw new IOException("Method not implemented");
        
    }  //- fromXML
    
    
    public void fromXML(Element root) throws JDOMException {
        if (!root.getName().equals(ACK_ROOT)) {
            throw new JDOMException("Invalid root \'"+root.getName()+
                    "\', expected \'"+ACK_ROOT+"\'");
        }
        String id = root.getAttributeValue(ID_ATTR);
        if (id != null) {
            setDatum(DOI_KEY, id);
        }
        List children = root.getChildren();
        for (Iterator it = children.iterator(); it.hasNext(); ) {
            Element child = (Element)it.next();
            if (child.getName().equals(CONTEXT_KEY)) {
                List contextElts = child.getChildren();
                for (Iterator cit = contextElts.iterator(); cit.hasNext(); ) {
                    Element cElt = (Element)cit.next();
                    String context =
                        SafeText.decodeHTMLSpecialChars(cElt.getValue());
                    addContext(context);
                }
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
        xml.append("<"+ACK_ROOT+" "+ID_ATTR+"=\""+
                getDatum(DOI_KEY, UNENCODED)+"\">");
        for (int i=0; i<fieldArray.length; i++) {
            String field = fieldArray[i];
            if (!sysData && privateFields.containsKey(field)) {
                continue;
            }
            if (field.equals(CONTEXT_KEY) && !contexts.isEmpty()) {
                xml.append("<"+CONTEXT_KEY+">");
                for (Iterator<String> it = contexts.iterator();
                it.hasNext(); ) {
                    xml.append("<"+CONTEXT_TAG+">");
                    xml.append(SafeText.cleanXML(it.next()));
                    xml.append("</"+CONTEXT_TAG+">");
                }
                xml.append("</"+CONTEXT_KEY+">");
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
        xml.append("</"+ACK_ROOT+">");

    }  //- buildXML
    
}  //- class Acknowledgment
