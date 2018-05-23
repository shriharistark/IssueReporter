package com.example.Springgaejdohello;

import javax.servlet.annotation.WebFilter;

import org.springframework.stereotype.Component;

import com.googlecode.objectify.ObjectifyFilter;

@Component
@WebFilter(urlPatterns = {"/*"})
public class ObjectifyWebFilter extends ObjectifyFilter {
	
	
}
