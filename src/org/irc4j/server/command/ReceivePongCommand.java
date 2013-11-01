package org.irc4j.server.command;

import java.util.List;

import org.irc4j.IRCEventHandler;
import org.irc4j.server.ClientConnection;
import org.irc4j.server.IRCServer;
import org.irc4j.server.ServerCommand;

public class ReceivePongCommand extends ServerCommand {
	@Override
	public void execute(IRCServer ircServer, ClientConnection selfClientConnection, List<IRCEventHandler> handlers) throws Throwable {
		selfClientConnection.recievePong();
	}
}
