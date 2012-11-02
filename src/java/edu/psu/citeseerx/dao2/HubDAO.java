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
import edu.psu.citeseerx.domain.Hub;
import java.util.List;

public interface HubDAO {

    public List getHubs(String doi) throws DataAccessException;

    public List getHubsForUrl(String url) throws DataAccessException;
    
    public Hub getHub(String url) throws DataAccessException;
    
    public long insertHub(Hub hub) throws DataAccessException;
    
    public void updateHub(Hub hub) throws DataAccessException;
    
    public void insertHubMapping(long urlID, long hubID)
    throws DataAccessException;
    
    public List getUrls(String doi) throws DataAccessException;
    
    public long insertUrl(String doi, String url) throws DataAccessException;
    
    public void addHubMapping(Hub hub, String url, String doi)
    throws DataAccessException;
    
}
