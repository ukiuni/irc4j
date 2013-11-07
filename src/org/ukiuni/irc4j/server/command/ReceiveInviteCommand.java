package org.ukiuni.irc4j.server.command;

import java.util.List;

import org.ukiuni.irc4j.IRCEventHandler;
import org.ukiuni.irc4j.server.ClientConnection;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.server.ServerChannel;
import org.ukiuni.irc4j.server.ServerCommand;

public class ReceiveInviteCommand extends ServerCommand {

	@Override
	public void execute(IRCServer ircServer, ClientConnection selfClientConnection, List<IRCEventHandler> handlers) throws Throwable {
		if (getCommandParameters().length < 2) {
			selfClientConnection.sendPrivateCommand("INVITE command specify nickname and channel ");
			return;
		}
		ClientConnection connection = ircServer.findConnection(getCommandParameters()[0]);
		if (null == connection) {
			selfClientConnection.sendPrivateCommand("No such user " + getCommandParameters()[0]);
			return;
		}
		ServerChannel channel = ircServer.getChannel(getCommandParameters()[1]);
		if (null == channel) {
			selfClientConnection.sendPrivateCommand("No such channel " + getCommandParameters()[1]);
			return;
		} else if (!channel.joins(selfClientConnection)) {
			selfClientConnection.sendPrivateCommand("You are not joined to " + getCommandParameters()[1]);
			return;
		}
		connection.sendInvite(selfClientConnection, channel);
	}
}
