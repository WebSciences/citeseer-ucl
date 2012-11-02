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

import edu.psu.citeseerx.myciteseer.domain.PaperNote;

import java.io.Serializable;

public class PaperNoteForm implements Serializable {
	private PaperNote paperNote;
	private String paperTitle;
	private String collectionName;
	private boolean newPaperNote;
	
	public PaperNoteForm() {
		paperNote = new PaperNote();
		newPaperNote = true;
	}
	public PaperNoteForm(PaperNote paperNote) {
		this.paperNote = paperNote;
		newPaperNote = false;
	}
	public PaperNote getPaperNote() {
		return paperNote;
	}
	public void setPaperNote(PaperNote paperNote) {
		this.paperNote = paperNote;
	}
	public String getPaperTitle() {
		return paperTitle;
	}
	public void setPaperTitle(String paperTitle) {
		this.paperTitle = paperTitle;
	}
	public String getCollectionName() {
		return collectionName;
	}
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public boolean isNewPaperNote() {
		return newPaperNote;
	}
	
	
}
