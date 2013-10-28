package org.irc4j.server;

import java.util.List;

import org.irc4j.IRCEventHandler;
import org.irc4j.User;

public class RecieveUserHostCommand extends ServerCommand {

	@Override
	public void execute(IRCServer ircServer, ClientConnection selfClientConnection, List<IRCEventHandler> handlers) throws Throwable {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < getCommandParameters().length; i++) {
			ClientConnection connection = ircServer.getConnectionMap().get(getCommandParameters()[i]);
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
