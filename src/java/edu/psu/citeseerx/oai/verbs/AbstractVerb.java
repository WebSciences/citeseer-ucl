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

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import edu.psu.citeseerx.utility.DateUtils;

/**
 * Base class for all the OAI-PMH verbs implemented in the repository
 * @author Pradeep Teregowda
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 787 $ $Date: 2008-11-18 03:13:46 -0500 (Tue, 18 Nov 2008) $
 */
public abstract class AbstractVerb implements Verb {
	
	protected final static String OAI_SCHEMA = "oai";
	protected final static String DOI_SAMPLE = "10.1.1.1.1867";
	
	protected static final String[] metadataFormats = {"oai_dc"};
	
	// Defines expected parameters and if they are required or not.
	protected static final String[] expectedArguments = {};
	
	private String baseURL;
	
	/**
	 * @return the OAI-PMH base URL for the repository
	 */
	public String getBaseURL() {
		return baseURL;
	} //- getBaseURL

	/**
	 * @param baseURL OAI-PMH base URL for the repository
	 */
	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	} //- setBaseURL

	private String granularity;
	
	/**
	 * Sets the granularity supported by the repository
	 * @param granularity
	 */
	public void setGranularity(String granularity) {
		this.granularity = granularity;
	} //- setGranularity
	
	/**
	 * Returns the granularity supported by this repository
	 * @return
	 */
	public String getGranularity() {
		return granularity;
	} //- getGranularity
	
	private String delimiter;
	
	/**
	 * Sets the delimiter used in the identifier for this repository
	 * @param delimiter
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	} //- delimiter
	
	/**
	 * Returns the delimiter used by this repository
	 * @return
	 */
	public String getDelimiter() {
		return delimiter;
	} //- getDelimiter
	
	private String repositoryIdentifier;
	
	/**
	 * Sets the PMH-OAI repository identifier
	 * @param repositoryIdentifier
	 */
	public void setRepositoryIdentifier(String repositoryIdentifier) {
		this.repositoryIdentifier = repositoryIdentifier;
	} //- setRepositoryIdentifier
	
	/**
	 * @return this repository identifier.
	 */
	public String getRepositoryIdentifier() {
		return repositoryIdentifier;
	} //- getRepositoryIdentifier
	
	private String earliestDatestamp;
	
	/**
	 * Sets the earliest date stamp for the repository
	 * @param earliestDatestamp
	 */
	public void setEarliestDatestamp(String earliestDatestamp) {
		this.earliestDatestamp = earliestDatestamp; 
	} //- setEarliestDatestamp
	
	/**
	 * Returns the earliest date stamp for the repository
	 * @return
	 */
	public String getEarliestDatestamp() {
		return earliestDatestamp;
	} //- getEarliestDatestamp
	
	private List<OAIError> errors = new ArrayList<OAIError>();
	
	/**
	 * Add an error to the error list
	 * @param error
	 */
	protected void addError(OAIError error) {
		errors.add(error);
	} //- addError
	
	/**
	 * Returns the error list. 
	 * @return
	 */
	protected List<OAIError> getErrors() {
		return errors;
	} //- getError
	
	/**
	 * Informs if errors have occurred 
	 * @return
	 */
	protected boolean hasErrors() {
		return errors.size() > 0;
	} //- hasErrors
	
	private List<String> requiredArguments = new ArrayList<String>();
	private List<String> validArguments = new ArrayList<String>();
	
	/**
	 * Stores the expected parameters for the verb indicating if the parameter is required or not
	 * @param parameter	Name of a valid parameter for this verb
	 * @param required	Indicates if the parameter is required or not
	 */
	public void addArgument(String parameter, boolean required) {
		if (required) {
			requiredArguments.add(parameter);
		}
		validArguments.add(parameter);
	} //- addArgument
	
	/**
	 * Validates the parameters send to the verb.
	 * @param request	Object containing the arguments for the verb
	 * @return True is parameters are OK false otherwise.
	 */
	protected boolean checkArguments(HttpServletRequest request) throws OAIVerbException {
		boolean valid = true;
		
		// check for resumptionToken exclusivity
		String resumptionToken = request.getParameter("resumptionToken");
		if (resumptionToken != null && resumptionToken.trim().length() > 0) {
			if (countParameters(request) > 2) {
				valid = false;
				addError(new OAIError("resumptionToken cannot be combined " +
						"with other parameters", OAIError.BAD_ARGUMENT_ERROR));
			}
		}else{
			// Check we got all the required arguments.
			Iterator<String> reqArgIter = requiredArguments.listIterator();
			while (reqArgIter.hasNext()) {
				String reqArg = reqArgIter.next();
				String argValue = request.getParameter(reqArg);
				if (argValue == null || argValue.trim().length() == 0) {
					valid = false;
					addError(new OAIError(reqArg + " is required", 
							OAIError.BAD_ARGUMENT_ERROR));
				}
			}
		}
		
		// Check illegal parameters and / or multi values
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			if (!validArguments.contains(paramName)) {
				valid = false;
				addError(new OAIError(paramName + " is an Illegal argument", 
						OAIError.BAD_ARGUMENT_ERROR));
			}
			
			// Check if the parameter has been set more than once.
			if (request.getParameterValues(paramName).length > 1) {
				valid = false;
				addError(new OAIError("multiple values are not allowed for " + 
						"the " + paramName + " argument", 
						OAIError.BAD_ARGUMENT_ERROR));
			}
		}

		if (false == valid) {
			throw new OAIVerbException(errors);
		}
		return valid;
	} //- areValidArguments
	
	/**
	 * Returns a String representing the request Element within the @see <a href="http://www.openarchives.org/OAI/openarchivesprotocol.html#XMLResponse"> XML response 
	 * format</a> as specified by the OAI-PMH specification.
	 * An example of a OAI-PMH request element is:
	 *{@code 
	 *<request verb="getRecord" identifier="oai:citeseerx.ist.psu.edu:10.1.1.1.6784" metadataPrefix="oai_dc">
	 *  http://citeseerx.ist.psu.edu/OAI2
	 *</request>}     
	 * @param request	request object containing the arguments needed to build the element.
	 * @param includeAttributes If false no attributes are generated inside the request element
	 * @return the request element
	 */
	protected String generateRequestElement(HttpServletRequest request, 
			boolean includeAttributes) {
		StringBuffer buf = new StringBuffer();
		
		if (!includeAttributes) { 
			buf.append("<request>");
		}else{
			buf.append("<request");
			Enumeration<String> params = request.getParameterNames();
			while (params.hasMoreElements()) {
				String paramName = params.nextElement();
				if (validArguments.contains(paramName)) {
					String paramValue = request.getParameter(paramName);
					if (paramValue != null && paramValue.trim().length() > 0) {
						buf.append(" ");
						buf.append(paramName);
						buf.append("=\"");
						buf.append(paramValue);
						buf.append("\"");
					}
				}
			}
			buf.append(">");
		}
		buf.append(getBaseURL());
		buf.append("</request>");

		return buf.toString();
	} //- generateRequestElement
	
	/**
	 * Creates the responseDate element for an OAI-PMH response. The received date 
	 * is converted to a @see <a href="http://www.openarchives.org/OAI/openarchivesprotocol.html#Dates">OAI-PMH date</a>
	 * A responseDate element looks like:
	 * {@code<responseDate>2002-05-01T19:20:30Z</responseDate>}
	 * @param date Date to be expressed as OAI-PMH date
	 * @return An OAI-PMH responseDateElement for the received date 
	 */
	protected String generateResponseDateElement(Date date) {
		StringBuffer buf = new StringBuffer();
		
		buf.append("<responseDate>");
		buf.append((DateUtils.formatDateTimeISO8601UTC(date)));
		buf.append("</responseDate>");
		return buf.toString();
	} //- generateResponseDateElement
	
	/**
	 * Creates the @see <a href="">OAI-PMH error</a> element with data provided by the OAIException
	 * The error element will look like:
	 * {@code <error code="badVerb">Illegal OAI verb</error>}
	 * @param e Data to be used when creating the error element.
	 * @return An OAI-PMH error element based ion the supplied date 
	 */
	protected String generateErrorElement(OAIVerbException e) {

		StringBuffer buf = new StringBuffer();
		for (OAIError err : e.getErrors()) {
			buf.append("<error code=\"");
			buf.append(err.getErrorCode());
			buf.append("\">");
			buf.append(err.getMessage());
			buf.append("</error>");
		}
		return buf.toString();
	} //- generateErrorElement

	/**
	 * Generates the XML declaration, the root, responseDate, and request element for the OAI-PMH response.
	 * @param request request object containing the arguments for the verb.
	 * @param includeAttributes Indicates if attributes needs to be included when generating the request
	 * element within the response
	 * @return XML header as defined in @see <a href="http://www.openarchives.org/OAI/openarchivesprotocol.html#XMLResponse">XML response Format</a>
	 */
	protected String generateXMLHeader(HttpServletRequest request, 
			boolean includeAttributes) {
		
		StringBuffer buf = new StringBuffer();
		
		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		buf.append("<OAI-PMH xmlns=\"http://www.openarchives.org/OAI/2.0/\"");
		buf.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
		buf.append(" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/");
		buf.append(" http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\">");
		buf.append(generateResponseDateElement(new Date()));
		buf.append(generateRequestElement(request, includeAttributes));
		return buf.toString();
	} //- generateXMLHeader
	
	/**
	 * Generates the closing tag for the XML response
	 * @return
	 */
	protected String generateXMLFooter() {
		return "</OAI-PMH>";
	} //- generateXMLFooter
	
	/**
	 * Determines if the given identifier have the structure of this repository
	 * identifiers. If the identifier doesn't comply with the structure an 
	 * error is created and stored in the errors list. 
	 * @param Identifier Identifier encoded in UTF-8
	 * @return True is the identifier has this repository identifiers structure
	 * false otherwise. 
	 */
	protected boolean isValidIdentifier(String identifier) {
		
		boolean valid = true;
		String doiRegExpr = 
			"^[0-9]{1,}\\.[0-9]{1,}\\.[0-9]{1,}\\.[0-9]{1,}\\.[0-9]{1,4}$";
		
		// If the identifier argument is present. Check it's well formed.
		try {
			new URI(identifier);
			String[] tokens = identifier.split(getDelimiter());
			
			Pattern pattern = Pattern.compile(doiRegExpr);
			Matcher matcher = pattern.matcher(tokens[2]);
			if (!matcher.find() || (tokens[0].compareTo(OAI_SCHEMA) != 0) ||
					(tokens[1].compareTo(getRepositoryIdentifier()) != 0)) {
				valid = false;
				addError(new OAIError(identifier + " has an invalid structure " +
						"for this repository identifiers", 
						OAIError.ID_DOES_NOT_EXISTS_ERROR));
			}
		}catch (Exception e) {
			valid = false;
			addError(new OAIError(identifier + " is not a valid URI", 
					OAIError.ID_DOES_NOT_EXISTS_ERROR));
		}
		return valid;
	} //- isValidIdentifier
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.oai.verbs.Verb#processRequest(javax.servlet.http.HttpServletRequest)
	 */
	public String processRequest(HttpServletRequest request) {
		
		String xmlResponse = "";
		boolean includeAttributes = true;
		StringBuffer buf = new StringBuffer();
		
		try {
			// Checking we have all the arguments we need.
			checkArguments(request);
			xmlResponse = doProcess(request);
		}catch (OAIVerbException e) {
			xmlResponse = generateErrorElement(e);
			
			/*
			 * The request element must not include attributes when errors 
			 * happens.
			 */ 
			includeAttributes = false;
		}finally{
			// Clean any errors occurred in this call.
			errors.clear();
			buf.append(generateXMLHeader(request, includeAttributes));
			buf.append(xmlResponse);
			buf.append(generateXMLFooter());
		}
		return buf.toString();
	} //- processRequest
	
	/**
	 * Process the request and produces the XML answer.
	 * This method is intended to be implemented by specialized classes which know
	 * how to process each verb 
	 * @param request
	 * @return The XML response or null if an error occurs
	 * @throws OAIVerbException
	 */
	protected abstract String doProcess(HttpServletRequest request) throws OAIVerbException;
	
	/*
	 * Counts how many parameters were sent
	 */
	private int countParameters(HttpServletRequest request) {
		
		int i = 0;
		Enumeration<String> params = request.getParameterNames();
		for (i = 0; params.hasMoreElements(); i++ ) {
			params.nextElement();
		}
		return i;
	} //- countArguments

} // class AbstractVerb
