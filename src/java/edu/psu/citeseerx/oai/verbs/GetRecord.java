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
package edu.psu.citeseerx.oai.verbs;

import javax.servlet.http.HttpServletRequest;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.Document;


/**
 * Handles the GetRecord OAI-PMH verb returning metadata for an individual 
 * record within the repository 
 * @author Pradeep Teregowda
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 787 $ $Date: 2008-11-18 03:13:46 -0500 (Tue, 18 Nov 2008) $
 */
public class GetRecord extends AbstractList {

	// Defines expected parameters and if they are required or not.
	protected static final String[] expectedArguments = {
		"verb:true", "identifier:true", "metadataPrefix:true"
	};
	
	public GetRecord() {
		super();
		for (int i =0; i < expectedArguments.length; ++i) {
			String[] values = expectedArguments[i].split(":");
			addArgument(values[0], Boolean.parseBoolean(values[1]));
		}
	} //- GetRecord

	CSXDAO csxdao;

	/**
	 * Sets the object to have access to the document storage area
	 * @param csxdao
	 */
	public void setCSXDAO(CSXDAO csxdao) {
		this.csxdao = csxdao;
	} //- setCsxdao
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.oai.verbs.AbstractVerb#doProcess(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected String doProcess(HttpServletRequest request)
			throws OAIVerbException {
		String identifier = request.getParameter("identifier");
		String metadataPrefix = request.getParameter("metadataPrefix");
		
		Document doc = null;
		if (isValidIdentifier(identifier) && 
				isValidMetaDataPrefix(metadataPrefix)) {
			String[] idParts = identifier.split(getDelimiter());
			doc = csxdao.getDocumentFromDB(idParts[2], false, false, false, 
					false, true, false);
			if (doc == null) {
				/* 
				 * The document doesn't exist in the repository or it's a bad
				 * DOI
				 */
				addError(new OAIError(identifier + " has a valid CiteSeerX " +
						"identifier, but it maps to no known item", 
						OAIError.ID_DOES_NOT_EXISTS_ERROR));
			}
		}
		
		StringBuffer buf;
		if (!hasErrors()) {
			buf = new StringBuffer();
			buf.append("<GetRecord>");
			buf.append(buildDocumentRecord(doc));
			buf.append("</GetRecord>");
		}else{
			throw new OAIVerbException(getErrors());
		}
		return buf.toString();
	} //- doProcess
	
} //- class GetRecord
