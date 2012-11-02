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
import edu.psu.citeseerx.domain.Keyword;

public interface KeywordDAO {

    public List<Keyword> getKeywords(String docID, boolean getSource,
            Connection con) throws SQLException;
    
    public void insertKeyword(String docID, Keyword keyword, Connection con)
    throws SQLException;
    
    public void updateKeyword(String docID, Keyword keyword, Connection con)
    throws SQLException;
    
    public void deleteKeyword(String docID, Keyword keyword, Connection con)
    throws SQLException;
    
    public void deleteKeywords(String docID, Connection con)
    throws SQLException;
    
}
