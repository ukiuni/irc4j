package org.ukiuni.irc4j.server.command;

import java.util.List;

import org.ukiuni.irc4j.IRCEventHandler;
import org.ukiuni.irc4j.User;
import org.ukiuni.irc4j.server.ClientConnection;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.server.ServerCommand;

public class ReceiveUserHostCommand extends ServerCommand {

	@Override
	public void execute(IRCServer ircServer, ClientConnection selfClientConnection, List<IRCEventHandler> handlers) throws Throwable {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < getCommandParameters().length; i++) {
			ClientConnection connection = ircServer.findConnection(getCommandParameters()[i]);
			if (null != connection) {
				User user = connection.getUser();
				builder.append(user.getNickName() + "=+" + user.getName() + "@" + user.getHostName());
			}
			if (i + 1 != getCommandParameters().length) {
				builder.append(" ");
			}
		}
		selfClientConnection.sendCommand("302 " + selfClientConnection.getNickName() + " :" + builder.toString());
	}
}
