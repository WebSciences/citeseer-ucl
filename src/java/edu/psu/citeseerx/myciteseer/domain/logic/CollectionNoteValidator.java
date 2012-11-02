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

import edu.psu.citeseerx.myciteseer.domain.Account;
import edu.psu.citeseerx.myciteseer.domain.CollectionNote;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

public class CollectionNoteValidator implements Validator {

	// MyCiteSeer data access
	private MyCiteSeerFacade myciteseer;
	
	private MessageSource messageSource;
	
	public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    } //- setMessageSource
	
    public void setMyCiteSeer(MyCiteSeerFacade myciteseer) {
        this.myciteseer = myciteseer;
    } //- setMyCiteSeer
	
	public boolean supports(Class clazz) {
		return CollectionNote.class.isAssignableFrom(clazz);
	}

	public void validate(Object obj, Errors errors) {
		CollectionNote collectionNote = (CollectionNote)obj;
		
		validateCollectionID(collectionNote.getCollectionID(), errors);
		validateNote(collectionNote.getNote(), errors);
	}

	public void validateCollectionID(long collectionID, Errors errors) {
		Account account = MCSUtils.getLoginAccount();
		
		ValidationUtils.rejectIfEmpty(errors, "collectionID",
                "COLLECTION_REQUIRED", "Please specify a collection.");
		if (!myciteseer.isUserCollection(collectionID, account)) {
			errors.rejectValue("collectionID", "INVALID_COLLECTION", 
					"The specified collection doesn't exists.");
		}
	}
	
	public void validateNote(String note, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "note", "NOTE_REQUIRED", "You have to provide a note.");
	}
}
