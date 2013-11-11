package org.ukiuni.irc4j.server.worker.webworker;

import java.io.OutputStream;

import org.ukiuni.irc4j.Channel;
import org.ukiuni.irc4j.User;
import org.ukiuni.irc4j.server.IRCServer;

public class ResponseRejoin extends AIRCResponse {

	public ResponseRejoin(IRCServer ircServer) {
		super(ircServer);
	}

	@Override
	public void onResponseSecure(OutputStream out) throws Throwable {
		WebWorkerClientConnection clientConnection = getAccessConnection();
		if (null != clientConnection) {
			write(out, 200, "{\"status\":\"aleady join\"}", "application/json; charset=utf-8", "UTF-8");
			return;
		}
		String channelNames = getRequest().getParameter("channelNames");
		if (null == channelNames || null == getNickName()) {
			write(out, 400, "{\"status\":\"wrong channelName\"}", "application/json; charset=utf-8", "UTF-8");
			return;
		}
		{
			clientConnection = new WebWorkerClientConnection(ircServer);

			clientConnection.setNickName(getNickName());
			clientConnection.getUser().setHostName("webIF");
			clientConnection.getUser().setName(getNickName());
			clientConnection.getUser().setRealName(getNickName());
			String password = getRequest().getParameter("password");
			if (null != password && !"".equals(password)) {
				clientConnection.getUser().setPasswordHashed(User.toHash(password));
			}

			ircServer.putConnection(clientConnection);
		}
		String[] channelNameArray = channelNames.split(",");
		for (String channelName : channelNameArray) {
			if (channelName.startsWith("#")) {
				if (Channel.wrongName(channelName)) {
					write(out, 400, "{\"status\":\"wrong channelName\"}", "application/json; charset=utf-8", "UTF-8");
					return;
				}
				// TODO password
				ircServer.joinToChannel(clientConnection, channelName);
			}
		}
		write(out, 200, "{\"status\":\"rejoined\"}", "application/json; charset=utf-8", "UTF-8");
	}
}
