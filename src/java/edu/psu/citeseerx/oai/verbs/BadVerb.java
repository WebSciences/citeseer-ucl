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

import org.springframework.web.bind.ServletRequestUtils;

/**
 * Utility class to generate the anwser when a badVerb error occurs.
 * @author Pradeep Teregowda
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 778 $ $Date: 2008-11-11 15:30:24 -0500 (Tue, 11 Nov 2008) $
 */
public class BadVerb extends AbstractVerb {

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.oai.verbs.AbstractVerb#doProcess(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected String doProcess(HttpServletRequest request)
			throws OAIVerbException {
		
		String verb = null;
		verb = ServletRequestUtils.getStringParameter(request, "verb", "");
		addError(new OAIError("Illegal OAI-PMH verb: " + verb, 
				OAIError.BAD_VERB_ERROR));
		throw new OAIVerbException(getErrors());
	} //- doProcess

} //- Class BadVerb
