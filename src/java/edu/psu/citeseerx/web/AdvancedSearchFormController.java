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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.*;

import org.springframework.validation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import edu.psu.citeseerx.web.domain.AdvancedSearch;
import edu.psu.citeseerx.webutils.RedirectUtils;

/**
 * Provides model objects to advance search view.
 * @author Isaac Councill
 * @version $Rev: 810 $ $Date: 2008-12-02 14:05:57 -0500 (Tue, 02 Dec 2008) $
 */
public class AdvancedSearchFormController extends SimpleFormController {

    private String searchUrl = "/search";
    
    public void setSearchUrl(String searchUrl) {
        this.searchUrl = searchUrl;
    }
    
    public AdvancedSearchFormController() {
        setSessionForm(true);
        setValidateOnBinding(false);
        setCommandName("advancedSearch");
        setFormView("advanced_search");
    } //- AdvancedSearchFormController
    
    protected Object formBackingObject(HttpServletRequest request) {
        return new AdvancedSearch();
    } //- formBackingObject
    
    protected void onBindAndValidate(HttpServletRequest request,
            Object command, BindException errors) throws Exception {
        
        AdvancedSearch advSearch = (AdvancedSearch)command;
        getValidator().validate(advSearch, errors);

    } //- onBindAndValidate
    
    
    protected Map referenceData(HttpServletRequest request) {
    	Map<String, Object> model = new HashMap<String, Object>();
    	model.put("pagetitle", "Advanced Search");
    	model.put("pagedescription", "Employ the full power of our search " +
    			"facility to target your searches.");
    	model.put("pagekeywords", "search, advanced search, advanced");
        return model;
    } //- referenceData
    
    
    protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object command,
            BindException errors) throws Exception {

        onBindAndValidate(request, command, errors);
        if (errors.hasErrors()) {
            return showForm(request, response, errors);
        }
        
        AdvancedSearch advSearch = (AdvancedSearch)command;
        
        String path = searchUrl + advSearch.getQuery();
        RedirectUtils.sendRedirect(request, response, path);
        return null;
        
    }  //- onSubmit
    
}  //- class AdvancedSearchFormController
