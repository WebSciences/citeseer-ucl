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
package edu.psu.citeseerx.ingestion.datamappers;

import java.io.*;
import java.util.*;

import org.jdom.*;
import org.jdom.input.SAXBuilder;

/**
 * Utilities for creating CiteSeerX domain objects from XML source.
 *
 * @author Isaac Councill
 * @version $Rev: 665 $ $Date: 2008-07-27 13:10:35 -0400 (Sun, 27 Jul 2008) $
 */
public class BatchMapper {


    /**
     * Creates a Document object based on an InputStream from an XML file. 
     * @param in
     * @return
     * @throws MappingException
     */
    public static edu.psu.citeseerx.domain.Document map(InputStream in)
    throws MappingException {
        
        SAXBuilder builder = new SAXBuilder();

        try {
            org.jdom.Document xmldoc = builder.build(in);
            Element root = xmldoc.getRootElement();
            edu.psu.citeseerx.domain.Document doc = map(root);
            return doc;
            
        } catch (IOException e) {
            MappingException exc = new MappingException(e.getMessage());
            exc.setStackTrace(e.getStackTrace());
            throw exc;
        } catch (JDOMException e) {
            MappingException exc = new MappingException(e.getMessage());
            exc.setStackTrace(e.getStackTrace());
            throw exc;
        }   
        
    }  //- map
        

    /**
     * Creates a Document object from the specified root element of
     * a preparsed DOM tree.
     * @param root
     * @return
     * @throws MappingException
     */
    public static edu.psu.citeseerx.domain.Document map(Element root)
    throws MappingException {
        
        edu.psu.citeseerx.domain.Document doc =
            new edu.psu.citeseerx.domain.Document();
        
        if (!root.getName().equals("document")) {
            throw new MappingException("Expected 'document' root element, " +
                    "found " + root.getName());
        }
        String doi = root.getAttributeValue("id");
        doc.setDatum(edu.psu.citeseerx.domain.Document.DOI_KEY, doi);
        
        List children = root.getChildren();
        for (Iterator it = children.iterator(); it.hasNext(); ) {
            Element child = (Element)it.next();
            if (child.getName().equals("fileInfo")) {
                FileInfoMapper.map(doc, child);
            }
            if (child.getName().equals("algorithm")) {
                if (child.getAttributeValue("name").equals("SVM HeaderParse")) {
                    ParsHedMapper.map(doc, child);
                }
                if (child.getAttributeValue("name").equals("ParsCit")) {
                    ParscitMapper.map(doc, child);
                }
            }
        }
        return doc;
        
    }  //- map
        
    
}  //- class BatchMapper
