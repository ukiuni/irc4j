package org.ukiuni.irc4j.server;

import org.ukiuni.irc4j.Command;
import org.ukiuni.irc4j.CommandFactory;
import org.ukiuni.irc4j.server.command.ReceiveFileUploadCommand;
import org.ukiuni.irc4j.server.command.ReceiveHistoryCommand;
import org.ukiuni.irc4j.server.command.ReceiveInviteCommand;
import org.ukiuni.irc4j.server.command.ReceiveJoinCommand;
import org.ukiuni.irc4j.server.command.ReceiveListCommand;
import org.ukiuni.irc4j.server.command.ReceiveModeCommand;
import org.ukiuni.irc4j.server.command.ReceiveNickCommand;
import org.ukiuni.irc4j.server.command.ReceiveNoticeCommand;
import org.ukiuni.irc4j.server.command.ReceivePartCommand;
import org.ukiuni.irc4j.server.command.ReceivePassCommand;
import org.ukiuni.irc4j.server.command.ReceivePingCommand;
import org.ukiuni.irc4j.server.command.ReceivePongCommand;
import org.ukiuni.irc4j.server.command.ReceivePrivmsgCommand;
import org.ukiuni.irc4j.server.command.ReceiveQuitCommand;
import org.ukiuni.irc4j.server.command.ReceiveReloadPluginCommand;
import org.ukiuni.irc4j.server.command.ReceiveTopicCommand;
import org.ukiuni.irc4j.server.command.ReceiveUserCommand;
import org.ukiuni.irc4j.server.command.ReceiveUserHostCommand;
import org.ukiuni.irc4j.server.command.ReceiveWeblogCommand;
import org.ukiuni.irc4j.server.command.ReceiveWhoCommand;
import org.ukiuni.irc4j.server.command.ReceiveWhoisCommand;
import org.ukiuni.irc4j.server.plugin.PluginFactory;

public class ServerCommandFactory extends CommandFactory<ServerCommand> {
	public ServerCommand createCommandInstance(String commandString) {
		ServerCommand command = null;
		if (Command.COMMAND_PRIVMSG.equals(commandString)) {
			command = new ReceivePrivmsgCommand();
		} else if (Command.COMMAND_NICK.equals(commandString)) {
			command = new ReceiveNickCommand();
		} else if (Command.COMMAND_USER.equals(commandString)) {
			command = new ReceiveUserCommand();
		} else if (Command.COMMAND_PING.equals(commandString)) {
			command = new ReceivePingCommand();
		} else if (Command.COMMAND_JOIN.equals(commandString)) {
			command = new ReceiveJoinCommand();
		} else if (Command.COMMAND_PING.equals(commandString)) {
			command = new ReceivePingCommand();
		} else if (Command.COMMAND_PONG.equals(commandString)) {
			command = new ReceivePongCommand();
		} else if (Command.COMMAND_WHO.equals(commandString)) {
			command = new ReceiveWhoCommand();
		} else if (Command.COMMAND_PRIVMSG.equals(commandString)) {
			command = new ReceivePrivmsgCommand();
		} else if (Command.COMMAND_USERHOST.equals(commandString)) {
			command = new ReceiveUserHostCommand();
		} else if (Command.COMMAND_INVITE.equals(commandString)) {
			command = new ReceiveInviteCommand();
		} else if (Command.COMMAND_MODE.equals(commandString)) {
			command = new ReceiveModeCommand();
		} else if (Command.COMMAND_PART.equals(commandString)) {
			command = new ReceivePartCommand();
		} else if (Command.COMMAND_QUIT.equals(commandString)) {
			command = new ReceiveQuitCommand();
		} else if (Command.COMMAND_TOPIC.equals(commandString)) {
			command = new ReceiveTopicCommand();
		} else if (Command.COMMAND_NOTICE.equals(commandString)) {
			command = new ReceiveNoticeCommand();
		} else if (Command.COMMAND_HISTORY.equals(commandString)) {
			command = new ReceiveHistoryCommand();
		} else if (Command.COMMAND_LIST.equals(commandString)) {
			command = new ReceiveListCommand();
		} else if (Command.COMMAND_PASS.equals(commandString)) {
			command = new ReceivePassCommand();
		} else if (Command.COMMAND_WHOIS.equals(commandString)) {
			command = new ReceiveWhoisCommand();
		} else if (Command.COMMAND_WEBLOG.equals(commandString)) {
			command = new ReceiveWeblogCommand();
		} else if (Command.COMMAND_FILEUPLOAD.equals(commandString)) {
			command = new ReceiveFileUploadCommand();
		} else if (Command.COMMAND_RELOADPLUGIN.equals(commandString)) {
			command = new ReceiveReloadPluginCommand();
		} else if (commandString.matches("^[4][0-9]{2}$")) {
			// command = new ServerErrorCommand();
		} else if (commandString.matches("^[23][0-9]{2}$")) {
			// command = new ServerCommand(Integer.parseInt(commandString));
		} else if (PluginFactory.getInstance().containsCommand(commandString)) {
			command = PluginFactory.getInstance().loadCommand(commandString);
		}
		if (null == command) {
			command = new OtherCommand();
		}
		return command;
	}
}
