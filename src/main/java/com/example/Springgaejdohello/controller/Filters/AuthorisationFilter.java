package com.example.Springgaejdohello.controller.Filters;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@WebFilter(urlPatterns = "/*")
@Order(2)
public class AuthorisationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;

        if(servletRequest.getSession(false) != null){
            if(servletRequest.getSession().getAttribute("user") != null){
                chain.doFilter(servletRequest,servletResponse);
            }

            else{
                Cookie user_presence = new Cookie("user_presence","false");
                user_presence.setMaxAge(300);
                servletResponse.addCookie(user_presence);
                chain.doFilter(servletRequest,servletResponse);
            }
        }

        else {
            servletRequest.getSession(true);
            System.out.println("no session!");
            chain.doFilter(servletRequest,servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
