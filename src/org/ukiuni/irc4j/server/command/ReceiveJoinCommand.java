package org.ukiuni.irc4j.server.command;

import java.util.List;

import org.ukiuni.irc4j.IRCEventHandler;
import org.ukiuni.irc4j.server.ClientConnection;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.server.ServerCommand;

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
