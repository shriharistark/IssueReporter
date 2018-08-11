package com.example.Springgaejdohello;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import com.example.Springgaejdohello.model.CommentModel;
import com.example.Springgaejdohello.model.IssueModel;
import com.example.Springgaejdohello.model.TagsModel;
import com.googlecode.objectify.ObjectifyService;
import org.springframework.stereotype.Component;

import com.googlecode.objectify.ObjectifyFilter;

import java.io.IOException;

@Component
@WebFilter(urlPatterns = {"/*","/"})
public class ObjectifyWebFilter extends ObjectifyFilter {

    public ObjectifyWebFilter() {
        ObjectifyService.register(IssueModel.class);
        ObjectifyService.register(TagsModel.class);
        ObjectifyService.register(CommentModel.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        super.doFilter(request, response, chain);
    }
}
