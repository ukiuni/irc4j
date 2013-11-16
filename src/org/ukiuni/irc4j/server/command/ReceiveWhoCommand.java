package org.ukiuni.irc4j.server.command;

import java.util.List;

import org.ukiuni.irc4j.Channel;
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
			con.sendPrivateCommand("WHO must specify name");
			return;
		}
		String targetName = getCommandParameters()[0];
		if (targetName.startsWith("#")) {
			ServerChannel channel = ircServer.getChannel(targetName);
			if (null == channel) {
				con.sendPrivateCommand("No such channel " + targetName);
				return;
			}
			for (User user : channel.getCurrentUserList()) {
				con.sendCommand("352 " + channel.getName() + " " + user.getName() + " " + user.getHostName() + " " + ircServer.getServerName() + " " + user.getNickName() + " H :0 " + (null != user.getDescription() ? user.getDescription() : ""));
			}
			con.send("315 " + targetName + " :End of /WHO list.");
		} else {
			ClientConnection targetConnection = ircServer.findConnection(targetName);
			if (null == targetConnection) {
				con.sendPrivateCommand("No such user " + targetName);
				return;
			}
			StringBuilder channelNames = new StringBuilder();
			for (Channel channel : targetConnection.getJoinedChannels()) {
				channelNames.append(channel.getName() + ",");
			}
			User user = targetConnection.getUser();
			con.sendCommand("352 " + channelNames.substring(0, channelNames.length() - 1) + " " + user.getName() + " " + user.getHostName() + " " + ircServer.getServerName() + " " + user.getNickName() + " H :0 " + (null != user.getDescription() ? user.getDescription() : ""));
			con.send("315 " + targetName + " :End of /WHO list.");
		}
	}
}
