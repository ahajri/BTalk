package com.ahajri.btalk.jms.service;

import javax.jms.*;
import javax.naming.*;
import org.apache.log4j.BasicConfigurator;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DemoPublisherSubscriberModel implements javax.jms.MessageListener {
	private TopicSession pubSession;
	private TopicPublisher publisher;
	private TopicConnection connection;

	private final static String topicName = "msgTopic";
	private final static String username = "mercure";
	private final static String password = "mercurepass";

	/* Establish JMS publisher and subscriber */
	public DemoPublisherSubscriberModel(String topicName, String username,
			String password) throws Exception {
		// Obtain a JNDI connection
		InitialContext jndi = new InitialContext();
		// Look up a JMS connection factory
		TopicConnectionFactory conFactory = (TopicConnectionFactory) jndi
				.lookup("topicConnectionFactry");
		// Create a JMS connection
		connection = conFactory.createTopicConnection(username, password);
		// Create JMS session objects for publisher and subscriber
		pubSession = connection.createTopicSession(false,
				Session.AUTO_ACKNOWLEDGE);
		TopicSession subSession = connection.createTopicSession(false,
				Session.AUTO_ACKNOWLEDGE);
		// Look up a JMS topic
		Topic chatTopic = (Topic) jndi.lookup(topicName);
		// Create a JMS publisher and subscriber
		publisher = pubSession.createPublisher(chatTopic);
		TopicSubscriber subscriber = subSession.createSubscriber(chatTopic);
		// Set a JMS message listener
		subscriber.setMessageListener(this);
		// Start the JMS connection; allows messages to be delivered
		connection.start();
		// Create and send message using topic publisher
		TextMessage message = pubSession.createTextMessage();
		message.setText(username + ": Howdy Friends!");
		publisher.publish(message);
	}

	/*
	 * A client can register a message listener with a consumer. A message
	 * listener is similar to an event listener. Whenever a message arrives at
	 * the destination, the JMS provider delivers the message by calling the
	 * listener's onMessage method, which acts on the contents of the message.
	 */
	public void onMessage(Message message) {
		try {
			TextMessage textMessage = (TextMessage) message;
			String text = textMessage.getText();
			System.out.println(text);
		} catch (JMSException jmse) {
			jmse.printStackTrace();
		}
	}

	public static void main(String[] args) {
		BasicConfigurator.configure();
		try {
			DemoPublisherSubscriberModel demo = new DemoPublisherSubscriberModel(
					topicName, username, password);
			BufferedReader commandLine = new java.io.BufferedReader(
					new InputStreamReader(System.in));
			// closes the connection and exit the system when 'exit' enters in
			// the command line
			while (true) {
				String s = commandLine.readLine();
				if (s.equalsIgnoreCase("exit")) {
					demo.connection.close();
					System.exit(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
