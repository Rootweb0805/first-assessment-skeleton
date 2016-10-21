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
				String userlist = System.currentTimeMillis() + ": " + "Currently connected users: \n" + Users.getStackedNames().toString();
				String raw = reader.readLine();
				Message message = mapper.readValue(raw, Message.class);
				Message usersMessage = new Message();
				String username = message.getCommand().substring(1); // username

				if (message.getCommand().substring(0, 1).equals("@") && Users.getUserlist().containsKey(username)) {
					log.info("user <{}> direct message to: " + username + " <" + message.getContents() + ">",
							message.getUsername());
					Users.sendMessage(username, message);
				}

				switch (message.getCommand()) {
				case "connect":
					log.info("user <{}> connected", message.getUsername());
					Users.getUserlist().put(message.getUsername(), socket);
					Users.alert("connect", message);
					break;
				case "disconnect":
					log.info("user <{}> disconnected", message.getUsername());
					Users.getUserlist().remove(message.getUsername());
					Users.alert("disconnect", message);
					this.socket.close();
					break;
				case "echo":
					log.info("user <{}> echoed message <{}>", message.getUsername(), message.getContents());
					String formatted = (System.currentTimeMillis() + ": " + message.getUsername() + " (echo): " + message.getContents());
					message.setContents(formatted);
					String response = mapper.writeValueAsString(message);
					writer.write(response);
					writer.flush();
					break;
				case "users":
					log.info("user <{}> users request <{}>", message.getUsername(), message.getContents());
					usersMessage.setUsername(message.getUsername());
					usersMessage.setCommand(message.getCommand());
					usersMessage.setContents(userlist);
					String UL = mapper.writeValueAsString(usersMessage);
					writer.write(UL);
					writer.flush();
					break;
				case "broadcast":
					log.info("user <{}> broadcasted", message.getUsername(), message.getContents());
					Users.broadcast(message);
					break;

				}
			}

		} catch (IOException e) {
			log.error("Something went wrong :/", e);
		}
	}

}
