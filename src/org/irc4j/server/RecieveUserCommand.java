package org.irc4j.server;

import java.util.List;

import org.irc4j.IRCEventHandler;

public class RecieveUserCommand extends ServerCommand {

	@Override
	public void execute(IRCServer ircServer, ClientConnection selfClientConnection, List<IRCEventHandler> handlers) throws Throwable {
		String newNickName = getCommandParameters()[0];
		if (getCommandParameters().length >= 3) {
			selfClientConnection.getUser().setName(getCommandParameters()[2]);
		}
		if (!newNickName.equals(selfClientConnection.getNickName()) && ircServer.hasConnection(newNickName)) {
			selfClientConnection.sendCommand("433 " + selfClientConnection.getNickName() + " :nickname " + newNickName + " aleady exists.");
			return;
		}
		selfClientConnection.getUser().setNickName(newNickName);
		if(!selfClientConnection.isServerHelloSended()){
			ircServer.sendServerHelloAndPutConnection(selfClientConnection);
		}
	}
}
