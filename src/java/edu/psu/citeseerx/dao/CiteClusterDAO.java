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
package edu.psu.citeseerx.dao;

import java.sql.SQLException;
import javax.sql.DataSource;
import java.util.List;

import edu.psu.citeseerx.domain.*;
import org.json.JSONException;

public interface CiteClusterDAO {

    public void setDataSource(DataSource ds);
    
    public Long clusterDocument(List<String> key, Document doc)
    throws SQLException, JSONException;
    
    public List<ThinDoc> getCitingDocuments(Long clusterid,
            int start, int amount) throws SQLException;
    
    public List<ThinDoc> getCitedDocuments(Long clusterid,
            int start, int amount) throws SQLException;
    
    public List<String> getCitingDocumentIDs(String paperid)
    throws SQLException;
    
    public List<String> getCitedDocumentIDs(String paperid)
    throws SQLException;
    
    public List<Long> getCitingClusters(Long clusterid) throws SQLException;

    public List<Long> getCitedClusters(Long clusterid) throws SQLException;

    public List<Long> getCitingClusters(Long clusterid, int amount)
    throws SQLException;

    public List<Long> getCitedClusters(Long clusterid, int amount)
    throws SQLException;

    public void deleteDocument(String paperid) throws SQLException;
    
    public List<ThinDoc> getClustersInCollection(Long start, int amount)
    throws SQLException;
    
    public List<String> getPaperIDs(Long clusterid) throws SQLException;
    
    public List<Long> getCitationIDs(Long clusterid) throws SQLException;
    
    public ThinDoc getThinDoc(Long clusterid) throws SQLException;
    
    public List<ThinDoc> getClustersToBeIndexed(int amount) throws SQLException;
    
    public void setClusterIndexFlag(Long clusterid, boolean reindex)
    throws SQLException;
    
    public List<ThinDoc> getUpdatedClusters(int amount) throws SQLException;
    
    public void updateCluster(ThinDoc cluster, boolean changed)
    throws SQLException;
    
    public void setClusterUpdatedFlag(Long clusterid, boolean updated)
    throws SQLException;
    
    public String getContext(Long citing, Long cited) throws SQLException;
    
    public Long getMinClusterID() throws SQLException;
    
    public Long getMaxClusterID() throws SQLException;
    
}
