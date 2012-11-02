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
package edu.psu.citeseerx.updates;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.psu.citeseerx.dao2.logic.CSXDAO;

/**
 * Utilities for generating statistics that are shown in the home page
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev: 810 $ $Date: 2008-12-02 14:05:57 -0500 (Tue, 02 Dec 2008) $
 */
public class HomePageStatisticsGenerator {
	
	private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String FILE_SEP = System.getProperty("file.separator");
	
	private CSXDAO csxdao;
    
    public void setCSXDAO(CSXDAO csxdao) {
        this.csxdao = csxdao;
    } //- setCSXDAO
    
    private String outputDir = "stats";
    
    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    } //- setOutputDir
    
    /**
     * Generate all statistics.
     * @throws IOException
     */
    public void genStats() throws IOException {
        makeOutputDir();
        genHomePageStats();
    } //- genStats
    
    /**
     * Generate all statistics and output to the given directory.
     * @param outputDir
     * @throws IOException
     */
    public void genStats(String outputDir) throws IOException {
        setOutputDir(outputDir);
        genStats();
    } //- genStats
    
    private void makeOutputDir() throws IOException {
        File file = new File(outputDir);
        if (!file.isDirectory()) {
            file.mkdir();
        }
    } //- makeOutputDir
    
    /*
     * Obtain the values, and store then in a file.
     */
    private void genHomePageStats() throws IOException {
    	String fileName = outputDir + FILE_SEP + "home";
    	Integer numDocs = csxdao.getNumberOfDocumentRecords();
    	Integer numCitations = csxdao.getNumberOfCitationsRecords();
    	
    	FileWriter writer = new FileWriter(fileName);
        BufferedWriter out = new BufferedWriter(writer);
        
        numDocs = (numDocs == null) ? new Integer(0) : numDocs;
        numCitations = (numCitations == null) ? new Integer(0) : numCitations;
        		
        out.write(numDocs.toString());
        out.write('\t');
        out.write(numCitations.toString());
        out.write(NEW_LINE);
        out.close();
    }
} //- Class HomePageStatisticsGenerator
