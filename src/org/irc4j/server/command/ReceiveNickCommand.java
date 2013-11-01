package org.irc4j.server.command;

import java.util.List;

import org.irc4j.IRCEventHandler;
import org.irc4j.server.ClientConnection;
import org.irc4j.server.IRCServer;
import org.irc4j.server.ServerCommand;

public class ReceiveNickCommand extends ServerCommand {

	@Override
	public void execute(IRCServer ircServer, ClientConnection selfClientConnection, List<IRCEventHandler> handlers) throws Throwable {
		ircServer.dumpUsers();
		String newNickName = getCommandParameters()[0];
		if ((!newNickName.equals(selfClientConnection.getNickName())) && ircServer.hasConnection(newNickName)) {
			selfClientConnection.sendCommand("433 " + selfClientConnection.getNickName() + " :nickname " + newNickName + " aleady exists.");
			return;
		}
		String oldNickName = selfClientConnection.getNickName();

		selfClientConnection.setNickName(newNickName);
		if (null != oldNickName) {
			String newNickCommand = ":" + oldNickName + "!" + selfClientConnection.getUser().getName() + "@" + selfClientConnection.getUser().getHostName() + " NICK :" + selfClientConnection.getNickName();
			selfClientConnection.send(newNickCommand);
			ircServer.sendToSameChannelUser(selfClientConnection, newNickCommand);
		}
		ircServer.putConnection(selfClientConnection);
	}
}
