package org.irc4j.server;

import java.util.List;

import org.irc4j.IRCEventHandler;

public class RecieveUserCommand extends ServerCommand {

	@Override
	public void execute(IRCServer ircServer, ClientConnection selfClientConnection, List<IRCEventHandler> handlers) throws Throwable {
		String userName = getCommandParameters()[0];
		if (getCommandParameters().length > 3) {
			selfClientConnection.getUser().setDescription(getCommandParameters()[3]);
		}
		selfClientConnection.getUser().setName(userName);
		if (!selfClientConnection.isServerHelloSended()) {
			ircServer.sendServerHello(selfClientConnection);
		}
	}
}
