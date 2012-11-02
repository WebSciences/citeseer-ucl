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
package edu.psu.citeseerx.myciteseer.web.groups;

import java.io.Serializable;

import edu.psu.citeseerx.myciteseer.domain.Group;
import edu.psu.citeseerx.myciteseer.web.utils.MCSUtils;

/**
 * @author Juan Pablo Fernandez Ramirez
 * @version $$Rev: 718 $$ $$Date: 2008-09-30 11:17:40 -0400 (Tue, 30 Sep 2008) $$
 */
public class GroupForm implements Serializable {
	private Group group;
	private boolean newGroup;
	private String userid = MCSUtils.getLoginAccount().getUsername();
	
	public GroupForm () {
		this.group = new Group();
		this.newGroup = true;
		this.group.setOwner(userid);
	} //- GroupForm
	
	public GroupForm(Group group) {
		this.group = group;
		newGroup = false;
	} //- GroupForm

	/**
	 * @return the group
	 */
	public Group getGroup() {
		return group;
	} //- getGroup

	/**
	 * @param group the group to set
	 */
	public void setGroup(Group group) {
		this.group = group;
	} //- setGroup

	/**
	 * @return the newGroup
	 */
	public boolean isNewGroup() {
		return newGroup;
	} //- newGroup
	
	/**
	 * returns if the current user is the owner of the group
	 * in this GroupForm
	 * @return true if the current user is the owner of the group
	 */
	public boolean getIsOwner(){
		return 
			(userid.toLowerCase().compareTo(group.getOwner().toLowerCase()) == 0);
	} //- getIsOwner
} //- class GroupForm
