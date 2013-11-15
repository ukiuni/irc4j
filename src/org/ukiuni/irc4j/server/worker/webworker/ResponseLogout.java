package org.ukiuni.irc4j.server.worker.webworker;

import java.io.OutputStream;

import org.ukiuni.irc4j.server.IRCServer;

public class ResponseLogout extends AIRCResponse {

	public ResponseLogout(IRCServer ircServer) {
		super(ircServer);
	}

	@Override
	public void onResponseSecure(OutputStream out) throws Throwable {
		ircServer.removeConnection(getAccessConnection());
		write(out, 200, "{}", "application/json; charset=utf-8", "UTF-8");
	}
}
