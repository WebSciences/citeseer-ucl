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
package edu.psu.citeseerx.web.oai;

import java.io.StringReader;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.springframework.web.servlet.view.xslt.AbstractXsltView;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * OAI-PMH XMl View 
 * @author Pradeep Teregowda
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 787 $ $Date: 2008-11-18 03:13:46 -0500 (Tue, 18 Nov 2008) $
 */
public class OAIView extends AbstractXsltView {

	public OAIView() {
		super();
	} //- OAIView

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.view.xslt.AbstractXsltView#createXsltSource(java.util.Map, java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected Source createXsltSource(Map model, String root,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setCoalescing(true);
		dbf.setValidating(false);
		DocumentBuilder db = dbf.newDocumentBuilder();
		String xmlData = (String)model.get("xml");
		InputSource source = new InputSource(new StringReader(xmlData));
		source.setEncoding("UTF-8");
		
		// This should convert the output into UTF-8
		Properties prop = new Properties();
		setContentType("text/xml; charset=UTF-8");
		prop.setProperty(OutputKeys.ENCODING, "UTF-8");
		setOutputProperties(prop);
		
		Document dom = db.parse(source);
		return new DOMSource(dom);
	} //- createXsltSource

} //- Class OAIView
