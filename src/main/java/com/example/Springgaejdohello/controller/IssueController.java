package com.example.Springgaejdohello.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

import com.example.Springgaejdohello.Service.BatchWrite;
import com.googlecode.objectify.Key;

import com.googlecode.objectify.ObjectifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import com.example.Springgaejdohello.dao.IssueDAOService;
import com.example.Springgaejdohello.model.IssueModel;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.cmd.Query;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = {"/", "/issue"})
public class IssueController {

	//singleton - so the wiring is done only at the run time
	@Autowired
	IssueDAOService issueDAOService;

	@Autowired
	BatchWrite batchWrite;


	BatchWrite getBatchWrite(){
		if(batchWrite == null){
			return new BatchWrite();
		}

		return batchWrite;
	}

	@RequestMapping("/")
	public String home(HttpServletResponse servletResponse, HttpServletRequest servletRequest) {
		//new request? set session : query db for the user data
		System.out.println("\n---Hit home!---\n");
//		System.out.println("State id: "+servletRequest.getSession(false) == null);

		if(servletRequest.getSession() != null && servletRequest.getSession().getAttribute("state") == null){
			String CSRF_token = new BigInteger(130, new SecureRandom()).toString(32);
			servletRequest.getSession().setAttribute("state",CSRF_token);

			Cookie CSRF_cookie = new Cookie("auth_state",CSRF_token);
			CSRF_cookie.setDomain("localhost");
			CSRF_cookie.setPath("/");
			CSRF_cookie.setSecure(true);
			servletResponse.addCookie(CSRF_cookie);
		}

		//cookie validations

		return "home.html";
	}

	@RequestMapping(value = "/viewissue/{issueid}")
	public ModelAndView showIssue(@PathVariable("issueid") String issueId,
								  ModelMap model){

		model.addAttribute("issueid",issueId);
		return new ModelAndView("redirect:/issuedetail.html",model);
	}


	//read issue by id
	@RequestMapping(value = "/readbyid", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody String getIssueById(@RequestParam("id")String id) throws JsonProcessingException {

		IssueModel issue = issueDAOService.getIssueById(id);
		Map<String,Object> response = new HashMap<>();

		if(issue != null){
			response.put("ok",true);
			response.put("issue",issue);
			response.put("issuewebsafekey",issue.getWebSafeKey());
		}

		else{
			response.put("ok",false);
		}

		String jsonresponse = "jsonparse exception";
		ObjectMapper mapper = new ObjectMapper();
		jsonresponse = mapper.writeValueAsString(response);

		return jsonresponse;
	}
	
	//create issue
	@RequestMapping(value = "/create", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody String setIssue(@RequestBody String body) throws JsonParseException, JsonMappingException, IOException {

        //List<String> tagslist = Arrays.asList(tags);
			
		ObjectMapper mapper = new ObjectMapper();
		
		Map<String,Object> issuePayload = mapper.readValue(body, new TypeReference<Map<String, Object>>() {});

		//temporary piece of code. Remove if you see it
		if(issuePayload.get("test") != null){
			IssueModel testissue = new IssueModel();
			testissue.setCode();
			//for tags
			String tagsStr[]= issuePayload.get("tags").toString()
					.substring(1,issuePayload.get("tags").toString().length()-1)
					.split(",");
			testissue.setTags(Arrays.asList(tagsStr));
			testissue.setAssignedto(issuePayload.get("assignedTo").toString());
			testissue.setAssignee(issuePayload.get("assignee").toString());
			System.out.println("issue status from frontend: "+issuePayload.get("status").toString());
			testissue.setStatus(issuePayload.get("status") == null?"open":issuePayload.get("status").toString().toLowerCase());
			testissue.setDescription(issuePayload.get("description").toString());
			testissue.setSubject(issuePayload.get("subject").toString());
			testissue.setDownvotes(1);
			testissue.setDownvoters(issuePayload.get("assignee").toString());
			testissue.setLastDateModified(new Date().getTime());
			testissue.setNumberOfComments(0);
			try {
				getBatchWrite().addToQueue(testissue, IssueModel.class);
			} catch (BatchWrite.BatchWriteException e) {
				e.printStackTrace();
			}
			return "test done.. check on the admin";
		}

		//boiler plate ugly piece of code
		System.out.println(issuePayload.get("tags").toString());
		String tagsStr[]= issuePayload.get("tags").toString()
				.substring(1,issuePayload.get("tags").toString().length()-1)
				.split(",");

		IssueModel newissue = new IssueModel();
		newissue.setCode();
		newissue.setTags(Arrays.asList(tagsStr));
		newissue.setAssignedto(issuePayload.get("assignedTo").toString());
		newissue.setAssignee(issuePayload.get("assignee").toString());
		System.out.println("issue status from frontend: "+issuePayload.get("status").toString());
		newissue.setStatus(issuePayload.get("status") == null?"open":issuePayload.get("status").toString().toLowerCase());
		newissue.setDescription(issuePayload.get("description").toString());
		newissue.setSubject(issuePayload.get("subject").toString());
		newissue.setDownvotes(1);
		newissue.setDownvoters(issuePayload.get("assignee").toString());
		newissue.setLastDateModified(new Date().getTime());
		newissue.setNumberOfComments(0);

        Map<String,Object> response = new HashMap<>();
        //ObjectifyService.ofy().save().entity(newTicket).now();

        
        try {
			Key<IssueModel> issueKeyObtained = issueDAOService.createIssue(newissue);
            response.put("ok",true);
            response.put("status","entity save success");
            response.put("code",issueKeyObtained.toWebSafeString());
        }catch(Exception e) {
			System.out.print("Writing erorr on the DB. From the Issue DAOService ");
			e.printStackTrace();
        }
        
        String jsonResponse = "problem with jackson";
        //mapper.writeValueAsString(response);
		try {
			jsonResponse = mapper.writeValueAsString(response);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return jsonResponse;
    }
	
	@RequestMapping(value = "/readall", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody String readAll(@RequestParam(value = "cursor", defaultValue = "") String cursorStr,
										@RequestParam(value = "limit", defaultValue = "5") String limit,
										@RequestParam(value = "sortby", defaultValue = "lastDateModified", required = false) String sortProperty,
										@RequestParam(value = "order", defaultValue = "ascending", required = false) String sortOrder){


		QueryResultIterator<IssueModel> resultSet = issueDAOService.getAllIssues(cursorStr,Integer.parseInt(limit), sortProperty, sortOrder);
		String cursorNext = "";
		boolean continu = false;
		List<IssueModel> issues = new ArrayList<IssueModel>();
		
		while(resultSet.hasNext()) {
			issues.add(resultSet.next());
			continu = true;
		}
		
		if(continu) {
			cursorNext = resultSet.getCursor().toWebSafeString();
//			cursorNext = resultSet.getCursor().toWebSafeString();
//			Queue queue = QueueFactory.getDefaultQueue();
//	        String url = "/issue/readAll?cursor="+cursorNext;
//	        TaskOptions fetchAllIssues = TaskOptions.Builder.withUrl(url);
//	        queue.add(fetchAllIssues);
		}
		
        Map<String,Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("next",cursorNext);
        response.put("issues", issues);
		
        String jsonResponse = "";
        ObjectMapper mapper = new ObjectMapper();
		
		try {
			jsonResponse = mapper.writeValueAsString(response);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jsonResponse;
	}
	
	@RequestMapping("/readbybatch")
	protected List<IssueModel> readAllService(@RequestParam(value = "cursor", defaultValue = "") String cursorStr) {

		Query<IssueModel> query = ObjectifyService.ofy().load().type(IssueModel.class).limit(10);
		
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
			Cursor cursornext = queryres.getCursor();
	        //Queue queue = QueueFactory.getDefaultQueue();
	        //String url = "/issue/readbybatch?cursor="+cursornext.toWebSafeString();
	        //TaskOptions fetchAllIssues = TaskOptions.Builder.withUrl(url);
	        //queue.add(fetchAllIssues);
	    }
		
		return resultSet;
	}

	@RequestMapping(value = "/downvote", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody String downvote(@RequestBody String downvoteObj) throws IOException {

		//to do - add additional parameter notes(may be required for later)

		ObjectMapper mapper = new ObjectMapper();
		Map<String,Object> downvotePayload = mapper.readValue(downvoteObj, new TypeReference<Map<String, Object>>() {});

		String numberOfDownvotes = issueDAOService.DodownVote(downvotePayload.get("issue").toString(),
				downvotePayload.get("name").toString());

		Map<String,Object> response = new HashMap<>();
		response.put("ok", true);
		response.put("downvotes",numberOfDownvotes);

		String jsonResponse = "";
		mapper = new ObjectMapper();

		try {
			jsonResponse = mapper.writeValueAsString(response);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonResponse;

	}

	@RequestMapping("/getdownvoters")
	public @ResponseBody String getDownvoters(@RequestParam(value = "issueid") String issueID){

		List<String> downvoters = issueDAOService.getDownvoters(issueID);

		Map<String,Object> response = new HashMap<>();
		response.put("ok", true);
		response.put("downvoters",downvoters);

		String jsonResponse = "";
		ObjectMapper mapper = new ObjectMapper();

		try {
			jsonResponse = mapper.writeValueAsString(response);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonResponse;

	}

	@RequestMapping("/getdownvotescount")
	public @ResponseBody String downvoteCount(@RequestParam(value = "issueid") String issueID){

		int downvoteCount = issueDAOService.getDownvoters(issueID).size();

		Map<String,Object> response = new HashMap<>();
		response.put("ok", true);
		response.put("downvoteCount",downvoteCount);

		String jsonResponse = "";
		ObjectMapper mapper = new ObjectMapper();

		try {
			jsonResponse = mapper.writeValueAsString(response);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return jsonResponse;

	}

	@RequestMapping(value = "/closeissue", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody String closeIssue(@RequestBody String issueId) throws IOException {


		ObjectMapper mapperIn = new ObjectMapper();
		Map<String, Object> issue = mapperIn.readValue(issueId, new TypeReference<Map<String, Object>>() {});

		IssueModel responseIssue = issueDAOService.closeIssue(issue.get("id").toString());

		String jsonResponse = "";
		ObjectMapper mapperOut = new ObjectMapper();

		Map<String,Object> response = new HashMap<>();
		response.put("ok",true);
		response.put("id",responseIssue.getWebSafeKey());
		response.put("message","issue is closed");

		try{
			jsonResponse = mapperOut.writeValueAsString(response);
		}catch (JsonProcessingException j){
			j.printStackTrace();
		}

		return jsonResponse;
	}

	@RequestMapping(value = "/openissue", method = RequestMethod.POST, produces = "application/json")
	public String openIssue(@RequestBody String issueId) throws IOException {

		ObjectMapper mapperIn = new ObjectMapper();
		Map<String, Object> issue = mapperIn.readValue(issueId, new TypeReference<Map<String, Object>>() {});

		IssueModel responseIssue = issueDAOService.openIssue(issue.get("id").toString());

		String jsonResponse = "";
		ObjectMapper mapper = new ObjectMapper();

		Map<String,Object> response = new HashMap<>();
		response.put("ok",true);
		response.put("id",responseIssue.getWebSafeKey());
		response.put("message","issue is re-opened");

		try{
			jsonResponse = mapper.writeValueAsString(response);
		}catch (JsonProcessingException j){
			j.printStackTrace();
		}

		return jsonResponse;
	}

}
