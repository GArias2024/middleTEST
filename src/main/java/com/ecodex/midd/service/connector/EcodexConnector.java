package com.ecodex.midd.service.connector;

import java.util.List;


public interface EcodexConnector {
	

//	void acknowledgeMessage(DomibusConnectorMessageResponseType acknowledgeMessageRequest)
//			throws EcodexException;

//	DomibusConnectorMessageType getMessageById(String getMessageByIdRequest) throws EcodexException;

//	List<String> listPendingMessageIds() throws EcodexException;
	
	List<String> listPendingMessageIds() throws Exception;

//	DomibsConnectorAcknowledgementType submitMessage(DomibusConnectorMessageType submitMessageRequest)
//			throws EcodexException;
//
//	Map<String, DomibusConnectorMessageType> requestNewMessagesFromConnector(Integer maxFetchCount,
//			boolean acknowledgeAutomatically) throws EcodexException;


}
