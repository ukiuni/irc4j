package org.ukiuni.irc4j.server.command;

import java.util.List;

import org.ukiuni.irc4j.IRCEventHandler;
import org.ukiuni.irc4j.User;
import org.ukiuni.irc4j.server.ClientConnection;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.server.ServerChannel;
import org.ukiuni.irc4j.server.ServerCommand;

public class ReceiveListCommand extends ServerCommand {

	@Override
	public void execute(IRCServer ircServer, ClientConnection selfClientConnection, List<IRCEventHandler> handlers) throws Throwable {
		selfClientConnection.sendCommand("321 Channel :Users Name");
		List<ServerChannel> channelList = ircServer.getChannelList();
		for (ServerChannel serverChannel : channelList) {
			String topic = null != serverChannel.getTopic() ? serverChannel.getTopic() : "";
			List<User> users = serverChannel.getCurrentUserList();
			selfClientConnection.sendCommand("322 " + selfClientConnection.getNickName() + " " + serverChannel.getName() + " " + users.size() + " :" + topic);
		}
		selfClientConnection.sendCommand("323 :End of /LIST");
	}
}
