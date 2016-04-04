package com.ahajri.btalk.data.domain;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlRootElement(name = "message")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message implements IModel {

	/**
	 * UID Serialization
	 */
	private static final long serialVersionUID = 2567331260181481195L;

	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy:MM:dd HH:mm:ss");


	private String text;

	private boolean ackited;

	private Timestamp creationTime;

	public Message() {
		super();
	}

	public Message(String text, boolean ackited,
			Timestamp creationTime) {
		super();
		this.text = text;
		this.ackited = ackited;
		this.creationTime = creationTime;
	}

	

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isAckited() {
		return ackited;
	}

	public void setAckited(boolean ackited) {
		this.ackited = ackited;
	}

	public Timestamp getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Timestamp creationTime) {
		this.creationTime = creationTime;
	}


	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (ackited ? 1231 : 1237);
		result = prime * result + ((creationTime == null) ? 0 : creationTime.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (ackited != other.ackited)
			return false;
		if (creationTime == null) {
			if (other.creationTime != null)
				return false;
		} else if (!creationTime.equals(other.creationTime))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Message [text=" + text
				+ ", ackited=" + ackited + ", creationTime="
				+ sdf.format(creationTime) + "]";
	}

}
