package org.ukiuni.irc4j.server.worker.webworker;

import java.io.OutputStream;

import org.ukiuni.irc4j.Log;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.server.ServerChannel;

public class ResponsePart extends AIRCResponse {

	public ResponsePart(IRCServer ircServer) {
		super(ircServer);
	}

	@Override
	public void onResponseSecure(OutputStream out) throws Throwable {
		String channelName = getRequest().getParameter("channelName");
		if (channelName.startsWith("#")) {
			ServerChannel channel = getAccessConnection().getJoinedChannel(channelName);
			if (null == channel) {
				write(out, 404, "{\"status\":\"you are not join to " + channelName + "\"}", "application/json; charset=utf-8", "UTF-8");
				return;
			}
			channel.part(getAccessConnection());
		}
		Log.log("part from web: " + getAccessConnection().getNickName() + " from " + channelName);
		write(out, 200, "{\"status\":\"parted\"}", "application/json; charset=utf-8", "UTF-8");
	}

}
