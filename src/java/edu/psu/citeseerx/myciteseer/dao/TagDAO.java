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
package edu.psu.citeseerx.myciteseer.dao;

import org.springframework.dao.DataAccessException;
import edu.psu.citeseerx.myciteseer.domain.Account;
import java.util.List;

public interface TagDAO {

    public void addTag(Account account, String doi, String tag)
    throws DataAccessException;
    
    public void deleteTag(Account account, String doi, String tag)
    throws DataAccessException;
    
    public List getTags(Account account) throws DataAccessException;
    
    public List getDoisForTag(Account account, String tag)
    throws DataAccessException;
    
}