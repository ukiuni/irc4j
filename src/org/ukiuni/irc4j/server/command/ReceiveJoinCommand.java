package org.ukiuni.irc4j.server.command;

import java.util.List;

import org.ukiuni.irc4j.IRCEventHandler;
import org.ukiuni.irc4j.server.ClientConnection;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.server.ServerCommand;

public class ReceiveJoinCommand extends ServerCommand {

	@Override
	public void execute(IRCServer ircServer, ClientConnection selfClientConnection, List<IRCEventHandler> handlers) throws Throwable {
		String[] channelNames = getCommandParameters()[0].split(",", -1);
		String[] channelPasswords = new String[channelNames.length];
		if (getCommandParameters().length >= 2 && !":".equals(getCommandParameters()[1])) {
			String[] inputPasswords = getCommandParameters()[1].split(",", -1);
			for (int i = 0; i < inputPasswords.length; i++) {
				channelPasswords[i] = inputPasswords[i];
			}
		}
		for (int i = 0; i < channelNames.length; i++) {
			ircServer.joinToChannel(selfClientConnection, channelNames[i], channelPasswords[i]);
		}
	}
}
