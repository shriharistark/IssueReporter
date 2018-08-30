package com.example.Springgaejdohello.controller;

import com.example.Springgaejdohello.Service.Auth.Google.AuthObject;
import com.example.Springgaejdohello.Service.Auth.Google.AuthObjectBuilder;
import com.example.Springgaejdohello.Service.Utils.AuthResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.appengine.repackaged.com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private static final String CLIENT_SECRET = "fwMZPTYbTp-9hDjuaBrETEdO";

    @Autowired
    private static final String CLIENT_ID = "126208571601-fitl8ba1afjkb8on2v64fg8gfdf6efc5.apps.googleusercontent.com";

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

//    @RequestMapping(value = "/do", method = RequestMethod.GET)
//    public void getAuthorizationCode(HttpServletRequest request, HttpSession session){
//
//        String uri = request.getRequestURI();
//        String base_url = request.getRequestURL().toString();
//        base_url = base_url.replaceFirst(uri, "");
//
//        RestTemplate template = new RestTemplate();
//        String AuthorisatonCodeResponse = template.getForObject(
//                "https://accounts.google.com/o/oauth2/v2/auth?" +
//                "client_id="+CLIENT_ID+"&" +
//                "response_type=code&" +
//                "scope="+"openid email profile"+"&" +
//                "redirect_uri="+base_url+"/auth/google"+"&" +
//                "state="+session.getAttribute("state").toString(),
//                String.class);
//
//        System.out.println("Authorisation code servlet get from browser?: "+AuthorisatonCodeResponse);
//
//    }

    //redirect_uri
    @RequestMapping(value = "/google", method = RequestMethod.GET)
    public @ResponseBody String
    authenticateWithGoogle(@RequestParam("code") String authCode,
                           @RequestParam("prompt")String prompt,
                           @RequestParam(value = "state", required = false) String stateToken,
                           HttpServletRequest request) {

        //for writing response
        ObjectMapper resp = new ObjectMapper();
        HashMap<String, String> responseMap = new HashMap<>();
        String response = "";

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

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", authCode);
            params.add("client_id", CLIENT_ID);
            params.add("client_secret", CLIENT_SECRET);
            params.add("grant_type", "authorization_code");

            //hack code to find the host
//            String uri = request.getRequestURI();
//            String base_url = request.getRequestURL().toString();
//            base_url = base_url.replaceFirst(uri, "");
            //

            params.add("redirect_uri", "https://localhost:8080"+"/auth/google");

            HttpEntity<MultiValueMap<String, String>> requestHeader = new HttpEntity<MultiValueMap<String, String>>(params, headers);
            System.out.print("\nAuthcode: " + authCode + "\n");

            String authResponseJson = restTemplate
                    .postForObject(
                            "https://www.googleapis.com/oauth2/v4/token"
                            , requestHeader
                            , String.class);

            System.out.print("\nResponse: " + authResponseJson + "\n");

            ObjectMapper authResponseMapper = new ObjectMapper();
            try {
                Map<String,Object> authResponse = authResponseMapper.readValue(String.valueOf(authResponseJson), new TypeReference<Map<String, Object>>() {});
                AuthObject authObject = new AuthObjectBuilder(CLIENT_ID,base_url+"/auth/google","openid email")
                        .setAccess_token(authResponse.get("access_token").toString())
                        .setExpires_in(authResponse.get("expires_in").toString())
                        .setToken_type(authResponse.get("token_type").toString()).build();

                if(authResponse.get("refresh_token") != null){
                    authObject.setRefresh_token(authResponse.get("refresh_token").toString());
                }

                String user_details_jwt = authResponse.get("id_token").toString();
                String payload = user_details_jwt.split("\\.")[1];
                String user_details_json = new String (Base64.getDecoder().decode(payload));
                HashMap<String, Object> user_details = authResponseMapper.readValue
                        (user_details_json,new TypeReference<Map<String, Object>>() {});

                if (!authResponse.isEmpty()) {
                    responseMap.put("ok","success");
                    responseMap.put("refreshToken", authObject.getRefresh_token());
                    responseMap.put("accessToken", authObject.getAccess_token());
                    responseMap.put("expires in", authObject.getExpires_in());
                    responseMap.put("id_token", authObject.getId_token());
                    responseMap.put("user_email",user_details.get("email").toString());
                    responseMap.put("email_verified",user_details.get("email_verified").toString());
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

    private String getBase_url(HttpServletRequest request){

        String url = request.getRequestURL().toString();
        String uri = request.getRequestURI();

        url = url.replaceFirst(uri,"");

        return url;
    }
}
