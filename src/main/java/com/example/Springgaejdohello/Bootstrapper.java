package com.example.Springgaejdohello;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.example.Springgaejdohello.dao.CommentBuilder;
import com.example.Springgaejdohello.model.CommentModel;
import com.example.Springgaejdohello.model.TagsModel;
import com.googlecode.objectify.annotation.Subclass;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.example.Springgaejdohello.model.IssueModel;
import com.googlecode.objectify.ObjectifyService;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

public class Bootstrapper extends SpringBootServletInitializer {


    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        //WebApplicationContext rootAppContext = createRootApplicationContext(servletContext);
        ObjectifyService.register(IssueModel.class);
        ObjectifyService.register(TagsModel.class);
        ObjectifyService.register(CommentModel.class);
}
}
