package com.example.Springgaejdohello.daoInterface;

import java.util.List;

import com.example.Springgaejdohello.model.IssueModel;
import com.google.appengine.api.datastore.QueryResultIterator;

public interface IssueDAO {

	QueryResultIterator<IssueModel> getAllIssues(String cursor,int limit);
	List<IssueModel> getIssuesByDate();
	List<IssueModel> getIssuesByStatus();
	List<IssueModel> getIssuesByAssignedTo();
	List<IssueModel> getIssuesByFilters(List<String> filters);
	IssueModel getIssueById(String id);
}
