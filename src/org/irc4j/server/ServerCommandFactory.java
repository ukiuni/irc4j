package org.irc4j.server;

import org.irc4j.Command;
import org.irc4j.CommandFactory;

public class ServerCommandFactory extends CommandFactory<ServerCommand> {
	public ServerCommand createCommandInstance(String commandString) {
		ServerCommand command = null;
		if (Command.COMMAND_NICK.equals(commandString)) {
			command = new RecieveNickCommand();
		} else if (Command.COMMAND_USER.equals(commandString)) {
			command = new RecieveUserCommand();
		} else if (Command.COMMAND_PING.equals(commandString)) {
			command = new RecievePingCommand();
		} else if (Command.COMMAND_JOIN.equals(commandString)) {
			command = new RecieveJoinCommand();
		} else if (Command.COMMAND_PING.equals(commandString)) {
			command = new RecievePingCommand();
		} else if (Command.COMMAND_PONG.equals(commandString)) {
			command = new RecievePongCommand();
		} else if (Command.COMMAND_WHO.equals(commandString)) {
			command = new RecieveWhoCommand();
		} else if (Command.COMMAND_USERHOST.equals(commandString)) {
			command = new RecieveUserHostCommand();
		} else if (Command.COMMAND_MODE.equals(commandString)) {
			command = new RecieveModeCommand();
		} else if (Command.COMMAND_PART.equals(commandString)) {
			command = new RecievePartCommand();
		} else if (Command.COMMAND_QUIT.equals(commandString)) {
			command = new RecieveQuitCommand();
		} else if (Command.COMMAND_PRIVMSG.equals(commandString)) {
			command = new RecievePrivmsgCommand();
		} else if (Command.COMMAND_TOPIC.equals(commandString)) {
			command = new RecieveTopicCommand();
		} else if (Command.COMMAND_NOTICE.equals(commandString)) {
			command = new RecieveNoticeCommand();
		} else if (Command.COMMAND_HISTORY.equals(commandString)) {
			command = new ReceiveHistoryCommand();
		} else if (commandString.matches("^[4][0-9]{2}$")) {
			// command = new ServerErrorCommand();
		} else if (commandString.matches("^[23][0-9]{2}$")) {
			// command = new ServerCommand(Integer.parseInt(commandString));
		}
		if (null == command) {
			command = new OtherCommand();
		}
		return command;
	}
}
