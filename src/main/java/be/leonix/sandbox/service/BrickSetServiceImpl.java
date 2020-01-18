package be.leonix.sandbox.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.leonix.sandbox.repository.BrickSetRepository;

/**
 * @author leonix
 */
@Service
public class BrickSetServiceImpl implements BrickSetService {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(BrickSetServiceImpl.class);
	
	@SuppressWarnings("unused")
	private final BrickSetRepository brickSetRepository;
	
	@Autowired
	public BrickSetServiceImpl(BrickSetRepository brickSetRepository) {
		this.brickSetRepository = brickSetRepository;
	}
}
