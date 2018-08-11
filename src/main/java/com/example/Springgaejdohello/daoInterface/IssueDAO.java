package com.example.Springgaejdohello.daoInterface;

import java.util.Date;
import java.util.List;

import com.example.Springgaejdohello.ObjectifyWorker;
import com.example.Springgaejdohello.model.IssueModel;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Key;

public interface IssueDAO {

	QueryResultIterator<IssueModel> getAllIssues(String cursor,int limit, String orderByProperty, String order);
	List<IssueModel> getIssuesByDate();
	List<IssueModel> getIssuesByStatus();
	List<IssueModel> getIssuesByAssignedTo();
	List<IssueModel> getIssuesByFilters(List<String> filters);
	IssueModel getIssueById(String id);
	IssueModel closeIssue(String id);
	IssueModel openIssue(String id);

	String DodownVote(String issueID,String downvoterName);
	List<String> getDownvoters(String issueID);
	static boolean updateTimeModified(String issueID){
		boolean success = false;
		try{
			IssueModel modifiedIssue = ObjectifyWorker.getofy().load().type(IssueModel.class).id(Long.parseLong(issueID)).now();
			modifiedIssue.setLastDateModified(new Date().getTime());
			success = true;
		}catch (Exception e){
			System.out.println("not found"+e.toString());
		}

		return success;
	}

	Key<IssueModel> createIssue(IssueModel issue);
}
