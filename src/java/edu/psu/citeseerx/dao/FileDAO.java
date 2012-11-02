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
import edu.psu.citeseerx.domain.DocumentFileInfo;
import edu.psu.citeseerx.domain.CheckSum;
import java.util.List;

public interface FileDAO {

    public DocumentFileInfo getFileInfo(String docID, Connection con)
    throws SQLException;
    
    public void insertFileInfo(String docID, DocumentFileInfo fileInfo,
            Connection con) throws SQLException;
    
    public void updateFileInfo(String docID, DocumentFileInfo fileInfo,
            Connection con) throws SQLException;
    
    public void deleteChecksums(String doi, Connection con)
    throws SQLException;
    
    public List<CheckSum> getChecksums(String sha1, Connection con)
    throws SQLException;
    
    public void insertChecksum(CheckSum checksum, Connection con)
    throws SQLException;
    
}
