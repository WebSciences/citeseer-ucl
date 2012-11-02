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

import edu.psu.citeseerx.domain.Citation;

import java.util.List;
import java.util.Iterator;
import java.sql.*;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContextException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.GeneratedKeyHolder;

public class CitationDAOImpl extends JdbcDaoSupport implements CitationDAO {

    private GetCites getCites;
    private GetCitesForCluster getCitesForCluster;
    private GetCite getCite;
    private GetCiteRange getCiteRange;
    private InsertCite insertCite;
    private InsertContext insertContext;
    private GetContexts getContexts;
    private SetCluster setCluster;
    private DeleteCites deleteCites;
    private DeleteCite deleteCite;
    private DeleteContexts deleteContexts;
    private CountCitations countCitations;
    
    protected void initDao() throws ApplicationContextException {
        initMappingSqlQueries();
    } //- initDao
    
    
    protected void initMappingSqlQueries() throws ApplicationContextException {
        getCites = new GetCites(getDataSource());
        getCitesForCluster = new GetCitesForCluster(getDataSource());
        getCite = new GetCite(getDataSource());
        getCiteRange = new GetCiteRange(getDataSource());
        insertCite = new InsertCite(getDataSource());
        insertContext = new InsertContext(getDataSource());
        getContexts = new GetContexts(getDataSource());
        setCluster = new SetCluster(getDataSource());
        deleteCites = new DeleteCites(getDataSource());
        deleteCite = new DeleteCite(getDataSource());
        deleteContexts = new DeleteContexts(getDataSource());
        countCitations = new CountCitations(getDataSource());
    }
    
    
    public List getCitations(String doi, boolean withContexts)
    throws DataAccessException {
        List citations = getCites.run(doi);
        if (withContexts) {
            for (Object o : citations) {
                Citation c = (Citation)o;
                Long id = new Long(Long.parseLong(c.getDatum(Citation.DOI_KEY)));
                List contexts = getContexts.run(id);
                for (Object oc : contexts) {
                    c.addContext((String)oc);
                }
            }
        }
        return citations;
        
    }  //- getCitations

    
    public List getCitationsForCluster(Long clusterid)
    throws DataAccessException {
        return getCitesForCluster.run(clusterid);
    }  //- getCitationsForCluster

    
    public Citation getCitation(long id) throws DataAccessException {
        return getCite.run(id);
    }  //- getCitation


    public List getCitations(long startID, int n) throws DataAccessException {
        return getCiteRange.run(startID, n);
    }  //- getCitations

    
    public void insertCitation(String doi, Citation citation)
    throws DataAccessException {
        insertCite.run(doi, citation);
        for (String context : citation.getContexts()) {
            insertContext.run(
                    new Long(Long.parseLong(
                            citation.getDatum(Citation.DOI_KEY))),
                    context);
        }
    }  //- insertCitations

    
    public List getCiteContexts(Long citationID) throws DataAccessException {
        return getContexts.run(citationID);
    }  //- getContexts
    
    
    public void insertCiteContexts(Long cid, List<String> contexts)
    throws DataAccessException {
        for (Iterator<String> it = contexts.iterator(); it.hasNext(); ) {
            insertContext.run(cid, it.next());
        }
    }  //- insertCitationContexts

    
    public void setCiteCluster(Citation citation, Long clusterID)
    throws DataAccessException {
        setCluster.run(citation, clusterID);
    }  //- setCluster
    
    
    public void deleteCitations(String doi) throws DataAccessException {
        deleteCites.run(doi);
    }  //- deleteCitations

    
    public void deleteCitation(Long citationID) throws DataAccessException {
        deleteCite.run(citationID);
    }  //- deleteCitation

    
    public void deleteCiteContexts(Long citationID) throws DataAccessException {
        deleteContexts.run(citationID);
    }  //- deleteContexts

    /* (non-Javadoc)
	 * @see edu.psu.citeseerx.dao2.CitationDAO#getNumberOfCitationsRecords()
	 */
	public Integer getNumberOfCitationsRecords() throws DataAccessException {
		return countCitations.run();
	} //- getNumberOfCitationsRecords



	private static final String DEF_GET_CITES_QUERY =
        "select id, cluster, authors, title, venue, venueType, year, " +
        "pages, editors, publisher, pubAddress, volume, number, tech, " +
        "raw, paperid, self from citations where paperid=?";

    private class GetCites extends MappingSqlQuery {
        
        public GetCites(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_CITES_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public Citation mapRow(ResultSet rs, int rowNum) throws SQLException {
            return mapCitation(rs);
        }
        
        public List run(String doi) {
            return execute(doi);
        }
        
    }  //- class GetCites
    
    
    private static final String DEF_GET_CITES_CLUST_QUERY =
        "select id, cluster, authors, title, venue, venueType, year, " +
        "pages, editors, publisher, pubAddress, volume, number, tech, " +
        "raw, paperid, self from citations where cluster=?";
    
    private class GetCitesForCluster extends MappingSqlQuery {
        
        public GetCitesForCluster(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_CITES_CLUST_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
        public Citation mapRow(ResultSet rs, int rowNum) throws SQLException {
            return mapCitation(rs);
        }
        
        public List run(Long clusterid) {
            return execute(clusterid);
        }
        
    }  //- class GetCitesForCluster
        
    
    private static final String DEF_GET_CITE_QUERY =
        "select id, cluster, authors, title, venue, venueType, year, " +
        "pages, editors, publisher, pubAddress, volume, number, tech, " +
        "raw, paperid, self from citations where id=?";

    private class GetCite extends MappingSqlQuery {
        
        public GetCite(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_CITE_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
        public Citation mapRow(ResultSet rs, int rowNum) throws SQLException {
            return mapCitation(rs);
        }
        
        public Citation run(Long id) {
            List list = execute(id);
            if (list.isEmpty()) {
                return null;
            } else {
                return (Citation)list.get(0);
            }
        }
        
    }  //- class GetCite
        
    
    private static final String DEF_GET_CITE_RANGE_QUERY =
        "select id, cluster, authors, title, venue, venueType, year, " +
        "pages, editors, publisher, pubAddress, volume, number, tech, " +
        "raw, paperid, self from citations where id>=? order by id limit ?";

    public class GetCiteRange extends MappingSqlQuery {
        
        public GetCiteRange(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_CITE_RANGE_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.INTEGER));
            compile();
        }
        
        public Citation mapRow(ResultSet rs, int rowNum) throws SQLException {
            return mapCitation(rs);
        }
        
        public List run(Long startID, int n) {
            Object[] params = new Object[] { startID, new Integer(n) };
            return execute(params);
        }
        
    }  //- class GetCiteRange
        
    
    private static Citation mapCitation(ResultSet rs) throws SQLException {
        Citation citation = new Citation();
        
        citation.setDatum(Citation.DOI_KEY, rs.getString("id"));
        citation.setClusterID(rs.getLong("cluster"));
        
        String[] authors = rs.getString("authors").split(",");
        for (int i=0; i<authors.length; i++) {
            citation.addAuthorName(authors[i]);
        }
        
        citation.setDatum(Citation.TITLE_KEY, rs.getString("title"));
        citation.setDatum(Citation.VENUE_KEY, rs.getString("venue"));
        citation.setDatum(Citation.VEN_TYPE_KEY,
                rs.getString("venueType"));
        citation.setDatum(Citation.YEAR_KEY, rs.getString("year"));
        citation.setDatum(Citation.PAGES_KEY, rs.getString("pages"));
        citation.setDatum(Citation.EDITORS_KEY, rs.getString("editors"));
        citation.setDatum(Citation.PUBLISHER_KEY,
                rs.getString("publisher"));
        citation.setDatum(Citation.PUB_ADDR_KEY,
                rs.getString("pubAddress"));
        citation.setDatum(Citation.VOL_KEY, rs.getString("volume"));
        citation.setDatum(Citation.NUMBER_KEY, rs.getString("number"));
        citation.setDatum(Citation.TECH_KEY, rs.getString("tech"));
        citation.setDatum(Citation.RAW_KEY, rs.getString("raw"));
        citation.setDatum(Citation.PAPERID_KEY, rs.getString("paperid"));
        citation.setSelf(rs.getBoolean("self"));
        
        return citation;
        
    }  //- mapCitation
    
    
    /* cluster, authors, title, venue, venuetype, year, pages, editors, 
     * publisher, pubAddress, volume, number, tech, raw, paperid, self */
    private static final String DEF_INSERT_CITE_QUERY =
        "insert into citations values (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
        "?, ?, ?, ?, ?, ?, ?)";
        
    private class InsertCite extends SqlUpdate {
        
        public InsertCite(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_CITE_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.BLOB));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.BLOB));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.INTEGER));
            declareParameter(new SqlParameter(Types.VARCHAR));            
            declareParameter(new SqlParameter(Types.BLOB));
            declareParameter(new SqlParameter(Types.VARCHAR));
            declareParameter(new SqlParameter(Types.TINYINT));
            setReturnGeneratedKeys(true);
            compile();
        }
        
        public int run(String doi, Citation citation) {
            citation.setDatum(Citation.PAPERID_KEY, doi);
            
            StringBuffer authorbuf = new StringBuffer();
            List<String> authors = citation.getAuthorNames();
            for (Iterator<String> ait = authors.iterator(); ait.hasNext(); ) {
                authorbuf.append(ait.next());
                if (ait.hasNext()) {
                    authorbuf.append(",");
                }
            }
            Integer year = null;
            try {
                year = Integer.parseInt(citation.getDatum(Citation.YEAR_KEY));
            } catch (Exception e) {}
            Integer vol = null;
            try {
                vol = Integer.parseInt(citation.getDatum(Citation.VOL_KEY));
            } catch (Exception e) {}
            Integer num = null;
            try {
                num = Integer.parseInt(citation.getDatum(Citation.NUMBER_KEY));
            } catch (Exception e) {}

            
            Object[] params = new Object[] {
                    citation.getClusterID(),
                    authorbuf.toString(),
                    citation.getDatum(Citation.TITLE_KEY),
                    citation.getDatum(Citation.VENUE_KEY),
                    citation.getDatum(Citation.VEN_TYPE_KEY),
                    year,
                    citation.getDatum(Citation.PAGES_KEY),
                    citation.getDatum(Citation.EDITORS_KEY),
                    citation.getDatum(Citation.PUBLISHER_KEY),
                    citation.getDatum(Citation.PUB_ADDR_KEY),
                    vol, num,
                    citation.getDatum(Citation.TECH_KEY),
                    citation.getDatum(Citation.RAW_KEY),
                    doi, new Boolean(citation.isSelf())
            };
            
            KeyHolder holder = new GeneratedKeyHolder();
            int n = update(params, holder);
            citation.setDatum(Citation.DOI_KEY,
                    (new Long(holder.getKey().longValue())).toString());
            return n;
        }
        
    }  //- class InsertCite
        
    
    /* id, citationid, context */
    private static final String DEF_INSERT_CONTEXT_QUERY =
        "insert into citationContexts values (NULL, ?, ?)";

    private class InsertContext extends SqlUpdate {
        
        public InsertContext(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_INSERT_CONTEXT_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.BLOB));
            compile();
        }
        
        public int run(Long cid, String context) {
            Object[] params = new Object[] { cid, context };
            return update(params);
        }
        
    }  //- class InsertContext
        
    
    private static final String DEF_GET_CONTEXTS_QUERY =
        "select context from citationContexts where citationid=?";
    
    private class GetContexts extends MappingSqlQuery {
        
        public GetContexts(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_GET_CONTEXTS_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString(1);
        }
        
        public List run(Long citationid) {
            return execute(citationid);
        }
        
    }  //- class GetContexts
    
    
    private static final String DEF_SET_CLUSTER_QUERY =
        "update citations set cluster=? where id=?";
    
    private class SetCluster extends SqlUpdate {
        
        public SetCluster(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_SET_CLUSTER_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
        public int run(Citation citation, Long clusterid) {
            Object[] params = new Object[] {
                    clusterid, new Long(
                            Long.parseLong(citation.getDatum(Citation.DOI_KEY)))
            };
            int n = update(params);
            citation.setClusterID(clusterid);
            return n;
        }
        
    }  //- class SetCluster
    
    
    private static final String DEF_DEL_CITATIONS_QUERY =
        "delete from citations where paperid=?";
    
    private class DeleteCites extends SqlUpdate {
        
        public DeleteCites(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_DEL_CITATIONS_QUERY);
            declareParameter(new SqlParameter(Types.VARCHAR));
            compile();
        }
        
        public int run(String doi) {
            return update(doi);
        }
        
    }  //- class DeleteCites
    
    
    private static final String DEF_DEL_CITATION_QUERY = 
        "delete from citations where id=?";
    
    private class DeleteCite extends SqlUpdate {
        
        public DeleteCite(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_DEL_CITATION_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
        public int run(Long citationid) {
            return update(citationid);
        }
        
    }  //- class DeleteCite
        
    
    private static final String DEF_DEL_CONTEXTS_QUERY =
        "delete from citationContexts where citationid=?";
    
    private class DeleteContexts extends SqlUpdate {
        
        public DeleteContexts(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_DEL_CONTEXTS_QUERY);
            declareParameter(new SqlParameter(Types.BIGINT));
            compile();
        }
        
        public int run(Long citationid) {
            return update(citationid);
        }
        
    }  //- class DeleteContexts
    
    private static final String DEF_COUNT_CITATIONS_QUERY =
        "select count(id) from citations";

    private class CountCitations extends MappingSqlQuery {
        
        public CountCitations(DataSource dataSource) {
            setDataSource(dataSource);
            setSql(DEF_COUNT_CITATIONS_QUERY);
            compile();
        } //- CountCitations.CountCitations
        
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt(1);
        } //- CountCitations.mapRow
        
        public Integer run() {
            List list = execute();
            if (list.isEmpty()) {
                return null;
            } else {
                return (Integer)list.get(0);
            }
        } //- CountCitations.run
        
    }  //- class CountCitations

}  //- class CitationDAOImpl
