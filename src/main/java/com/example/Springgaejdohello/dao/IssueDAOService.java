package com.example.Springgaejdohello.dao;

import java.util.ArrayList;
import java.util.List;

import com.example.Springgaejdohello.daoInterface.IssueDAO;
import com.example.Springgaejdohello.model.IssueModel;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IssueDAOService implements IssueDAO{

	@Autowired
	private static ObjectifyService Objectify;

	@Override
	public QueryResultIterator<IssueModel> getAllIssues(String cursorStr,int limit,String orderByProperty, String order) {

		String sortProp = "";

		//add - to sort descending
		sortProp = order.startsWith("desc")?"-":"";

		//select corresponding property filter
		if(orderByProperty.contains("date")){
			sortProp += "lastDateModified";
		}
		else if(orderByProperty.contains("down")){
			sortProp += "downvotes";
		}

		else if(orderByProperty.contains("comment")){
			sortProp += "numberOfComments";
		}

		else if(orderByProperty.contains("assigne")){
			sortProp += "assignee";
		}

		else {
			sortProp = "";
		}

		Query<IssueModel> query = Objectify.ofy().load().type(IssueModel.class).order(sortProp).limit(limit);
		
		if(!cursorStr.isEmpty()) {
			query = query.startAt(Cursor.fromWebSafeString(cursorStr));
		}
		
		boolean continu = false;
		QueryResultIterator<IssueModel> queryres = query.iterator();
		
		List<IssueModel> resultSet = new ArrayList<IssueModel>();
		while(queryres.hasNext()) {
			resultSet.add(queryres.next());
			continu = true;
		}
		
		if(continu) {
//			Cursor cursornext = queryres.getCursor();
//	        Queue queue = QueueFactory.getDefaultQueue();
//	        String url = "/issue/readbybatch?cursor="+cursornext.toWebSafeString();
//	        TaskOptions fetchAllIssues = TaskOptions.Builder.withUrl(url);
//	        queue.add(fetchAllIssues);
	    }
		
		return query.iterator();
	}	

	@Override
	public List<IssueModel> getIssuesByDate() {
		
		return null;
	}

	@Override
	public List<IssueModel> getIssuesByStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IssueModel> getIssuesByAssignedTo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IssueModel> getIssuesByFilters(List<String> filters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueModel getIssueById(String id) {
		IssueModel issuemodel = Objectify.ofy().load().type(IssueModel.class).id(Long.parseLong(id)).now();

		return issuemodel;
	}

	@Override
	public String DodownVote(String issueID, String downvoter) {

		IssueModel issueObject = Objectify.ofy().load().type(IssueModel.class).id(Long.parseLong(issueID)).now();
		System.out.print(issueObject.getWebSafeKey());
		Integer downVotesPrev = issueObject.getDownvotes();
		issueObject.setDownvotes(++downVotesPrev);
		issueObject.setDownvoters(downvoter);
		IssueDAO.updateTimeModified(issueID);

		Objectify.ofy().save().entity(issueObject).now();
		return downVotesPrev.toString();

	}

	@Override
	public List<String> getDownvoters(String issueID) {

		IssueModel issueObject = Objectify.ofy().load().type(IssueModel.class).id(Long.parseLong(issueID)).now();
		List<String> downvoters = issueObject.getDownvoters();

		return downvoters;
	}

	@Override
	public IssueModel closeIssue(String id) {
		IssueModel issueToClose = Objectify.ofy().load().type(IssueModel.class).id(Long.parseLong(id)).now();
		IssueDAO.updateTimeModified(id);
		issueToClose.setStatus("close");

		Objectify.ofy().save().entity(issueToClose);
		return issueToClose;
	}

	@Override
	public IssueModel openIssue(String id) {
		IssueModel issueToOpen = Objectify.ofy().load().type(IssueModel.class).id(Long.parseLong(id)).now();
		issueToOpen.setStatus("open");
		IssueDAO.updateTimeModified(id);
		Objectify.ofy().save().entity(issueToOpen);
		return issueToOpen;
	}

	@Override
	public Key<IssueModel> createIssue(IssueModel issue) {
		Key<IssueModel> issuekey = null;
		try {
			issuekey = Objectify.ofy().save().entity(issue).now();
		}catch(Exception e) {
			System.out.print(e.getCause());
		}

		return issuekey;
	}

	//createIssue
	//readIssueBydate
	//readIssueBystatus
	//readIssueByAssignedTo
	//readAllIssues

}
