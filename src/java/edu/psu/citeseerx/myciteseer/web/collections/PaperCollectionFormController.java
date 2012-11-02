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
package edu.psu.citeseerx.myciteseer.web.collections;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.dao.DataAccessException;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.Collection;
import edu.psu.citeseerx.myciteseer.domain.PaperCollection;
import edu.psu.citeseerx.myciteseer.domain.logic.MyCiteSeerFacade;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

public class PaperCollectionFormController extends SimpleFormController {

	// MyCiteSeer data access
	private MyCiteSeerFacade myciteseer;
	
	// CiteSeer data access
	private CSXDAO csxdao;

	public PaperCollectionFormController() {
		setSessionForm(true);
        setValidateOnBinding(false);
        setCommandName("paperCollectionForm");
        setFormView("AddPaperCollection");
	} //- PaperCollectionFormController
	
	public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
		this.myciteseer = myciteseer;
	} //- setMyciteseer
	
	public void setCSXDAO (CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO

	protected Object formBackingObject(HttpServletRequest request) 
	throws Exception {

		Account account = MCSUtils.getLoginAccount();
		PaperCollectionForm form = null;
		
		// User comes from interactive services
		String DOI = request.getParameter("doi");
		
		if (DOI != null) {
			Document doc = null;
			try {
	            doc = csxdao.getDocumentFromDB(DOI, false, false);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
	        if (doc != null) {
	        	String paperTitle = 
	        		doc.getDatum(Document.TITLE_KEY);
	        	paperTitle = (paperTitle != null && !paperTitle.matches("^\\s*$")) ?
	        			paperTitle : "unknown";
	        	form = new PaperCollectionForm(paperTitle, DOI);
	        }
	        else {
	        	form = new PaperCollectionForm();
	        }
		}
		else {
			form = new PaperCollectionForm();
		}
		List<Collection> userCollections = 
			myciteseer.getCollections(account.getUsername());
		form.setCollections(userCollections);
		return form;
	} //- formBackingObject
	
	protected void onBindAndValidate(HttpServletRequest request,
            Object command, BindException errors) throws Exception {

		PaperCollectionForm paperCollectionForm = (PaperCollectionForm)command;
		PaperCollection paperCollection = paperCollectionForm.getPaperCollection();
		errors.setNestedPath("paperCollection");
        getValidator().validate(paperCollection, errors);
        errors.setNestedPath("");
	} //- onBindAndValidate
	
	protected ModelAndView onSubmit(HttpServletRequest request,
            HttpServletResponse response, Object command,
            BindException errors) throws Exception {
		
		PaperCollectionForm paperCollectionForm = (PaperCollectionForm)command;
		try {
			myciteseer.addPaperToCollection(paperCollectionForm.getPaperCollection());
		}
		catch (DataAccessException ex) {
			ex.printStackTrace();
			errors.rejectValue("paperCollection.paperID", "COLLECTION_ERROR",
                    "There was an error adding this paper to the collection.");
            return showForm(request, response, errors);
		}
		
		return new ModelAndView(new RedirectView(getSuccessView()), "cid", 
				paperCollectionForm.getPaperCollection().getCollectionID());
		
	} //- onSubmit
}
