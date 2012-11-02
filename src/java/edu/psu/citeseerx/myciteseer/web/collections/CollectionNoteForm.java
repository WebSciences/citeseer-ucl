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

import edu.psu.citeseerx.myciteseer.domain.CollectionNote;

import java.io.Serializable;

public class CollectionNoteForm implements Serializable {
	private CollectionNote collectionNote;
	private String collectionName;
	private boolean newCollectionNote;

	public CollectionNoteForm() {
		collectionNote = new CollectionNote();
		this.newCollectionNote = true;
	}
	
	
	public CollectionNoteForm(CollectionNote collectionNote) {
		this.collectionNote = collectionNote;
		this.newCollectionNote = false;
	}
	
	
	public String getCollectionName() {
		return collectionName;
	}
	
	
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	
	
	public CollectionNote getCollectionNote() {
		return collectionNote;
	}
	
	
	public void setCollectionNote(CollectionNote noteCollection) {
		this.collectionNote = noteCollection;
	}


	public boolean isNewCollectionNote() {
		return newCollectionNote;
	}
	
	
}
