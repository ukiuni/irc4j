package org.ukiuni.irc4j.server.command;

import java.util.List;

import org.ukiuni.irc4j.IRCEventHandler;
import org.ukiuni.irc4j.db.Database;
import org.ukiuni.irc4j.server.ClientConnection;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.server.ServerCommand;
import org.ukiuni.irc4j.server.plugin.Plugin;

public class ReceiveHelpCommand extends ServerCommand {

	@Override
	public void execute(IRCServer ircServer, ClientConnection client, List<IRCEventHandler> handlers) throws Throwable {
		client.sendPrivateCommand(ircServer.getServerName() + " ----------");
		client.sendPrivateCommand("/FILEUPLOAD");
		client.sendPrivateCommand("    FILEUPLOAD {channelName}");
		client.sendPrivateCommand("    upload file to channel.");
		client.sendPrivateCommand("    After send command, You can find AIR_IRC user on specify channel.");
		client.sendPrivateCommand("    You send file via DDC to AIR_IRC, file to be uploaded.");
		client.sendPrivateCommand("");
		client.sendPrivateCommand("/HISTORY");
		client.sendPrivateCommand("    HISTORY {channelName} [count]");
		client.sendPrivateCommand("    It\'s Send you history of specify channel. [count] is amount of log.");
		client.sendPrivateCommand("");
		client.sendPrivateCommand("/WLOG");
		client.sendPrivateCommand("    WLOG {channelName} [count]");
		client.sendPrivateCommand("    You get URL to show log of specify channel. [count] is amount of log.");
		client.sendPrivateCommand("");
		List<Plugin> plugins = Database.getInstance().loadMovingPlugin();
		for (Plugin plugin : plugins) {
			client.sendPrivateCommand("/" + plugin.getCommand());
			client.sendPrivateCommand("    " + plugin.getName());
			client.sendPrivateCommand("    " + plugin.getDescription().replace("\n", "\n    "));
			client.sendPrivateCommand("");
		}
		client.sendPrivateCommand("---------------");
	}
}
