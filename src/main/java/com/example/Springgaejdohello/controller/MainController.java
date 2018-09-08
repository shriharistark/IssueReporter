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
                servletRequest.getSession(false).getAttribute("user") != null){

            try {
                servletResponse.sendRedirect("/issue/home");
                return new ModelAndView("/issue/home");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else {
            return new ModelAndView("redirect:/landing.html");
        }

        return new ModelAndView("redirect:/landing.html");

    }

    @RequestMapping(value = "/statetoken",method = RequestMethod.GET)
    public @ResponseBody String generateStateToken(HttpServletRequest servletRequest, HttpServletResponse servletResponse){
        ObjectMapper objectMapper = new ObjectMapper();

        String CSRF_token = new BigInteger(130, new SecureRandom()).toString(32);
        HttpSession session = servletRequest.getSession();
        session.setAttribute("google_state",CSRF_token);

        HashMap<String,Object> responseObject = new HashMap<>();
        try {
            if (session != null && session.getAttribute("state") == null) {


                Cookie CSRF_cookie = new Cookie("auth_state", CSRF_token);
                CSRF_cookie.setDomain("localhost");
                CSRF_cookie.setPath("/");
                CSRF_cookie.setMaxAge(120);
                servletResponse.addCookie(CSRF_cookie);
                responseObject.put("ok", true);
                responseObject.put("state", CSRF_token);
            }
            else{
                responseObject.put("ok",false);
            }

            return objectMapper.writeValueAsString(responseObject);

        }catch (JsonProcessingException e){
            return "Json processing exception";
        }
    }
}
