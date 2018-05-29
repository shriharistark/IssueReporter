package com.example.Springgaejdohello.dao;

import com.example.Springgaejdohello.ObjectifyWorker;
import com.example.Springgaejdohello.daoInterface.TagsDAO;
import com.example.Springgaejdohello.model.IssueModel;
import com.example.Springgaejdohello.model.TagsModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagsDAOService implements TagsDAO {

    static Objectify objectifyInstance;
    Query<TagsModel> listOfTags;

    public TagsDAOService(){
        if(objectifyInstance == null) {
            objectifyInstance = ObjectifyWorker.getofy();
        }
    }

    @Override
    public List<String> getTagsByPartialText(String partialTag) {
        listOfTags = objectifyInstance.load().type(TagsModel.class).filter("tagSubs",partialTag);

        List<String> matchedTags = new ArrayList<>();

        if(listOfTags.list().size() > 0) {
            for (TagsModel tagEntity : listOfTags) {
                matchedTags.add(tagEntity.getTag());
            }
        }
        return matchedTags;
    }

    @Override
    public String createTag(String newtag) {

        ObjectMapper mapper = new ObjectMapper();

        Map<String,Object> response = new HashMap<>();
        String jsonResponse = "";

        TagsModel tag = new TagsModel(newtag);
        tag.setTagSubs();

        try{
            objectifyInstance.save().entity(tag).now();
            response.put("ok",true);
            response.put("tagsSub",tag.getTagSubs().toString());
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
}
