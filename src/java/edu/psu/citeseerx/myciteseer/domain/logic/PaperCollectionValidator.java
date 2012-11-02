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
package edu.psu.citeseerx.myciteseer.domain.logic;

import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.PaperCollection;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

public class PaperCollectionValidator implements Validator {

	// MyCiteSeer data access
	private MyCiteSeerFacade myciteseer;
	
	private MessageSource messageSource;
	
	// CiteSeer data access
	private CSXDAO csxdao;
	
	public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    } //- setMessageSource
	
    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    } //- setMyCiteSeer
    
    public void setCSXDAO (CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
	
	public boolean supports(Class clazz) {
		return PaperCollection.class.isAssignableFrom(clazz);
	} //- supports

	public void validate(Object obj, Errors errors) {
		
		PaperCollection paperCollection = (PaperCollection)obj;
		Account account = MCSUtils.getLoginAccount();
		validatePaperID(paperCollection.getPaperID(), errors);
		validateCollectionID(paperCollection.getCollectionID(), errors);
		if (myciteseer.isPaperInCollection(
				paperCollection, account)) {
			errors.rejectValue("paperID", "PAPER_ALREADY_IN_COLLECTION", 
				"The specified paper is already in the specified collection.");
		}

	} //- validate
	
	public void validatePaperID(String paperID, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "paperID",
                "PAPER_REQUIRED", "Please specify a paper.");
		if (paperID == null) {
			errors.rejectValue("paperID", "PAPER_REQUIRED", 
					"Please specify a paper.");
		}
		else {
			// is a valid paper?
			Document doc = null;
			try {
	            doc = csxdao.getDocumentFromDB(paperID, false, false);
	        } catch (Exception e) {
	            e.printStackTrace();
	            errors.rejectValue("paperID", "INVALID_DOI", 
					"The specified paper doesn't exists.");
	        }
	        if (doc == null) {
	        	errors.rejectValue("paperID", "INVALID_DOI", 
	        			"The specified paper doesn't exists.");
	        }
		}
	} //- validatePaperID

	public void validateCollectionID(long collectionID, Errors errors) {
		Account account = MCSUtils.getLoginAccount();
		
		ValidationUtils.rejectIfEmpty(errors, "collectionID",
                "COLLECTION_REQUIRED", "Please specify a collection.");
		try {
			if (!myciteseer.isUserCollection(collectionID, account)) {
				errors.rejectValue("collectionID", "INVALID_COLLECTION", 
						"The specified collection doesn't exists.");
			}
		}catch (Exception e) {
            errors.rejectValue("collectionID", "UNKNOWN_ERROR",
                    "An error occurred during the processing of " +
                    "your request.  Please try again later.");
		}    
	} //- validateCollectionID
}
