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
import edu.psu.citeseerx.domain.Document;
import java.io.IOException;

public interface VersionDAO {

    public void setVersion(String doi, int version)
    throws DataAccessException, IOException;

    public boolean insertVersion(Document doc)
    throws DataAccessException, IOException;
    
    public void createNewVersion(Document doc) throws DataAccessException;
    
    public void setVersionName(String doi, int version, String name)
    throws DataAccessException;
    
    public void deprecateVersion(String doi, int version)
    throws DataAccessException;
    
    public void deprecateVersionsAfter(String doi, int version)
    throws DataAccessException;
    
    public void setVersionSpam(String doi, int version, boolean isSpam)
    throws DataAccessException;
    
    public void insertCorrection(String userid, String paperid, int version)
    throws DataAccessException;
    
    public String getCorrector(String paperid, int version)
    throws DataAccessException;
    
}
