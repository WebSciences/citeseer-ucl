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


/**
 * Handles the ListSets OAI-PMH verb used to retrieve the set structure of the 
 * repository 
 * @author Pradeep Teregowda
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 778 $ $Date: 2008-11-11 15:30:24 -0500 (Tue, 11 Nov 2008) $
 */
public class ListSets extends AbstractVerb {

	// Defines expected parameters and if they are required or not.
	protected static final String[] expectedArguments = {
		"verb:true", "resumptionToken:false"
	};
	
	public ListSets() {
		super();
		for (int i =0; i < expectedArguments.length; ++i) {
			String[] values = expectedArguments[i].split(":");
			addArgument(values[0], Boolean.parseBoolean(values[1]));
		}
	} //- ListSets

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.oai.verbs.AbstractVerb#doProcess(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected String doProcess(HttpServletRequest request)
			throws OAIVerbException {
		// Currently there is no support for sets.
		addError(new OAIError("This repository does not support sets", 
				OAIError.NO_SET_HIERARCHY_ERROR));
		throw new OAIVerbException(getErrors());
	} //- doProcess
	
} //- class ListSets
