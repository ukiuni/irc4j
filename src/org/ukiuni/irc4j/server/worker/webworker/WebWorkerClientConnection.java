package org.ukiuni.irc4j.server.worker.webworker;

import java.io.IOException;
import java.util.Date;

import org.ukiuni.irc4j.server.ClientConnection;
import org.ukiuni.irc4j.server.IRCServer;

public class WebWorkerClientConnection extends ClientConnection {

	public WebWorkerClientConnection(IRCServer ircServer) throws IOException {
		super(ircServer, null);
	}

	@Override
	public synchronized void send(String lowCommand) throws IOException {
		System.out.println("********WebWorkerClientConnection#send > " + lowCommand);
	}
	@Override
	public Date getLastRecievePongDate() {
		return new Date();
	}

}
