package com.ecodex.midd.service.rest;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecodex.midd.service.connector.EcodexConnector;
import com.ecodex.midd.service.connector.EcodexConnectorImpl;

@RestController
@RequestMapping("/connector")
public class EcodexController {
	
	@Autowired
	EcodexConnector ecodexConnector ;
	
	private Logger logger = LogManager.getLogger(EcodexConnectorImpl.class);
	
	@GetMapping("/getlistPendingMessageIds")
	public List<String> getlistPendingMessageIds () throws Exception {
		
		logger.debug(">>>EcodexController>>>  /getlistPendingMessageIds");
		
		return ecodexConnector.listPendingMessageIds();
		
	}

}
