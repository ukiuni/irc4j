package org.irc4j.server;

import java.util.List;

import org.irc4j.IRCEventHandler;

public class RecieveUserCommand extends ServerCommand {

	@Override
	public void execute(IRCServer ircServer, ClientConnection selfClientConnection, List<IRCEventHandler> handlers) throws Throwable {
		String newNickName = getCommandParameters()[0];
		if (!newNickName.equals(selfClientConnection.getNickName()) && ircServer.getConnectionMap().containsKey(newNickName)) {
			selfClientConnection.sendCommand("433 " + selfClientConnection.getNickName() + " :nickname " + newNickName + " aleady exists.");
			return;
		}
		selfClientConnection.getUser().setNickName(newNickName);
		if (getCommandParameters().length >= 3) {
			selfClientConnection.getUser().setName(getCommandParameters()[2]);
		}
		String nickName = selfClientConnection.getNickName();
		selfClientConnection.sendCommand("001 " + nickName + " :Welcome to " + ircServer.getServerName() + ", Multi-Communication server IRC interface.");
		selfClientConnection.sendCommand("004 " + nickName + " " + ircServer.getServerName() + " ");
		selfClientConnection.sendCommand("375 " + nickName + " :- " + ircServer.getServerName() + " Message of the Day -");
		selfClientConnection.sendCommand("372 " + nickName + " :- Hello. Welcome to " + ircServer.getServerName() + ", a test.");
		selfClientConnection.sendCommand("372 " + nickName + " :- forsome " + "for more in.");
		selfClientConnection.sendCommand("376 " + nickName + " :End of MOTD command. is what");
	}
}
