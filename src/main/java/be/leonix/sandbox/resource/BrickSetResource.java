package be.leonix.sandbox.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import be.leonix.sandbox.service.BrickSetService;

/**
 * @author leonix
 */
@Controller
public class BrickSetResource {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(BrickSetResource.class);
	
	@SuppressWarnings("unused")
	private final BrickSetService brickSetService;
	
	@Autowired
	public BrickSetResource(BrickSetService brickSetService) {
		this.brickSetService = brickSetService;
	}
}
