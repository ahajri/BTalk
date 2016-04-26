package com.ahajri.btalk.jms.service;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ahajri.btalk.data.domain.ActionResult;
import com.ahajri.btalk.utils.ActionResultName;
import com.google.gson.Gson;

/**
 * JMS Messaging producer Service class
 * 
 * @author
 *         <p>
 *         ahajri
 *         </p>
 */
@Service("producerService")
public class ProducerService {
	/** Topic Name */
	private static final String TOPIC_NAME = "btalkTopic";

	/** LOGGER */
	private static final Logger LOGGER = Logger.getLogger(ProducerService.class);

	@Autowired
	protected ActiveMQConnectionFactory jmsConnectionFactory;

	private final Gson gson = new Gson();

	/**
	 * Send Text Message to JMS Broker and save it in MarkLogic
	 * 
	 * @param textMessage:
	 *            JSON Map of Text Message
	 * @return true if Message sent false if not
	 */
	public ActionResult sendTextMessage(Map<String, Object> jsonMsg) {
		ActionResult result = null;
		Connection connection = null;
		try {
			connection = jmsConnectionFactory.createConnection();
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Topic topic = session.createTopic(TOPIC_NAME);
			MessageProducer producer = session.createProducer(topic);
			TextMessage message = session.createTextMessage();
			String textMessage = (String) jsonMsg.get("textMessage");
			message.setText(textMessage);
			producer.send(message);
			result = createResult("OK", HttpStatus.OK, ActionResultName.SUCCESSFULL, "Message Sent");
		} catch (JMSException e) {
			result = createResult("KO", HttpStatus.INTERNAL_SERVER_ERROR, ActionResultName.FAIL, e.getMessage());
			LOGGER.error(e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
					result.setActionResultName(ActionResultName.SUCCESSFULL_WITH_WARNING);
					LOGGER.error(e);
				}
			}
		}
		return result;
	}

	/**
	 * Create {@link ActionResult} object
	 * 
	 * @param jsonStatus
	 * @param status
	 *            {@link HttpStatus}: HTTP Status
	 * @param resultName:
	 *            Result Name <code>Example: E ===> Error</code>
	 * @param jsonMessage:
	 *            displayer message on UI
	 */
	private ActionResult createResult(String jsonStatus, HttpStatus status, ActionResultName resultName,
			String jsonMessage) {
		ActionResult result = new ActionResult();
		result.setActionResultName(resultName);
		result.setStatus(status);
		Map<String, Object> resultMap = new LinkedHashMap<>();
		resultMap.put("message", jsonMessage);
		resultMap.put("status", jsonStatus);
		result.setJsonReturnData(gson.toJson(resultMap));
		return result;
	}
}
