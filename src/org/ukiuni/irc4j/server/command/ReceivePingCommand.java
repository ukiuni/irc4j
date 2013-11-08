package org.ukiuni.irc4j.server.command;

import java.util.List;

import org.ukiuni.irc4j.IRCEventHandler;
import org.ukiuni.irc4j.server.ClientConnection;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.server.ServerCommand;

public class ReceivePingCommand extends ServerCommand {

	@Override
	public void execute(IRCServer ircServer, ClientConnection selfClientConnection, List<IRCEventHandler> handlers) throws Throwable {
		selfClientConnection.sendPong(getCommandParameters()[0]);
		//caz xchat never send pong.
		selfClientConnection.recievePong();
	}
}
