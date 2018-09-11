package com.example.Springgaejdohello.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;

@Controller
@RequestMapping("/")
public class MainController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView displayLandingPage(HttpServletRequest servletRequest, HttpServletResponse servletResponse){


        if(servletRequest.getSession(false) != null &&
                (servletRequest.getSession(false).getAttribute("user") != null||
                servletRequest.getSession(false).getAttribute("google_login_token")!=null)){

            try {
                System.out.println("redirecting to issue/home");
                servletResponse.sendRedirect("/issue/home");
                return new ModelAndView("redirect:/issue/home");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else {
            System.out.println("redirecting to landing.html");
            return new ModelAndView("redirect:/landing.html");
        }

        return new ModelAndView("redirect:/landing.html");

    }

    @RequestMapping(value = "/statetoken",method = RequestMethod.GET)
    public @ResponseBody String generateStateToken(HttpServletRequest servletRequest, HttpServletResponse servletResponse){

        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String,Object> responseObject = new HashMap<>();
        responseObject.put("ok",false);

        try{
            String CSRF_token = new BigInteger(130, new SecureRandom()).toString(32);
            HttpSession session = servletRequest.getSession();
            session.setAttribute("google_state",CSRF_token);
            responseObject.put("ok",true);
            responseObject.put("state",CSRF_token);
        }catch (Exception e){
            e.printStackTrace();
            responseObject.put("ok",false);
            responseObject.put("state","");
        }

        try {
            return objectMapper.writeValueAsString(responseObject);
        } catch (JsonProcessingException e) {
            return "Json parse exception";
        }
    }

//    private Cookie getCookie(HttpServletRequest request, String name){
//
//        Cookie[] cookies = request.getCookies();
//        if(cookies != null) {
//            for (Cookie cookie : cookies) {
//                if (cookie.getName().equals(name)) {
//                    return cookie;
//                }
//            }
//        }
//
//        return null;
//    }
}
