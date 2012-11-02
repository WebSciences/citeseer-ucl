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
import edu.psu.citeseerx.domain.Author;


public class AuthorDAOImpl implements AuthorDAO {

    private static final String DEF_GET_AUTH_QUERY =
        "select id, cluster, name, affil, address, email, ord from authors " +
        "where paperid=? order by ord ASC";
    
    private static final String DEF_GET_AUTH_SRC_QUERY =
        "select name, affil, address, email, ord from " +
        "authors_versionShadow where id=?";
    
    
    public List<Author> getDocAuthors(String doi, boolean getSource,
            Connection con) throws SQLException {

        ArrayList<Author> authors = new ArrayList<Author>();

        PreparedStatement stmt = con.prepareStatement(DEF_GET_AUTH_QUERY);
        PreparedStatement sstmt =
            con.prepareStatement(DEF_GET_AUTH_SRC_QUERY);

        stmt.setString(1, doi);
        ResultSet ars = stmt.executeQuery();

        while (ars.next()) {
            Author auth = new Author();
            auth.setDatum(Author.DOI_KEY, ars.getString("id"));
            auth.setClusterID(ars.getLong("cluster"));
            auth.setDatum(Author.NAME_KEY, ars.getString("name"));
            auth.setDatum(Author.AFFIL_KEY, ars.getString("affil"));
            auth.setDatum(Author.ADDR_KEY, ars.getString("address"));
            auth.setDatum(Author.EMAIL_KEY, ars.getString("email"));
            auth.setDatum(Author.ORD_KEY, ars.getString("ord"));
            
            if (getSource) {
                sstmt.setLong(1,
                        new Long(auth.getDatum(Author.DOI_KEY,
                                Author.UNENCODED)));
                ResultSet srs = sstmt.executeQuery();
                if (srs.first()) {
                    auth.setSource(Author.NAME_KEY, srs.getString("name"));
                    auth.setSource(Author.AFFIL_KEY, srs.getString("affil"));
                    auth.setSource(Author.ADDR_KEY, srs.getString("address"));
                    auth.setSource(Author.EMAIL_KEY, srs.getString("email"));
                    auth.setSource(Author.ORD_KEY, srs.getString("ord"));
                }
                srs.close();
            }
            authors.add(auth);
        }
        ars.close();
        stmt.close();
        sstmt.close();

        return authors;
        
    }  //- getDocAuthors 
    
    
    /* cluster, name, affil, address, email, ord, paperid */
    private static final String DEF_INSERT_AUTH_QUERY =
        "insert into authors values (NULL, ?, ?, ?, ?, ?, ?, ?)";
    
    /* id, name, affil, address, email, ord */
    private static final String DEF_INSERT_AUTH_SRC_QUERY =
        "insert into authors_versionShadow values (?, ?, ?, ?, ?, ?)";

    
    public void insertAuthor(String doi, Author auth, Connection con)
    throws SQLException {
    
        PreparedStatement stmt = con.prepareStatement(DEF_INSERT_AUTH_QUERY);
        stmt.setLong(1, auth.getClusterID());
        stmt.setString(2, auth.getDatum(Author.NAME_KEY, Author.UNENCODED));
        stmt.setString(3, auth.getDatum(Author.AFFIL_KEY, Author.UNENCODED));
        stmt.setString(4, auth.getDatum(Author.ADDR_KEY, Author.UNENCODED));
        stmt.setString(5, auth.getDatum(Author.EMAIL_KEY, Author.UNENCODED));
        stmt.setString(6, auth.getDatum(Author.ORD_KEY, Author.UNENCODED));
        stmt.setString(7, doi);
        stmt.executeUpdate();

        ResultSet krs = stmt.getGeneratedKeys();
        Long id = null;
        if (krs.first()) {
            id = new Long(krs.getLong(1));
            auth.setDatum(Author.DOI_KEY, Long.toString(id));
        }
        krs.close();
        stmt.close();

        if (auth.hasSourceData()) {
            PreparedStatement sstmt =
                con.prepareStatement(DEF_INSERT_AUTH_SRC_QUERY);
            sstmt.setLong(1, id);
            sstmt.setString(2, auth.getSource(Author.NAME_KEY));
            sstmt.setString(3, auth.getSource(Author.AFFIL_KEY));
            sstmt.setString(4, auth.getSource(Author.ADDR_KEY));
            sstmt.setString(5, auth.getSource(Author.EMAIL_KEY));
            sstmt.setString(6, auth.getSource(Author.ORD_KEY));

            sstmt.executeUpdate();
            sstmt.close();
        }
        
    }  //- insertAuthors
    

    private static final String DEF_UPDATE_AUTH_QUERY =
        "update authors set cluster=?, name=?, affil=?, address=?, email=?, " +
        "ord=? where id=?";
    
    private static final String DEF_UPDATE_AUTH_SRC_QUERY =
        "update authors_versionShadow set name=?, affil=?, address=?, " +
        "email=?, ord=? where id=?";
    
    
    public void updateAuthor(Author auth, Connection con) throws SQLException {
        
        PreparedStatement stmt = con.prepareStatement(DEF_UPDATE_AUTH_QUERY);
        stmt.setLong(1, auth.getClusterID());
        stmt.setString(2, auth.getDatum(Author.NAME_KEY, Author.UNENCODED));
        stmt.setString(3, auth.getDatum(Author.AFFIL_KEY, Author.UNENCODED));
        stmt.setString(4, auth.getDatum(Author.ADDR_KEY, Author.UNENCODED));
        stmt.setString(5, auth.getDatum(Author.EMAIL_KEY, Author.UNENCODED));
        stmt.setString(6, auth.getDatum(Author.ORD_KEY, Author.UNENCODED));
        stmt.setLong(7, Long.parseLong(
                auth.getDatum(Author.DOI_KEY, Author.UNENCODED)));
        stmt.executeUpdate();
        stmt.close();

        if (auth.hasSourceData()) {
            PreparedStatement sstmt =
                con.prepareStatement(DEF_UPDATE_AUTH_SRC_QUERY);
            sstmt.setString(1, auth.getSource(Author.NAME_KEY));
            sstmt.setString(2, auth.getSource(Author.AFFIL_KEY));
            sstmt.setString(3, auth.getSource(Author.ADDR_KEY));
            sstmt.setString(4, auth.getSource(Author.EMAIL_KEY));
            sstmt.setString(5, auth.getSource(Author.ORD_KEY));
            sstmt.setLong(6, Long.parseLong(
                    auth.getDatum(Author.DOI_KEY, Author.UNENCODED)));

            sstmt.executeUpdate();
            sstmt.close();
        }
        
    }  //- updateAuthor
    
    
    private static final String DEF_UPDATE_CLUSTER_QUERY =
        "update authors set cluster=? where id=?";

    
    public void setCluster(Author auth, Long clusterID, Connection con)
    throws SQLException {
        PreparedStatement stmt = con.prepareStatement(DEF_UPDATE_CLUSTER_QUERY);
        stmt.setLong(1, clusterID);
        stmt.setLong(2, Long.parseLong(
                auth.getDatum(Author.DOI_KEY, Author.UNENCODED)));
        stmt.executeUpdate();
        stmt.close();
        
    }  //- setCluster
    
    
    private static final String DEF_DEL_AUTHORS_QUERY =
        "delete from authors where paperid=?";
    
    
    public void deleteAuthors(String doi, Connection con) throws SQLException {
        PreparedStatement stmt = con.prepareStatement(DEF_DEL_AUTHORS_QUERY);
        stmt.setString(1, doi);
        stmt.executeUpdate();
        stmt.close();

    }  //- deleteAuthors
    
    
    private static final String DEF_DEL_AUTHOR_QUERY =
        "delete from authors where id=?";
    
    
    public void deleteAuthor(Long authorID, Connection con)
    throws SQLException {
        PreparedStatement stmt = con.prepareStatement(DEF_DEL_AUTHOR_QUERY);
        stmt.setLong(1, authorID);
        stmt.executeUpdate();
        stmt.close();
        
    }  //- deleteAuthor

    
}  //- class AuthorDAOImpl
