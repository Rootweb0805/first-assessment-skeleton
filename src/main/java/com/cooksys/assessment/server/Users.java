package com.cooksys.assessment.server;

import java.util.HashSet;

public class Users {

	private HashSet<String> userlist = new HashSet<String>();

	public HashSet<String> getUserlist() {
		return userlist;
	}

	public void setUserlist(HashSet<String> userlist) {
		this.userlist = userlist;
	}

}