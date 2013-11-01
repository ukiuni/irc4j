package org.ukiuni.irc4j.server.worker;

import org.ukiuni.irc4j.server.IRCServer;

public interface Worker {
	public void work(IRCServer ircServer);

	public void stop();
}
