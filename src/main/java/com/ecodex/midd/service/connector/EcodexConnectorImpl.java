package com.ecodex.midd.service.connector;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.domibus.connector.ws.backend.webservice.DomibusConnectorBackendWebService;
import eu.domibus.connector.ws.backend.webservice.EmptyRequestType;
import eu.domibus.connector.ws.backend.webservice.ListPendingMessageIdsResponse;

@Service
public class EcodexConnectorImpl implements EcodexConnector{
	
	
	  private Logger logger = LogManager.getLogger(EcodexConnectorImpl.class);

	  @Autowired
	  private DomibusConnectorBackendWebService connector;
	
	
	  @Override
	  public List<String> listPendingMessageIds() throws Exception {
		  
		logger.debug(">>>EcodexConnectorImpl>>> listPendingMessageIds()");

	    ListPendingMessageIdsResponse pendingMessagesIds = this.connector.listPendingMessageIds(new EmptyRequestType());

	    return pendingMessagesIds.getMessageTransportIds();
	  }
	  
	  
	  
	  

}
