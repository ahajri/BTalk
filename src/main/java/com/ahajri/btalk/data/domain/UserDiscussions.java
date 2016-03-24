package com.ahajri.btalk.data.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlRootElement(name = "discussions")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDiscussions extends AModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1931660853065211976L;

	private String id;
	private Date creationDate;
	private List<Discussion> discussions=new ArrayList<Discussion>();

	public UserDiscussions() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlElementWrapper(name = "discussions")
	@XmlElement(name = "discussion")
	public List<Discussion> getDiscussions() {
		return discussions;
	}

	public void setDiscussions(List<Discussion> discussions) {
		this.discussions = discussions;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public String toString() {
		return "Discussions [id=" + id + ", creationDate=" + creationDate
				+ ", discussions=" + discussions + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((creationDate == null) ? 0 : creationDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		UserDiscussions other = (UserDiscussions) obj;
		if (creationDate == null) {
			if (other.creationDate != null)
				return false;
		} else if (!creationDate.equals(other.creationDate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
