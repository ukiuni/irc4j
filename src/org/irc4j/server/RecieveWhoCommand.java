package org.irc4j.server;

import java.util.List;

import org.irc4j.IRCEventHandler;
import org.irc4j.User;

public class RecieveWhoCommand extends ServerCommand {

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
		for (User user : channel.getUserList()) {
			con.sendCommand("352 " + con.getNickName() + " " + channel.getName() + " " + user.getName() + " " + user.getHostName() + " " + ircServer.getServerName() + " " + user.getNickName() + " H :0 " + (null != user.getDescription() ? user.getDescription() : ""));
		}
		con.send("315 " + con.getNickName() + " " + channelName + " :End of /WHO list.");
	}

}
