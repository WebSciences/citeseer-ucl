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

import edu.psu.citeseerx.domain.*;
import edu.psu.citeseerx.utility.BiblioTransformer;
import edu.psu.citeseerx.utility.DateUtils;
import edu.psu.citeseerx.webutils.RedirectUtils;
import edu.psu.citeseerx.dao2.logic.CSXDAO;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import java.io.*;
import java.util.*;
import java.net.*;

import org.json.*;

/**
 * Handle user searches
 * @author Isaac Councill 
 * @author Juan Pablo Fernandez Ramirez
 * Version: $$Rev: 810 $$ $$Date: 2008-12-02 14:05:57 -0500 (Tue, 02 Dec 2008) $$
 */
public class SearchController implements Controller {
    
    private String solrSelectUrl;
    
    public void setSolrSelectUrl(String solrSelectUrl)
    throws MalformedURLException {
        new URL(solrSelectUrl);
        this.solrSelectUrl = solrSelectUrl;
    }
    
    
    private CSXDAO csxdao;
    
    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    

    private int nrows = 10;
    
    public void setNrows(int nrows) {
        this.nrows = nrows;
    } //- setNrows
    
    
    private int feedSize = 100;
    
    public void setFeedSize(int feedSize) {
        this.feedSize = feedSize;
    } //- setFeedSize
    
    
    private int maxresults = 500;
    
    public void setMaxResults(int maxresults) {
        this.maxresults = maxresults;
    } //- setMaxResults
    
    private String systemBaseURL;
    
    /**
	 * @param systemBaseURL Base URL for the system
	 */
	public void setSystemBaseURL(String systemBaseURL) {
		this.systemBaseURL = systemBaseURL;
	} //- setSystemBaseURL
    
    private static final String SORT_RLV  = "rlv";
    private static final String SORT_CITE = "cite";
    private static final String SORT_DATE = "date";
    private static final String SORT_ASCDATE = "ascdate";
    private static final String SORT_TIME = "recent";

    private static final HashMap<String,String> sortTypes =
        new HashMap<String,String>();

    static {
        sortTypes.put(SORT_RLV, "");
        sortTypes.put(SORT_CITE, "sort=ncites+desc");
        sortTypes.put(SORT_DATE, "sort=year+desc");
        sortTypes.put(SORT_ASCDATE, "sort=year+asc");
        sortTypes.put(SORT_TIME, "sort=vtime+desc");
    }
    
    
    private static final String RSS = "rss";
    private static final String ATOM = "atom";
    
    private static final HashSet<String> feedTypes = new HashSet<String>();
    
    static {
        feedTypes.add(RSS);
        feedTypes.add(ATOM);
    }

    
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException, JSONException {
        
        String qi = request.getParameter("q");

        if (qi == null || qi.equals("")) {
            RedirectUtils.sendRedirect(request, response, "/");
            return null;
        }

        String q = normalizeQuery(qi);

        String t = request.getParameter("t");
        if (t != null && t.equals("auth")) {
            q = "authorNorms:("+q+")";
        }
        
        String feed = request.getParameter("feed");
        if (!feedTypes.contains(feed)) {
            feed = null;
        }
        
        String ic = request.getParameter("ic");
        boolean incolFilter = false;
        if (ic == null) {
            incolFilter = true;
        }

        String sort = request.getParameter("sort");
        if (sort == null || !sortTypes.containsKey(sort)) {
            sort = SORT_RLV;
        }
                
        Integer start = 0;
        try {
            start = new Integer(request.getParameter("start"));
        } catch (Exception e) { }

        StringBuffer urlBuffer = new StringBuffer();
        urlBuffer.append(solrSelectUrl);
        urlBuffer.append("?");
        urlBuffer.append("q=");
        urlBuffer.append(URLEncoder.encode(q, "UTF-8"));

        if (sort != null && !sort.equals(SORT_RLV)) {
            urlBuffer.append("&");
            urlBuffer.append(sortTypes.get(sort));
        }
        
        urlBuffer.append("&");
        urlBuffer.append("rows=");
        if (feed != null) {
            urlBuffer.append(feedSize);
        } else {
            urlBuffer.append(nrows);
        }

        urlBuffer.append("&");
        urlBuffer.append("start=");
        urlBuffer.append(start);

        urlBuffer.append("&");
        urlBuffer.append("qt=");
        if (q.indexOf(':') != -1) {
            urlBuffer.append("standard");
        } else {
            urlBuffer.append("dismax");
        }
        urlBuffer.append("&hl=true");
        urlBuffer.append("&wt=json");

        if (incolFilter) {
            urlBuffer.append("&fq=incol:true");
        }
        
        boolean error = false;
        String errMsg = null;
        
        if (start >= maxresults) {
            error = true;
            errMsg = "Only the top " + maxresults + " hits are available.  "
            + "Please try a more specific query.";
        }
        
        Integer numFound = 0;
        List<ThinDoc> hits = new ArrayList<ThinDoc>();
        List<String> coins = new ArrayList<String>();
        try {
            if (!error) {
                JSONObject output =
                    SolrSelectUtils.doJSONQuery(urlBuffer.toString());

                JSONObject responseObj = output.getJSONObject("response");
                JSONObject respHeaderObj =
                    output.getJSONObject("responseHeader");
        
                numFound = responseObj.getInt("numFound");

                hits = SolrSelectUtils.buildHitListJSON(output);
                
                // Obtain COinS representation for hits.
                String url = request.getRequestURL().toString();
                url = url.replace("search", "viewdoc/summary");
                coins = BiblioTransformer.toCOinS(hits, url);
            }
        } catch (SolrException e) {
            error = true;
            int code = e.getStatusCode();
            if (code == 400) {
                errMsg = "Invalid query type.  " +
                        "Please check your syntax.";
            } else {
                errMsg = "<p><span class=\"char_emphasized\">" +
                        "Error processing query.</span></p><br>" +
                        "<p>The most likely cause of this condition " +
                        "is a malformed query. Please check your query  " +
                        "syntax and, if the problem persists, " +
                        "contact an admin for assistance.</p>";
            }
        }

        String rlvQuery, citeQuery, dateQuery, ascDateQuery, timeQuery;
        String rssUrl, atomUrl;
        
        StringBuffer nextPageParams = new StringBuffer();
        nextPageParams.append("q=");
        nextPageParams.append(URLEncoder.encode(qi, "UTF-8"));
        if (ic != null) {
            nextPageParams.append("&amp;ic=1");
        }
        if (t != null) {
            nextPageParams.append("&amp;t=");
            nextPageParams.append(URLEncoder.encode(t, "UTF-8"));
        }
        
        rlvQuery = citeQuery = dateQuery = ascDateQuery = timeQuery =
            nextPageParams.toString();
        citeQuery += "&amp;sort="+SORT_CITE;
        dateQuery += "&amp;sort="+SORT_DATE;
        ascDateQuery += "&amp;sort="+SORT_ASCDATE;
        timeQuery += "&amp;sort="+SORT_TIME;

        if (sort != null) {
            nextPageParams.append("&amp;sort=");
            nextPageParams.append(URLEncoder.encode(sort, "UTF-8"));
        }

        rssUrl = atomUrl = nextPageParams.toString();
        rssUrl += "&amp;feed="+RSS;
        atomUrl += "&amp;feed="+ATOM;
        
        nextPageParams.append("&amp;start=");
        nextPageParams.append(start+nrows);
        
        // Generate title, description and keywords meta tag values.
        String pageTitle, pageDescription, pageKeywords;
        
        pageTitle = "Search Results";
        if (t != null && t.equals("auth")) {
        	pageTitle = "Author Search Results";
        }
        pageTitle += " &mdash; " + qi;
        pageKeywords = qi;
        pageDescription = "Scientific articles matching the query: " + qi;
        
        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put("pagetitle", pageTitle);
        model.put("pagedescription", pageDescription);
        model.put("pagekeywords", pageKeywords);
        model.put("query", qi);
        model.put("error", new Boolean(error));
        model.put("errorMsg", errMsg);
        model.put("nfound", numFound);
        model.put("start", start);
        model.put("nrows", nrows);
        model.put("sorttype", sort);
        model.put("rlvq", rlvQuery);
        model.put("citeq", citeQuery);
        model.put("dateq", dateQuery);
        model.put("ascdateq", ascDateQuery);
        model.put("timeq", timeQuery);
        model.put("rss", rssUrl);
        model.put("atom", atomUrl);
        model.put("dblpparams", "author="+qi);
        if (start+nrows < numFound && !error) {
            model.put("nextpageparams", nextPageParams.toString());
        }
        model.put("hits", (!error) ? hits : new ArrayList<ThinDoc>());
        model.put("coins", (!error) ? coins : new ArrayList<String>());

        if (feed != null) {
            if (feed.equals(RSS)) {
                return rssView(request, model);
            }
            if (feed.equals(ATOM)) {
                return atomView(request, model);
            }
        }
        String banner = csxdao.getBanner();
        if (banner != null && banner.length() > 0) {
            model.put("banner", banner);
        }
        
        return new ModelAndView("searchDocs", model);
        
    }  //- handleRequest
    
    
    private static String normalizeQuery(String q) {
        q = q.replaceAll("author\\:", "authorNorms:");
        return q;
    } //- normalizeQuery
    
    
    private ModelAndView rssView(HttpServletRequest request, Map<String, Object> model) {
        String feedTitle = "Documents: "
            + (String)model.get("query");
        String feedLink = (String)model.get("rss");
        String feedDate =
            DateUtils.formatRFC822(new Date(System.currentTimeMillis()));
        String feedDesc = "document results for the query: "
            + (String)model.get("query");
        
        feedLink = systemBaseURL + "/search?" + feedLink;
        
        model.put("feedTitle", feedTitle);
        model.put("feedLink", feedLink);
        model.put("feedDate", feedDate);
        model.put("feedDesc", feedDesc);
        model.put("baseUrl", systemBaseURL);
        
        return new ModelAndView("feeds/rss", model);
    } //- rssView
    
    
    private ModelAndView atomView(HttpServletRequest request, Map<String, Object> model) {

        String feedTitle = "Documents: "
            + (String)model.get("query");
        String feedLink = (String)model.get("atom");
        String feedDate =
            DateUtils.formatRFC3339(new Date(System.currentTimeMillis()));

        feedLink = systemBaseURL + "/search?" + feedLink;

        model.put("feedTitle", feedTitle);
        model.put("feedLink", feedLink);
        model.put("feedDate", feedDate);
        model.put("baseUrl", systemBaseURL);

        return new ModelAndView("feeds/atom", model);
    } //- atomView

    
}  //- class SearchController
