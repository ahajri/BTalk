package com.ahajri.btalk.data.domain;

import java.sql.Timestamp;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlRootElement(name = "discussion")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Discussion implements IModel {

	
	/**
	 * UID of Serialization
	 */
	private static final long serialVersionUID = -8665882915966219907L;

	private Timestamp startTime;
	private Timestamp endTime;
	private List<DiscussionMember> members;
	

	public Discussion() {
		super();
	}

	public Discussion(Timestamp startTime, Timestamp endTime,
			List<DiscussionMember> members) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		this.members = members;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public List<DiscussionMember> getMembers() {
		return members;
	}

	public void setMembers(List<DiscussionMember> members) {
		this.members = members;
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
