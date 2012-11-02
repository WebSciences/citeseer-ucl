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
package edu.psu.citeseerx.myciteseer.domain.logic;

import org.springframework.dao.DataAccessException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;

import edu.psu.citeseerx.myciteseer.dao.*;
import edu.psu.citeseerx.myciteseer.domain.*;

import java.util.Date;
import java.util.List;

import java.sql.SQLException;

public class MyCiteSeerImpl implements MyCiteSeerFacade {

    private AccountDAO accountDAO;

    public void setAccountDAO(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }
    
    private SubmissionDAO submissionDAO;

    public void setSubmissionDAO(SubmissionDAO submissionDAO) {
        this.submissionDAO = submissionDAO;
    }
    
    private MyNetDAO myNetDAO;
    
    public void setMyNetDAO(MyNetDAO myNetDAO) {
        this.myNetDAO = myNetDAO;
    }
    
    private ConfigurationDAO configurationDAO;
    
    public void setConfigurationDAO(ConfigurationDAO configurationDAO) {
        this.configurationDAO = configurationDAO;
    }
    
    private CollectionDAO collectionDAO;
    
    public void setCollectionDAO(CollectionDAO collectionDAO) {
		this.collectionDAO = collectionDAO;
	}
    
    private SubscriptionDAO subscriptionDAO;
    
    public void setSubscriptionDAO(SubscriptionDAO subscriptionDAO) {
        this.subscriptionDAO = subscriptionDAO;
    }
    
    private TagDAO tagDAO;
    
    public void setTagDAO(TagDAO tagDAO) {
        this.tagDAO = tagDAO;
    }
    
    private FeedDAO feedDAO;
    
    public void setFeedDAO(FeedDAO feedDAO) {
        this.feedDAO = feedDAO;
    }
    
    private GroupDAO groupDAO;
    
    public void setGroupDAO(GroupDAO groupDAO) {
    	this.groupDAO = groupDAO;
    }
   

    // AccountDAO
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#getAccount(java.lang.String)
	 */
	public Account getAccount(String username) {
        return accountDAO.getAccount(username);
    } //- getAccount

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#getAccountOrNull(java.lang.String)
     */
    public Account getAccountOrNull(String username) {
        return accountDAO.getAccountOrNull(username);
    } //- getAccountOrNull

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#getAccount(java.lang.String, java.lang.String)
     */
    public Account getAccount(String username, String password) {
        return accountDAO.getAccount(username, password);
    } //- getAccount
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#getAccountByEmail(java.lang.String)
     */
    public Account getAccountByEmail(String emailAddress) {
        return accountDAO.getAccountByEmail(emailAddress);
    } //- getAccountByEmail

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#insertAccount(edu.psu.citeseerx.myciteseer.domain.Account)
     */
    public void insertAccount(Account account) {
        accountDAO.insertAccount(account);
    } //- insertAccount

    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#updateAccount(edu.psu.citeseerx.myciteseer.domain.Account)
     */
    public void updateAccount(Account account) {
        accountDAO.updateAccount(account);
    } //- updateAccount
    
    /* (non-Javadoc)
     * @see org.springframework.security.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
     */
    public UserDetails loadUserByUsername(String name) {
    	try {
    		setGroupsEnable(
    				configurationDAO.getConfiguration().getGroupsEnabled());
    	}catch (SQLException e) {
			setGroupsEnable(false);
		}
        return accountDAO.loadUserByUsername(name);
    } //- loadUserByUsername
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#changePassword(edu.psu.citeseerx.myciteseer.domain.Account)
     */
    public void changePassword(Account account) {
        accountDAO.changePassword(account);
    } //- changePassword
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#storeActivationCode(java.lang.String, java.lang.String)
     */
    public void storeActivationCode(String username, String code) {
        accountDAO.storeActivationCode(username, code);
    } //- storeActivationCode
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#deleteActivationCode(java.lang.String)
     */
    public void deleteActivationCode(String username) {
        accountDAO.deleteActivationCode(username);
    } //- deleteActivationCode
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#isValidActivationCode(java.lang.String, java.lang.String)
     */
    public boolean isValidActivationCode(String username, String code) {
        return accountDAO.isValidActivationCode(username, code);
    } //- isValidActivationCode
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#storeInvitationTicket(java.lang.String)
     */
    public void storeInvitationTicket(String ticket)
    throws DataAccessException {
        accountDAO.storeInvitationTicket(ticket);
    } //- storeInvitationTicket
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#deleteInvitationTicket(java.lang.String)
     */
    public void deleteInvitationTicket(String ticket)
    throws DataAccessException {
        accountDAO.deleteInvitationTicket(ticket);
    } //- deleteInvitationTicket
    
    /* (non-Javadoc)
     * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#isValidInvitationTicket(java.lang.String)
     */
    public boolean isValidInvitationTicket(String ticket)
    throws DataAccessException {
        return accountDAO.isValidInvitationTicket(ticket);
    } //- isValidInvitationTicket
    
    public void setGroupsEnable(boolean isGroupEnable) {
    	accountDAO.setGroupsEnable(isGroupEnable);
    } //- setGroupsEnable
    
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#getUsers(java.lang.Long, int)
	 */
	public List<Account> getUsers(Long start, int amount) 
	throws DataAccessException {
		return accountDAO.getUsers(start, amount);
	} //- getAllUsers
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#getUsersSinceTime(java.util.Date, java.lang.Long, int)
	 */
	public List<Account> getUsersSinceTime(Date time, Long start, int amount) 
    throws DataAccessException {
		return accountDAO.getUsersSinceTime(time, start, amount);
	} //- getUsersSinceTime
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.AccountDAO#getUserLastIndexTime()
	 */
	public Date getUserLastIndexTime() throws DataAccessException {
		return accountDAO.getUserLastIndexTime();
	} //- getUserLastIndexTime
	
	public void setUsersLastIndexTime(Date time) throws DataAccessException {
		accountDAO.setUsersLastIndexTime(time);
	} //- setUsersLastIndexTime
	
	public List<Long> getDisabled(Date date) throws DataAccessException {
		return accountDAO.getDisabled(date);
	} //- getDisabled
    
    // SubmissionDAO


	public boolean isUrlAlreadySubmitted(String url, String username) {
        return submissionDAO.isUrlAlreadySubmitted(url, username);
    }
    
    public void insertUrlSubmission(UrlSubmission submission) {
        submissionDAO.insertUrlSubmission(submission);
    }
    
    public List getUrlSubmissions(String username) {
        return submissionDAO.getUrlSubmissions(username);
    }
    
    public void insertSubmissionComponent(SubmissionNotificationItem item) {
        submissionDAO.insertSubmissionComponent(item);
    }
    
    public List getSubmissionComponents(String JID) {
        return submissionDAO.getSubmissionComponents(JID);
    }
    
    public UrlSubmission getUrlSubmission(String JID) {
        return submissionDAO.getUrlSubmission(JID);
    }
    
    public void updateJobStatus(String JID, int status) {
        submissionDAO.updateJobStatus(JID, status);
    }
    
    
    // MyNetDAO
    
    public void sendMsg(Account sender, String msgTo, String msgBody) {
        myNetDAO.sendMsg(sender, msgTo, msgBody);
    }
    
    public List getMessages(Account account) {
        return myNetDAO.getMessages(account);
    }
    
    public void addFriend(Account account, String friendId) {
        myNetDAO.addFriend(account, friendId);
    }
    
    public List getFriends(Account account) {
        return myNetDAO.getFriends(account);
    }
    
    public List getFriendsOfFriend(Account account, String friendId) {
        return myNetDAO.getFriendsOfFriend(account, friendId);
    }
    
    
    // ConfigurationDAO
    
    public MCSConfiguration getConfiguration() throws SQLException {
        return configurationDAO.getConfiguration();
    }
    
    public void saveConfiguration(MCSConfiguration configuration)
    throws SQLException {
        configurationDAO.saveConfiguration(configuration);
    }


    // CollectionDAO
    public void addCollection(Collection collection) {
		collectionDAO.addCollection(collection);
	}


	public List getCollections(String username) {
		return collectionDAO.getCollections(username);
	}


	public boolean getCollectionAlreadyExists(Collection collection, Account account) {
		return collectionDAO.getCollectionAlreadyExists(collection, account);
	}


	public List getUserCollectionPapers(long collectionID, Account account) {
		return collectionDAO.getUserCollectionPapers(collectionID, account);
	}


	public boolean isUserCollection(long collectionID, Account account)
			throws DataAccessException {
		return collectionDAO.isUserCollection(collectionID, account);
	}


	public boolean isPaperInCollection(PaperCollection paperCollection, Account account)
			throws DataAccessException {
		return collectionDAO.isPaperInCollection(paperCollection, account);
	}


	public void addPaperToCollection(PaperCollection paperCollection)
			throws DataAccessException {
		collectionDAO.addPaperToCollection(paperCollection);
		
	}


	public Collection getCollection(long collectionID, Account account)
			throws DataAccessException {
		return collectionDAO.getCollection(collectionID, account);
	}


	public void addNoteToCollection(CollectionNote noteCollection)
			throws DataAccessException {
		collectionDAO.addNoteToCollection(noteCollection);
		
	}


	public List getCollectionNotes(long collectionID)
			throws DataAccessException {
		return collectionDAO.getCollectionNotes(collectionID);
	}


	public void addNoteToPaper(PaperNote paperNote) throws DataAccessException {
		collectionDAO.addNoteToPaper(paperNote);
		
	}


	public List<PaperNote> getPaperNotes(String paperID, long collectionID)
    throws DataAccessException {
		return collectionDAO.getPaperNotes(paperID, collectionID);
	}


	public void updateCollection(Collection collection, Account account)
			throws DataAccessException {
		collectionDAO.updateCollection(collection, account);
		
	}


	public void updateCollectionNote(CollectionNote collectionNote, Account account)
			throws DataAccessException {
		collectionDAO.updateCollectionNote(collectionNote, account);
		
	}


	public CollectionNote getCollectionNote(long collectionNoteID, long collectionID,
			Account account) throws DataAccessException {
		return collectionDAO.getCollectionNote(collectionNoteID, collectionID, account);
	}


	public PaperNote getPaperNote(long paperNoteID, long collectionID, 
			String paperID, Account account)
		throws DataAccessException {
		return collectionDAO.getPaperNote(paperNoteID, collectionID, paperID,
				account);
	}
	
	
	public void updatePaperNote(PaperNote paperNote) throws DataAccessException {
		collectionDAO.updatePaperNote(paperNote);
	}
    

	public void deleteCollection(Collection collection, Account account) {
		collectionDAO.deleteCollection(collection, account);
	}	


	public void deletePaperFromCollection(PaperCollection paperCollection, Account account)
			throws DataAccessException {
		collectionDAO.deletePaperFromCollection(paperCollection, account);
	}	


	public void deletePaperNote(long paperNoteID, String paperID, long collectionID, 
    		Account account) throws DataAccessException {
		collectionDAO.deletePaperNote(paperNoteID, paperID, collectionID, 
				account);
	}


	public void deleteCollectionNote(long collectionID, long collectionNoteID,
			Account account) throws DataAccessException {
		collectionDAO.deleteCollectionNote(collectionID, collectionNoteID, 
				account);
		
	}	
	
	
    // Subscription DAO

	public void addMonitor(Account account, String paperid)
    throws DataAccessException {
        subscriptionDAO.addMonitor(account, paperid);
    }
    
    public void deleteMonitor(Account account, String paperid)
    throws DataAccessException {
        subscriptionDAO.deleteMonitor(account, paperid);
    }
    
    public List getMonitors(Account account) throws DataAccessException {
        return subscriptionDAO.getMonitors(account);
    }
    
    public List getUsersMonitoring(String paperid) {
        return subscriptionDAO.getUsersMonitoring(paperid);
    }
    
    
    // Tag DAO
    
    public void addTag(Account account, String doi, String tag)
    throws DataAccessException {
        tagDAO.addTag(account, doi, tag);
    }
    
    public void deleteTag(Account account, String doi, String tag)
    throws DataAccessException {
        tagDAO.deleteTag(account, doi, tag);
    }
    
    public List getTags(Account account) throws DataAccessException {
        return tagDAO.getTags(account);
    }
    
    public List getDoisForTag(Account account, String tag)
    throws DataAccessException {
        return tagDAO.getDoisForTag(account, tag);
    }
    
    
    // Feed DAO
    
    public void addFeed(Feed feed) {
        feedDAO.addFeed(feed);
    }
    
    public List getFeeds(String userid) {
        return feedDAO.getFeeds(userid);
    }
    
    public Feed getFeed(long id) {
        return feedDAO.getFeed(id);
    }
    
    public void deleteFeed(long id, String userid) {
        feedDAO.deleteFeed(id, userid);
    }

    // Group DAO
    
    
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#addGroup(edu.psu.citeseerx.myciteseer.domain.Group)
	 */
	public Group addGroup(Group group)
			throws DataAccessException {
		return groupDAO.addGroup(group);
	} //- addGroup

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#addMember(edu.psu.citeseerx.myciteseer.domain.Group, java.lang.String, boolean)
	 */
	public void addMember(Group group, String userid, boolean validating)
			throws DataAccessException {
		groupDAO.addMember(group, userid, validating);
	} //- addMember


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#getGroup(long)
	 */
	public Group getGroup(long groupID)
			throws DataAccessException {
		return groupDAO.getGroup(groupID);
	} //- getGroup

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#getGroups(java.lang.String)
	 */
	public List<Group> getGroups(String username) throws DataAccessException {
		return groupDAO.getGroups(username);
	} //- getGroups

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#isNameRepeated(edu.psu.citeseerx.myciteseer.domain.Group, edu.psu.citeseerx.myciteseer.domain.Account)
	 */
	public boolean isNameRepeated(Group group, Account account)
			throws DataAccessException {
		return groupDAO.isNameRepeated(group, account);
	} //- isNameRepeated
    

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#deleteGroup(edu.psu.citeseerx.myciteseer.domain.Group)
	 */
	public void deleteGroup(Group group)
	throws DataAccessException {
		groupDAO.deleteGroup(group);

	} //- deleteGroup

	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#getMembers(edu.psu.citeseerx.myciteseer.domain.Group)
	 */
	public List<GroupMember> getMembers(Group group) throws DataAccessException {
		return groupDAO.getMembers(group);
	} //- getMembers
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#updateGroup(edu.psu.citeseerx.myciteseer.domain.Group)
	 */
	public void updateGroup(Group group)
			throws DataAccessException {
		groupDAO.updateGroup(group);
	} //- updateGroup
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#leaveGroup(edu.psu.citeseerx.myciteseer.domain.Group, java.lang.String)
	 */
	public void leaveGroup(Group group, String userid)
			throws DataAccessException {
		groupDAO.leaveGroup(group, userid);
	} //- leaveGroup
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#removeMember(edu.psu.citeseerx.myciteseer.domain.Group, java.lang.String)
	 */
	public void removeMember(Group group, String userid)
			throws DataAccessException {
		groupDAO.removeMember(group, userid);

	} //- removeMember


	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#validateUser(edu.psu.citeseerx.myciteseer.domain.Group, java.lang.String)
	 */
	public void validateUser(Group group, String userid)
			throws DataAccessException {
		groupDAO.validateUser(group, userid);
		
	} //- validateUser
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#getGroupAuthorities(java.lang.String)
	 */
	public List<GrantedAuthority> getGroupAuthorities(String username)
			throws DataAccessException {
		return groupDAO.getGroupAuthorities(username);
	} //- getGroupAuthorities
	
	/* (non-Javadoc)
	 * @see edu.psu.citeseerx.myciteseer.dao.GroupDAO#isMember(edu.psu.citeseerx.myciteseer.domain.Group, java.lang.String)
	 */
	public boolean isMember(Group group, String userid)
	throws DataAccessException {
		return groupDAO.isMember(group, userid);
	} //- isMember
	
} //- class MyCiteSeerImpl
