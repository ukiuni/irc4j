package org.ukiuni.irc4j.server.worker.webworker;

import java.io.OutputStream;

import org.ukiuni.irc4j.server.ClientConnection;
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
		if (channelName.startsWith("#")) {
			if (null == channelName || !ircServer.hasChannel(channelName)) {
				writeError(out, 404, "channelNotFound");
				return;
			} else if (null == message || "".equals(message)) {
				writeError(out, 400, "message must be not null or empty");
				return;
			}
			ServerChannel channel = ircServer.getChannel(channelName);
			channel.sendMessage("PRIVMSG", getAccessConnection(), message);
		} else {
			ClientConnection connection = ircServer.findConnection(channelName);
			if (null == connection) {
				writeError(out, 404, "user not found");
			}
			connection.sendPrivateMessage("PRIVMSG", getAccessConnection(), message);
		}
		write(out, 201, "{\"status\":\"success\"}", "application/json; charset=utf-8", "UTF-8");
	}
}
