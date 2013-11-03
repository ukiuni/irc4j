package org.ukiuni.irc4j.server.worker.webworker;

import java.io.OutputStream;

import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.server.ServerChannel;

public class ResponsePostMessage extends AIRCResponse {

	public ResponsePostMessage(IRCServer ircServer) {
		super(ircServer);
	}

	@Override
	public void onResponseSecure(OutputStream out) throws Throwable {
		String channelName = getRequest().getParameter("channelName");
		String message = getRequest().getParameter("message");
		if (null == channelName || !ircServer.hasChannel(channelName)) {
			writeError(out, 404, "channelNotFound");
			return;
		}
		if (null == message || "".equals(message)) {
			writeError(out, 400, "message must be not null or empty");
			return;
		}
		ServerChannel channel = ircServer.getChannel(channelName);
		channel.sendMessage("PRIVMSG", getAccessConnection().getUser().getFQUN(), message, getAccessConnection());
		write(out, 201, "{\"status\":\"success\"}", "application/json; charset=utf-8", "UTF-8");
	}
}
