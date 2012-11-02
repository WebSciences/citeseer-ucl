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
import java.io.IOException;
import edu.psu.citeseerx.domain.Document;

public interface CSXOperations {

    public void insertDocumentEntry(Document doc) throws DataAccessException;

    public void importDocument(Document doc)
        throws DataAccessException, IOException;

    public Document getDocumentFromDB(String doi, boolean getCitations,
            boolean getContexts, boolean getSource, boolean getAcks,
            boolean getKeywords, boolean getTags) throws DataAccessException;

    public Document getDocumentFromDB(String doi, boolean getContexts,
            boolean getSource) throws DataAccessException;

    public Document getDocumentFromDB(String doi) throws DataAccessException;

    public Document getDocumentFromXML(String doi)
        throws DataAccessException, IOException;

    public void updateDocumentData(Document doc)
    throws DataAccessException, IOException;
    
    public void updateDocumentData(Document doc, boolean updateAuthors,
            boolean updateCitations,
            boolean updateAcknowledgements, boolean updateKeywords)
        throws DataAccessException, IOException;
    
}
