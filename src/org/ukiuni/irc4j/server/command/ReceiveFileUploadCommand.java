package org.ukiuni.irc4j.server.command;

import java.util.List;

import org.ukiuni.irc4j.IRCEventHandler;
import org.ukiuni.irc4j.server.ClientConnection;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.server.ServerChannel;
import org.ukiuni.irc4j.server.ServerCommand;

public class ReceiveFileUploadCommand extends ServerCommand {

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
		if (null != selfClientConnection.getCurrentFileUploadChannel()) {
			selfClientConnection.sendPartCommand(ircServer.getFQSN(), selfClientConnection.getCurrentFileUploadChannel().getName());
		}
		selfClientConnection.setCurrentFileUploadChannel(channel);
		selfClientConnection.sendJoin(ircServer.getFQSN(), channel.getName());
		selfClientConnection.sendPrivateCommand("send file with DCC to " + ircServer.getServerName());
	}
}
