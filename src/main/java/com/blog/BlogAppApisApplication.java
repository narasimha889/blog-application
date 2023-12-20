package com.blog;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

import com.blog.config.AppConstants;
import com.blog.entities.Role;
import com.blog.repositories.RolesRepo;

import springfox.documentation.swagger2.annotations.EnableSwagger2;



@SpringBootApplication
public class BlogAppApisApplication implements CommandLineRunner{

	@Autowired
	private RolesRepo rolesRepo;
	public static void main(String[] args) {
		SpringApplication.run(BlogAppApisApplication.class, args);
	}
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
	@Override
	public void run(String... args) throws Exception {
		try {
			Role role = new Role();
			role.setId(AppConstants.ROLE_ADMIN);
			role.setName("ROLE_ADMIN");
			
			Role role1=new Role();
			role1.setId(AppConstants.ROLE_NORMAL);
			role1.setName("ROLE_NORMAL");
			
			List<Role> roles = List.of(role,role1);
			List<Role> result = this.rolesRepo.saveAll(roles);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
	}
	

}
