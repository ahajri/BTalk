package com.ahajri.btalk.data.domain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.ahajri.btalk.utils.DiscussRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlRootElement(name = "discussion")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Discussion implements IModel {

	/**
	 * UID of Serialization
	 */
	private static final long serialVersionUID = -8665882915966219907L;

	private String id;
	private Date startTime;
	private Date endTime;
	private List<DiscussionMember> members;
	private List<Message> messages=new ArrayList<Message>();

	private static final transient SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");

	public Discussion() {
		super();
	}

	public String getId() {
		if (this.id == null) {
			for (DiscussionMember member : this.members) {
				if (member.getDiscussRole().equals(DiscussRole.DISCUSS_CREATOR.getValue())) {
					String[] ids = member.getMember_id().split("\\.");
					this.id = ids[0].replaceAll("@", "_")+"_"+sdf.format(new Date());
				}
			}
		}
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@XmlElementWrapper(name = "members")
	@XmlElement(name = "member")
	public List<DiscussionMember> getMembers() {
		return members;
	}

	public void setMembers(List<DiscussionMember> members) {
		this.members = members;
	}

	@XmlElementWrapper(name = "messages")
	@XmlElement(name = "message")
	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public String getDocName() {
		return "discussion__"+this.getId()+".json";
	}

	@Override
	public String toString() {
		return "Discussion [id=" + id + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", members=" + members + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result + ((members == null) ? 0 : members.hashCode());
		result = prime * result
				+ ((startTime == null) ? 0 : startTime.hashCode());
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
		Discussion other = (Discussion) obj;
		if (endTime == null) {
			if (other.endTime != null)
				return false;
		} else if (!endTime.equals(other.endTime))
			return false;
		if (members == null) {
			if (other.members != null)
				return false;
		} else if (!members.equals(other.members))
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		return true;
	}

}
