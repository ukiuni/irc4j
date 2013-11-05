package org.ukiuni.irc4j.server;

import java.util.List;

import org.ukiuni.irc4j.Command;
import org.ukiuni.irc4j.IRCEventHandler;

public abstract class ServerCommand extends Command {
	public abstract void execute(IRCServer ircServer, ClientConnection selfClientConnection, List<IRCEventHandler> handlers) throws Throwable;

	public ServerChannel loadJoindChannel(IRCServer ircServer, ClientConnection connection, String channelName) throws ChannelException {
		String targetChannel = channelName;
		ServerChannel channel = ircServer.getChannel(targetChannel);
		if (channel == null) {
			throw new ChannelException("No such channel " + targetChannel);
		} else if (!channel.joins(connection)) {
			throw new ChannelException("You are not joined to " + targetChannel);
		}
		return channel;
	}

	protected class ChannelException extends Exception {
		private ChannelException(String message) {
			super(message);
		}
	}
}
