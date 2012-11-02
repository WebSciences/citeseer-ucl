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
import java.util.List;
import java.util.ArrayList;

import edu.psu.citeseerx.domain.Tag;

public class TagDAOImpl implements TagDAO {

    
    public void addTag(String paperid, String tag, Connection con)
    throws SQLException {
        
        if (tagExists(paperid, tag, con)) {
            incrTagCount(paperid, tag, con);
        } else {
            insertTag(paperid, tag, con);
        }
    }
    
    public void deleteTag(String paperid, String tag, Connection con)
    throws SQLException {
        
        decrTagCount(paperid, tag, con);
        if (tagAtZero(paperid, tag, con)) {
            removeTag(paperid, tag, con);
        }
    }
    
    
    private static final String DEF_GET_TAGS_QUERY =
        "select tag, count from tags where paperid=? order by count desc";
    
    public List<Tag> getTags(String paperid, Connection con)
    throws SQLException {
        
        List<Tag> tags = new ArrayList<Tag>();
        PreparedStatement stmt = con.prepareStatement(DEF_GET_TAGS_QUERY);
        ResultSet rs = null;
        try {
            stmt.setString(1, paperid);
            rs = stmt.executeQuery();
            while(rs.next()) {
                Tag tag = new Tag();
                tag.setTag(rs.getString("tag"));
                tag.setCount(rs.getInt("count"));
                tags.add(tag);
            }
            
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { rs.close(); } catch (Exception e) { }
            try { stmt.close(); } catch (Exception e) { }
        }
        return tags;
        
    }  //- getTags
    
    
    private static final String DEF_GET_TAG_QUERY =
        "select count from tags where paperid=? and tag=?";
    
    private boolean tagExists(String paperid, String tag, Connection con)
    throws SQLException {
        
        PreparedStatement stmt = con.prepareStatement(DEF_GET_TAG_QUERY);
        ResultSet rs = null;
        boolean tagExists = false;
        try {
            stmt.setString(1, paperid);
            stmt.setString(2, tag);
            rs = stmt.executeQuery();
            if (rs.first()) {
                tagExists = true;
            }
            
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { rs.close(); } catch (Exception e) { }
            try { stmt.close(); } catch (Exception e) { }
        }
        return tagExists;
        
    }  //- tagExists

    
    private static final String DEF_INS_TAG_STMT =
        "insert into tags values (NULL, ?, ?, 1)";

    private void insertTag(String paperid, String tag, Connection con)
    throws SQLException {
        
        PreparedStatement stmt = con.prepareStatement(DEF_INS_TAG_STMT);
        try {
            stmt.setString(1, paperid);
            stmt.setString(2, tag);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { stmt.close(); } catch (Exception e) { }
        }
        
    }  //- insertTag
    
    
    private static final String DEF_INCR_TAG_STMT =
        "update tags set count=count+1 where paperid=? and tag=?";
    
    private void incrTagCount(String paperid, String tag, Connection con)
    throws SQLException {
        
        PreparedStatement stmt = con.prepareStatement(DEF_INCR_TAG_STMT);
        try {
            stmt.setString(1, paperid);
            stmt.setString(2, tag);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { stmt.close(); } catch (Exception e) { }
        }
        
    }  //- incrTagCount
    
    
    private static final String DEF_DECR_TAG_STMT =
        "update tags set count=count-1 where paperid=? and tag=?";
    
    private void decrTagCount(String paperid, String tag, Connection con)
    throws SQLException {
        
        PreparedStatement stmt = con.prepareStatement(DEF_DECR_TAG_STMT);
        try {
            stmt.setString(1, paperid);
            stmt.setString(2, tag);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { stmt.close(); } catch (Exception e) { }
        }
        
    }  //- decrTagCount
    
    
    private boolean tagAtZero(String paperid, String tag, Connection con)
    throws SQLException {
        
        PreparedStatement stmt = con.prepareStatement(DEF_GET_TAG_QUERY);
        ResultSet rs = null;
        boolean tagAtZero = true;
        
        try {
            stmt.setString(1, paperid);
            stmt.setString(2, tag);
            rs = stmt.executeQuery();
            if (rs.first()) {
                int count = rs.getInt("count");
                if (count > 0) {
                    tagAtZero = false;
                }
            }
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { rs.close(); } catch (Exception e) { }
            try { stmt.close(); } catch (Exception e) { }
        }
        return tagAtZero;
        
    }  //- tagAtZero
    
    
    private static final String DEF_DEL_TAG_STMT =
        "delete from tags where paperid=? and tag=?";
    
    private void removeTag(String paperid, String tag, Connection con)
    throws SQLException {
        
        PreparedStatement stmt = con.prepareStatement(DEF_DEL_TAG_STMT);
        try {
            stmt.setString(1, paperid);
            stmt.setString(2, tag);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw(e);
        } finally {
            try { stmt.close(); } catch (Exception e) { }
        }
        
    }  //- removeTag
    
    
}  //- class TagDAOImpl
