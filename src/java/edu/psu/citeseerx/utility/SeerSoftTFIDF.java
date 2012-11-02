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
package edu.psu.citeseerx.utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.wcohen.ss.BasicStringWrapperIterator;
import com.wcohen.ss.JaroWinkler;
import com.wcohen.ss.*;
import com.wcohen.ss.api.StringWrapper;
import com.wcohen.ss.api.Token;
import com.wcohen.ss.tokens.SimpleTokenizer;

/**
 * TFIDF-based distance metric.
 * @author Puck Treeratpituk
 * @author Juan Pablo Fernandez Ramirez
 * @see com.wcohen.ss.SoftTFIDF
 * @version $Rev$ $Date$ 
 */
public class SeerSoftTFIDF extends SoftTFIDF {
	
	static private final double MIN_TOKEN_SIMILARITY = 0.8;

	public SeerSoftTFIDF() {
		super(new SimpleTokenizer(false, true), new JaroWinkler(), 
				MIN_TOKEN_SIMILARITY);
	} //- SeerSoftTFIDF
	
	/**
	 * Train the distance on some strings. This should be a large corpus in 
	 * order to accumulate good frequency estimates.
	 * @param trainingCorpus
	 */
	public void trainFromText(List<String> trainingCorpus) {
		List<StringWrapper> preparedCorpus = new ArrayList<StringWrapper>();
		Iterator<String> corpusIterator = trainingCorpus.iterator();
		while (corpusIterator.hasNext()) {
			preparedCorpus.add(prepare(corpusIterator.next()));
		}
		train(new BasicStringWrapperIterator(preparedCorpus.iterator()));
	} //- trainFromText
	
	/**
	 * 
	 * @param model List of ModelItems each one with a string and its
	 * frequency within the corpus
	 */
	public void trainFromModel(List<ModelItem> model) {
		Iterator<ModelItem> iter = model.iterator();
		
		setCollectionSize(model.size());
		while(iter.hasNext()) {
			ModelItem item = iter.next();
			Token token = tokenizer.intern(item.getItem());
			setDocumentFrequency(token, item.getFrequency());
		}
	} //- trainFromModel
	
	/**
	 * 
	 * @return A list of ModelItems indicating where each item has
	 * a string and its frequency within the corpus used to train the
	 * distance metric comparator
	 */
	public List<ModelItem> getModel() {
		List<ModelItem> model = new ArrayList<ModelItem>();
		Iterator<Token> iter = tokenizer.tokenIterator();
		
		while (iter.hasNext()) {
			ModelItem item = new ModelItem();
			Token token = iter.next();
			int freq = getDocumentFrequency(token);
			item.setItem(token.getValue());
			item.setFrequency(freq);
			model.add(item);
		}
		return model;
	} //- getModel
	
	/*
	 * Utility class for representing a string and its frequency within
	 * the training corpus 
	 */
	public class ModelItem {
		private String item;
		private int frequency;
		
		/**
		 * @return the item
		 */
		public String getItem() {
			return item;
		} //- getItem
		
		/**
		 * @param item the item to set
		 */
		public void setItem(String item) {
			this.item = item;
		} //- setItem
		
		/**
		 * @return the frequency
		 */
		public int getFrequency() {
			return frequency;
		} //- getFrequency
		
		/**
		 * @param frequency the frequency to set
		 */
		public void setFrequency(int frequency) {
			this.frequency = frequency;
		} //- setFrequency

	} // -Class Model
} //- class SeerSoftTFIDF
