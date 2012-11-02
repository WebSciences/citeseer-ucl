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
import edu.psu.citeseerx.domain.CheckSum;
import java.util.List;

/**
 * File data access methods.
 *
 * @author Isaac Councill
 * @version $Rev: 671 $ $Date: 2008-07-27 16:30:52 -0400 (Sun, 27 Jul 2008) $
 */
public interface FileDAO {

    /**
     * Replaces existing checksum data for a given document ID with the
     * supplied checksums.
     * @param doi
     * @param checksums
     * @throws DataAccessException
     */
    public void updateChecksums(String doi, List<CheckSum> checksums);
    
    /**
     * Deletes checksum information for the given document ID.
     * @param doi
     * @throws DataAccessException
     */
    public void deleteChecksums(String doi);
    
    /**
     * Retreives a list of CheckSum objects that match the specified SHA1
     * hex string.
     * @param sha1
     * @return
     * @throws DataAccessException
     */
    public List<CheckSum> getChecksums(String sha1);

    /**
     * Retreives a list of CheckSum objects for a specific document.
     * @param doi
     * @return
     * @throws DataAccessException
     */
    public List<CheckSum> getChecksumsForDocument(String doi);

    /**
     * Adds a CheckSum record to storage.
     * @param checksum
     * @throws DataAccessException
     */
    public void insertChecksum(CheckSum checksum);
    
    /**
     * Add the supplied CheckSums records to storage.
     * @param checksums
     */
    public void insertChecksums(String doi, List<CheckSum> checksums);
    
}
