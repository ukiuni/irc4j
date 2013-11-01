package org.irc4j.server;

import java.util.List;

import org.irc4j.IRCEventHandler;

public class ReceiveJoinCommand extends ServerCommand {

	@Override
	public void execute(IRCServer ircServer, ClientConnection selfClientConnection, List<IRCEventHandler> handlers) throws Throwable {
		String[] channelNames = getCommandParameters()[0].split(",");
		String password = null;
		if (getCommandParameters().length >= 2 && !":".equals(getCommandParameters()[1])) {
			password = getCommandParameters()[1];
		}
		for (String channelName : channelNames) {
			ircServer.joinToChannel(selfClientConnection, channelName, password);
		}
	}
}
