package pl.sk.photosharingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class PhotoSharingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhotoSharingServiceApplication.class, args);
	}
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/login").allowedOrigins("http://localhost:3000");
				registry.addMapping("/users").allowedOrigins("http://localhost:3000");
				registry.addMapping("/users/register").allowedOrigins("http://localhost:3000");
				registry.addMapping("/users/follow").allowedOrigins("http://localhost:3000");
				registry.addMapping("/users/unfollow").allowedOrigins("http://localhost:3000");
				registry.addMapping("/users/followers").allowedOrigins("http://localhost:3000");
				registry.addMapping("/users/following").allowedOrigins("http://localhost:3000");
				registry.addMapping("/users/search").allowedOrigins("http://localhost:3000");
				registry.addMapping("/users/update/description").allowedOrigins("http://localhost:3000");
				registry.addMapping("/users/page").allowedOrigins("http://localhost:3000");
				registry.addMapping("/images/upload").allowedOrigins("http://localhost:3000");
				registry.addMapping("/images/user").allowedOrigins("http://localhost:3000");

			}
		};
	}

}
