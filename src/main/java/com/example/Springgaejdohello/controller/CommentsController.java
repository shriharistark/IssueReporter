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

import java.io.IOException;
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


        Map<String,Object> response = new HashMap<>();
        response.put("ok", commentID.equals("failed")?false:true);
        response.put("id",commentID);
        response.put("commentwebsafekey",commentBuilder.getCommentWebSafeKey());
        response.put("issuewebsafekey",commentBuilder.getIssueWebSafeKey());

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
                                        @RequestParam(value = "issueid") String issueID){

        // not required
//        class CommentAux{
//            String commentId;
//            String message;
//            String author;
//
//            public CommentAux(String commentId, String message, String author) {
//                this.commentId = commentId;
//                this.message = message;
//                this.author = author;
//            }
//
//            public String json() throws JsonProcessingException {
//                Map<String,Object> jsonResp = new HashMap<>();
//                ObjectMapper mapper = new ObjectMapper();
//
//                jsonResp.put("commentid",commentId);
//                jsonResp.put("message",message);
//                jsonResp.put("author",author);
//
//                return mapper.writeValueAsString(jsonResp);
//            }
//
//            @Override
//            public String toString() {
//                return super.toString();
//            }
//        }

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

        System.out.println("get replies test: "+commentDAOService.getReplies(issueID,comments.get(0).getId()));
        comments.forEach(comment -> {
            System.out.print(commentDAOService.getReplies(issueID,comment.getId().toString()));
                commentReplyMap.put(comment.getId()
                        ,commentDAOService.getReplies(issueID,comment.getId().toString()));
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
}
