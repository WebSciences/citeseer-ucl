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
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;

import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.DocumentFileInfo;
import edu.psu.citeseerx.utility.FileNamingUtils;
import edu.psu.citeseerx.utility.CSXConstants;

public class VersionDAOImpl extends JdbcDaoSupport implements VersionDAO {
    
    private UpdateVersion updateVersion;
    private GetMaxVersion getMaxVersion;
    private InsertVersion insertVersion;
    private UpdateVersionName updateVersionName;
    private UpdateDeprecated updateDeprecated;
    private GetVersionsAfter getVersionsAfter;
    private UpdateSpam updateSpam;
    private GetVersionForName getVersionForName;
    private UpdateVTime updateVTime;
    private UpdateVTimeByName updateVTimeByName;
    private InsertCorrection insertCorrection;
    private GetCorrector getCorrector;
    
    protected void initDao() throws ApplicationContextException {
        initMappingSqlQueries();
    } //- initDao
    
    
    protected void initMappingSqlQueries() throws ApplicationContextException {
        updateVersion = new UpdateVersion(getDataSource());
        getMaxVersion = new GetMaxVersion(getDataSource());
        insertVersion = new InsertVersion(getDataSource());
        updateVersionName = new UpdateVersionName(getDataSource());
        updateDeprecated = new UpdateDeprecated(getDataSource());
        getVersionsAfter = new GetVersionsAfter(getDataSource());
        updateSpam = new UpdateSpam(getDataSource());
        getVersionForName = new GetVersionForName(getDataSource());
        updateVTime = new UpdateVTime(getDataSource());
        updateVTimeByName = new UpdateVTimeByName(getDataSource());
        insertCorrection = new InsertCorrection(getDataSource());
        getCorrector = new GetCorrector(getDataSource());
    }
    

    public void setVersion(String doi, int version) throws DataAccessException {
        updateVersion.run(doi, version);
    }  //- setVersion

    
    public boolean insertVersion(Document doc) throws DataAccessException {
        
        String doi = doc.getDatum(Document.DOI_KEY);
        String vname = doc.getVersionName();
        int priorVersion = getVersionForName.run(vname, doi);
        
        if (priorVersion > 0 && !vname.equals(CSXConstants.USER_VERSION)) {
            DocumentFileInfo finfo = doc.getFileInfo();
            String vrepID = finfo.getDatum(DocumentFileInfo.REP_ID_KEY);
            String vpath = FileNamingUtils.buildVersionPath(doi, priorVersion); 

            doc.setVersion(priorVersion);
            doc.setVersionRepID(vrepID);
            doc.setVersionPath(vpath);
            
            updateVTimeByName.run(doi, vname);
            
            return false;

        } else {
            createNewVersion(doc);
            return true;
        }
        
    }  //- insertVersion
    
    
    public void insertCorrection(String userid, String paperid, int version) {
        insertCorrection.run(userid, paperid, version);
    }
    
    
    public String getCorrector(String paperid, int version) {
        return getCorrector.run(paperid, version);
    }
    
    
    public void setVersionTime(String doi, int version) {
        updateVTime.run(doi, version);
    }
    
    public void setVersionTime(String doi, String versionName) {
        updateVTimeByName.run(doi, versionName);
    }
    
    
    public void createNewVersion(Document doc) throws DataAccessException {
        
        String doi = doc.getDatum(Document.DOI_KEY);
        int maxVersion = getMaxVersion.run(doi);
        int version = ++maxVersion;
        
        DocumentFileInfo finfo = doc.getFileInfo();
        String vrepID = finfo.getDatum(DocumentFileInfo.REP_ID_KEY);
        String vpath = FileNamingUtils.buildVersionPath(doi, version); 

        doc.setVersion(version);
        doc.setVersionRepID(vrepID);
        doc.setVersionPath(vpath);
        
        insertVersion.run(doc);
        
    }  //- createNewVersion
    
    
    public void setVersionName(String doi, int version, String name)
    throws DataAccessException {
        updateVersionName.run(name, doi, version);
    }  //- setVersionName

    
    public void deprecateVersion(String doi, int version)
    throws DataAccessException {
        updateDeprecated.run(doi, version);
    }  //- deprecateVersion
    
    
    public void deprecateVersionsAfter(String doi, int version)
    throws DataAccessException {
        List versions = getVersionsAfter.run(doi, version);
        for (Object v : versions) {
            int vers = ((Integer)v).intValue();
            deprecateVersion(doi, vers);
        }
    }  //- deprecateVersionsAfter

    
    public void setVersionSpam(String doi, int version, boolean isSpam)
    throws DataAccessException {
        updateSpam.run(doi, version, isSpam);
    }  //- setSpam

    
    
    private static final String DEF_SET_VERSION_QUERY =
        "update papers set version=? where id=?";
    
    private class UpdateVersion extends SqlUpdate {
        
        public UpdateVersion(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_SET_VERSION_QUERY);
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(String doi, int version) {
            Object[] params = new Object[] { new Integer(version), doi };
            return update(params);
        }
    }
    
    
    private static final String DEF_UPDATE_VTIME_STMT =
        "update paperVersions set time=current_timestamp " +
        "where paperid=? and version=?";
    
    private class UpdateVTime extends SqlUpdate {
        
        public UpdateVTime(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_UPDATE_VTIME_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        }
        
        public int run(String doi, int version) {
            return update( new Object[] { doi, new Integer(version) } );
        }
        
    }  //- class UpdateVTime
    
    
    private static final String DEF_UPDATE_VTIME_BY_NAME_STMT =
        "update paperVersions set time=current_timestamp " +
        "where paperid=? and name=?";
    
    private class UpdateVTimeByName extends SqlUpdate {
        
        public UpdateVTimeByName(DataSource ds) {
            setDataSource(ds);
            setSql(DEF_UPDATE_VTIME_BY_NAME_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(String doi, String name) {
            return update( new Object[] { doi, name } );
        }
        
    }  //- class UpdateVTimeByName

    
    private static final String DEF_GET_MAX_VERSION_QUERY =
        "select max(version) from paperVersions where paperid=?";
    
    private class GetMaxVersion extends MappingSqlQuery {
        
        public GetMaxVersion(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_MAX_VERSION_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt(1);
        }
        
        public int run(String doi) {
            List list = execute(doi);
            if (list.isEmpty()) {
                return 0;
            } else {
                return ((Integer)list.get(0)).intValue();
            }
        }
    }
    
    
    /* name, paperid, version, repositoryID, path */
    private static final String DEF_INSERT_VERSION_QUERY =
        "insert into paperVersions values (NULL, ?, ?, ?, ?, ?, 0, 0, " +
        "current_timestamp)";
    
    private class InsertVersion extends SqlUpdate {
        
        public InsertVersion(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_VERSION_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(Document doc) {
            Object[] params = new Object[] {
                    doc.getVersionName(),
                    doc.getDatum(Document.DOI_KEY),
                    doc.getVersion(),
                    doc.getFileInfo().getDatum(DocumentFileInfo.REP_ID_KEY),
                    doc.getVersionPath()
            };
            return update(params);
        }
    }
    
    
    private static final String DEF_SET_VNAME_QUERY =
        "update paperVersions set name=? where paperid=? and version=?";
    
    private class UpdateVersionName extends SqlUpdate {
        
        public UpdateVersionName(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_SET_VNAME_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        }
        
        public int run(String name, String doi, int version) {
            Object[] params = new Object[] { name, doi, new Integer(version) };
            return update(params);
        }
    }
    
    
    private static final String DEF_SET_DEPRECATED_QUERY =
        "update paperVersions set deprecated=? where paperid=? and version=?";
    
    private class UpdateDeprecated extends SqlUpdate {
        
        public UpdateDeprecated(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_SET_DEPRECATED_QUERY);
            declareParameter(new SqlParameter(Types.TINYINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        }
        
        public int run(String doi, int version) {
            Object[] params = new Object[] {
                    new Boolean(false), doi,
                    new Integer(version)
            };
            return update(params);
        }
    }
    
    
    private static final String DEF_GET_VERSIONS_AFTER_QUERY =
        "select version from paperVersions where paperid=? and version>?";
    
    private class GetVersionsAfter extends MappingSqlQuery {

        public GetVersionsAfter(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_VERSIONS_AFTER_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        }
        
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt(1);
        }
        
        public List run(String doi, int version) {
            Object[] params = new Object[] { doi, new Integer(version) };
            return execute(params);
        }
    }
        
    
    private static final String DEF_SET_SPAM_QUERY =
        "update paperVersions set spam=? where paperid=? and version=?";
    
    private class UpdateSpam extends SqlUpdate {
        
        public UpdateSpam(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_SET_SPAM_QUERY);
            declareParameter(new SqlParameter(Types.TINYINT));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        }
        
        public int run(String doi, int version, boolean isSpam) {
            Object[] params = new Object[] {
                    new Boolean(isSpam),
                    doi, new Integer(version)
            };
            return update(params);
        }
    }
        
    
    private static final String DEF_GET_VERSION_FOR_NAME_QUERY =
        "select version from paperVersions where name=? and paperid=?";
    
    private class GetVersionForName extends MappingSqlQuery {
        
        public GetVersionForName(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_VERSION_FOR_NAME_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt(1);
        }
        
        public int run(String name, String doi) {
            Object[] params = new Object[] { name, doi };
            List list = execute(params);
            if (list.isEmpty()) {
                return -1;
            } else {
                return ((Integer)list.get(0)).intValue();
            }
        }
    }
    
    
    private static final String DEF_INSERT_CORRECTION_STMT =
        "insert into userCorrections values (NULL, ?, ?, ?)";
    
    private class InsertCorrection extends SqlUpdate {
        
        public InsertCorrection(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_CORRECTION_STMT);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        }
     
        public int run(String userid, String paperid, int version) {
            Object[] params = new Object[] {
                    userid, paperid, new Integer(version)
            };
            return update(params);
        }
        
    }  //- class InsertCorrection
    
    
    private static final String DEF_GET_CORRECTOR_QUERY =
        "select userid from userCorrections where paperid=? and version=?";
    
    private class GetCorrector extends MappingSqlQuery {
        
        public GetCorrector(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_CORRECTOR_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        }
        
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString(1);
        }
        
        public String run(String paperid, int version) {
            Object[] params = new Object[] { paperid, new Integer(version) };
            List results = execute(params);
            if (results.isEmpty()) {
                return null;
            } else {
                return (String)results.get(0);
            }
        }
        
    }  //- class GetCorrector
    
}  //- class VersionDAOImpl
