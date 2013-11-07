package org.ukiuni.irc4j.server.command;

import java.util.List;

import org.ukiuni.irc4j.Conf;
import org.ukiuni.irc4j.IRCEventHandler;
import org.ukiuni.irc4j.User;
import org.ukiuni.irc4j.server.ClientConnection;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.server.ServerChannel;
import org.ukiuni.irc4j.server.ServerCommand;

public class ReceiveWhoisCommand extends ServerCommand {

	@Override
	public void execute(IRCServer ircServer, ClientConnection selfClientConnection, List<IRCEventHandler> handlers) throws Throwable {
		if (getCommandParameters().length < 1) {
			selfClientConnection.sendPrivateCommand("WHOIS command specify nickname");
			return;
		}
		String nickName = getCommandParameters()[0];
		ClientConnection target = ircServer.findConnection(nickName);
		if (null == target) {
			selfClientConnection.sendPrivateCommand("No such user " + getCommandParameters()[0]);
			return;
		}
		User user = selfClientConnection.getUser();
		selfClientConnection.sendCommand("311 " + target.getNickName() + " " + user.getName() + " " + user.getHostName() + " * :" + user.getRealName());
		List<ServerChannel> channelList = ircServer.getChannelList();
		StringBuilder channelBuilder = new StringBuilder();
		for (ServerChannel serverChannel : channelList) {
			channelBuilder.append(serverChannel.getName() + " ");
		}
		selfClientConnection.sendCommand("319 " + target.getNickName() + " " + user.getName() + " :" + channelBuilder.substring(0, channelBuilder.length()));
		selfClientConnection.sendCommand("312 " + target.getNickName() + " " + user.getName() + " " + Conf.getHttpServerURL());
		selfClientConnection.sendCommand("318 :End of /WHOIS");
	}
}
