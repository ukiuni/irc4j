package org.ukiuni.irc4j.client;

import java.io.IOException;

import org.ukiuni.irc4j.Channel;
import org.ukiuni.irc4j.IRCClient;

public class ClientChannel extends Channel {
	private IRCClient ircClient;

	public ClientChannel(IRCClient ircClient, String name) {
		super(name);
		this.ircClient = ircClient;
	}
	public void join() throws IOException {
		ircClient.sendJoin(getName());
	}

	public void sendMessage(String message) throws IOException {
		ircClient.sendMessage(getName(), message);
	}
}
