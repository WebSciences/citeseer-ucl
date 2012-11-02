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
 * Handles the Identify OAI-PMH verb returning information about the repository 
 * @author Pradeep Teregowda
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 787 $ $Date: 2008-11-18 03:13:46 -0500 (Tue, 18 Nov 2008) $
 */
public class Identify extends AbstractVerb {

	private final static String PROTOCOL_VERSION = "2.0";
	private final static String OAI_IDENTIFIER_SCHEMA_LOCATION =
		"http://www.openarchives.org/OAI/2.0/oai-identifier " +
		"http://www.openarchives.org/OAI/2.0/oai-identifier.xsd ";
	private final static String OAI_IDENTIFIER_NAMESPACE =
		"xmlns=\"http://www.openarchives.org/OAI/2.0/oai-identifier\" " +
        "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ";
	
	private final static String OAI_EPRINTS_SCHEMA_LOCATION =
		"http://www.openarchives.org/OAI/1.1/eprints " + 
		"http://www.openarchives.org/OAI/1.1/eprints.xsd";
	private final static String OAI_EPRINTS_SCHEMA_NAMESPACE =
		"xmlns=\"http://www.openarchives.org/OAI/1.1/eprints\" " +
        "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ";
	
	// Defines expected parameters and if they are required or not.
	protected static final String[] expectedArguments = {"verb:true"};
	
	private String repositoryName;
	
	/**
	 * Sets the OAI-PMH repository name.
	 * @param repositoryName	Repository name
	 */
	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	} //- setRepositoryName
	
	private String deletedRecord;
	
	/**
	 * Sets the support of deleted records within the repository
	 * @param deletedRecord
	 */
	public void setDeletedRecord(String deletedRecord) {
		this.deletedRecord = deletedRecord;
	} //- setDeletedRecord
	
	private String adminEmail;
	
	/**
	 * Sets the e-mail for the repository manager
	 * @param adminEmail
	 */
	public void setAdminEmail(String adminEmail) {
		this.adminEmail = adminEmail;
	} //- setAdminEmail
	
	private String compressionFormats;
	
	/**
	 * Sets the compression formats supported by the repository.
	 * I could happen that the software support some compression format but
	 * it's not listed here because performance issues.
	 * @param compressionFormats
	 */
	public void setCompressionFormats(String compressionFormats) {
		this.compressionFormats = compressionFormats;
	} //- setCompressionFormats
	
	private String eprintsContent;
	
	/**
	 * Sets the ePrints content
	 * @param eprintsContent
	 */
	public void setEprintsContent(String eprintsContent) {
		this.eprintsContent = eprintsContent;
	} //- setEprintsContent

	private String eprintsMetadataPolicy;
	
	/**
	 * Sets the ePrints metadata policy
	 * @param eprintsContent
	 */
	public void setEprintsMetadataPolicy(String eprintsMetadataPolicy) {
		this.eprintsMetadataPolicy = eprintsMetadataPolicy;
	} //- setEprintsMetadaPolicy
	
	private String eprintsDataPolicy;
	
	/**
	 * Sets the ePrints data policy
	 * @param eprintsContent
	 */
	public void setEprintsDataPolicy(String eprintsDataPolicy) {
		this.eprintsDataPolicy = eprintsDataPolicy;
	} //- setEprintsMetadaPolicy
	
	public Identify() {
		super();
		for (int i =0; i < expectedArguments.length; ++i) {
			String[] values = expectedArguments[i].split(":");
			addArgument(values[0], Boolean.parseBoolean(values[1]));
		}
	} //- Identify

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.oai.verbs.AbstractVerb#doProcess(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected String doProcess(HttpServletRequest request)
			throws OAIVerbException {
		StringBuffer buf = new StringBuffer();
		buf.append("<Identify>");
		buf.append("<repositoryName>");
		buf.append(repositoryName);
		buf.append("</repositoryName>");
		buf.append("<baseURL>");
		buf.append(getBaseURL());
		buf.append("</baseURL>");
		buf.append("<protocolVersion>");
		buf.append(PROTOCOL_VERSION);
		buf.append("</protocolVersion>");
		buf.append("<adminEmail>");
		buf.append(adminEmail);
		buf.append("</adminEmail>");
		buf.append("<earliestDatestamp>");
		buf.append(getEarliestDatestamp());
		buf.append("</earliestDatestamp>");
		buf.append("<deletedRecord>");
		buf.append(deletedRecord);
		buf.append("</deletedRecord>");
		buf.append("<granularity>");
		buf.append(getGranularity());
		buf.append("</granularity>");
		
		String[] cFormats = compressionFormats.split(",");
		for (int i=0; i < cFormats.length; ++i) {
			buf.append("<compression>");
			buf.append(cFormats[i]);
			buf.append("</compression>");
		}
		
		buf.append("<description>");
		buf.append("<oai-identifier ");
		buf.append(OAI_IDENTIFIER_NAMESPACE);
		buf.append(" xsi:schemaLocation=\"");
		buf.append(OAI_IDENTIFIER_SCHEMA_LOCATION);
		buf.append("\">");
		buf.append("<scheme>");
		buf.append(OAI_SCHEMA);
		buf.append("</scheme>");
		buf.append("<repositoryIdentifier>");
		buf.append(getRepositoryIdentifier());
		buf.append("</repositoryIdentifier>");
		buf.append("<delimiter>");
		buf.append(getDelimiter());
		buf.append("</delimiter>");
		buf.append("<sampleIdentifier>");
		buf.append(OAI_SCHEMA + getDelimiter() + getRepositoryIdentifier() + 
				getDelimiter() + DOI_SAMPLE);
		buf.append("</sampleIdentifier>");
		buf.append("</oai-identifier>");
		buf.append("</description>");
		buf.append("<description>");
		buf.append("<eprints ");
		buf.append(OAI_EPRINTS_SCHEMA_NAMESPACE);
		buf.append(" xsi:schemaLocation=\"");
		buf.append(OAI_EPRINTS_SCHEMA_LOCATION);
		buf.append("\">");
		buf.append("<content>");
		buf.append("<text>");
		buf.append(eprintsContent);
		buf.append("</text>");
		buf.append("</content>");
		buf.append("<metadataPolicy>");
		buf.append("<text>");
		buf.append(eprintsMetadataPolicy);
		buf.append("</text>");
		buf.append("</metadataPolicy>");
		buf.append("<dataPolicy>");
		buf.append("<text>");
		buf.append(eprintsDataPolicy);
		buf.append("</text>");
		buf.append("</dataPolicy>");
		buf.append("</eprints>");
		buf.append("</description>");
		buf.append("</Identify>");
		return buf.toString();
	} //- doProcess
} //- class Identify
