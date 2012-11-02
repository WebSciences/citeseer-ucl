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
package edu.psu.citeseerx.myciteseer.web.collections;

import java.util.List;

import edu.psu.citeseerx.myciteseer.domain.Collection;
import edu.psu.citeseerx.myciteseer.domain.PaperCollection;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

import java.io.Serializable;

public class PaperCollectionForm implements Serializable {
	
	private PaperCollection paperCollection;
	private List<Collection> collections;
	private String paperTitle;
	
	public PaperCollectionForm() {
		paperCollection = new PaperCollection();
		paperCollection.setUID(MCSUtils.getLoginAccount().getUsername());
	}
	
	public PaperCollectionForm(String paperTitle, String DOI) {
		paperCollection = new PaperCollection();
		paperCollection.setPaperID(DOI);
		paperCollection.setUID(MCSUtils.getLoginAccount().getUsername());
		this.paperTitle = paperTitle;
	}
	
	public PaperCollection getPaperCollection() {
		return paperCollection;
	}

	public void setPaperCollection(PaperCollection paperCollection) {
		this.paperCollection = paperCollection;
	}

	public List<Collection> getCollections() {
		return collections;
	}

	public void setCollections(List<Collection> collections) {
		this.collections = collections;
	}

	public String getPaperTitle() {
		return paperTitle;
	}

	public void setPaperTitle(String paperTitle) {
		this.paperTitle = paperTitle;
	}

	
}
