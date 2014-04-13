package org.ukiuni.irc4j.client;

import java.util.List;

import org.ukiuni.irc4j.IRCClient;
import org.ukiuni.irc4j.IRCEventHandler;

public class JoinCommand extends ClientCommand {

	@Override
	public void execute(IRCClient ircClient, List<IRCEventHandler> handlers) throws Throwable {
		String channelName = getCommandParameters()[0];
		if (channelName.startsWith(":")) {
			channelName = channelName.substring(1);
		}
		String from = getPrefix();
		if (null != from) {
			if (from.startsWith(":")) {
				from = from.substring(1);
			}
			int excrametionIndex = from.indexOf("!");
			if (0 < excrametionIndex) {
				from = from.substring(0, excrametionIndex);
			}
		}

		for (IRCEventHandler ircEventHandler : handlers) {
			ircEventHandler.onJoinToChannel(channelName, from);
		}
	}

}
