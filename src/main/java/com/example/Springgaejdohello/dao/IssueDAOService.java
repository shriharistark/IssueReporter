package com.example.Springgaejdohello.dao;

import java.util.ArrayList;
import java.util.List;

import com.example.Springgaejdohello.ObjectifyWorker;
import com.example.Springgaejdohello.daoInterface.IssueDAO;
import com.example.Springgaejdohello.model.IssueModel;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

public class IssueDAOService implements IssueDAO{
	
	static Objectify objectifyInstance;
	Query<IssueModel> result;
	
	public IssueDAOService() {
		if(objectifyInstance == null) {
			objectifyInstance = ObjectifyWorker.getofy();
		}
	}

	@Override
	public QueryResultIterator<IssueModel> getAllIssues(String cursorStr,int limit) {
		
		Query<IssueModel> query = ObjectifyWorker.getofy().load().type(IssueModel.class).limit(limit);
		
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
		IssueModel issuemodel = ObjectifyWorker.getofy().load().type(IssueModel.class).id(Long.parseLong(id)).now();

		return issuemodel;
	}

	//createIssue
	//readIssueBydate
	//readIssueBystatus
	//readIssueByAssignedTo
	//readAllIssues

}
