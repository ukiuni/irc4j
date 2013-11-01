package org.ukiuni.irc4j.server.command;

import java.util.List;

import org.ukiuni.irc4j.IRCEventHandler;
import org.ukiuni.irc4j.server.ClientConnection;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.server.ServerChannel;
import org.ukiuni.irc4j.server.ServerCommand;

public class ReceiveQuitCommand extends ServerCommand {

	@Override
	public void execute(IRCServer ircServer, ClientConnection selfClientConnection, List<IRCEventHandler> handlers) throws Throwable {
		for (ServerChannel serverChannel : selfClientConnection.getJoinedChannels()) {
			try {
				selfClientConnection.partFromChannel(serverChannel.getName());
				serverChannel.part(selfClientConnection);
			} catch (Throwable e) {
			}
		}
		ircServer.removeConnection(selfClientConnection.getNickName());
		String quitMessage = getCommandParameters().length > 0 ? getCommandParameters()[1] : "";
		selfClientConnection.sendQuit(quitMessage);
		selfClientConnection.close();
	}
}
