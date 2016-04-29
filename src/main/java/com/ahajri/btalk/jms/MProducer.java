package com.ahajri.btalk.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

public class MProducer {

	@Autowired
	private JmsTemplate jmsTemplate;

	public void sendMessageToDefaultDestination(final String message) {
		jmsTemplate.convertAndSend(message);
	}

}
