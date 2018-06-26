package com.example.Springgaejdohello.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.appengine.tools.cloudstorage.*;
import com.googlecode.objectify.Key;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import com.example.Springgaejdohello.ObjectifyWorker;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
//@RequestMapping("/")
public class IssueController {

	@RequestMapping("/")
	public String home() {
		return "home.html";
	}

	@RequestMapping(value = "/newissue/{issueid}")
	public ModelAndView showIssue(@PathVariable("issueid") String issueId,
								  ModelMap model){

		model.addAttribute("issueid",issueId);
		return new ModelAndView("redirect:/issuedetail.html",model);
	}
	
	/*
	@RequestMapping("/testjdo")
	public @ResponseBody String testJDO(@RequestParam("name")String name,
						 @RequestParam("age")String age) {
		
        try {
    			ObjectifyService.ofy().save().entity(new IssueModel(name,age)).now();
        }catch(Exception e) {
        		return e.getMessage();
        }
        
        return "worked!";
	}*/


	//read issue by id
	@RequestMapping(value = "/issue/readbyid", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody String getIssueById(@RequestParam("id")String id) throws JsonProcessingException {

		IssueDAOService issueDAOService = new IssueDAOService();

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
	@RequestMapping(value = "/issue/create", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody String setIssue(@RequestBody String body) throws JsonParseException, JsonMappingException, IOException {

        //List<String> tagslist = Arrays.asList(tags);
			
		ObjectMapper mapper = new ObjectMapper();
		
		Map<String,Object> issuePayload = mapper.readValue(body, new TypeReference<Map<String, Object>>() {});
        IssueModel newTicket = generateIssue(issuePayload);



        Map<String,Object> response = new HashMap<>();
        //ObjectifyService.ofy().save().entity(newTicket).now();

        
        try {
            Key<IssueModel> issuekey = ObjectifyWorker.getofy().save().entity(newTicket).now();
            response.put("ok",true);
            response.put("status","entity save success");
            response.put("code",issuekey.toWebSafeString());
        }catch(Exception e) {
        		
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
	
	@RequestMapping(value = "/issue/readall", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody String readAll(@RequestParam(value = "cursor", defaultValue = "") String cursorStr,
										@RequestParam(value = "limit", defaultValue = "5") String limit){


		IssueDAOService issuedao = new IssueDAOService();
		QueryResultIterator<IssueModel> resultSet = issuedao.getAllIssues(cursorStr,Integer.parseInt(limit));
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
	
	@RequestMapping("/issue/readbybatch")
	protected List<IssueModel> readAllService(@RequestParam(value = "cursor", defaultValue = "") String cursorStr) {
		
		Query<IssueModel> query = ObjectifyWorker.getofy().load().type(IssueModel.class).limit(10);
		
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

		IssueDAOService issueDAOService = new IssueDAOService();
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

		IssueDAOService issueDAOService = new IssueDAOService();
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

		IssueDAOService issueDAOService = new IssueDAOService();
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
	
	private IssueModel generateIssue(Map<String,Object> issuePayload){

		List<Object> tagsArray = Arrays.asList(issuePayload.get("tags"));
		List<String> tagsList = new ArrayList<>();
		for (Object object : tagsArray) {
		    tagsList.add(Objects.toString(object, null));
		}
		
		IssueModel newissue = new IssueModel();
		newissue.setCode();
		newissue.setTags(tagsList);
		newissue.setAssignedto(issuePayload.get("assignedTo").toString());
		newissue.setAssignee(issuePayload.get("assignee").toString());
		newissue.setStatus(issuePayload.get("status") == null?"unassigned":issuePayload.get("status").toString());
		newissue.setDescription(issuePayload.get("description").toString());
		newissue.setSubject(issuePayload.get("subject").toString());
		newissue.setDownvotes(1);
		newissue.setDownvoters(issuePayload.get("assignee").toString());
		
		return newissue;
	}

	@RequestMapping(value = {"/gcs/{bucket}/{fileName}"},method = RequestMethod.POST)
	public @ResponseBody String uploadFile(@PathVariable("bucket") String bucket,
			@PathVariable("fileName") String nameFile,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		System.out.print("I am here inside sampleuplaod");
		final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
				.initialRetryDelayMillis(10)
				.retryMaxAttempts(10)
				.totalRetryPeriodMillis(15000)
				.build());


		GcsFileOptions instance = GcsFileOptions.getDefaultInstance();
		GcsFilename fileName = new GcsFilename(bucket,nameFile);
		GcsOutputChannel outputChannel;
		outputChannel = gcsService.createOrReplace(fileName, instance);
		copy(request.getInputStream(), Channels.newOutputStream(outputChannel));

		return "{'name':'hari'}";
	}

	@RequestMapping(value = {"/gcs/{bucket}/{fileName}"},method = RequestMethod.GET)
	public void getFile(@PathVariable("bucket") String bucket,
										   @PathVariable("fileName") String nameFile,
										   HttpServletRequest request,
										   HttpServletResponse response) throws IOException {

		System.out.print("I am here inside sample get");
		final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
				.initialRetryDelayMillis(10)
				.retryMaxAttempts(10)
				.totalRetryPeriodMillis(15000)
				.build());

		GcsFilename fileName = new GcsFilename(bucket,nameFile);
		GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(fileName, 0, 2 * 1024 * 1024);
		copy(Channels.newInputStream(readChannel), response.getOutputStream());

		System.out.println("op stream: "+response.getOutputStream().toString());
	}

	private GcsFilename getFileName(HttpServletRequest req) {
		String[] splits = req.getRequestURI().split("/", 4);
		if (!splits[0].equals("") || !splits[1].equals("gcs")) {
			throw new IllegalArgumentException("The URL is not formed as expected. " +
					"Expecting /gcs/<bucket>/<object>");
		}
		return new GcsFilename(splits[2], splits[3]);
	}

	private void copy(InputStream input, OutputStream output) throws IOException {
		try {
			byte[] buffer = new byte[2 * 1024 * 1024];
			int bytesRead = input.read(buffer);
			while (bytesRead != -1) {
				output.write(buffer, 0, bytesRead);
				System.out.println("bytres: "+buffer);
				bytesRead = input.read(buffer);
			}
		} finally {
			input.close();
			output.close();
		}
	}

}
