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
package edu.psu.citeseerx.fixers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.wcohen.ss.api.StringWrapper;

import edu.psu.citeseerx.dao2.logic.CSXDAO;
import edu.psu.citeseerx.domain.Author;
import edu.psu.citeseerx.domain.Document;
import edu.psu.citeseerx.updates.UpdateManager;
import edu.psu.citeseerx.utility.SafeText;
import edu.psu.citeseerx.utility.SeerSoftTFIDF;
import edu.psu.citeseerx.utility.SeerSoftTFIDF.ModelItem;

/**
 * Used for apply corrections made in the old citeseer into the new one.
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 765 $ $Date: 2008-11-03 07:04:41 -0500 (Mon, 03 Nov 2008) $
 */
public class LegacyMetadataFixer {
	
	static final String CORRECTION_SOURCE = "user correction"; 
	static final String	USERNAME = "legacyUser"; 
		
	private CSXDAO csxdao;
    
    public void setCSXDAO (CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    private UpdateManager updateManager;
    
    public void setUpdateManager(UpdateManager updateManager) {
        this.updateManager = updateManager;
    } //- setUpdateManager
    
    private double similarityFactor = 0.8;
    
    public void setSimilarityFactor(double similarityFactor) {
    	this.similarityFactor = similarityFactor;
    } //- setAcceptedDistance
    
    private String modelFile = null;

	/**
	 * @param modelFile Path to the model file. This file is a text file
	 * with the following format:
	 * string frequency
	 * Each line contains a string and its frequency within a corpus. The file
	 * is the result of a previous training of a SeerSortTFIDF string metrics
	 * comparator. 
	 */
	public void setModelFile(String modelFile) {
		this.modelFile = modelFile;
	} //- setModelFile
	
	private String pathToCorpusFile = null;

	/**
	 * @param trainingCorpusFile Path to the file containing strings to be
	 * used to train the distance metric based comparator
	 */
	public void setPathToCorpusFile(String pathToCorpusFile) {
		this.pathToCorpusFile = pathToCorpusFile;
	}

	/**
     * Apply the correction in xmlFile to a citeseerx document if legacyId
     * maps to an actual document and there are corrections to be made
     * @param xmlFile
     * @param legacyId
     * @throws Exception
     */
    public void process(String xmlFile, int legacyId) throws Exception {
    	
    	File file = new File(xmlFile);
    	if (!file.exists()) {
    		throw new IOException("File not found: " + xmlFile);
    	}
    	System.out.println("Loading file: " + xmlFile);
    	FileInputStream in = new FileInputStream(file);
    	LegacyData oldData = new LegacyData(in);
    	
    	Document doc = null;
    	String doi = csxdao.getNewID(legacyId);
    	boolean corrected = false;
    	if (doi != null) {
    		try {
    			System.out.println("Processing document: " + doi);
    			// Got the citeseerx id now retrieve the document
    			doc = csxdao.getDocumentFromDB(doi);
    			if (doc != null) {
	    			if (doc.getVersionName() != "USER") {
		    			// Do corrections.
		    			corrected = doCorrections(doc, oldData);
		    			
		    			if (corrected) {
		    				// Something changed send the corrections to
		    				// storage.
		    				System.out.println("Document: " + doi + " changed. Storing new data.");
		    				updateManager.doCorrection(doc, USERNAME);
		    			}
	    			}else{
	    				System.out.println("Document: " + doi + " not corrected.");
	    				System.out.println("The document was corrected by a user.");
	    			}
	    			System.out.println("Document: " + doi + " processed.");
    			}else{
    				System.out.println("No Document with id: " + doi + 
    						" was found. Check your legacy id mappings table");
    				System.err.println("No Document with id: " + doi + 
    						" was found. Check your legacy id mappings table");
    			}
    		}catch (Exception e) {
				e.printStackTrace();
			}
    	}else{
    		System.out.println("LegacyId: " + legacyId + " doesn't have a " +
    				"mapping in CiteSeerX.");
    	}
    	System.out.println("File: " + xmlFile + " was processed");
    } //- process
    
    /*
     * Apply corrections if any to a CiteSeerX document using the
     * legacy data
     */
    private boolean doCorrections(Document doc, LegacyData lData) {
    	boolean corrected = false;
    	
    	String oldTitle = lData.title.toLowerCase(); 
    	if ( (oldTitle.trim().length() > 0) && 
    			(oldTitle.compareTo("unknown") != 0) ) {
    		corrected = updateDocField(doc, Document.TITLE_KEY, lData.title);
    	}
    	if ( (lData.abs.trim().length() > 0) && 
    			(lData.abs.toLowerCase().compareTo("unknown") != 0) ) {
    		corrected = 
    			updateDocField(doc, Document.ABSTRACT_KEY, lData.abs);
    	}
    	if (lData.volume.trim().length() > 0 && 
    			(lData.volume.toLowerCase().compareTo("unknown") != 0)) {
    		try {
    			Integer.parseInt(lData.volume);
    			corrected = 
        			updateDocField(doc, Document.VOL_KEY, lData.volume);
    		}catch(NumberFormatException nfE) {}
    	}
    	if (lData.year.trim().length() > 0 && 
    			(lData.year.toLowerCase().compareTo("unknown") != 0)) {
    		try {
    			Integer.parseInt(lData.year);
    			corrected = 
        			updateDocField(doc, Document.YEAR_KEY, lData.year);
    		}catch(NumberFormatException nfE) {}
    	}
    	if ( (lData.publisher.trim().length() > 0) && 
    			(lData.publisher.toLowerCase().compareTo("unknown") != 0) ) {
    		corrected = 
    			updateDocField(doc, Document.PUBLISHER_KEY, lData.publisher);
    	}
    	
    	// Now the authors.
    	List<Author> currentAuthors = doc.getAuthors();
    	boolean authorAdded = false;
    	SeerSoftTFIDF distance= getStringComparator();
    	
    	for (int i = 0; i < lData.authors.length; ++i) {
    		boolean matchFound = false;

    		String oldAuthor = SafeText.stripPunctuation(lData.authors[i]);
    		StringWrapper oldAuthPrep = distance.prepare(oldAuthor);
    		double similarity = 0;
    		for (Author currentAuthor : currentAuthors) {
    			String currentAuthName = SafeText.stripPunctuation( 
    				currentAuthor.getDatum(Author.NAME_KEY));
    			similarity = distance.score(oldAuthPrep, 
    					distance.prepare(currentAuthName));
    			if (similarity >= 1.0) {
    				// An exact match found
    				matchFound = true;
    				break;
    			}else if(similarity >= similarityFactor && similarity < 1.0) {
    				// They are similar but not equals. Changed it
    				currentAuthor.setDatum(Author.NAME_KEY, lData.authors[i]);
    				currentAuthor.setSource(Author.NAME_KEY, CORRECTION_SOURCE);
    				corrected = true;
    				matchFound = true;
    				break;
    			}
    		}
    		if (!matchFound) {
				// it's one that we don't have
				Author newAuthor = new Author();
				newAuthor.setDatum(Author.NAME_KEY, lData.authors[i]);
				newAuthor.setSource(Author.NAME_KEY, CORRECTION_SOURCE);
				authorAdded = true;
				currentAuthors.add(i, newAuthor);
			}
    	}
    	if (authorAdded) {
    		// when adding an author the order could change. Fix it!
    		for (int j = 0; j < currentAuthors.size(); ++j) {
    			int currentOrder = Integer.parseInt(
    					currentAuthors.get(j).getDatum(Author.ORD_KEY));
    			if (currentOrder != j) {
    				currentAuthors.get(j).setDatum(
    						Author.ORD_KEY, Integer.toString(j));
    				currentAuthors.get(j).setSource(
    						Author.ORD_KEY, CORRECTION_SOURCE);
    			}
    		}
    	}
    	return corrected;
    } //- doCorrections
    
    /*
     * Updates a document field with the value provided
     */
    private boolean updateDocField(Document doc, String key,
            String newVal) {

    	boolean update = false;
        if (doc.getDatum(key) == null && newVal == null) {
            return update;
        }
        
        if (doc.getDatum(key) == null && newVal != null) {
            update = true;
        } else if (doc.getDatum(key) != null && newVal == null) {
            update = true;
        } else if (!doc.getDatum(key).equals(newVal)) {
            update = true;
        }
        if (update) {
            doc.setDatum(key, newVal);
            doc.setSource(key, CORRECTION_SOURCE);
        }
        return update;
        
    }  //- updateDocField
    
    /*
     * Gets the string comparator.
     */
    private SeerSoftTFIDF getStringComparator() {
    	
    	SeerSoftTFIDF myDistance = null;
    	File mFile = new File(modelFile);
    	
    	if (mFile.exists()) {
    		myDistance = trainFromModel(mFile);
    	}
    	
    	if (myDistance == null) {
    		// Try to train with a corpus if any
    		File cFile = new File(pathToCorpusFile);
        	
        	if (cFile.exists()) {
        		myDistance = trainFromCorpus(cFile);
        	}
    	}
    	return (myDistance != null) ? myDistance : new SeerSoftTFIDF();
    } //- getStringComparator
    
    /*
     * Creates a SeerSoftTFIDF object and train it if a model file is
     * found.
     */
    private SeerSoftTFIDF trainFromModel(File modelFile) {
    	// Load the file, create the comparator and train it.
		BufferedReader bufReader = null;
		SeerSoftTFIDF myDistance = null;
		try {
			bufReader = new BufferedReader(
    				new InputStreamReader(
    						new FileInputStream(modelFile), "UTF8"));
    		
    		List<ModelItem> model = 
    			new ArrayList<ModelItem>();
    		String line = null;
    		while (null != (line = bufReader.readLine())) {
    			String[] terms 	= line.split(" ");
    			int df          = Integer.parseInt(terms[1]);
    			ModelItem item 	= myDistance.new ModelItem();
    			item.setFrequency(df);
    			item.setItem(terms[0]);
    			model.add(item);
    		}
    		bufReader.close();
    		myDistance = new SeerSoftTFIDF();
    		myDistance.trainFromModel(model);
		}catch (Exception e) {
			myDistance = null;
			System.err.println("trainFromModel: No training was done " +
					"from model.");
		}finally{
			try {
				bufReader.close();
			}catch (Exception e) {
				// ignore it. Nothing can be done!
			}
		}
		return myDistance;
    } //- trainFromModel
    
    /*
     * Creates a SeerSoftTFIDF object and tries to train it using a corpus file.
     * Returns a SeerSoftTFIDF object trained, null if no file was found
     */
    private SeerSoftTFIDF trainFromCorpus(File corpusFile) {
    	SeerSoftTFIDF myDistance = null;
    	
		BufferedReader bufReader = null;
		try {
    		// Load file content, and train based on it.
			bufReader = new BufferedReader(
    				new InputStreamReader(
    						new FileInputStream(corpusFile), "UTF8"));
    		List<String> corpus = new ArrayList<String>();
    		String line = null;
    		while (null != (line = bufReader.readLine())) {
    			corpus.add(line);
    		}
    		myDistance = new SeerSoftTFIDF();
    		myDistance.trainFromText(corpus);
		}catch (Exception e) {
			// Ignore training.
			myDistance = null;
			System.err.println("trainFromCorpus: No training: " + 
					e.getMessage());
		}finally{
			try {
				bufReader.close();
			}catch (Exception e) {
				// ignore it. Nothing can be done!
			}
		}
    	
    	return myDistance;
    } //- trainFromCorpus
    
    class LegacyData {
    	String title;
    	String abs;
    	String[] authors;
    	String publisher;
    	String volume;
    	String year;
    	
    	public LegacyData (InputStream in) throws Exception {
    		SAXBuilder builder = new SAXBuilder();
    		
    		org.jdom.Document xmldoc = builder.build(in);
            Element root = xmldoc.getRootElement();
            
            if (!root.getName().equals("record")) {
                throw new Exception("Expected 'record' root element, " +
                        "found " + root.getName());
            }
            
            List<Element> children = root.getChildren();
            for (Iterator<Element> it = children.iterator(); it.hasNext(); ) {
            	Element child = (Element)it.next();
            	if (child.getName().equals("publisher")) {
            		publisher = 
            			SafeText.HtmlAccentsToNonASCII(child.getValue());
            	}else if (child.getName().equals("title")) {
            		title = SafeText.HtmlAccentsToNonASCII(child.getValue());
            	}else if (child.getName().equals("abstract")) {
            		abs = SafeText.HtmlAccentsToNonASCII(child.getValue());
            	}else if (child.getName().equals("author")) {
            		authors = child.getValue().split(",");
            		for (int i = 0; i < authors.length; ++i) {
            			authors[i] = 
            				SafeText.decodeHTMLSpecialChars(authors[i]);
            			authors[i] =
            				SafeText.HtmlAccentsToNonASCII(authors[i]);
            		}
            	}else if (child.getName().equals("volume")) {
            		volume = SafeText.HtmlAccentsToNonASCII(child.getValue());
            	}else if (child.getName().equals("year")) {
            		year = SafeText.HtmlAccentsToNonASCII(child.getValue());
            	}
            }
    	} //- LegacyData
    	
    } // class legacyData

} //- LegacyMetadataFixer
