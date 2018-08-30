package com.example.Springgaejdohello.Service.Utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.InputStreamReader;
import java.util.Collections;

@Controller
public class Authorize {

//    @RequestMapping(value = "/auth")
    public void authorize() throws Exception {
        // load client secrets
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(new JacksonFactory(),
                new InputStreamReader(APICredentials.class.getResourceAsStream("client_secrets.json")));

        final String uriToRead =  "https://accounts.google.com/o/oauth2/v2/auth?" +
                "redirect_uri=https://springboot-issuev1.appspot.com/&" +
                "prompt=consent&response_type=code&" +
                "client_id=126208571601-qoufn463ar3hd0hke7a3ql8q9uhehqpv.apps.googleusercontent.com" +
                "&scope={scope}&" +
                "access_type=offline";

        // set up authorization code flow


        // authorize
    }

    @RequestMapping(value = "https://www.googleapis.com/auth/plus.login")
    public void getToken(){
        
    }

}
