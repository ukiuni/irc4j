package org.ukiuni.irc4j.server.command;

import java.util.List;

import org.ukiuni.irc4j.IRCEventHandler;
import org.ukiuni.irc4j.entity.Message;
import org.ukiuni.irc4j.server.ClientConnection;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.server.ServerChannel;
import org.ukiuni.irc4j.server.ServerCommand;

public class ReceiveHistoryCommand extends ServerCommand {

	@SuppressWarnings("deprecation")
	@Override
	public void execute(IRCServer ircServer, ClientConnection selfClientConnection, List<IRCEventHandler> handlers) throws Throwable {
		if (1 > getCommandParameters().length) {
			selfClientConnection.sendPrivateCommand("channel and history length must be specified");
			return;
		}
		ServerChannel channel;
		try {
			channel = loadJoindChannel(ircServer, selfClientConnection, getCommandParameters()[0]);
		} catch (ChannelException e) {
			selfClientConnection.sendPrivateCommand(e.getMessage());
			return;
		}
		int length = 10;
		try {
			length = Integer.valueOf(getCommandParameters()[1]);
		} catch (Exception e) {
		}
		List<Message> messages = channel.getHistory(length);
		for (Message message : messages) {
			selfClientConnection.sendPrivateCommand(message.getCreatedAt().getHours() + ":" + message.getCreatedAt().getMinutes() + " " + message.getSenderNickName() + ": " + message.getMessage());
		}
	}
}
