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

import java.io.IOException;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.SQLException;

import edu.psu.citeseerx.domain.*;

public interface FileSysDAO {

    public Document getDocFromXML(String repID, String relPath)
    throws IOException;
    
    public void writeXML(Document doc) throws IOException;
    
    public Document getDocVersion(String doi, int version, Connection con)
    throws SQLException, IOException;

    public Document getDocVersion(String doi, String name, Connection con)
    throws SQLException, IOException;
    
    public void writeVersion(Document doc) throws IOException;
    
    public FileInputStream getFileInputStream(String doi, String repID,
            String type) throws IOException;
    
}
