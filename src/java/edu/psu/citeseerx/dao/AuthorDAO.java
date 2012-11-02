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
import java.sql.Connection;
import java.sql.SQLException;
import edu.psu.citeseerx.domain.Author;

public interface AuthorDAO {

    public List<Author> getDocAuthors(String docID, boolean getSource,
            Connection con) throws SQLException;

    public void insertAuthor(String docID, Author auth, Connection con)
    throws SQLException;
    
    public void updateAuthor(Author auth, Connection con) throws SQLException;
    
    public void setCluster(Author auth, Long clusterID, Connection con)
    throws SQLException;
    
    public void deleteAuthors(String docID, Connection con) throws SQLException;
    
    public void deleteAuthor(Long authorID, Connection con) throws SQLException;
    
}
