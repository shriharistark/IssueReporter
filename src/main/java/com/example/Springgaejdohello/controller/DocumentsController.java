package com.example.Springgaejdohello.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class DocumentsController {

    public Document createDocument(){
        User currentUser = UserServiceFactory.getUserService().getCurrentUser();
        String userEmail = currentUser == null ? "" : currentUser.getEmail();
        String userDomain = currentUser == null ? "" : currentUser.getAuthDomain();
        String myDocId = "PA6-5000";

        Document doc =
                Document.newBuilder()
                        // Setting the document identifer is optional.
                        // If omitted, the search service will create an identifier.
                        .setId(myDocId)
                        .addField(Field.newBuilder().setName("content").setText("the rain in spain"))
                        .addField(Field.newBuilder().setName("email").setText(userEmail))
                        .addField(Field.newBuilder().setName("domain").setAtom(userDomain))
                        .addField(Field.newBuilder().setName("published").setDate(new Date()))
                        .build();

        return doc;
    }

    @RequestMapping(value = "/search",method = RequestMethod.GET)
    public @ResponseBody String getDoc(){

        Document document =
                Document.newBuilder()
                        .addField(Field.newBuilder().setName("coverLetter").setText("CoverLetter"))
                        .addField(Field.newBuilder().setName("resume").setHTML("<html></html>"))
                        .addField(Field.newBuilder().setName("fullName").setAtom("Foo Bar"))
                        .addField(Field.newBuilder().setName("submissionDate").setDate(new Date()))
                        .build();
        // [START access_document]
        String coverLetter = document.getOnlyField("coverLetter").getText();
        String resume = document.getOnlyField("resume").getHTML();
        String fullName = document.getOnlyField("fullName").getAtom();
        Date submissionDate = document.getOnlyField("submissionDate").getDate();

        Map<String,Object> response = new HashMap<>();
        response.put("fullname",fullName);

        ObjectMapper mapper = new ObjectMapper();
        String jsonResponse = "";
        try {
            jsonResponse = mapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return jsonResponse;
    }
}
