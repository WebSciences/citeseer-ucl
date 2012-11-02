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
import edu.psu.citeseerx.domain.Keyword;


public class KeywordDAOImpl implements KeywordDAO {

    private static final String DEF_GET_KEYWORD_QUERY =
        "select id, keyword from keywords where paperid=?";

    private static final String DEF_GET_KEYWORD_SRC_QUERY =
        "select keyword from keywords_versionShadow where id=?";

    
    public List<Keyword> getKeywords(String id, boolean getSource,
            Connection con) throws SQLException {

        ArrayList<Keyword> keywords = new ArrayList<Keyword>();
        
        PreparedStatement kstmt = con.prepareStatement(DEF_GET_KEYWORD_QUERY);
        PreparedStatement sstmt =
            con.prepareStatement(DEF_GET_KEYWORD_SRC_QUERY);
        kstmt.setString(1, id);
        ResultSet krs = kstmt.executeQuery();
        while (krs.next()) {
            Keyword keyword = new Keyword();
            keyword.setDatum(Keyword.DOI_KEY, krs.getString("id"));
            keyword.setDatum(Keyword.KEYWORD_KEY, krs.getString("keyword"));
            
            if (getSource) {
                sstmt.setLong(1, new Long(keyword.getDatum(Keyword.DOI_KEY,
                        Keyword.UNENCODED)));
                ResultSet srs = sstmt.executeQuery();
                if (srs.first()) {
                    keyword.setSource(Keyword.KEYWORD_KEY,
                            srs.getString("keyword"));
                }
                srs.close();
            }
            
            keywords.add(keyword);
        }
        krs.close();
        sstmt.close();
        kstmt.close();
        
        return keywords;
        
    }  //- getKeywords
    
    
    /* id, keyword, paperid */
    private static final String DEF_INSERT_KEYWORD_QUERY =
        "insert into keywords values (NULL, ?, ?)";
    
    /* id, keyword */
    private static final String DEF_INSERT_KEYWORD_SRC_QUERY =
        "insert into keywords_versionShadow values (?, ?)";


    public void insertKeyword(String doi, Keyword keyword, Connection con)
    throws SQLException {
        
        PreparedStatement stmt = con.prepareStatement(DEF_INSERT_KEYWORD_QUERY);
        stmt.setString(1, keyword.getDatum(Keyword.KEYWORD_KEY,
                Keyword.UNENCODED));
        stmt.setString(2, doi);
        stmt.executeUpdate();
        
        Long kid = new Long(-1);
        ResultSet ids = stmt.getGeneratedKeys();
        if (ids.first()) {
            kid = new Long(ids.getLong(1));
        } else {
            System.err.println("No citation ID generated!");
        }
        ids.close();
        stmt.close();
        
        keyword.setDatum(Keyword.DOI_KEY, Long.toString(kid));
        
        if (keyword.hasSourceData()) {
            PreparedStatement sstmt =
                con.prepareStatement(DEF_INSERT_KEYWORD_SRC_QUERY);
            sstmt.setLong(1, kid);
            sstmt.setString(2, keyword.getSource(Keyword.KEYWORD_KEY));
            sstmt.executeUpdate();
            sstmt.close();
        }
        
    }  //- insertKeywords
    
    
    private static final String DEF_UPDATE_KEYWORD_QUERY =
        "update keywords set keyword=?, paperid=? where id=?";
    
    private static final String DEF_UPDATE_KEYWORD_SRC_QUERY =
        "update keywords_versionShadow set keyword=? where id=?";

    
    public void updateKeyword(String doi, Keyword keyword, Connection con)
    throws SQLException {
        
        PreparedStatement stmt = con.prepareStatement(DEF_UPDATE_KEYWORD_QUERY);
        stmt.setString(1, keyword.getDatum(Keyword.KEYWORD_KEY,
                Keyword.UNENCODED));
        stmt.setString(2, doi);
        stmt.setLong(3, new Long(keyword.getDatum(Keyword.KEYWORD_KEY,
                Keyword.UNENCODED)));
        stmt.executeUpdate();
        stmt.close();
        
        if (keyword.hasSourceData()) {
            PreparedStatement sstmt =
                con.prepareStatement(DEF_UPDATE_KEYWORD_SRC_QUERY);
            sstmt.setString(1, keyword.getSource(Keyword.KEYWORD_KEY));
            sstmt.setLong(2, new Long(keyword.getDatum(Keyword.KEYWORD_KEY,
                    Keyword.UNENCODED)));
            sstmt.executeUpdate();
            sstmt.close();
        }
        
    }  //- updateKeyword
    
    
    private static final String DEF_DELETE_KEYWORD_QUERY =
        "delete from keywords where keyword=? and paperid=?";
    
    
    public void deleteKeyword(String docID, Keyword keyword, Connection con)
    throws SQLException {
        PreparedStatement stmt = con.prepareStatement(DEF_DELETE_KEYWORD_QUERY);
        stmt.setString(1, keyword.getDatum(Keyword.KEYWORD_KEY,
                Keyword.UNENCODED));
        stmt.setString(2, docID);
        stmt.executeUpdate();
        stmt.close();
        
    }  //- deleteKeyword
    
    
    private static final String DEF_DELETE_KEYWORDS_QUERY =
        "delete from keywords where paperid=?";
    
    
    public void deleteKeywords(String docID, Connection con)
    throws SQLException {
        PreparedStatement stmt =
            con.prepareStatement(DEF_DELETE_KEYWORDS_QUERY);
        stmt.setString(1, docID);
        stmt.executeUpdate();
        stmt.close();
        
    }  //- deleteKeywords
    
    
}  //- class KeywordDAOImpl
