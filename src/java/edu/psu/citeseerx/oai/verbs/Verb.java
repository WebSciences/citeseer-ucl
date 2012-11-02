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
 * Defines all the necessary methods to allow verbs to answers OAI-PMH requests. 
 * @author Pradeep Teregowda
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 766 $ $Date: 2008-11-03 13:53:18 -0500 (Mon, 03 Nov 2008) $
 */
public interface Verb {
	
	/**
	 * process the OAI-PMH request and produces the adequate XML response as defined in 
	 * @see <a href="http://www.openarchives.org/OAI/openarchivesprotocol.html">OAI-PMH Version 2 specification</a> 
	 * @param request  Object containing the OAI-PMH request
	 * @return A response to the OAI-PMH request which can be either a set of errors, if any happened, or
	 * the result of answering the required OAI-PMH Verb. 
	 */
	String processRequest(HttpServletRequest request);
} //- interface Verb
