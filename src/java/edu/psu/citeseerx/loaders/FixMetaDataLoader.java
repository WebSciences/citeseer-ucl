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
package edu.psu.citeseerx.loaders;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.ListableBeanFactory;

import edu.psu.citeseerx.fixers.LegacyMetadataFixer;
import edu.psu.citeseerx.utility.FileUtils;
import edu.psu.citeseerx.utility.SeerSoftTFIDF;
import edu.psu.citeseerx.utility.SeerSoftTFIDF.ModelItem;

/**
 * @author Juan Pablo Fernandez Ramirez
 * @version $Rev$ $Date$
 */
public class FixMetaDataLoader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length == 1) {
			// User want to train the metric-distance comparator based on a file, and produce a model.
			try {
				generateModel(args[0]);
			}catch (FileNotFoundException fnfE) {
				System.err.println("No corpus file was found");
				System.exit(0);
			}catch (IOException ioE) {
				System.err.println("An error ocurred while reading the file");
				System.exit(0);
			}
		}else if (((args.length > 1) && (args.length < 4 || args.length > 5)) ||
				(args.length == 0)) {
			System.out.println("Specify directory, -t=file || -m=file, starting ID, ending ID, [Similarity Factor]");
			System.out.println("or");
			System.out.println("Specify path to training corpus");
            System.exit(0);
		}
		
		String dir = args[0];
		int start = Integer.parseInt(args[2]);
        int end = Integer.parseInt(args[3]);
        
        String[] training = args[1].split("=");
        if (training.length != 2) {
        	System.out.println("Specify directory, -t=file || -m=file, starting ID, ending ID, [Similarity Factor]");
			System.out.println("or");
			System.out.println("Specify path to training corpus");
            System.exit(0);
        }
        
        double similarityFactor = 0;
        if (args.length == 5) {
        	similarityFactor = Double.parseDouble(args[4]);
	        if (similarityFactor <= 0.0 || similarityFactor >= 1.0) {
	        	System.out.println("Similarity Factor should be a value between 0.0 and 1.0 borders not included");
	            System.exit(0);
	        }
        }
        
        ListableBeanFactory factory = null;
        try {
        	factory = ContextReader.loadContext();
        }catch (IOException e) {
        	e.printStackTrace();
            System.exit(1);
		}
        LegacyMetadataFixer fixMeta = 
        	(LegacyMetadataFixer)factory.getBean("legacyMetadataFixer");
        if (args.length == 5) {
        	fixMeta.setSimilarityFactor(similarityFactor);
        }
        
        if (training[0].compareToIgnoreCase("-t") == 0) {
        	fixMeta.setPathToCorpusFile(training[1]);
        }else if (training[0].compareToIgnoreCase("-m") == 0) {
        	fixMeta.setModelFile(training[1]);
        }else{
        	System.out.println("Specify directory, -t=file || -m=file, starting ID, ending ID, [Similarity Factor]");
			System.out.println("or");
			System.out.println("Specify path to training corpus");
            System.exit(0);
        }
        
        String sep = System.getProperty("file.separator");
        
        int processed = 0;
        for (int i=start; i<=end; i++) {
        	File file = new File(dir+sep+i+".xml");
        	if (file.exists()) {
        		try {
        			processed++;
        			fixMeta.process(file.getAbsolutePath(), i);
        		}catch (Exception e) {
        			System.err.println("Processing file: " + 
        					file.getAbsolutePath());
					e.printStackTrace();
				}
        	}
        }
        System.out.println(processed + " files were processed.");
	} //- main
	
	private static void generateModel(String filePath) throws FileNotFoundException, IOException {
		File file = new File(filePath);
		
		if (file.exists()) {
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF8"));
			String line = "";
			List<String> names = new ArrayList<String>();
			while ( null != (line = bufReader.readLine())) {
				names.add(line);
			}
			SeerSoftTFIDF distance = new SeerSoftTFIDF();
			distance.trainFromText(names);
			
			// Get the model and save it.
			String modelName = FileUtils.changeExtension(filePath, "model");
			BufferedWriter writeBuf = new  BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(modelName), "UTF8"));
			
			List<ModelItem> model = distance.getModel();
			Iterator<ModelItem> iter = model.iterator();
			while (iter.hasNext()) {
				ModelItem item = iter.next();
				String toWrite = item.getItem() + " " + item.getFrequency() + 
					"\n";
				writeBuf.write(toWrite);
			}
			writeBuf.close();
  		}
	} //- generateModel

} //- FixMetaDataLoader
