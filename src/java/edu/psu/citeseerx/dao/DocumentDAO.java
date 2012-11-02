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
import java.sql.Connection;
import java.util.List;

import edu.psu.citeseerx.domain.Document;

public interface DocumentDAO {

    public Document getDocument(String docID, boolean getSource, Connection con)
    throws SQLException;
    
    public void insertDocument(Document doc, Connection con)
    throws SQLException;
    
    public void updateDocument(Document doc, Connection con)
    throws SQLException;
    
    public void setIndexFlag(Document doc, boolean reindex, Connection con)
    throws SQLException;
    
    public void setPublic(Document doc, boolean isPublic, Connection con)
    throws SQLException;
    
    public void setCluster(Document doc, Long clusterID, Connection con)
    throws SQLException;
    
    public void setNcites(Document doc, int ncites, Connection con)
    throws SQLException;
    
    public List<String> getDocsToBeIndexed(int numDocs, Connection con)
    throws SQLException;
    
    public int getNumberOfDocumentRecords(Connection con) throws SQLException;
    
    public List<String> getDOIs(String start, int amount, Connection con)
    throws SQLException;
    
}
