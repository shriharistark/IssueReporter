package com.example.Springgaejdohello.controller;

import com.example.Springgaejdohello.dao.CommentBuilder;
import com.example.Springgaejdohello.dao.CommentDAOService;
import com.example.Springgaejdohello.dao.IssueDAOService;
import com.example.Springgaejdohello.model.CommentModel;
import com.example.Springgaejdohello.model.IssueModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Key;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/comments")
public class CommentsController {

    @RequestMapping(value = "/add", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody String addComments(@RequestBody String commentPayload) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        CommentDAOService commentDao = new CommentDAOService();

        Map<String,Object> commentpayload = mapper.readValue(commentPayload, new TypeReference<Map<String, Object>>() {});

        String issueid = commentpayload.get("issueid").toString();

        //this is a hackcode where we construct key from the id
        Key keygeneratedissue = Key.create(IssueModel.class,Long.parseLong(issueid));
        String parentid = commentpayload.get("parentid") == null ? keygeneratedissue.toWebSafeString() : commentpayload.get("parentid").toString();
        String message = commentpayload.get("message").toString();
        String author = commentpayload.get("author").toString();

        //persisting
//        String commentID = commentDao.addComment(issueid,parentid,message,author);
//
//        Map<String,Object> response = new HashMap<>();
//        response.put("ok", commentID.equals("failed")?false:true);
//        response.put("id",commentID);
//
//        String jsonResponse = "problem with jackson";
//        //mapper.writeValueAsString(response);
//        try {
//            jsonResponse = mapper.writeValueAsString(response);
//        } catch (JsonProcessingException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        return jsonResponse;


        return "";
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody String newComment(@RequestBody String commentPayload) throws IOException{

        ObjectMapper mapper = new ObjectMapper();

        Map<String,Object> commentIn = mapper.readValue(commentPayload, new TypeReference<Map<String,Object>>(){});

        System.out.println(Boolean.valueOf(commentIn.get("hasparent").toString()));

        CommentModel commentBuilder = new CommentBuilder(commentIn.get("message").toString(),
                commentIn.get("author").toString())
                .hasParent(Boolean.valueOf(commentIn.get("hasparent").toString()))
                .addParent(commentIn.get("parentid").toString())
                .bindTo(commentIn.get("issueid").toString())
                .build();

        CommentModel elem = commentBuilder;
        CommentDAOService daoService = new CommentDAOService();
        String commentID = daoService.addComment(elem);


        Map<String,Object> responseMap = new HashMap<>();
        responseMap.put("ok", commentID.equals("failed")?false:true);
        responseMap.put("id",commentID);
        responseMap.put("commentwebsafekey",commentBuilder.getCommentWebSafeKey());
        responseMap.put("issuewebsafekey",commentBuilder.getIssueWebSafeKey());

        String jsonResponse = "problem with jackson";
        //mapper.writeValueAsString(response);
        try {
            jsonResponse = mapper.writeValueAsString(responseMap);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //making internal call - hackjob code
        String webSafeIssueKey = commentBuilder.getIssueWebSafeKey();
        final String uriToRead =  "http://localhost:8080/issue/readbyid?id="+commentIn.get("issueid");
//        String reqJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString();
        RestTemplate restTemplate = new RestTemplate();
        String issueResp = restTemplate.getForObject(uriToRead, String.class);
        System.out.print("internal call response: "+issueResp);

        return jsonResponse;

    }


    @RequestMapping(value = "/readall", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody String readAll(@RequestParam(value = "cursor", defaultValue = "") String cursorStr,
                                        @RequestParam(value = "issueid") String issueID){

        // not required

        CommentDAOService commentDAOService = new CommentDAOService();
        QueryResultIterator<CommentModel> resultSet = commentDAOService.getAllCommentsOf(issueID,cursorStr);
        String cursorNext = "";
        boolean continu = false;
        List<CommentModel> comments = new ArrayList<CommentModel>();

        while(resultSet.hasNext()) {
            comments.add(resultSet.next());
            continu = true;
        }

        Map<String,List<CommentModel>> commentReplyMap = new HashMap<String,List<CommentModel>>();

//        System.out.println("get replies test: "+commentDAOService.getReplies(issueID,comments.get(0).getId()));
        comments.forEach(comment -> {
//            System.out.print(commentDAOService.getReplies(issueID,comment.getId()));
                commentReplyMap.put(comment.getId()
                        ,commentDAOService.getReplies(issueID,comment.getId()));
        });

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
        response.put("commentsList", comments);
        response.put("replies",commentReplyMap);

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

    /*
    @RequestMapping(value = "/getcommentscount", method = RequestMethod.POST, produces = "application/json")
    public String getCommentsCount(@RequestBody String issueId) throws IOException {

        CommentDAOService issueService = new CommentDAOService();

        ObjectMapper mapperIn = new ObjectMapper();
        Map<String, Object> issue = mapperIn.readValue(issueId, new TypeReference<Map<String, Object>>() {});

        IssueModel responseIssue = issueService.openIssue(issue.get("id").toString());

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
    }*/
}
