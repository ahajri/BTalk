package com.ahajri.btalk.jms.service;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ahajri.btalk.jms.MProducer;

public class MessageProducerTest {
	private MProducer messageProducer;

	@Before
	public void setUp() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"classpath:apache-activemq-context.xml");
		messageProducer = (MProducer) applicationContext
				.getBean("messageProducer");
	}

	@Test
	public void testSendMessageToDefaultDestination() {
		messageProducer
				.sendMessageToDefaultDestination("Send this message to default destination.");
	}
}
