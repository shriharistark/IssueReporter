package com.example.Springgaejdohello.controller;

import com.example.Springgaejdohello.ObjectifyWorker;
import com.example.Springgaejdohello.dao.CommentDAOService;
import com.example.Springgaejdohello.daoInterface.CommentDAO;
import com.example.Springgaejdohello.model.CommentModel;
import com.example.Springgaejdohello.model.IssueModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.objectify.Key;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
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
        String commentID = commentDao.addComment(issueid,parentid,message,author);

        Map<String,Object> response = new HashMap<>();
        response.put("ok", commentID.equals("failed")?false:true);
        response.put("id",commentID);

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
}
