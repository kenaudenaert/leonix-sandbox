package be.leonix.webapi.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Component
public class Example {
	
	private final static Logger logger = LoggerFactory.getLogger(Example.class);
	
	@Autowired
	public Example() {
		logger.info("Example::new");
	}
	
	@PostConstruct
	public void registerListener() {
		logger.info("Example::registerListener ");
	}
	
	@Transactional
	public void foobar() {
		logger.info("Example::foobar");
	}
}
