package org.ukiuni.irc4j.server.plugin;

import org.ukiuni.irc4j.server.ClientConnection;
import org.ukiuni.irc4j.server.IRCServer;

public interface CommandPlugin {
	public void execute(IRCServer ircServer, ClientConnection selfClientConnection, String[] params) throws Throwable;

	public String getCommand();
}
