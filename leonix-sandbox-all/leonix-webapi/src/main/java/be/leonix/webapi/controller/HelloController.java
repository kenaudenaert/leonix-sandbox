package be.leonix.webapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import be.leonix.webapi.services.HelloService;

@Controller
@RequestMapping("/hello")
public class HelloController {
	
	private final HelloService helloService;
	
	@Autowired
	public HelloController(HelloService helloService) {
		this.helloService = helloService;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String printWelcome(ModelMap model) {
		// Set properties for the view page.
		model.addAttribute("message", helloService.greet());
		// Now return the view page by name.
		return "helloWorld";
	}
}
