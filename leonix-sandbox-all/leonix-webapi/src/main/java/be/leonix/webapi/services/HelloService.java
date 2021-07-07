package be.leonix.webapi.services;

import org.springframework.stereotype.Service;

@Service
public class HelloService {
	
	public String greet() {
		return "Spring MVC Hello World !!";
	}
}
