package org.irc4j.server;

import java.util.List;

import org.irc4j.IRCEventHandler;

public class RecievePongCommand extends ServerCommand {
	@Override
	public void execute(IRCServer ircServer, ClientConnection selfClientConnection, List<IRCEventHandler> handlers) throws Throwable {
		selfClientConnection.recievePong();
	}
}
