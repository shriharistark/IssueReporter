package com.example.Springgaejdohello.Service.Utils.Authenticate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Scanner;

//@Controller
//@RequestMapping("/")
public class Login {

//    @Autowired
//    HttpSession httpSession;
//
//    private static final String client_id = "126208571601-fitl8ba1afjkb8on2v64fg8gfdf6efc5.apps.googleusercontent.com";
//    private static final String APPLICATION_NAME = "springboot-issuev1";
//
//    @RequestMapping("/")
//    public String goHome(HttpServletResponse response){
//
//        response.addCookie(new Cookie("client_id",client_id));
//        response.addCookie(new Cookie("application_name",APPLICATION_NAME));
//        return loginUser();
//    }
//
//    @RequestMapping(value = "/login")
//    public @ResponseBody String loginUser(){
//        String state = new BigInteger(130, new SecureRandom()).toString(32);
////        request.session().attribute("state", state);
//        httpSession.setAttribute("state",state);
//
//        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.getForObject("https://accounts.google.com/o/oauth2/v2/auth?\n" +
//                " client_id=424911365001.apps.googleusercontent.com&\n" +
//                " response_type=code&\n" +
//                " scope=openid%20email&\n" +
//                " redirect_uri=https://oauth2-login-demo.example.com/code&\n" +
//                " state=security_token%3D138r5719ru3e1%26url%3Dhttps://oauth2-login-demo.example.com/myHome&\n" +
//                " login_hint=jsmith@example.com&\n" +
//                " openid.realm=example.com&\n" +
//                " nonce=0394852-3190485-2490358&\n" +
//                " hd=example.com")
//        return "home.html";
//    }
}
