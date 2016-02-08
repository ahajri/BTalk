package com.ahajri.msgsys.data.domain;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.ahajri.msgsys.utils.SecurityUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAuth implements IModel {

	/**
	 * Serialization UID
	 */
	private static final long serialVersionUID = 1320503090396175739L;
	/**
	 * Fields
	 */
	@Id
	public ObjectId _id;
	public String email;
	public String password;
	public List<String> roles;

	/**
	 * Constructor
	 */
	public UserAuth() {
		super();
	}

	public UserAuth(String email, String password, List<String> roles) {
		super();
		this.email = email;
		this.password = password;
		this.roles = roles;
	}

	/**
	 * Getters & Setters
	 */
	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = SecurityUtils.md5(password);
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	/**
	 * Equals & hashcode methods
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
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
		UserAuth other = (UserAuth) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (roles == null) {
			if (other.roles != null)
				return false;
		} else if (!roles.equals(other.roles))
			return false;
		return true;
	}

}
