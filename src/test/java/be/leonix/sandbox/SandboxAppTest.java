package be.leonix.sandbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@TestPropertySource("classpath:config/sandbox-test.properties")
public class SandboxAppTest {
	
	private static final Logger logger = LoggerFactory.getLogger(SandboxAppTest.class);
	
	@Autowired
	private ApplicationContext context;
	
	@Test
	void printMongoContext() {
		List<String> mongoBeanTypeIDs = new ArrayList<>();
		
		String[] beanDefNames = context.getBeanDefinitionNames();
		for (String beanDefName : beanDefNames) {
			String beanTypeID = context.getType(beanDefName).toString();
			if (beanTypeID.toLowerCase().contains("mongo")) {
				mongoBeanTypeIDs.add(beanTypeID);
			}
		}
		
		Collections.sort(mongoBeanTypeIDs);
		for (String mongoBeanTypeID : mongoBeanTypeIDs) {
			logger.info(mongoBeanTypeID);
		}
	}
}
