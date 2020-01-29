package be.leonix.sandbox;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {
	// We don't need an in memory UserDetailsManager with an auto-generated password
	UserDetailsServiceAutoConfiguration.class
})
public class SandboxApp {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(SandboxApp.class);
		
		// Ensure that we are using the application properties we need.
		String[] arguments = Arrays.copyOf(args, args.length + 1);
		arguments[arguments.length - 1] = "--spring.config.name=sandbox";
		
		application.run(arguments);
	}
}
