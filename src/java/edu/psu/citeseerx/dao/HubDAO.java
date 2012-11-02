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
import edu.psu.citeseerx.domain.Hub;
import java.util.List;

public interface HubDAO {

    public List<Hub> getHubs(String docID, Connection con) throws SQLException;

    public Hub getHub(String url, Connection con) throws SQLException;
    
    public long insertHub(Hub hub, Connection con) throws SQLException;
    
    public void updateHub(Hub hub, Connection con) throws SQLException;
    
    public void insertHubMapping(String doi, long hubID, Connection con)
    throws SQLException;
    
    public void addHubMapping(Hub hub, String doi, Connection con)
    throws SQLException;
    
}
