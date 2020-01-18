package be.leonix.sandbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SandboxApp {
	
	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(SandboxApp.class);
		application.run(args);
	}
}
