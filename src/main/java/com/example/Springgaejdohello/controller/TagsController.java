package com.example.Springgaejdohello.controller;

import com.example.Springgaejdohello.dao.TagsDAOService;
import com.example.Springgaejdohello.model.TagsModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/tags")
public class TagsController {


    @RequestMapping("/create")
    public @ResponseBody String createTag(@RequestParam("tag") String tag){

        TagsDAOService tagservice = new TagsDAOService();
        String response = tagservice.createTag(tag);

        return response;
    }

    @RequestMapping("/getpossibletags")
    public @ResponseBody String getPossibleTags(@RequestParam("partialTag")String partialTag){

        TagsDAOService tagservice = new TagsDAOService();
        List<String> possibleTags = tagservice.getTagsByPartialText(partialTag);

        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> response = new HashMap<>();
        String jsonResponse = "";


        try{
            response.put("ok",true);
            response.put("tagsSub",possibleTags.toString());
        }catch (Exception e){
            response.put("ok",false);
            response.put("reason",e.getMessage());
        }

        try {
            jsonResponse = mapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return jsonResponse;
    }

    @RequestMapping("/getalltags")
    public @ResponseBody String getAllTags(){

        TagsDAOService tagservice = new TagsDAOService();

        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> response = new HashMap<>();
        String jsonResponse = "";

        try{
            List<String> allTags = tagservice.getAllTags();
            response.put("ok",true);
            response.put("tags",allTags.toString());
        }catch (Exception e){
            response.put("ok",false);
            response.put("tags",null);
            e.printStackTrace();
        }

        try {
            jsonResponse = mapper.writeValueAsString(response);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }

        return jsonResponse.length() > 0 ? jsonResponse : "Json processing error";
    }
}
