package org.ukiuni.irc4j.server.command;

import java.util.List;

import org.ukiuni.irc4j.IRCEventHandler;
import org.ukiuni.irc4j.User;
import org.ukiuni.irc4j.server.ClientConnection;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.server.ServerCommand;

public class ReceivePassCommand extends ServerCommand {

	@Override
	public void execute(IRCServer ircServer, ClientConnection selfClientConnection, List<IRCEventHandler> handlers) throws Throwable {
		if (0 == getCommandParameters().length) {
			return;
		}
		String password = getCommandParameters()[0];
		selfClientConnection.getUser().setPasswordHashed(User.toHash(password));
	}
}
