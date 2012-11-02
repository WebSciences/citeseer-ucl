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

import java.sql.*;
import java.util.*;
import edu.psu.citeseerx.domain.Acknowledgment;


public class AckDAOImpl implements AckDAO {

    private static final String DEF_GET_ACKS_QUERY =
        "select id, cluster, name, entType, ackType from acknowledgments " +
        "where paperid=?";

    private static final String DEF_GET_ACK_SRC_QUERY =
        "select name, entType, ackType from acknowledgments_versionShadow " +
        "where id=?";
    
    
    public List<Acknowledgment> getAcknowledgments(String doi,
            boolean getContexts, boolean getSource, Connection con)
            throws SQLException {

        ArrayList<Acknowledgment> acks = new ArrayList<Acknowledgment>();

        PreparedStatement stmt = con.prepareStatement(DEF_GET_ACKS_QUERY);
        PreparedStatement sstmt =
            con.prepareStatement(DEF_GET_ACK_SRC_QUERY);

        stmt.setString(1, doi);
        ResultSet ars = stmt.executeQuery();

        while (ars.next()) {
            Acknowledgment ack = new Acknowledgment();
            ack.setDatum(Acknowledgment.DOI_KEY, ars.getString("id"));
            ack.setClusterID(ars.getLong("cluster"));
            ack.setDatum(Acknowledgment.NAME_KEY, ars.getString("name"));
            ack.setDatum(Acknowledgment.ENT_TYPE_KEY, ars.getString("entType"));
            ack.setDatum(Acknowledgment.ACK_TYPE_KEY, ars.getString("ackType"));
            
            if (getSource) {
                sstmt.setLong(1,
                        new Long(ack.getDatum(Acknowledgment.DOI_KEY,
                                Acknowledgment.UNENCODED)));
                ResultSet srs = sstmt.executeQuery();
                if (srs.first()) {
                    ack.setSource(Acknowledgment.NAME_KEY,
                            srs.getString("name"));
                    ack.setSource(Acknowledgment.ENT_TYPE_KEY,
                            srs.getString("entType"));
                    ack.setSource(Acknowledgment.ACK_TYPE_KEY,
                            srs.getString("ackType"));
                }
                srs.close();
            }
            
            if (getContexts) {
                List<String> contexts =
                    getContexts(Long.parseLong(
                            ack.getDatum(Acknowledgment.DOI_KEY,
                                    Acknowledgment.UNENCODED)), con);
                for (Iterator<String> it = contexts.iterator();
                it.hasNext(); ) {
                    ack.addContext(it.next());
                }
            }
        }
        ars.close();
        stmt.close();
        sstmt.close();

        return acks;
        
    }  //- getAcknowledgments
    
    
    private static final String DEF_GET_CONTEXTS_QUERY =
        "select context from acknowledgmentContexts where ackid=?";


    public List<String> getContexts(Long ackID, Connection con)
    throws SQLException {

        ArrayList<String> contexts = new ArrayList<String>();
        
        PreparedStatement stmt = con.prepareStatement(DEF_GET_CONTEXTS_QUERY);  
        stmt.setLong(1, ackID);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            contexts.add(rs.getString("context"));
        }
        rs.close();
        stmt.close();
        
        return contexts;
        
    }  //- getContexts
    

    
    /* cluster, name, entType, ackType, paperid */
    private static final String DEF_INSERT_ACK_QUERY =
        "insert into acknowledgments values (NULL, ?, ?, ?, ?, ?)";

    /* id, name, entType, ackType */
    private static final String DEF_INSERT_ACK_SRC_QUERY =
        "insert into acknowledgments_versionShadow values (?, ?, ?, ?)";

    
    public void insertAcknowledgment(String doi, Acknowledgment ack,
            Connection con) throws SQLException {
        
        PreparedStatement stmt = con.prepareStatement(DEF_INSERT_ACK_QUERY);
        stmt.setLong(1, ack.getClusterID());
        stmt.setString(2, ack.getDatum(Acknowledgment.NAME_KEY,
                Acknowledgment.UNENCODED));
        stmt.setString(3, ack.getDatum(Acknowledgment.ENT_TYPE_KEY,
                Acknowledgment.UNENCODED));
        stmt.setString(4, ack.getDatum(Acknowledgment.ACK_TYPE_KEY,
                Acknowledgment.UNENCODED));
        stmt.setString(5, doi);
        
        stmt.executeUpdate();
        stmt.close();

        ResultSet krs = stmt.getGeneratedKeys();
        Long id = null;
        if (krs.first()) {
            id = new Long(krs.getLong(1));
            ack.setDatum(Acknowledgment.DOI_KEY, Long.toString(id));
            insertContexts(id, ack.getContexts(), con);
        }
        krs.close();
        stmt.close();

        if (ack.hasSourceData()) {
            PreparedStatement sstmt =
                con.prepareStatement(DEF_INSERT_ACK_SRC_QUERY);
            stmt.setLong(1, id);
            stmt.setString(2, ack.getDatum(Acknowledgment.NAME_KEY,
                    Acknowledgment.UNENCODED));
            stmt.setString(3, ack.getDatum(Acknowledgment.ENT_TYPE_KEY,
                    Acknowledgment.UNENCODED));
            stmt.setString(4, ack.getDatum(Acknowledgment.ACK_TYPE_KEY,
                    Acknowledgment.UNENCODED));
            sstmt.executeUpdate();
            sstmt.close();
        }
        
    }  //- insertAcknowledgment
    
    
    /* ackid, context */
    private static final String DEF_INSERT_CONTEXT_QUERY =
        "insert into acknowledgmentContexts values (NULL, ?, ?)";

    
    public void insertContexts(Long ackID, List<String> contexts,
            Connection con) throws SQLException {
        
        PreparedStatement stmt = con.prepareStatement(DEF_INSERT_CONTEXT_QUERY);
        
        for (Iterator<String> it = contexts.iterator(); it.hasNext(); ) {
            stmt.setLong(1, ackID);
            stmt.setString(2, it.next());
            stmt.executeUpdate();
        }
        stmt.close();
        
    }  //- insertContexts
    

    
    private static final String DEF_UPDATE_CLUSTER_QUERY =
        "update acknowledgments set cluster=? where id=?";

    
    public void setCluster(Acknowledgment ack, Long clusterID, Connection con)
    throws SQLException {
        
        PreparedStatement stmt = con.prepareStatement(DEF_UPDATE_CLUSTER_QUERY);
        stmt.setLong(1, clusterID);
        stmt.setLong(2, Long.parseLong(
                ack.getDatum(Acknowledgment.DOI_KEY,
                        Acknowledgment.UNENCODED)));
        stmt.executeUpdate();
        stmt.close();
        
    }  //- setCluster
    
        
    private static final String DEF_UPDATE_ACK_QUERY =
        "update acknowledgments set cluster=?, name=?, entType=?, ackType=?, " +
        "where id=?";
    
    private static final String DEF_UPDATE_ACK_SRC_QUERY =
        "update acknowledgments_versionShadow set name=?, entType=?, " +
        "ackType=? where id=?";

    
    public void updateAcknowledgment(Acknowledgment ack, Connection con)
    throws SQLException {
        
        PreparedStatement stmt = con.prepareStatement(DEF_UPDATE_ACK_QUERY);
        stmt.setLong(1, ack.getClusterID());
        stmt.setString(2, ack.getDatum(Acknowledgment.NAME_KEY,
                Acknowledgment.UNENCODED));
        stmt.setString(3, ack.getDatum(Acknowledgment.ENT_TYPE_KEY,
                Acknowledgment.UNENCODED));
        stmt.setString(4, ack.getDatum(Acknowledgment.ACK_TYPE_KEY,
                Acknowledgment.UNENCODED));
        stmt.setLong(5, Long.parseLong(
                ack.getDatum(Acknowledgment.DOI_KEY,
                        Acknowledgment.UNENCODED)));
        
        stmt.executeUpdate();
        stmt.close();

        if (ack.hasSourceData()) {
            PreparedStatement sstmt =
                con.prepareStatement(DEF_UPDATE_ACK_SRC_QUERY);
            stmt.setString(1, ack.getDatum(Acknowledgment.NAME_KEY,
                    Acknowledgment.UNENCODED));
            stmt.setString(2, ack.getDatum(Acknowledgment.ENT_TYPE_KEY,
                    Acknowledgment.UNENCODED));
            stmt.setString(3, ack.getDatum(Acknowledgment.ACK_TYPE_KEY,
                    Acknowledgment.UNENCODED));
            stmt.setLong(4, Long.parseLong(
                    ack.getDatum(Acknowledgment.DOI_KEY,
                            Acknowledgment.UNENCODED)));
            sstmt.executeUpdate();
            sstmt.close();
        }
        
    }  //- -updateAcknowledgment
    
    
    private static final String DEF_DEL_ACKS_QUERY =
        "delete from acknowledgments where paperid=?";
    
    
    public void deleteAcknowledgments(String doi, Connection con)
    throws SQLException {
        PreparedStatement stmt = con.prepareStatement(DEF_DEL_ACKS_QUERY);
        stmt.setString(1, doi);
        stmt.executeUpdate();
        stmt.close();
        
    }  //- deleteAcknowledgments
    
    
    private static final String DEF_DEL_ACK_QUERY =
        "delete from acknowledgments where id=?";
    
    public void deleteAcknowledgment(Long ackid, Connection con)
    throws SQLException {
        PreparedStatement stmt = con.prepareStatement(DEF_DEL_ACK_QUERY);
        stmt.setLong(1, ackid);
        stmt.executeUpdate();
        stmt.close();
        
    }  //- deleteAcknowledgment
    
    
    private static final String DEF_DEL_CONTEXTS_QUERY =
        "delete from acknowledgmentContexts where ackid=?";
    
    
    public void deleteContexts(Long ackID, Connection con)
    throws SQLException {
        PreparedStatement stmt = con.prepareStatement(DEF_DEL_CONTEXTS_QUERY);
        stmt.setLong(1, ackID);
        stmt.executeUpdate();
        stmt.close();
        
    }  //- deleteContexts

    
}  //- class AcknowledgmentDAOImpl
