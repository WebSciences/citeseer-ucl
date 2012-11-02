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

import edu.psu.citeseerx.domain.Hub;

public class HubDAOImpl implements HubDAO {

    private static final String DEF_GET_HUBS_QUERY =
        "select url, lastCrawl, repositoryID from hubUrls, paperToHubMap " +
        "where paperid=? and hubUrls.id=paperToHubMap.hubId";
    
    public List<Hub> getHubs(String docID, Connection con) throws SQLException {
        
        PreparedStatement stmt = con.prepareStatement(DEF_GET_HUBS_QUERY);
        stmt.setString(1, docID);
        
        ArrayList<Hub> hubs = new ArrayList<Hub>();
        ResultSet rs = stmt.executeQuery();
        while(rs.next()) {
            Hub hub = new Hub();
            hub.setUrl(rs.getString("url"));
            hub.setLastCrawled(
                    new java.util.Date(rs.getTimestamp("lastCrawl").getTime()));
            hub.setRepID(rs.getString("repositoryID"));
            hubs.add(hub);
        }
        rs.close();
        stmt.close();
        
        return hubs;
        
    }  //- getHubs
    

    private static final String DEF_GET_HUB_QUERY =
        "select lastCrawl, repositoryID from hubUrls where url=?";
    
    public Hub getHub(String url, Connection con) throws SQLException {
        
        PreparedStatement stmt = con.prepareStatement(DEF_GET_HUB_QUERY);
        stmt.setString(1, url);
        
        Hub hub = null;
        
        ResultSet rs = stmt.executeQuery();
        if (rs.first()) {
            hub = new Hub();
            hub.setUrl(url);
            hub.setLastCrawled(
                    new java.util.Date(rs.getTimestamp("lastCrawl").getTime()));
            hub.setRepID(rs.getString("repositoryID"));
        }
        
        rs.close();
        stmt.close();
        
        return hub;
        
    }  //- getHub

    
    /* id, url, lastCrawled */
    private static final String DEF_INSERT_HUB_STMT =
        "insert into hubUrls values (NULL, ?, ?, ?)";
    
    public long insertHub(Hub hub, Connection con) throws SQLException {
        
        PreparedStatement stmt = con.prepareStatement(DEF_INSERT_HUB_STMT);
        stmt.setString(1, hub.getUrl());
        if (hub.getLastCrawled() == null) {
            hub.setLastCrawled(new java.util.Date(System.currentTimeMillis()));
        }
        stmt.setTimestamp(2, new Timestamp(hub.getLastCrawled().getTime()));
        stmt.setString(3, hub.getRepID());
        
        stmt.executeUpdate();
        
        long id = -1;
        ResultSet keySet = stmt.getGeneratedKeys();
        if (keySet.first()) {
            id = keySet.getLong(1);
        }
        keySet.close();
        stmt.close();
        
        return id;
        
    }  //- insertHub
    
    
    private static final String DEF_UPDATE_HUB_STMT =
        "update hubUrls set lastCrawl=?, repositoryID=? where url=?";
    
    public void updateHub(Hub hub, Connection con) throws SQLException {
        
        PreparedStatement stmt = con.prepareStatement(DEF_UPDATE_HUB_STMT);
        stmt.setTimestamp(1, new Timestamp(hub.getLastCrawled().getTime()));
        stmt.setString(2, hub.getRepID());
        stmt.setString(3, hub.getUrl());
        
        stmt.executeUpdate();
        stmt.close();
        
    }  //- updateHub
    
    
    /* paperid, hubid */
    private static final String DEF_INSERT_HUBMAP_STMT =
        "insert into paperToHubMap values (NULL, ?, ?)";
    
    public void insertHubMapping(String doi, long hubID, Connection con)
    throws SQLException {
        
        PreparedStatement stmt = con.prepareStatement(DEF_INSERT_HUBMAP_STMT);
        stmt.setString(1, doi);
        stmt.setLong(2, hubID);
        
        stmt.executeUpdate();
        stmt.close();
        
    }  //- insertHubMapping
    

    private static final String DEF_GET_HUBID_QUERY =
        "select id from hubUrls where url=?";
    
    public void addHubMapping(Hub hub, String doi, Connection con)
    throws SQLException {
        
        String url = hub.getUrl();
        PreparedStatement stmt = con.prepareStatement(DEF_GET_HUBID_QUERY);
        stmt.setString(1, url);
        
        long id = -1;
        ResultSet rs = stmt.executeQuery();
        if (rs.first()) {
            id = rs.getLong(1);
        } else {
            id = insertHub(hub, con);            
        }
        rs.close();
        stmt.close();
        
        insertHubMapping(doi, id, con);

    }  //- addHubMapping
    
}  //- class HubDAOImpl
