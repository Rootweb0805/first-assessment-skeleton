package com.cooksys.assessment.server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.cooksys.assessment.model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Users {
	private static ConcurrentHashMap<String, Socket> userlist = new ConcurrentHashMap<>();
	private static Set<String> names = userlist.keySet();
	static java.util.Date date = new java.util.Date();
	private static Timestamp time = new Timestamp(date.getTime());
	private static LocalDateTime local = time.toLocalDateTime();
	private static LocalTime localTime = local.toLocalTime();

	public static synchronized ConcurrentHashMap<String, Socket> getUserlist() {
		return userlist;
	}

	public void setUserlist(ConcurrentHashMap<String, Socket> userlist) {
		Users.userlist = userlist;
	}

	public static synchronized LocalTime getLocalTime() {
		return localTime;
	}

	public static synchronized Set<String> getNames() {
		return names;
	}

	public static void setNames(Set<String> names) {
		Users.names = names;
	}

	// public synchronized Timestamp getTime() {
	// return time;
	// }
	//
	// public void setTime(Timestamp time) {
	// this.time = time;
	// }

	public static synchronized void sendMessage(String username, Message message) throws IOException {
		Socket socket = userlist.get(username);
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
		ObjectMapper mapper = new ObjectMapper();
		String formatted = (System.currentTimeMillis() + ": " + message.getUsername() + " (whisper): " + message.getContents());
		message.setContents(formatted);
		String jMessage = mapper.writeValueAsString(message);
		writer.write(jMessage);
		writer.flush();
	}

	public static synchronized void broadcast(Message message) throws IOException {
		String formatted = (System.currentTimeMillis() + ": " + message.getUsername() + " (all): " + message.getContents());
		message.setContents(formatted);
		for (String n : names) {
			Socket socket = userlist.get(n);
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			ObjectMapper mapper = new ObjectMapper();
			message.setContents(formatted);
			String jMessage = mapper.writeValueAsString(message);
			writer.write(jMessage);
			writer.flush();
		}
	}

	public static synchronized void alert(String action, Message message) throws IOException {
		for (String n : names) {
			Socket socket = userlist.get(n);
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			ObjectMapper mapper = new ObjectMapper();
			if (action.equals("connect")) {
				message.setContents(System.currentTimeMillis() + ": " + message.getUsername() + " has connected.");
			} else if (action.equals("disconnect")) {
				message.setContents(System.currentTimeMillis() + ": " + message.getUsername() + " has disconnected.");
			}
			String jMessage = mapper.writeValueAsString(message);
			writer.write(jMessage);
			writer.flush();

		}
	}

}
