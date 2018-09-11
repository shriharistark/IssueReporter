package com.example.Springgaejdohello.controller;

import com.example.Springgaejdohello.Builders.UserBuilder;
import com.example.Springgaejdohello.Service.Auth.Google.AuthObject;
import com.example.Springgaejdohello.Service.Auth.Google.AuthObjectBuilder;
import com.example.Springgaejdohello.Service.Auth.Google.Credentials;
import com.example.Springgaejdohello.Service.Utils.AuthResponse;
import com.example.Springgaejdohello.dao.UserDAOService;
import com.example.Springgaejdohello.daoInterface.UserDAO;
import com.example.Springgaejdohello.model.UserModel;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
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
        return "authentication";
    }

    //redirect_uri
    @RequestMapping(value = "/google", method = RequestMethod.GET)
    public @ResponseBody String
    authenticateWithGoogle(@RequestParam("code") String authCode,
                           @RequestParam(value = "prompt", required = false)String prompt,
                           @RequestParam(value = "state", required = false) String stateTokenFromGoogle,
                           HttpServletRequest servletRequest,
                           HttpServletResponse servletResponse) {

        HttpSession session = servletRequest.getSession(false);

        //user is already logged in
        if(session != null &&
                session.getAttribute("user") != null){

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

        //state that we applied on our client side which was
        // generated from the server side - verify it on the server side as well
        System.out.println("\nState returned from google: "+stateTokenFromGoogle+"\n");

        String stateSetByUs = session.getAttribute("google_state") != null ? session.getAttribute("google_state").toString() : "";

        System.out.println("\nState applied from our issue reporter: "+stateSetByUs+"\n");

        Map<String,Object> googleResponseAsMap = new HashMap<>();

        //validating the state tokens here
        if (stateTokenFromGoogle.equals(stateSetByUs)) {

            //clear the session attribute
            session.removeAttribute("google_state");
            //hack code to find the host
            String uri = servletRequest.getRequestURI();
            String base_url = servletRequest.getRequestURL().toString();
            base_url = base_url.replaceFirst(uri, "");

            googleResponseAsMap = exchangeAuthCodeForAccessTokenWithGoogle(authCode,base_url+"/auth/google");
            Map<String, Object> user_details = getUserDetails_jwt(googleResponseAsMap.get("id_token").toString());

            //if not an existing user(a new user) ->
            // redirect to sign up page with pre-filled data from google such as user's name, email etc.
            //setting google's id token to the session

            if(userDAOService.readUser(user_details.get("email").toString().toLowerCase()) == null){

                //if user fails to signup in 10 mins, the cookie will expire
                System.out.println("\n"+user_details.get("email").toString()+" is a new user!"+"\n");

                //set the user details as jwt
                //this user jwt will expire in 10 minutes regardless of whether the user signed in or not
                Cookie user_cookie_temporary = new Cookie("user_details",googleResponseAsMap.get("id_token").toString());
                user_cookie_temporary.setMaxAge(300);
                user_cookie_temporary.setDomain("localhost");
                user_cookie_temporary.setPath("/");
                servletResponse.addCookie(user_cookie_temporary);

                session.setAttribute("google_signup_token",user_details);

                try {
                    servletResponse.sendRedirect("/");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return "new user";
            }

            System.out.println("\n"+user_details.get("email").toString()+" is an existing user!"+"\n");


            if (!googleResponseAsMap.isEmpty()) {
                responseMap.put("ok","success");
                responseMap.put("refreshToken", googleResponseAsMap.get("refresh_token").toString());
                responseMap.put("accessToken", googleResponseAsMap.get("access_token").toString());
                responseMap.put("expires in", googleResponseAsMap.get("expires_in").toString());
                responseMap.put("id_token", googleResponseAsMap.get("id_token").toString());
                responseMap.put("user_email",user_details.get("email").toString());
                responseMap.put("email_verified",user_details.get("email_verified").toString());
                responseMap.put("user_picture",user_details.get("picture").toString());

                //set user details to user's session ID
                servletRequest.getSession().setAttribute("user",user_details);

                //set user details in a cookie
                Cookie user_details_cookie = new Cookie("user_jwt", responseMap.get("id_token"));
                user_details_cookie.setDomain("localhost");
                user_details_cookie.setPath("/");
                servletResponse.addCookie(user_details_cookie);

                try {
                    servletResponse.sendRedirect("/issue/home");
                } catch (IOException e1) {
                    e1.printStackTrace();
                    System.out.println("issue/home doesn't exist");
                }
            }


        }

        else {

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

    //for the signup with social logins
    @RequestMapping(value = "/signup/email", method = RequestMethod.POST,
            consumes = "application/json",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String userSignup(@RequestBody String signupParams,
                                           HttpServletRequest servletRequest,
                                           HttpServletResponse servletResponse) throws IOException {


        //todo

        return "endpoint is not active yet";

    }

    //for the signup with social logins
    @RequestMapping(value = "/signup/{type}", method = RequestMethod.POST,
            consumes = "application/json",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String userSignup(@RequestBody String signupParams,
                                           @PathVariable("type") String signupType,
                                           HttpServletRequest servletRequest,
                                           HttpServletResponse servletResponse) throws IOException {

        HttpSession session = servletRequest.getSession(false);
        //parse protocol and base uri from the request
        String uri = servletRequest.getRequestURI();
        String base_url = servletRequest.getRequestURL().toString();
        base_url = base_url.replaceFirst(uri, "");

        Map<String,Object> responseMap = new HashMap<>();

        //sesion not equal null is an insanity check
        if(session != null && session.getAttribute("google_signup_token") != null &&
                //protect this using a filter //todo
                session.getAttribute("user") == null){

            String[] paramsTobePresentIntheRequest = new String[]{"name","email","team","type"};
            Map<String,Object> paramsReceived = new ObjectMapper().readValue
                    (signupParams,new TypeReference<Map<String, Object>>() {});
            paramsReceived.put("type",signupType);

            //invalidate the signup Cookie
            Cookie invalid_cookie = getCookie(servletRequest,"user_details");
            invalid_cookie.setMaxAge(1);
            invalid_cookie.setDomain("localhost");
            invalid_cookie.setPath("/");
            servletResponse.addCookie(invalid_cookie);

            if(checkForMandatoryParams(paramsReceived,paramsTobePresentIntheRequest)){
                if(userDAOService.readUser(paramsReceived.get("email").toString().toLowerCase()) == null){
                    //if no user is accounted with this email address
                    userDAOService.createUser(new UserBuilder(
                            paramsReceived.get("email").toString().toLowerCase(),paramsReceived.get("name").toString())
                            .setUser_status("active")
                            .setUser_type(signupType)
                            .setUser_team(paramsReceived.get("team").toString())
                            .build());

                    ObjectMapper googleTokenWriter = new ObjectMapper();

                    responseMap.put("email",paramsReceived.get("email").toString());
                    responseMap.put("googleToken",googleTokenWriter.writeValueAsString(
                            session.getAttribute("google_signup_token")));
                    System.out.println(session.getAttribute("google_signup_token").getClass());
                    session.setAttribute("google_login_token",session.getAttribute("google_signup_token"));
                    session.removeAttribute("google_signup_token");

                    Cookie user_presence = new Cookie("user_presence","true");
                    user_presence.setMaxAge(-1);
                    user_presence.setDomain("localhost");
                    user_presence.setPath("/");
                    servletResponse.addCookie(user_presence);

                    return writeResponseToJson(true,responseMap);

                }
                else{
                    responseMap.put("reason","user is already present");
                    return writeResponseToJson(false,responseMap);
                }
            }
            else{
                responseMap.put("reason","name, email and team are mandatory");
                return writeResponseToJson(false,responseMap);
            }

        }

        //write response finally
        //todo
        responseMap.put("reason","google token is unavailable at the session");
        return writeResponseToJson(false,responseMap);

    }

    //for generic sign-ups

    @RequestMapping(value = "/login/{type}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public @ResponseBody String userLogin(@RequestParam("email")String userEmail,
                                          @RequestParam("type")String userType,
                                          @RequestParam("password")String password){


        return "";

    }

    private Map<String, Object> getUserDetails_jwt(String jsonWebToken) {

        String payload = jsonWebToken.split("\\.")[1];
        String user_details_json = new String (Base64.getDecoder().decode(payload));
        Map<String, Object> user_details = null;
        try {
            user_details = new ObjectMapper().readValue
                    (user_details_json,new TypeReference<Map<String, Object>>() {});
        } catch (IOException e1) {
            e1.printStackTrace();
            System.out.println("json exception");
        }

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

        //invalidate the user_presence cookie
        if(getCookie(servletRequest,"user_presence") != null){
            Cookie user_presence = getCookie(servletRequest,"user_presence");
            user_presence.setMaxAge(0);
            user_presence.setPath("/");
            user_presence.setDomain("localhost");
            servletResponse.addCookie(user_presence);
        }

        servletRequest.getSession(false).removeAttribute("state");
        servletRequest.getSession(false).removeAttribute("user");
        servletRequest.getSession(false).removeAttribute("google_signup_token");
        servletRequest.getSession(false).removeAttribute("google_login_token");

        servletRequest.getSession(false).invalidate();
        try {
            servletRequest.logout();
        } catch (ServletException e) {
            e.printStackTrace();
        }


        return "Good bye! :))";

    }

    private Map<String,Object> exchangeAuthCodeForAccessTokenWithGoogle(String authorization_code,
                                                                        String redirect_uri) {

        //state must have been verified on the previous end point itself
        //use the authorization code and the redirect uri to post request to google server
        //get the token response and return it to the callee
        Map<String,Object> googleResponse = new HashMap<>();

        if(!authorization_code.isEmpty() && !redirect_uri.isEmpty()){

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);


            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", authorization_code);
            params.add("client_id", Credentials.CLIENT_ID);
            params.add("client_secret", Credentials.CLIENT_SECRET);
            params.add("grant_type", "authorization_code");
            params.add("redirect_uri", redirect_uri);


            System.out.print("\nAuthcode: " + authorization_code + "\n");

            HttpEntity<MultiValueMap<String, String>> requestHeader = new HttpEntity<MultiValueMap<String, String>>(params, headers);

            String authResponseJson = "sample error";

            try {
                ResponseEntity<String> authResponse_entity = restTemplate
                        .exchange(
                                "https://www.googleapis.com/oauth2/v4/token",
                                HttpMethod.POST
                                , requestHeader
                                , String.class);

                if (authResponse_entity.getStatusCode().is2xxSuccessful()) {
                    authResponseJson = authResponse_entity.getBody();
                } else {
                    authResponseJson = authResponse_entity.getBody();
                }
            }catch (HttpClientErrorException e) {
                e.printStackTrace();
                System.out.println("Invalid post requst to google");
                googleResponse.put("error message", "Client exception");
            }
            try {
                googleResponse = new ObjectMapper().readValue(authResponseJson,new TypeReference<Map<String, Object>>() {});
            } catch (IOException e1) {
                e1.printStackTrace();
                System.out.println("Error while trying to parse Google's response as json");
                googleResponse.put("error message","json exception");
            }

        }

        return googleResponse;
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

    private boolean checkForMandatoryParams(Map<String,Object> userInput, String[] paramsToCheckAgainst){

        for(String param : paramsToCheckAgainst){
            if(!userInput.containsKey(param)){
                return false;
            }
        }

        return true;
    }

    private String writeResponseToJson(boolean ok, Map<String,Object> body){

        Map<String, Object> responseMap = new HashMap<>();
        ObjectMapper responseMapper = new ObjectMapper();
        try {
            responseMap.put("ok", ok);
            responseMap.put("message", body);

            return responseMapper.writeValueAsString(responseMap);
        }catch (JsonProcessingException js){
            System.out.println("json processing exception");
            return "";
        }
    }

}
