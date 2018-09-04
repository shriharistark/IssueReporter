package com.example.Springgaejdohello.controller;

import com.example.Springgaejdohello.Builders.UserBuilder;
import com.example.Springgaejdohello.Builders.UserObjectSerializer;
import com.example.Springgaejdohello.dao.UserDAOService;
import com.example.Springgaejdohello.model.UserModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.googlecode.objectify.Key;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserDAOService userDAOService;

    private static Map<String,Object> response = new HashMap<String, Object>();

    //create user
    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = "application/json")
    public @ResponseBody String createUser(@RequestBody String userPayload){

        boolean result = false;
        String message = "";


        try {
            Map<String,String> userData = new ObjectMapper().readValue(userPayload, new TypeReference<Map<String, String>>() {});

            UserModel new_user = new UserBuilder(userData.get("user_email").toLowerCase(),userData.get("user_name"))
                    .setUser_picture(userData.get("user_picture") != null ? userData.get("user_picture") : "")
                    .setUser_status("active")
                    .setUser_team(userData.get("user_team") != null ? userData.get("user_team") : "")
                    .setUser_type(userData.get("user_type") != null ? userData.get("user_type") : "")
                    .setRefresh_token(userData.get("user_refresh_token") != null ? userData.get("user_refresh_token") : "")
                    .build();

            try{
                Key<UserModel> newuser_key = userDAOService.createUser(new_user);

                if(newuser_key != null){
                    result = true;
                    message = getUserResponseMapper().writeValueAsString(new_user);
                }
                else {
                    message = "user already exists";
                }

            }catch (Exception e){
                e.printStackTrace();
                message = e.getMessage();
            }

        } catch (IOException e) {
            e.printStackTrace();
            message = "email and username is required, "+e.getMessage();
        }

        return writeJsonResponseToClient(result,message);
    }

    //read user
    @RequestMapping(value = "/read",method = RequestMethod.GET)
    public @ResponseBody String readUser(@RequestParam("user_email") String userEmail){

        boolean result = false;
        String messageInJson = "";

        try{
            UserModel user_requested = userDAOService.readUser(userEmail);

            if(user_requested != null){
                result = true;
                messageInJson = getUserResponseMapper().writeValueAsString(user_requested);
            }
            else {
                messageInJson = userEmail+ "doesn't exist";
            }

        }catch (Exception e){
            e.printStackTrace();
            messageInJson = e.getMessage();
        }

        return writeJsonResponseToClient(result,messageInJson);
    }

    //update user

    //delete user

    //util methods
    private static ObjectMapper getUserResponseMapper(){
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("UserObjectSerializer"
                , new Version(1, 0, 0, null, null, null));

        module.addSerializer(UserModel.class,new UserObjectSerializer());
        mapper.registerModule(module);

        return mapper;
    }

    private static String writeJsonResponseToClient(boolean success, String responseMessageInJson) {

        response.put("ok",success);
        response.put("message",responseMessageInJson);
        ObjectMapper responseMapper = new ObjectMapper();
        try {
            return responseMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "json parse exception";
        }
    }
}
