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

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.*;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.dao2.logic.CiteClusterDAO;
import edu.psu.citeseerx.domain.Author;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.domain.DocumentFileInfo;
import edu.psu.citeseerx.web.domain.*;
import edu.psu.citeseerx.myciteseer.web.utils.FoulWordFilter;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.domain.MCSConfiguration;
import edu.psu.citeseerx.updates.UpdateManager;


/**
 * Controller used to handle user corrections to papers
 * @author Isaac Councill
 * @version $$Rev: 810 $$ $$Date: 2008-12-02 14:05:57 -0500 (Tue, 02 Dec 2008) $$
 */
public class CorrectionFormController extends SimpleFormController {

    private CSXDAO csxdao;
    
    public void setCSXDAO (CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    

    private CiteClusterDAO citedao;
    
    public void setCiteClusterDAO(CiteClusterDAO citedao) {
        this.citedao = citedao;
    } //- setCiteClusterDAO
    
    
    private MyCiteSeerFacade myciteseer;
    
    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    } //- setMyCiteSeer
    
    
    private UpdateManager updateManager;
    
    public void setUpdateManager(UpdateManager updateManager) {
        this.updateManager = updateManager;
    } //- setUpdateManager
    
    
    private FoulWordFilter foulWordFilter;
    
    public void setFoulWordFilter(FoulWordFilter foulWordFilter) {
        this.foulWordFilter = foulWordFilter;
    } //-setFoulWordFilter
    
    
    public CorrectionFormController() {
        setValidateOnBinding(false);
        setCommandName("correction");
        setFormView("correct");
    } //CorrectionFormController
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
     */
    protected Object formBackingObject(HttpServletRequest request)
    throws Exception {

        String doi = request.getParameter("doi");
        String cid = request.getParameter("cid");
        
        if (doi == null && cid != null) {
            try {
                Long cluster = Long.parseLong(cid);
                List<String> dois = citedao.getPaperIDs(cluster);
                doi = dois.get(0);
            } catch (Exception e) { };
        }
        DocumentContainer dc = new DocumentContainer();
        if (doi != null) {
            try {
                dc = DocumentContainer.fromDocument(
                        csxdao.getDocumentFromDB(doi));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dc;
        
    }  //- formBackingObject

    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.BaseCommandController#onBindAndValidate(javax.servlet.http.HttpServletRequest, java.lang.Object, org.springframework.validation.BindException)
     */
    protected void onBindAndValidate(HttpServletRequest request,
            Object command, BindException errors) throws Exception {
        
        String foul;
        DocumentContainer dc = (DocumentContainer)command;
        
        if (dc.getPaperID() == null) {
            errors.reject("INVALID_DOI", "No DOI specified");
        } else {
            Document doc = csxdao.getDocumentFromDB(dc.getPaperID());
            if (doc == null) {
                errors.reject("INVALID_DOI", "Invalid DOI specified");
            }
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title",
                "TITLE_REQUIRED", "Title is required.");
        if ((foul = foulWordFilter.findFoulWord(dc.getTitle())) != null) {
            errors.rejectValue("title", "FOUL_WORD",
                    "Flagged as innappropriate content.");
            foul = null;
        }
        
        List<AuthorContainer> authors = dc.getAuthors();
        if (!dc.hasAuthors()) {
            errors.reject("AUTHOR_REQUIRED", "At least one author is required");
        }
        for (int i=0; i<authors.size(); i++) {
            AuthorContainer ac = (AuthorContainer)authors.get(i);
            if (!ac.getDeleted()) {
                String authPrefix =  "authors["+i+"].";
                ValidationUtils.rejectIfEmptyOrWhitespace(errors,
                        authPrefix+"name", "AUTHOR_NAME_REQUIRED",
                        "Author name is required.");
                if ((foul = foulWordFilter.findFoulWord(ac.getName()))
                        != null) {
                    errors.rejectValue(authPrefix+"name", "FOUL_WORD",
                            "Flagged as innappropriate content.");
                    foul = null;
                }
                if ((foul = foulWordFilter.findFoulWord(ac.getAffil()))
                        != null) {
                    errors.rejectValue(authPrefix+"affil", "FOUL_WORD",
                            "Flagged as innappropriate content.");
                    foul = null;
                }
                if ((foul = foulWordFilter.findFoulWord(ac.getAddress()))
                        != null) {
                    errors.rejectValue(authPrefix+"address", "FOUL_WORD",
                            "Flagged as innappropriate content.");
                    foul = null;
                }
            }
        }
        if ((foul = foulWordFilter.findFoulWord(dc.getAbs()))
                != null) {
            errors.rejectValue("abs", "FOUL_WORD",
                    "Flagged as innappropriate content.");
            foul = null;
        }        
        if ((foul = foulWordFilter.findFoulWord(dc.getVenue()))
                != null) {
            errors.rejectValue("venue", "FOUL_WORD",
                    "Flagged as innappropriate content.");
            foul = null;
        }        
        if ((foul = foulWordFilter.findFoulWord(dc.getPublisher()))
                != null) {
            errors.rejectValue("publisher", "FOUL_WORD",
                    "Flagged as innappropriate content.");
            foul = null;
        }        
        if ((foul = foulWordFilter.findFoulWord(dc.getPubAddr()))
                != null) {
            errors.rejectValue("pubAddr", "FOUL_WORD",
                    "Flagged as innappropriate content.");
            foul = null;
        }        
        if ((foul = foulWordFilter.findFoulWord(dc.getTech()))
                != null) {
            errors.rejectValue("tech", "FOUL_WORD",
                    "Flagged as innappropriate content.");
            foul = null;
        }
        try {
            if (dc.getYear() != null && !dc.getYear().equals(""))
                Integer.parseInt(dc.getYear());
        } catch (NumberFormatException e) {
            errors.rejectValue("year", "INT_REQUIRED", "Integer required.");
        }
        try {
            if (dc.getVol() != null && !dc.getVol().equals(""))
                Integer.parseInt(dc.getVol());
        } catch (NumberFormatException e) {
            errors.rejectValue("vol", "INT_REQUIRED", "Integer required.");
        }
        try {
            if (dc.getNum() != null && !dc.getNum().equals(""))
                Integer.parseInt(dc.getNum());
        } catch (NumberFormatException e) {
            errors.rejectValue("num", "INT_REQUIRED", "Integer required.");
        }
        
    }  //- onBindAndValidate
    
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.SimpleFormController#referenceData(javax.servlet.http.HttpServletRequest)
     */
    protected Map referenceData(HttpServletRequest request) throws Exception {
        
        String doi = null;
        String cid = request.getParameter("cid");
        
        if (cid != null) {
            try {
                Long cluster = Long.parseLong(cid);
                List<String> dois = citedao.getPaperIDs(cluster);
                doi = dois.get(0);
            } catch (Exception e) { };
        }
        
        if (doi == null) {
            doi = request.getParameter("doi");
        }
        
        if (doi == null) {
            return new HashMap();
        }

        Document doc = null;
        try {
            doc = csxdao.getDocumentFromDB(doi, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Map<String, Object> model = new HashMap<String, Object>();
        MCSConfiguration config = myciteseer.getConfiguration();
        model.put("correctionsEnabled",
                new Boolean(config.getCorrectionsEnabled()));
        model.put("pagetype", "");

        if (doc == null) {
            model.put("error", new Boolean(true));
            model.put("errMsg", "Invalid DOI specified");
            return model;
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

        model.put("pagetitle", "Correct: "+title);
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
        model.put("rep", rep);
        model.put("ncites", doc.getNcites());
        model.put("selfCites", doc.getSelfCites());
        model.put("fileTypes", csxdao.getFileTypes(doi, rep));
        
        return model;
        
    }  //- referenceData
    
    
    /* (non-Javadoc)
     * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
     */
    protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object command,
            BindException errors) throws Exception {
        
        MCSConfiguration config = myciteseer.getConfiguration();
        if (!config.getCorrectionsEnabled()) {
            return super.onSubmit(request, response, command, errors);
        }

        onBindAndValidate(request, command, errors);
        if (errors.hasErrors()) {
            return showForm(request, response, errors);
        }
        
        DocumentContainer container = (DocumentContainer)command;
        Document doc = csxdao.getDocumentFromDB(
                container.getPaperID(),
                false,  // don't get citation contexts
                true);  // retrieve provenance info
                
        if (doc == null) {
            // TODO add error page
            System.out.println("NO DOC!!!");
            return super.onSubmit(request, response, command, errors);            
        }

        String userid = MCSUtils.getLoginAccount().getUsername();
        if (userid == null) {
            // TODO add error page
            System.out.println("NO USERID!!!");
            return super.onSubmit(request, response, command, errors);
        }
        
        container.toDocument(doc, "user correction");
        
        updateManager.doCorrection(doc, userid);

        ModelAndView mav = onSubmit(command, errors);
        Map refData = referenceData(request, command, errors);
        mav.getModel().putAll(refData);
        return mav;
        
    }  //- onSubmit
        
}  //- class CorrectionFormController
