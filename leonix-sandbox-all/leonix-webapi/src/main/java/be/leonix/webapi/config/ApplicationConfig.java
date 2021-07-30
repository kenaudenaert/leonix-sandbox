package be.leonix.webapi.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages={"be.leonix.webapi.services", "be.leonix.webapi.tools"})
public class ApplicationConfig {

}
