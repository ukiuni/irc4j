package org.irc4j.server.worker;

import org.irc4j.server.IRCServer;

public interface Worker {
	public void work(IRCServer ircServer);

	public void stop();
}
