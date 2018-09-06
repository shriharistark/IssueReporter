package com.example.Springgaejdohello.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
}
