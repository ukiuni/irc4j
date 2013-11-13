package org.ukiuni.irc4j.server.worker.webworker;

import java.io.OutputStream;

import net.arnx.jsonic.JSON;

import org.ukiuni.irc4j.server.IRCServer;

public class ResponseUser extends AIRCResponse {

	public ResponseUser(IRCServer ircServer) {
		super(ircServer);
	}

	@Override
	public void onResponseSecure(OutputStream out) throws Throwable {
		write(out, 200, JSON.encode(getAccessConnection().getUser()), "application/json; charset=utf-8", "UTF-8");
	}
}
