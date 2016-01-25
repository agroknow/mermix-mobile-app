package com.realestate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.realestate.model.common.Pojo;
import com.realestate.model.common.User;

import java.io.Serializable;
import java.util.List;

/**
 * Created on 24/01/2016
 * Description:
 */
public class ListOfUsers extends Pojo implements Serializable {
	@JsonProperty("list")
	private List<User> users;

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
}
