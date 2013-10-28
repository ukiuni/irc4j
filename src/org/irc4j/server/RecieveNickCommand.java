package org.irc4j.server;

import java.util.List;

import org.irc4j.IRCEventHandler;

public class RecieveNickCommand extends ServerCommand {

	@Override
	public void execute(IRCServer ircServer, ClientConnection selfClientConnection, List<IRCEventHandler> handlers) throws Throwable {
		String newNickName = getCommandParameters()[0];
		if (!newNickName.equals(selfClientConnection.getNickName()) && ircServer.getConnectionMap().containsKey(newNickName)) {
			selfClientConnection.sendCommand("433 " + selfClientConnection.getNickName() + " :nickname " + newNickName + " aleady exists.");
			return;
		}
		if (null != selfClientConnection.getNickName()) {
			String newNickCommand = ":" + newNickName + "!" + selfClientConnection.getUser().getName() + "@" + selfClientConnection.getUser().getHostName() + " NICK :" + selfClientConnection.getNickName();
			selfClientConnection.send(newNickCommand);
			ircServer.getConnectionMap().remove(selfClientConnection.getNickName());
			ircServer.sendToSameChannelUser(selfClientConnection, newNickCommand);
		}
		selfClientConnection.setNickName(newNickName);
	}
}
