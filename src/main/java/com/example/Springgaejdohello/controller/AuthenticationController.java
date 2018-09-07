package com.example.Springgaejdohello.controller;

import com.example.Springgaejdohello.Service.Auth.Google.AuthObject;
import com.example.Springgaejdohello.Service.Auth.Google.AuthObjectBuilder;
import com.example.Springgaejdohello.Service.Auth.Google.Credentials;
import com.example.Springgaejdohello.Service.Utils.AuthResponse;
import com.example.Springgaejdohello.dao.UserDAOService;
import com.example.Springgaejdohello.daoInterface.UserDAO;
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

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
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


    @Autowired
    UserDAOService userDAOService;

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
                           HttpServletRequest servletRequest,
                           HttpServletResponse servletResponse) {

        if(servletRequest.getSession(false) != null &&
                servletRequest.getSession(false).getAttribute("user") != null){

            try {
                System.out.println("\nAlready has details in the session..\n");
                servletResponse.sendRedirect("/");
                return "session already available";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //for writing response
        ObjectMapper resp = new ObjectMapper();
        HashMap<String, String> responseMap = new HashMap<>();
        String response = "";

        System.out.println("\nState returned from google: "+stateToken+"\n");

        String state = "";

        //nothing actually happens here in localhost. This piece of code does nothing.
        //JsessionID is not flagged as secure whatsoever.
        Cookie[] ck = servletRequest.getCookies();
        if(ck != null) {
            for (Cookie cookie : ck) {
                if (cookie.getName().equals("auth_state")) {
                    state = cookie.getValue();
                } else if (cookie.getName().equals("JSESSIONID")) {
                    System.out.println("\nJsession_id: " + cookie.getValue());
                    cookie.setDomain("localhost");
                    cookie.setPath("/");
                    cookie.setHttpOnly(true);
                    cookie.setSecure(true);
                    servletResponse.addCookie(cookie);
                }
            }
        }

        System.out.println("\nState applied from our issue reporter: "+servletRequest.getSession().getAttribute("state")+"\n");

        //even if the state is null, this will get executed
        if (stateToken == null ||
                stateToken.equals(servletRequest.getSession().getAttribute("state"))) {

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            //hack code to find the host
            String uri = servletRequest.getRequestURI();
            String base_url = servletRequest.getRequestURL().toString();
            base_url = base_url.replaceFirst(uri, "");


            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", authCode);
            params.add("client_id", Credentials.CLIENT_ID);
            params.add("client_secret", Credentials.CLIENT_SECRET);
            params.add("grant_type", "authorization_code");
            params.add("redirect_uri", base_url+"/auth/google");


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
                e.getMessage();
                e.getStackTrace();
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

                //if not an existing user(a new user) ->
                // redirect to sign up page with pre-filled data from google such as user's name, email etc.
                if(!userDAOService.isExistingUser(user_details.get("email").toString().toLowerCase())){

                    //if user fails to signup in 10 mins, the cookie will expire
                    System.out.println("\n"+user_details.get("email").toString()+" is a new user!"+"\n");

                    //set the user details as jwt
                    Cookie user_cookie_temporary = new Cookie("user_details",authResponse.get("id_token").toString());
                    user_cookie_temporary.setMaxAge(600);
                    user_cookie_temporary.setDomain("localhost");
                    user_cookie_temporary.setPath("/");
                    servletResponse.addCookie(user_cookie_temporary);
                    servletResponse.sendRedirect("/");
                    return "new user";

                }

                if (!authResponse.isEmpty()) {
                    responseMap.put("ok","success");
                    responseMap.put("refreshToken", authObject.getRefresh_token());
                    responseMap.put("accessToken", authObject.getAccess_token());
                    responseMap.put("expires in", authObject.getExpires_in());
                    responseMap.put("id_token", authResponse.get("id_token").toString());
                    responseMap.put("user_email",user_details.get("email").toString());
                    responseMap.put("email_verified",user_details.get("email_verified").toString());

                    //once state validation is done, remove the state token
                    if(getCookie(servletRequest,"auth_state") != null) {
                        Cookie auth_cookie = getCookie(servletRequest,"auth_state");
                        auth_cookie.setPath("/");
                        auth_cookie.setDomain("localhost");
                        auth_cookie.setMaxAge(0);
                        servletResponse.addCookie(auth_cookie);
                    }

                    servletRequest.getSession().setAttribute("user",user_details);

                    //set user details in a cookie
                    Cookie user_details_cookie = new Cookie("user_jwt", responseMap.get("id_token"));
                    user_details_cookie.setDomain("localhost");
                    user_details_cookie.setPath("/");
                    servletResponse.addCookie(user_details_cookie);

                    servletResponse.sendRedirect("/issue/home");
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

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public @ResponseBody String logoutUser(HttpServletRequest servletRequest,
                                           HttpServletResponse servletResponse){

        if(servletRequest.getSession(false) == null){
            return "login First";
        }

        //invalidate state cookie, if present
        System.out.println(getCookie(servletRequest,"auth_state"));
        if(getCookie(servletRequest,"auth_state") != null) {
            Cookie auth_cookie = getCookie(servletRequest,"auth_state");
            auth_cookie.setMaxAge(0);
            auth_cookie.setDomain("localhost");
            auth_cookie.setPath("/");
            servletResponse.addCookie(auth_cookie);
        }

        //invalidate the user_jwt cookie
        if(getCookie(servletRequest,"user_jwt") != null){
            Cookie user_jwtCookie = getCookie(servletRequest,"user_jwt");
            user_jwtCookie.setMaxAge(0);
            user_jwtCookie.setPath("/");
            user_jwtCookie.setDomain("localhost");
            servletResponse.addCookie(user_jwtCookie);
        }

        servletRequest.getSession(false).setAttribute("state",null);
        servletRequest.getSession(false).setAttribute("user",null);
        servletRequest.getSession(false).invalidate();
        try {
            servletRequest.logout();
        } catch (ServletException e) {
            e.printStackTrace();
        }


        return "Good bye! :))";

    }

    private Cookie getCookie(HttpServletRequest request, String name){

        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }
        }

        return null;
    }

}
