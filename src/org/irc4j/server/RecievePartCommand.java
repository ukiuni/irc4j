package org.irc4j.server;

import java.util.List;

import org.irc4j.IRCEventHandler;

public class RecievePartCommand extends ServerCommand {

	@Override
	public void execute(IRCServer ircServer, ClientConnection selfClientConnection, List<IRCEventHandler> handlers) throws Throwable {
		String[] channels = getCommandParameters()[0].split(",");
		for (String channelName : channels) {
			ServerChannel channel = selfClientConnection.getJoinedChannels().get(channelName);
			if (channelName == null) {
				selfClientConnection.sendCommand("You're not a member of the channel " + channelName + ", so you can't part it.");
			} else {
				channel.sendPartCommand(selfClientConnection.getUser().getFQUN(), channelName);
				channel.getUserList().remove(selfClientConnection.getUser());
				selfClientConnection.partFromChannel(channelName);
			}
		}
	}
}
