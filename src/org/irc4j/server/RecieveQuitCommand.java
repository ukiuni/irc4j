package org.irc4j.server;

import java.util.List;

import org.irc4j.IRCEventHandler;

public class RecieveQuitCommand extends ServerCommand {

	@Override
	public void execute(IRCServer ircServer, ClientConnection selfClientConnection, List<IRCEventHandler> handlers) throws Throwable {
		for (ServerChannel serverChannel : selfClientConnection.getJoinedChannels().values()) {
			try {
				selfClientConnection.partFromChannel(serverChannel.getName());
				serverChannel.part(selfClientConnection);
			} catch (Throwable e) {
			}
		}
		ircServer.removeConnection(selfClientConnection.getNickName());

		selfClientConnection.close();
	}

}
