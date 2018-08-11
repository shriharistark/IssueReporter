package com.example.Springgaejdohello;

import com.example.Springgaejdohello.model.CommentModel;
import com.example.Springgaejdohello.model.IssueModel;
import com.example.Springgaejdohello.model.TagsModel;
import com.googlecode.objectify.ObjectifyService;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SpringgaejdohelloApplication.class);
	}
}
