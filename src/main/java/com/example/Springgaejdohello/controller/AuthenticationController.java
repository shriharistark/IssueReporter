package com.example.Springgaejdohello.controller;

import com.example.Springgaejdohello.Service.Auth.Google.AuthObject;
import com.example.Springgaejdohello.Service.Auth.Google.AuthObjectBuilder;
import com.example.Springgaejdohello.Service.Utils.AuthResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    HttpSession httpSession;

    private static final String CLIENT_SECRET = "fwMZPTYbTp-9hDjuaBrETEdO";
    private static final String CLIENT_ID = "126208571601-fitl8ba1afjkb8on2v64fg8gfdf6efc5.apps.googleusercontent.com";

    @RequestMapping("/*")
    public void setCSRF_state(HttpServletRequest request){

        //new request? set session : query db for the user data
        if(request.getSession(false) == null){

            String state = new BigInteger(130, new SecureRandom()).toString(32);
            httpSession.setAttribute("state",state);

        }

        else {
            //query the db for user data

        }
    }

    //redirect_uri
    @RequestMapping(value = "/google", method = RequestMethod.GET)
    public @ResponseBody String
    authenticateWithGoogle(@RequestParam("code") String authCode,
                           @RequestParam(value = "state", required = false) String stateToken,
                           HttpServletRequest request) {

        //for writing response
        ObjectMapper resp = new ObjectMapper();
        HashMap<String, String> responseMap = new HashMap<>();
        String response = "";


        if (stateToken == null ||
                stateToken.equals(request.getSession(false)
                        .getAttribute("state").toString())) {

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", authCode);
            params.add("client_id", CLIENT_ID);
            params.add("client_secret", CLIENT_SECRET);
            params.add("grant_type", "authorization_code");

            //hack code to find the host
            String uri = request.getRequestURI();
            String base_url = request.getRequestURL().toString();
            base_url = base_url.replaceFirst(uri, "");
            //

            params.add("redirect_uri", base_url+"/auth/google");

            HttpEntity<MultiValueMap<String, String>> requestHeader = new HttpEntity<MultiValueMap<String, String>>(params, headers);
            System.out.print("\nAuthcode: " + authCode + "\n");

            AuthObject responseBody = restTemplate
                    .postForObject(
                            "https://www.googleapis.com/oauth2/v4/token"
                            , requestHeader
                            , AuthObject.class);

            System.out.print("\nResponse: " + responseBody.toString() + "\n");

            try {
                if (responseBody != null) {
                    responseMap.put("ok","success");
                    responseMap.put("refreshToken", responseBody.getRefresh_token());
                    responseMap.put("accessToken", responseBody.getAccess_token());
                    responseMap.put("expires in", responseBody.getExpires_in());
                    responseMap.put("id_token", responseBody.getId_token());
                }
            } catch (NullPointerException n) {
                response = "Some error occured NPE";
                n.printStackTrace();
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
