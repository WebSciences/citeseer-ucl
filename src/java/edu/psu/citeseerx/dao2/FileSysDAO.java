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
package edu.psu.citeseerx.dao2;

import java.io.IOException;
import java.io.FileInputStream;
import org.springframework.dao.DataAccessException;

import edu.psu.citeseerx.domain.*;
import java.util.List;

/**
 * File access methods.
 *
 * @author Isaac Councill
 * @version $Rev: 671 $ $Date: 2008-07-27 16:30:52 -0400 (Sun, 27 Jul 2008) $
 */
public interface FileSysDAO {

    /**
     * Build a Document object from the XML file located at relPath, a path
     * relative to the root of the repository specified by repID.
     * @param repID
     * @param relPath
     * @return
     * @throws IOException
     */
    public Document getDocFromXML(String repID, String relPath)
    throws IOException;
    
    /**
     * Writes an XML representation of the specified document to
     * persistent storage.
     * @param doc
     * @throws IOException
     */
    public void writeXML(Document doc) throws IOException;
    
    /**
     * Retrieve a Document object at the version specified.  If the version
     * does not exist, a null value will be returned.
     * @param doi
     * @param version
     * @return
     * @throws DataAccessException
     * @throws IOException
     */
    public Document getDocVersion(String doi, int version) throws IOException;

    /**
     * Retrieve a Document object based on the named version specified.  If
     * the named version does not exist, a null value will be returned.
     * @param doi
     * @param name
     * @return
     * @throws DataAccessException
     * @throws IOException
     */
    public Document getDocVersion(String doi, String name) throws IOException;
    
    /**
     * Stores version data for a Document object.  The version number and name
     * will be read from the Document.
     * @param doc
     * @throws IOException
     */
    public void writeVersion(Document doc) throws IOException;
    
    /**
     * Retrieves a FileInputStream for a file with the specified extension
     * (type) located on the specified repository.
     * @param doi the ID of the document whose file is to be retrieved.
     * @param repID the ID of the repository on which to search.
     * @param type the file extension.
     * @return
     * @throws IOException
     */
    public FileInputStream getFileInputStream(String doi, String repID,
            String type) throws IOException;
    
    /**
     * Returns a list of file extensions available for a specific document
     * within a specific repository. 
     * @param doi
     * @param repID
     * @return
     * @throws IOException
     */
    public List<String> getFileTypes(String doi, String repID)
    throws IOException;

    /**
     * Return the ID of the repository on which a given document is stored. 
     * @param doi
     * @return
     */
    public String getRepositoryID(String doi);
    
}
