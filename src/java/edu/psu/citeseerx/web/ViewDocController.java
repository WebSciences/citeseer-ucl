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

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.dao2.logic.CiteClusterDAO;
import edu.psu.citeseerx.domain.*;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;
import edu.psu.citeseerx.webutils.RedirectUtils;
import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.dao2.RepositoryMap;
import edu.psu.citeseerx.utility.*;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import java.io.IOException;
import java.io.File;
import java.util.*;

/**
 * Provides model objects to document summary view.
 * @author Isaac Councill
 * @version $Rev: 810 $ $Date: 2008-12-02 14:05:57 -0500 (Tue, 02 Dec 2008) $
 */
public class ViewDocController implements Controller {

    private CSXDAO csxdao;
    
    public void setCSXDAO (CSXDAO csxdao) {
        this.csxdao = csxdao;
    }
    
    
    private CiteClusterDAO citedao;
    
    public void setCiteClusterDAO(CiteClusterDAO citedao) {
        this.citedao = citedao;
    }
    
    
    private int maxTags = 5;
    
    public void setMaxTags(int maxTags) {
        this.maxTags = maxTags;
    }

    
    private RepositoryMap repMap;
    
    public void setRepositoryMap(RepositoryMap repMap) {
        this.repMap = repMap;
    }
    
    
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        
    	String errorTitle = "Document Not Found";
        String doi = null;
        String cid = request.getParameter("cid");
        Map<String, Object> model = new HashMap<String, Object>();
        
        if (cid != null) {
        	Long cluster;
        	try {
        		cluster = Long.parseLong(cid);
        	}catch (NumberFormatException e) {
        		e.printStackTrace();
        		model.put("pagetitle", errorTitle);
        		return new ModelAndView("viewDocError", model);
			}
            List<String> dois = citedao.getPaperIDs(cluster);
		    if(!dois.isEmpty()) {
            	doi = dois.get(0);
            	RedirectUtils.sendDocumentCIDRedirect(request, response, doi);
            	return null;
		    }
		    else {
		    	model.put("pagetitle", errorTitle);
        		return new ModelAndView("viewDocError", model);
		    }
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
        
        if (doc == null || doc.isPublic() == false) {
        	model.put("doi", doi);
        	model.put("pagetitle", errorTitle);
            return new ModelAndView("baddoi", model);
        }

        
        if (bxml) {
            response.getWriter().print(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            Account account = MCSUtils.getLoginAccount();
            if (bsysData && account != null && account.isAdmin()) {
                response.getWriter().print(doc.toXML(true));
            } else {
                response.getWriter().print(doc.toXML(false));                
            }
            return null;
            //return new ModelAndView("xml", model);
        }
        String authors = "";
        int c = 1;
        for (Iterator<Author> it = doc.getAuthors().iterator(); it.hasNext(); ) {
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
        List<ThinDoc> citations = null;
        if (clusterID != null) {
            citations = citedao.getCitedDocuments(clusterID, 0, 50);
            for (Object cite : citations) {
                SolrSelectUtils.prepCitation((ThinDoc)cite);
            }
            Collections.sort(citations, new CitationComparator());
        }
        
        String repID = doc.getFileInfo().getDatum(DocumentFileInfo.REP_ID_KEY);
        String relPath = FileNamingUtils.getDirectoryFromDOI(doi);
        relPath += "citechart.png";
        String chartPath = repMap.buildFilePath(repID, relPath);
        File chartFile = new File(chartPath);
        if (chartFile.exists()) {
            model.put("chartfile", new Boolean(true));
        } else {
            model.put("chartfile", new Boolean(false));
        }
        
        String bibtex =
            BiblioTransformer.toBibTeX(DomainTransformer.toThinDoc(doc));
        bibtex = SafeText.cleanXML(bibtex);
        bibtex = bibtex.replaceAll("\\n", "<br/>");
        bibtex = bibtex.replaceAll("\\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
        model.put("bibtex", bibtex);
        
        String coins =
        	BiblioTransformer.toCOinS(DomainTransformer.toThinDoc(doc), 
        			request.getRequestURL().toString());
        model.put("coins", coins);
        
        List<Tag> tags = doc.getTags();
        if (tags.size() > maxTags) {
            tags = tags.subList(0, maxTags);
        }
        model.put("pagetype", "summary");
        model.put("pagetitle", title);
        model.put("pagedescription", "Document Details (Isaac Councill, " +
        		"Lee Giles): " + abs);
        model.put("pagekeywords", authors);
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
        model.put("tags", tags);
        model.put("citations", citations);
        model.put("fileTypes", csxdao.getFileTypes(doi, repID));
        
        String banner = csxdao.getBanner();
        if (banner != null && banner.length() > 0) {
            model.put("banner", banner);
        }
        
        return new ModelAndView("viewDoc", model);
    }
    
}  //- ViewDocController


class CitationComparator implements java.util.Comparator {
    public int compare(Object o1, Object o2) {
        if (((ThinDoc)o1).getNcites() > ((ThinDoc)o2).getNcites()) {
            return -1;
        }
        if (((ThinDoc)o1).getNcites() < ((ThinDoc)o2).getNcites()) {
            return 1;
        }
        return 0;
    }
}
