package com.ahajri.btalk.data.domain;

import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlRootElement(name = "message")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message extends AModel {

	/**
	 * UID Serializaion
	 */
	private static final long serialVersionUID = 2567331260181481195L;

	private Discussion discussion;

	private String text;

	private boolean ackited;

	private Timestamp creationTime;

	public Message() {
		super();
	}

	public Message(Discussion discussion, String text, boolean ackited,
			Timestamp creationTime) {
		super();
		this.discussion = discussion;
		this.text = text;
		this.ackited = ackited;
		this.creationTime = creationTime;
	}

	public Discussion getDiscussion() {
		return discussion;
	}

	public void setDiscussion(Discussion discussion) {
		this.discussion = discussion;
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
		result = prime * result
				+ ((creationTime == null) ? 0 : creationTime.hashCode());
		result = prime * result
				+ ((discussion == null) ? 0 : discussion.hashCode());
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
		if (discussion == null) {
			if (other.discussion != null)
				return false;
		} else if (!discussion.equals(other.discussion))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}

}
