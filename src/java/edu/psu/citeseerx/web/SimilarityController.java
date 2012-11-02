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
package edu.psu.citeseerx.web;

import edu.psu.citeseerx.dao2.logic.*;
import edu.psu.citeseerx.domain.*;
import edu.psu.citeseerx.webutils.RedirectUtils;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import java.sql.SQLException;
import java.io.IOException;
import java.util.*;

import org.json.*;

/**
 * Provides model objects to document similarity view.
 * @author Isaac Councill
 * @version $Rev: 810 $ $Date: 2008-12-02 14:05:57 -0500 (Tue, 02 Dec 2008) $
 */
public class SimilarityController implements Controller {

    private CSXDAO csxdao;
    
    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    }
    
    
    private CiteClusterDAO citedao;
    
    public void setCiteClusterDAO(CiteClusterDAO citedao) {
        this.citedao = citedao;
    }
    
    
    private String solrSelectUrl;
    
    public void setSolrSelectUrl(String solrSelectUrl) {
        this.solrSelectUrl = solrSelectUrl;
    }
    
    
    private int maxQueryTerms = 20;
    
    public void setMaxQueryTerms(int maxQueryTerms) {
        this.maxQueryTerms = maxQueryTerms;
    }
    
    
    private static final String SIM_AB  = "ab";
    private static final String SIM_CC = "cc";

    private static final HashSet<String> simTypes = new HashSet<String>();

    static {
        simTypes.add(SIM_AB);
        simTypes.add(SIM_CC);
    }

    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException,
            SolrException, JSONException, SQLException {
        
        String doi = null;
        String errorTitle = "Document Not Found";
        String cid = request.getParameter("cid");
        Long cluster;
        Map<String, Object> model = new HashMap<String, Object>();
        
        if (cid != null) {
        	try {
        		cluster = Long.parseLong(cid);
        	}catch (NumberFormatException e) {
        		e.printStackTrace();
        		model.put("pagetitle", errorTitle);
        		return new ModelAndView("viewDocError", model);
			}
            List<String> dois = citedao.getPaperIDs(cluster);
            doi = dois.get(0);
            RedirectUtils.sendDocumentCIDRedirect(request, response, doi);
            return null;
        }
        
        if (doi == null) {
            doi = request.getParameter("doi");
        }
        
        if (doi == null) {
        	model.put("pagetitle", errorTitle);
    		return new ModelAndView("viewDocError", model);
        }
        
        
        String xml = request.getParameter("xml");
        boolean bxml = false;
        try {
            bxml = Boolean.parseBoolean(xml);
        } catch (Exception e) {}

        String src = request.getParameter("src");
        boolean bsrc = false;
        try {
            if (bxml) {
                bsrc = Boolean.parseBoolean(src);
            }
        } catch (Exception e) {}

        String sysData = request.getParameter("sysData");
        boolean bsysData = false;
        try {
            bsysData = Boolean.parseBoolean(sysData);
        } catch (Exception e) {}

        Document doc = null;
        try {
            doc = csxdao.getDocumentFromDB(doi, false, bsrc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (doc == null) {
        	model.put("doi", doi);
        	model.put("pagetitle", errorTitle);
            return new ModelAndView("baddoi", model);
        }

        
        String authors = "";
        int c = 1;
        for (Iterator<Author> it = doc.getAuthors().iterator();
        it.hasNext(); ) {
            authors += it.next().getDatum(Author.NAME_KEY);
            if (it.hasNext()) {
                authors += ", ";
            }
            c++;
        }

        String title = doc.getDatum(Document.TITLE_KEY);
        String abs =  doc.getDatum(Document.ABSTRACT_KEY);
        String venue = doc.getDatum(Document.VENUE_KEY);
        String year = doc.getDatum(Document.YEAR_KEY);

        DocumentFileInfo finfo = doc.getFileInfo();
        String url = finfo.getUrls().get(0);
        String rep = finfo.getDatum(DocumentFileInfo.REP_ID_KEY);

        Long clusterID = doc.getClusterID();
        
        model.put("pagetype", "similar");
        model.put("pagetitle", "Similarity Options: "+title);
        model.put("pagekeywords", authors);
        model.put("pagedescription", "Document Details (Isaac Councill, " +
        		"Lee Giles): " + abs);
        model.put("title", title); 
        model.put("authors", authors);            
        model.put("abstract", abs);
        model.put("venue", venue);
        model.put("year", year);
        model.put("url", url);
        model.put("doi", doi);
        model.put("clusterid", clusterID);
        model.put("rep", rep);
        model.put("ncites", doc.getNcites());
        model.put("selfCites", doc.getSelfCites());
        model.put("fileTypes", csxdao.getFileTypes(doi, rep));

        String banner = csxdao.getBanner();
        if (banner != null && banner.length() > 0) {
            model.put("banner", banner);
        }
        
        String type = request.getParameter("type");
        if (type == null || !simTypes.contains(type)) {
            return new ModelAndView("docSimilarity", model);
            //default page
        }

        List<ThinDoc> citations = new ArrayList<ThinDoc>();
        if (type.equals(SIM_AB)) {
            citations = doABQuery(clusterID);
            model.put("pagetitle",
                    "Active Bibliography: "+title);
            model.put("citations", citations);
            return new ModelAndView("activeBib", model);
        }
        if (type.equals(SIM_CC)) {
            citations = doCCQuery(clusterID);
            model.put("pagetitle",
                    "Related by Co-Citation: "+title);
            model.put("citations", citations);
            return new ModelAndView("coCite", model);
        }
        
        return new ModelAndView("viewDoc", model);

    }  //- handleRequest
    
    
    private List<ThinDoc> doABQuery(Long clusterid) throws SQLException,
    SolrException, JSONException, IOException {

        List<Long> cited = citedao.getCitedClusters(clusterid, maxQueryTerms);
        if (cited.isEmpty()) return new ArrayList<ThinDoc>();
        
        StringBuffer urlBuffer = new StringBuffer();
        urlBuffer.append(solrSelectUrl);
        urlBuffer.append("?qt=citesim&q=cites:(");
        for (Iterator<Long> it = cited.iterator(); it.hasNext(); ) {
            urlBuffer.append(it.next());
            if (it.hasNext()) {
                urlBuffer.append("+OR+");
            }
        }
        urlBuffer.append(")");
        
        JSONObject output =
            SolrSelectUtils.doJSONQuery(urlBuffer.toString());

        JSONObject responseObj = output.getJSONObject("response");
        JSONObject respHeaderObj =
            output.getJSONObject("responseHeader");

        List<ThinDoc> hits =
            SolrSelectUtils.buildHitListJSON(output, clusterid);
        
        return hits;
        
    }  //- doABQuery

    
    private List<ThinDoc> doCCQuery(Long clusterid) throws SQLException,
    SolrException, JSONException, IOException {

        List<Long> citing = citedao.getCitingClusters(clusterid, maxQueryTerms);
        if (citing.isEmpty()) return new ArrayList<ThinDoc>();
        
        StringBuffer urlBuffer = new StringBuffer();
        urlBuffer.append(solrSelectUrl);
        urlBuffer.append("?qt=citesim&q=citedby:(");
        for (Iterator<Long> it = citing.iterator(); it.hasNext(); ) {
            urlBuffer.append(it.next());
            if (it.hasNext()) {
                urlBuffer.append("+OR+");
            }
        }
        urlBuffer.append(")");
        
        JSONObject output =
            SolrSelectUtils.doJSONQuery(urlBuffer.toString());

        JSONObject responseObj = output.getJSONObject("response");
        JSONObject respHeaderObj =
            output.getJSONObject("responseHeader");

        List<ThinDoc> hits =
            SolrSelectUtils.buildHitListJSON(output, clusterid);
        
        return hits;

    }  //- doCCQuery

}  //- class SimilarityController
