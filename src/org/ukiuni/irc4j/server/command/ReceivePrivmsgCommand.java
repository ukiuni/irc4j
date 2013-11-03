package org.ukiuni.irc4j.server.command;

import java.util.List;

import org.ukiuni.irc4j.IRCEventHandler;
import org.ukiuni.irc4j.server.ClientConnection;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.server.ServerChannel;
import org.ukiuni.irc4j.server.ServerCommand;

public class ReceivePrivmsgCommand extends ServerCommand {

	@Override
	public void execute(IRCServer ircServer, ClientConnection selfClientConnection, List<IRCEventHandler> handlers) throws Throwable {
		String[] targets = getCommandParameters()[0].split(",");
		String message = getCommandParametersString().split(" ", 2)[1];
		if (message.startsWith(":")) {
			message = message.substring(1);
		}
		for (String target : targets) {
			if (target.startsWith("#")) {
				ServerChannel channel = ircServer.getChannel(target);
				if (channel == null) {
					selfClientConnection.sendPrivateCommand("No such channel " + target);
				} else if (!channel.joins(selfClientConnection)) {
					selfClientConnection.sendPrivateCommand("You are not joined to " + target);
				} else {
					channel.sendMessage(getCommandString(), selfClientConnection.getUser().getFQUN(), message, selfClientConnection);
				}
			} else {
				ClientConnection clientConnection = ircServer.findConnection(target);
				if (clientConnection == null) {
					selfClientConnection.sendPrivateCommand("The user " + target + " is not online.");
				} else {
					clientConnection.sendPrivateMessage(getCommandString(), selfClientConnection.getUser().getFQUN(), target, message);
				}
			}
		}
	}

	protected String getCommandString() {
		return "PRIVMSG";
	}
}
