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

import java.util.Date;
import java.util.List;

import edu.psu.citeseerx.domain.DOIInfo;
import edu.psu.citeseerx.domain.Document;

public interface DocumentDAO {

    public Document getDocument(String docID, boolean getSource)
    throws DataAccessException;
    
    public void insertDocument(Document doc) throws DataAccessException;

    public void insertDocumentSrc(Document doc) throws DataAccessException;

    public void updateDocument(Document doc) throws DataAccessException;
    
    public void setDocPublic(Document doc, boolean isPublic)
    throws DataAccessException;
    
    public void setDocCluster(Document doc, Long clusterID)
    throws DataAccessException;
    
    public void setDocNcites(Document doc, int ncites) throws DataAccessException;
    
    public Integer getNumberOfDocumentRecords() throws DataAccessException;
    
    public List getDOIs(String start, int amount) throws DataAccessException;
    
    /**
     * Returns all the DOIs between start and end beginning in
     * prev
     * @param start		Star date
     * @param end		End date
     * @param prev  	last id from previous call
     * @param amount	Number of records to be returned
     * @return
     * @throws DataAccessException
     */
    public List<DOIInfo> getSetDOIs(Date start, Date end, String prev,  
    		int amount) throws DataAccessException;
    
    /**
     * Returns the number of DOIs between start and end beginning in
     * prev
     * @param start
     * @param end
     * @param prev
     * @return
     * @throws DataAccessException
     */
    public Integer getSetDOICount(Date start, Date end, String prev) 
    throws DataAccessException;
}
