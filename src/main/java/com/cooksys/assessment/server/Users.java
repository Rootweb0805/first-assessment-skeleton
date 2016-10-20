package com.cooksys.assessment.server;

import java.net.Socket;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import com.cooksys.assessment.model.Message;

public class Users {
	private static ConcurrentHashMap<String, Socket> userlist = new ConcurrentHashMap<>();

	public static synchronized ConcurrentHashMap<String, Socket> getUserlist() {
		return userlist;
	}

	public void setUserlist(ConcurrentHashMap<String, Socket> userlist) {
		Users.userlist = userlist;
	}
//doesn't return strings of keys when called, Michael mentioned something about converting to array??
	public static synchronized Enumeration<String> getUsers() {
		return userlist.keys();
	}
	
	public static synchronized void directMessage(String user, Message m) {
		
	}
	
}
