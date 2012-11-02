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

import java.util.List;
import java.sql.SQLException;
import java.sql.Connection;
import edu.psu.citeseerx.domain.Citation;

public interface CitationDAO {

    public List<Citation> getCitations(String docID, boolean getContexts,
            Connection con) throws SQLException;
    
    public List<Citation> getCitationsForCluster(Long clusterid, Connection con)
    throws SQLException;
    
    public List<Citation> getCitations(long startID, int n,
            Connection con) throws SQLException;
    
    public Citation getCitation(long id, Connection con) throws SQLException;
    
    public void insertCitation(String DOI, Citation citation, Connection con)
    throws SQLException;
    
    public List<String> getContexts(Long citationID, Connection con)
    throws SQLException;
    
    public void insertContexts(Long citationID, List<String> contexts,
            Connection con) throws SQLException;
    
    public void setCluster(Citation citation, Long clusterID, Connection con)
    throws SQLException;
    
    public void deleteCitations(String DOI, Connection con) throws SQLException;
    
    public void deleteCitation(Long citationID, Connection con)
    throws SQLException;

    public void deleteContexts(Long citationID, Connection con)
    throws SQLException;
    
}
