package org.irc4j.client;

import java.util.List;

import org.irc4j.Command;
import org.irc4j.IRCClient;
import org.irc4j.IRCEventHandler;

public abstract class ClientCommand extends Command {
	public abstract void execute(IRCClient ircClient, List<IRCEventHandler> handlers) throws Throwable;
}
