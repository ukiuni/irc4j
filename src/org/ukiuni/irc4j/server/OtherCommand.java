package org.ukiuni.irc4j.server;

import java.util.List;

import org.ukiuni.irc4j.IRCEventHandler;

public class OtherCommand extends ServerCommand {

	@Override
	public void execute(IRCServer ircServer, ClientConnection selfClientConnection, List<IRCEventHandler> handlers) throws Throwable {
		selfClientConnection.sendNotice("No such command " + getCommand());
	}
}
