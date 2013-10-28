package org.irc4j.server;

import java.util.List;

import org.irc4j.Command;
import org.irc4j.IRCEventHandler;

public abstract class ServerCommand extends Command {
	public abstract void execute(IRCServer ircServer, ClientConnection selfClientConnection, List<IRCEventHandler> handlers) throws Throwable;

}
