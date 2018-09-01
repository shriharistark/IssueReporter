package com.example.Springgaejdohello.controller;

import com.example.Springgaejdohello.Service.Auth.Google.AuthObject;
import com.example.Springgaejdohello.Service.Auth.Google.AuthObjectBuilder;
import com.example.Springgaejdohello.Service.Auth.Google.Credentials;
import com.example.Springgaejdohello.Service.Utils.AuthResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.appengine.repackaged.com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.*;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody String setCSRF_state(){

        //new request? set session : query db for the user data
//        if(httpSession.getAttribute("state") == null){
//
//            String state = new BigInteger(130, new SecureRandom()).toString(32);
//            httpSession.setAttribute("state",state);
////            Cookie cookie = new Cookie("state",state);
////            response.addCookie(cookie);
//
////            RestTemplate restTemplate = new RestTemplate();
////            AuthObject authObject = restTemplate.getForObject("https://accounts.google.com/o/oauth2/v2/auth?" +
////                    "client_id="+CLIENT_ID+
////                    "&response_type="+"code"+
////                    "&scope="+"openid email"+
////                    "&redirect_uri="+getBase_url(request)+"/auth/google/"+
////                    "&state="+state,AuthObject.class);
//
//
//            return state;
//
//        }
//
//        else {
//            //query the db for user data
//            return "";
//        }
        return "authentication";
    }

    //redirect_uri
    @RequestMapping(value = "/google", method = RequestMethod.GET)
    public @ResponseBody String
    authenticateWithGoogle(@RequestParam("code") String authCode,
                           @RequestParam(value = "prompt", required = false)String prompt,
                           @RequestParam(value = "state", required = false) String stateToken,
                           HttpServletRequest request,
                           HttpServletResponse servletResponse) {

        //for writing response
        ObjectMapper resp = new ObjectMapper();
        HashMap<String, String> responseMap = new HashMap<>();
        String response = "";

        System.out.println("\nState: "+Credentials.CLIENT_ID+"--"+Credentials.CLIENT_SECRET+"\n");

        String state = "";
        Cookie[] ck = request.getCookies();
        for(Cookie cookie : ck){
            if(cookie.getName().equals("state")){
                state = cookie.getValue();
            }
        }

        if (stateToken == null ||
                stateToken.equals(state)) {

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            //hack code to find the host
            String uri = request.getRequestURI();
            String base_url = request.getRequestURL().toString();
            base_url = base_url.replaceFirst(uri, "");


            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", authCode);
            params.add("client_id", Credentials.CLIENT_ID);
            params.add("client_secret", Credentials.CLIENT_SECRET);
            params.add("grant_type", "authorization_code");
            params.add("redirect_uri", base_url+"/auth/google");

            /* test nude
            Map<String, String> body_request = new HashMap<String, String>();
            for(Map.Entry<String, String> elem: params.toSingleValueMap().entrySet()){
                body_request.put(elem.getKey(),elem.getValue());
            }

            Map<String, String> headers_request = new HashMap<>();
            headers_request.put("Content-type","application/x-www-form-urlencoded");
            */

            //This is how the request is suppsed to look like
            /*
            code=4%2FTACcyCPkqiHv8RjfuM3w2jEXr68lL-U4dESYEAlNzyiZdltYzhkPsa2kdNs2uUrlbY0rjLS71FIAV_lBOejoC9s
            &redirect_uri=https%3A%2F%2Fdevelopers.google.com%2Foauthplayground
            &client_id=407408718192.apps.googleusercontent.com
            &client_secret=************
            &scope=
            &grant_type=authorization_code

             */

//            System.out.println("\n---Request: "+constructRequest(body_request,headers_request)+"----\n");
            System.out.print("\nAuthcode: " + authCode + "\n");

            HttpEntity<MultiValueMap<String, String>> requestHeader = new HttpEntity<MultiValueMap<String, String>>(params, headers);

            String authResponseJson = "sample error";
            try{

                ResponseEntity<String> authResponse_entity= restTemplate
                        .exchange(
                                "https://www.googleapis.com/oauth2/v4/token",
                                HttpMethod.POST
                                , requestHeader
                                , String.class);

                if(authResponse_entity.getStatusCode().is2xxSuccessful()){
                    authResponseJson = authResponse_entity.getBody();
                }

                else {
                    authResponseJson = authResponse_entity.getBody();
                }
            }catch (HttpClientErrorException e){
                System.out.println("Client Exception 400");
            }


            System.out.print("\nResponse: " + authResponseJson + "\n");

            ObjectMapper authResponseMapper = new ObjectMapper();
            try {
                Map<String,Object> authResponse = authResponseMapper.readValue(authResponseJson, new TypeReference<Map<String, Object>>() {});

                System.out.println("----\nResponse we got from google Oauth = "+authResponse+"\n------");

                AuthObject authObject = new AuthObjectBuilder(Credentials.CLIENT_ID,"http://localhost:8080"+"/auth/google","openid email")
                        .setAccess_token(authResponse.get("access_token").toString())
                        .setExpires_in(authResponse.get("expires_in").toString())
                        .setToken_type(authResponse.get("token_type").toString())
                        .build();
                if(authResponse.get("refresh_token") != null){
                    authObject.setRefresh_token(authResponse.get("refresh_token").toString());
                }

                Map<String, Object> user_details = getUserDetails_jwt(authResponse.get("id_token").toString());

                if (!authResponse.isEmpty()) {
                    responseMap.put("ok","success");
                    responseMap.put("refreshToken", authObject.getRefresh_token());
                    responseMap.put("accessToken", authObject.getAccess_token());
                    responseMap.put("expires in", authObject.getExpires_in());
                    responseMap.put("id_token", authResponse.get("id_token").toString());
                    responseMap.put("user_email",user_details.get("email").toString());
                    responseMap.put("email_verified",user_details.get("email_verified").toString());
                    servletResponse.addCookie(new Cookie("user_jwt",responseMap.get("id_token")));
//                    servletResponse.sendRedirect("/");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }else {

            responseMap.put("ok", "failed to authenticate");
            responseMap.put("reason","Invalid state Token");
        }
        try {
            response = resp.writeValueAsString(responseMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return response;
    }


    private Map<String, Object> getUserDetails_jwt(String jsonWebToken) throws IOException {

        String payload = jsonWebToken.split("\\.")[1];
        String user_details_json = new String (Base64.getDecoder().decode(payload));
        Map<String, Object> user_details = new ObjectMapper().readValue
                (user_details_json,new TypeReference<Map<String, Object>>() {});

        return user_details;
    }


}
