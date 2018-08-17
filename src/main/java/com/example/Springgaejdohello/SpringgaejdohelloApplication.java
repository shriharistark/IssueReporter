package com.example.Springgaejdohello;

import com.example.Springgaejdohello.model.CommentModel;
import com.example.Springgaejdohello.model.IssueModel;
import com.example.Springgaejdohello.model.TagsModel;
import com.googlecode.objectify.ObjectifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class SpringgaejdohelloApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(SpringgaejdohelloApplication.class, args);
	}

	public SpringgaejdohelloApplication(){
		ObjectifyService.register(IssueModel.class);
		ObjectifyService.register(TagsModel.class);
		ObjectifyService.register(CommentModel.class);
	}

//	@Bean
//	public FilterRegistrationBean objectifyFilter(){
//		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//		registrationBean.setFilter(new ObjectifyWebFilter());
//		registrationBean.addUrlPatterns("/*");
//		registrationBean.setOrder(1);
//
//		return registrationBean;
//	}
//	@GetMapping("/home")
//	public String home(@RequestParam("data")String homename) {
//		return "home is "+homename;
//	}
}
