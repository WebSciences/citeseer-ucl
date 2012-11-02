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
import java.util.List;
import java.util.Date;

import edu.psu.citeseerx.domain.*;
import org.json.JSONException;

public interface CiteClusterDAO {

    public Long clusterDocument(List<String> key, Document doc)
    throws DataAccessException, JSONException;
    
    public Long reclusterDocument(List<String> keys, Document doc)
    throws DataAccessException, JSONException;
    
    public List getCitingDocuments(Long clusterid, int start, int amount)
    throws DataAccessException;
    
    public List getCitedDocuments(Long clusterid, int start, int amount)
    throws DataAccessException;
    
    public List<String> getCitingDocumentIDs(String paperid) 
    throws DataAccessException;
    
    public List<String> getCitedDocumentIDs(String paperid) 
    throws DataAccessException;
    
    public List getCitingClusters(Long clusterid) throws DataAccessException;

    public List getCitedClusters(Long clusterid) throws DataAccessException;

    public List getCitingClusters(Long clusterid, int amount)
    throws DataAccessException;

    public List getCitedClusters(Long clusterid, int amount)
    throws DataAccessException;

    public void deleteDocument(Document doc) throws DataAccessException;
    
    public void removeDeletion(Long clusterid) throws DataAccessException;
    
    public void removeDeletions(Date date);
    
    public List getDeletions(Date date);
    
    public List getClustersInCollection(Long start, int amount)
    throws DataAccessException;
    
    public List getPaperIDs(Long clusterid) throws DataAccessException;
    
    public List getCitationIDs(Long clusterid) throws DataAccessException;
    
    public ThinDoc getThinDoc(Long clusterid) throws DataAccessException;
    
    public List getClustersSinceTime(Date date, Long id, int amount)
    throws DataAccessException;
    
    public void updateCluster(ThinDoc cluster, boolean changed)
    throws DataAccessException;
    
    public String getContext(Long citing, Long cited)
    throws DataAccessException;
    
    public Long getMinClusterID() throws DataAccessException;
    
    public Long getMaxClusterID() throws DataAccessException;
    
    public Date checkInfUpdateRequired(Long cid) throws DataAccessException;
    
    public void insertInfUpdateTime(Long cid, Date time)
    throws DataAccessException;
    
    public void setLastIndexTime(Date time);
    
    public Date getLastIndexTime();
    
} //- Interface CiteClusterDAO
