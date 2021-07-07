package be.leonix.webapi.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Example {
	
	private final static Logger logger = LoggerFactory.getLogger(Example.class);
	
	@Autowired
	public Example() {
		logger.info("Example: " + this);
	}
	
	@Component
	public static class Listener {
		
		@Autowired
		public Listener(Example ex) {
			logger.info("Listener: " + ex);
		}
	}
}
