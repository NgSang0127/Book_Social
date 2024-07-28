package org.sang.booksocialnetwork;

import org.sang.booksocialnetwork.role.Role;
import org.sang.booksocialnetwork.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class BookSocialNetworkApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookSocialNetworkApplication.class, args);

	}
	@Bean
	public CommandLineRunner runner(RoleRepository repo){
		return args ->{
			if(repo.findByName("USER").isEmpty()){
				repo.save(
						Role.builder()
								.name("USER")
								.build()
				);
			}
		};
	}

}
