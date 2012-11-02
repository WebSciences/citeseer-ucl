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
import edu.psu.citeseerx.domain.Document;

public interface VersionDAO {

    public void setVersion(String doi, int version, Connection con)
    throws SQLException;

    public boolean insertVersion(Document doc, Connection con)
    throws SQLException;
    
    public void createNewVersion(Document doc, Connection con)
    throws SQLException;
    
    public void setVersionName(String doi, int version, String name,
            Connection con) throws SQLException;
    
    public void deprecateVersion(String doi, int version, Connection con)
    throws SQLException;
    
    public void deprecateVersionsAfter(String doi, int version, Connection con)
    throws SQLException;
    
    public void setSpam(String doi, int version, boolean isSpam,
            Connection con) throws SQLException;
    
}
