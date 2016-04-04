package com.ahajri.btalk.data.domain;

import java.sql.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement(name = "member")
public class DiscussionMember implements IModel {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = 7041184108524473923L;

	/**
	 * Fields
	 */
	private String member_id;
	private String password;
	private String discussRole;
	private String firstName;
	private String lastName;
	private String gender;
	private String status;
	private Date birthday;

	public DiscussionMember() {
		super();
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getDiscussRole() {
		return discussRole;
	}

	/**
	 * identity supposed to be Unique
	 * 
	 * @return Unique identifier
	 */
	public String getMember_id() {
		return member_id;
	}
	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}

	public void setDiscussRole(String discussRole) {
		this.discussRole = discussRole;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "DiscussionMember [member_id= " + member_id + " , password=" + password
				+ ", discussRole=" + discussRole + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", gender=" + gender + ", status="
				+ status + ", birthday=" + birthday + "]";
	}



}
