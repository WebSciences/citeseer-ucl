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
import edu.psu.citeseerx.domain.Acknowledgment;

public interface AckDAO {

    public List<Acknowledgment> getAcknowledgments(String doi,
            boolean getContexts, boolean getSource, Connection con)
            throws SQLException;

    public void insertAcknowledgment(String doi, Acknowledgment ack,
            Connection con) throws SQLException;
    
    public List<String> getContexts(Long ackID, Connection con)
    throws SQLException;
    
    public void insertContexts(Long ackID, List<String> contexts,
            Connection con) throws SQLException;
    
    public void updateAcknowledgment(Acknowledgment ack, Connection con)
    throws SQLException;
    
    public void setCluster(Acknowledgment ack, Long clusterID, Connection con)
    throws SQLException;
    
    public void deleteAcknowledgments(String doi, Connection con)
    throws SQLException;
    
    public void deleteAcknowledgment(Long ackID, Connection con)
    throws SQLException;
    
    public void deleteContexts(Long ackID, Connection con)
    throws SQLException;
    
}
