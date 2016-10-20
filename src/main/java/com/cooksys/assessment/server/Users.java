package com.cooksys.assessment.server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.cooksys.assessment.model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Users {
	private static ConcurrentHashMap<String, Socket> userlist = new ConcurrentHashMap<>();

	public static synchronized ConcurrentHashMap<String, Socket> getUserlist() {
		return userlist;
	}

	public void setUserlist(ConcurrentHashMap<String, Socket> userlist) {
		Users.userlist = userlist;
	}

	private static Set<String> names = userlist.keySet();

	public static Set<String> getNames() {
		return names;
	}

	public static void setNames(Set<String> names) {
		Users.names = names;
	}

	public static synchronized void sendMessage(String username, Message message) throws IOException {
		Socket socket = userlist.get(username);
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		ObjectMapper mapper = new ObjectMapper();
		String jMessage = mapper.writeValueAsString(message);
		writer.write(jMessage);
		writer.flush();
	}

	public static synchronized void broadcast(Message message) throws IOException {
		for (String n : names) {
			sendMessage(n, message);
		}
	}

}
