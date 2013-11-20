package org.ukiuni.irc4j.server.plugin;

import java.util.List;

import org.ukiuni.irc4j.IRCEventHandler;
import org.ukiuni.irc4j.server.ClientConnection;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.server.ServerCommand;

public class ScriptPluginExecuteServerCommand extends ServerCommand {
	private CommandPlugin commandPlugin;

	public ScriptPluginExecuteServerCommand(CommandPlugin commandPlugin) {
		this.commandPlugin = commandPlugin;
	}

	@Override
	public void execute(IRCServer ircServer, ClientConnection selfClientConnection, List<IRCEventHandler> handlers) throws Throwable {
		commandPlugin.execute(ircServer, selfClientConnection, getCommandParameters());
	}
}
