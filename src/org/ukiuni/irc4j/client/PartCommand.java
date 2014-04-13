package org.ukiuni.irc4j.client;

import java.util.List;

import org.ukiuni.irc4j.IRCClient;
import org.ukiuni.irc4j.IRCEventHandler;

public class PartCommand extends ClientCommand {

	@Override
	public void execute(IRCClient ircClient, List<IRCEventHandler> handlers) throws Throwable {
		String channelName = getCommandParameters()[0];
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
		String message = getCommandParametersString().substring(channelName.length() + 1 + 1);// space
																								// and
																								// :
		for (IRCEventHandler ircEventHandler : handlers) {
			ircEventHandler.onPartFromChannel(channelName, from, message);
		}
	}

}
