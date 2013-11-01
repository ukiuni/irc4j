package org.ukiuni.irc4j.server.command;

import java.util.List;

import org.ukiuni.irc4j.IRCEventHandler;
import org.ukiuni.irc4j.server.ClientConnection;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.server.ServerChannel;
import org.ukiuni.irc4j.server.ServerCommand;

public class ReceiveTopicCommand extends ServerCommand {

	@Override
	public void execute(IRCServer ircServer, ClientConnection selfClientConnection, List<IRCEventHandler> handlers) throws Throwable {
		String channelName = getCommandParameters()[0];
		ServerChannel channel = ircServer.getChannel(channelName);
		if (channel == null) {
			selfClientConnection.sendPrivateCommand("No such channel " + channelName);
			return;
		}
		if (getCommandParameters().length == 1) {
			if (channel.getTopic() != null) {
				selfClientConnection.sendCommand("332 " + selfClientConnection.getNickName() + " " + channel.getName() + " :" + channel.getTopic());
			} else {
				selfClientConnection.sendCommand("331 " + selfClientConnection.getNickName() + " " + channel.getName() + " :No topic is set");
			}
		} else {
			String topic = getCommandParameters()[1];
			channel.setTopic(topic);
			channel.send(":" + selfClientConnection.getUser().getFQUN() + " TOPIC " + channel.getName() + " :" + channel.getTopic());
		}
	}
}
