package com.cooksys.assessment.server;

import java.net.Socket;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

public class Users {
	private static ConcurrentHashMap<String, Socket> userlist = new ConcurrentHashMap<>();

	public static synchronized ConcurrentHashMap<String, Socket> getUserlist() {
		return userlist;
	}

	public void setUserlist(ConcurrentHashMap<String, Socket> userlist) {
		Users.userlist = userlist;
	}

	public static synchronized Enumeration<String> getUsers() {
		return userlist.keys();
	}
	
}

// private static HashSet<String> userlist = new HashSet<String>();
//
// public static synchronized HashSet<String> getUserlist() {
// return userlist;
// }
//
// public void setUserlist(HashSet<String> userlist) {
// Users.userlist = userlist;
// }