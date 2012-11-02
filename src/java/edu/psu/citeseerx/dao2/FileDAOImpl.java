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

import java.util.List;
import java.sql.*;
import javax.sql.DataSource;

import org.springframework.context.ApplicationContextException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;

import edu.psu.citeseerx.domain.CheckSum;

/**
 * Spring-based JDBC implementation of FileDAO.
 *
 * @author Isaac Councill
 * @version $Rev: 671 $ $Date: 2008-07-27 16:30:52 -0400 (Sun, 27 Jul 2008) $
 */
public class FileDAOImpl extends JdbcDaoSupport implements FileDAO {

    private GetCheckSums getCheckSums;
    private GetCheckSum getCheckSum;
    private InsertCheckSum insertCheckSum;
    private DeleteCheckSums deleteCheckSums;
    
    protected void initDao() throws ApplicationContextException {
        initMappingSqlQueries();
    } //- initDao
    
    
    protected void initMappingSqlQueries() throws ApplicationContextException {
        getCheckSums = new GetCheckSums(getDataSource());
        getCheckSum = new GetCheckSum(getDataSource());
        insertCheckSum = new InsertCheckSum(getDataSource());
        deleteCheckSums = new DeleteCheckSums(getDataSource());
    }
    
    
    /*
     * (non-javadoc)
     * @see edu.psu.citeseerx.dao2.FileDAO#updateChecksums(java.lang.String, java.util.List)
     */
    public void updateChecksums(String doi, List<CheckSum> checksums) {
        deleteCheckSums.run(doi);
        for ( CheckSum sum : checksums ) {
            sum.setDOI(doi);
            insertCheckSum.run(sum);
        }
    }

    
    /*
     * (non-javadoc)
     * @see edu.psu.citeseerx.dao2.FileDAO#getChecksums(java.lang.String)
     */
    public List getChecksums(String sha1) {
        return getCheckSum.run(sha1);
    }  //- getDOIbyChecksum
    
    
    /*
     * (non-javadoc)
     * @see edu.psu.citeseerx.dao2.FileDAO#getChecksumsForDocument(java.lang.String)
     */
    public List getChecksumsForDocument(String doi) {
        return getCheckSums.run(doi);
    }

    
    /*
     * (non-javadoc)
     * @see edu.psu.citeseerx.dao2.FileDAO#deleteChecksums(java.lang.String)
     */
    public void deleteChecksums(String doi) {
        deleteCheckSums.run(doi);
    }  //- deleteChecksums
    
    
    /*
     * (non-javadoc)
     * @see edu.psu.citeseerx.dao2.FileDAO#insertChecksum(edu.psu.citeseerx.domain.CheckSum)
     */
    public void insertChecksum(CheckSum checksum) {
        insertCheckSum.run(checksum);
    }  //- insertChecksum


    /*
     * (non-javadoc)
     * @see edu.psu.citeseerx.dao2.FileDAO#insertChecksums(java.lang.String, java.util.List)
     */
    public void insertChecksums(String doi, List<CheckSum> checksums) {
        for ( CheckSum sum : checksums ) {
            sum.setDOI(doi);
            insertCheckSum.run(sum);
        }
    }  //- insertChecksum

    
    private static final String DEF_GET_CHECKSUMS_QUERY =
        "select sha1, fileType from checksum where paperid=?";

    private class GetCheckSums extends MappingSqlQuery {
        
        public GetCheckSums(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_CHECKSUMS_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public CheckSum mapRow(ResultSet rs, int rowNum) throws SQLException {
            CheckSum checksum = new CheckSum();
            checksum.setSha1(rs.getString(1));
            checksum.setFileType(rs.getString(2));
            return checksum;
        }
        
        public List run(String doi) {
            List list = execute(doi);
            for (Object o : list) {
                ((CheckSum)o).setDOI(doi);
            }
            return list;
        }
        
    }  //- class GetCheckSums
    
    
    private static final String DEF_GET_CHECKSUM_QUERY =
        "select paperid, fileType from checksum where sha1=?";

    private class GetCheckSum extends MappingSqlQuery {
        
        public GetCheckSum(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_CHECKSUM_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public CheckSum mapRow(ResultSet rs, int rowNum) throws SQLException {
            CheckSum checksum = new CheckSum();
            checksum.setDOI(rs.getString(1));
            checksum.setFileType(rs.getString(2));
            return checksum;
        }
        
        public List run(String sha1) {
            List list = execute(sha1);
            for (Object o : list) {
                ((CheckSum)o).setSha1(sha1);
            }
            return list;
        }
        
    }  //- class GetCheckSum
        
    
    /* sha1, paperid, fileType */
    private static final String DEF_INSERT_CHECKSUM_QUERY =
        "insert into checksum values (?, ?, ?)";

    private class InsertCheckSum extends SqlUpdate {
        
        public InsertCheckSum(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_CHECKSUM_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(CheckSum checksum) {
            Object[] params = new Object[] {
                    checksum.getSha1(),
                    checksum.getDOI(),
                    checksum.getFileType()
            };
            return update(params);
        }
        
    }  //- class InsertCheckSum
        
    
    private static final String DEF_DEL_CHECKSUM_QUERY =
        "delete from checksum where paperid=?";
    
    private class DeleteCheckSums extends SqlUpdate {
        
        public DeleteCheckSums(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_DEL_CHECKSUM_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(String doi) {
            return update(doi);
        }
        
    }  //- class DeleteCheckSums 
    
}  //- class FileDAOImpl
