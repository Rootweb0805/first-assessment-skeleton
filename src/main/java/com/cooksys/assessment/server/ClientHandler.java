package com.cooksys.assessment.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cooksys.assessment.model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientHandler implements Runnable {
	private Logger log = LoggerFactory.getLogger(ClientHandler.class);

	private Socket socket;

	// private Users userObj() {
	// Users users = new Users();
	// //ConcurrentHashMap<String, Socket> userlist = new ConcurrentHashMap<>();
	// users.setUserlist(Users.getUserlist());
	// return users;
	//
	// }
	//
	// Users users = userObj();

	public ClientHandler(Socket socket) {
		super();
		this.socket = socket;
	}

	public void run() {
		try {

			ObjectMapper mapper = new ObjectMapper();
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

			while (!socket.isClosed()) {
				String userlist = Users.getUserlist().toString();
				String raw = reader.readLine();
				Message message = mapper.readValue(raw, Message.class);

				switch (message.getCommand()) {
				case "connect":
					log.info("user <{}> connected", message.getUsername());
					Users.getUserlist().put(message.getUsername(), socket);
					break;
				case "disconnect":
					log.info("user <{}> disconnected", message.getUsername());
					Users.getUserlist().remove(message.getUsername());
					this.socket.close();
					break;
				case "echo":
					log.info("user <{}> echoed message <{}>", message.getUsername(), message.getContents());
					String response = mapper.writeValueAsString(message);
					writer.write(response);
					writer.flush();
					System.out.println(response);
					break;
				case "users":
					log.info("user <{}> users request <{}>", message.getUsername(), message.getContents());
					Message usersMessage = new Message();
					usersMessage.setUsername(message.getUsername());
					usersMessage.setCommand("users");
					usersMessage.setContents(userlist);
					String UL = mapper.writeValueAsString(usersMessage);
					writer.write(UL);
					writer.flush();
					break;
				case "@":
					log.info("user <{}> direct message <{}>", message.getUsername(), message.getContents());
					String direct = mapper.writeValueAsString(message);
					writer.write(direct);
					writer.flush();
					break;

				}
			}

		} catch (IOException e) {
			log.error("Something went wrong :/", e);
		}
	}

}
