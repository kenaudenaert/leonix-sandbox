package be.leonix.sandbox;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.config.ConfigFileApplicationListener;

/**
 * @author leonix
 */
@SpringBootApplication(exclude = {
	// We don't need an in memory UserDetailsManager with an auto-generated password
	UserDetailsServiceAutoConfiguration.class
})
public class SandboxApp {
	
	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(SandboxApp.class);
		
		// Ensure that we are using the application properties we need.
		String configName = ConfigFileApplicationListener.CONFIG_NAME_PROPERTY;
		
		String[] arguments = Arrays.copyOf(args, args.length + 1);
		arguments[arguments.length - 1] = "--" + configName + "=sandbox";
		
		application.run(arguments);
	}
}
