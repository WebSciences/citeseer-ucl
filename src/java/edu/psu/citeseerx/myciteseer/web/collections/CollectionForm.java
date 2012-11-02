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
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

import java.io.Serializable;

public class CollectionForm implements Serializable {
	private Collection collection;
	private List collections;
	private boolean hasCollections = false;
	private boolean addPaper = false;
	private String paperID;
	private boolean newCollection;
	
	public CollectionForm() {
		collection = new Collection();
		collection.setUsername(MCSUtils.getLoginAccount().getUsername());
		newCollection = true;
		// It's only false for System defined collections.
		collection.setDeleteAllowed(true);
	}
	
	public CollectionForm(Collection collection) {
		this.collection = collection;
		newCollection = false;
	}
	
	public List getPreviousCollections() {
		return collections;
	}
	
	public void setPreviousCollections(List collections) {
		this.collections = collections;
	}
	
	public Collection getCollection() {
		return collection;
	}
	
	public boolean isAddPaper() {
		return addPaper;
	}

	public void setAddPaper(boolean addPaper) {
		this.addPaper = addPaper;
	}

	public String getPaperID() {
		return paperID;
	}

	public void setPaperID(String paperID) {
		this.paperID = paperID;
	}

	public void setCollection(Collection collection) {
		this.collection = collection;
	}

	public boolean getHasCollections() {
		return hasCollections;
	}

	public void setHasCollections(boolean hasCollections) {
		this.hasCollections = hasCollections;
	}

	public boolean isNewCollection() {
		return newCollection;
	}
}
