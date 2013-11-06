package org.ukiuni.irc4j.server.worker.webworker;

import java.io.OutputStream;

import org.ukiuni.irc4j.Channel;
import org.ukiuni.irc4j.Log;
import org.ukiuni.irc4j.server.IRCServer;

public class ResponseJoin extends AIRCResponse {

	public ResponseJoin(IRCServer ircServer) {
		super(ircServer);
	}

	@Override
	public void onResponseSecure(OutputStream out) throws Throwable {
		String channelName = getRequest().getParameter("channelName");
		if (channelName.startsWith("#")) {
			if (Channel.wrongName(channelName)) {
				write(out, 400, "{\"status\":\"wrong channelName\"}", "application/json; charset=utf-8", "UTF-8");
				return;
			}
			String password = getRequest().getParameter("password");
			if ("".equals(password)) {
				password = null;
			}
			//TODO if password wrong
			ircServer.joinToChannel(getAccessConnection(), channelName, password);
			
		}
		Log.log("join from web: " + getAccessConnection().getNickName() + " to " + channelName);
		write(out, 200, "{\"status\":\"joined\"}", "application/json; charset=utf-8", "UTF-8");
	}

}
