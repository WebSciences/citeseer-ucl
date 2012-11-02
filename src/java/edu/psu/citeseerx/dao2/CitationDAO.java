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

import org.springframework.dao.DataAccessException;
import java.util.List;
import edu.psu.citeseerx.domain.Citation;

public interface CitationDAO {

    public List getCitations(String docID, boolean getContexts)
    throws DataAccessException;
    
    public List getCitationsForCluster(Long clusterid)
    throws DataAccessException;
    
    public List getCitations(long startID, int n)
    throws DataAccessException;
    
    public Citation getCitation(long id) throws DataAccessException;
    
    public void insertCitation(String DOI, Citation citation)
    throws DataAccessException;
    
    public List getCiteContexts(Long citationID) throws DataAccessException;
    
    public void insertCiteContexts(Long citationID, List<String> contexts)
    throws DataAccessException;
    
    public void setCiteCluster(Citation citation, Long clusterID)
    throws DataAccessException;
    
    public void deleteCitations(String DOI) throws DataAccessException;
    
    public void deleteCitation(Long citationID) throws DataAccessException;

    public void deleteCiteContexts(Long citationID) throws DataAccessException;
    
    /**
     * @return The number of citations
     * @throws DataAccessException
     */
    public Integer getNumberOfCitationsRecords() throws DataAccessException;
}
