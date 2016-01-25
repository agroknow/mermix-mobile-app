package com.realestate.model.common;

import java.io.Serializable;

/**
 * Created on 24/01/2016
 * Description:
 */
public class User extends Pojo implements Serializable {
	String uid;
	String mail;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}
}
