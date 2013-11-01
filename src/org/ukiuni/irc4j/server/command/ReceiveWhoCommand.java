package org.ukiuni.irc4j.server.command;

import java.util.List;

import org.ukiuni.irc4j.IRCEventHandler;
import org.ukiuni.irc4j.User;
import org.ukiuni.irc4j.server.ClientConnection;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.server.ServerChannel;
import org.ukiuni.irc4j.server.ServerCommand;

public class ReceiveWhoCommand extends ServerCommand {

	@Override
	public void execute(IRCServer ircServer, ClientConnection con, List<IRCEventHandler> handlers) throws Throwable {
		if (1 != getCommandParameters().length) {
			con.sendPrivateCommand("WHO must specify channel name");
			return;
		}
		String channelName = getCommandParameters()[0];
		ServerChannel channel = ircServer.getChannel(channelName);
		if (null == channel) {
			con.sendPrivateCommand("No such channel " + channelName);
			return;
		}
		for (User user : channel.getCurrentUserList()) {
			con.sendCommand("352 " + con.getNickName() + " " + channel.getName() + " " + user.getName() + " " + user.getHostName() + " " + ircServer.getServerName() + " " + user.getNickName() + " H :0 " + (null != user.getDescription() ? user.getDescription() : ""));
		}
		con.send("315 " + con.getNickName() + " " + channelName + " :End of /WHO list.");
	}

}
