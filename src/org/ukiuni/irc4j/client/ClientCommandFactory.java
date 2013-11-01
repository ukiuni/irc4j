package org.ukiuni.irc4j.client;

import org.ukiuni.irc4j.Command;
import org.ukiuni.irc4j.CommandFactory;

public class ClientCommandFactory extends CommandFactory<ClientCommand> {
	public ClientCommand createCommandInstance(String commandString) {
		ClientCommand command = null;
		if (Command.COMMAND_PING.equals(commandString)) {
			command = new RequestPingCommand();
		} else if (Command.COMMAND_ERROR.equals(commandString)) {
			command = new ServerErrorCommand();
		} else if (Command.COMMAND_PRIVMSG.equals(commandString)) {
			command = new MessageCommand();
		} else if (Command.COMMAND_NOTICE.equals(commandString)) {
			command = new MessageCommand();
		} else if (commandString.matches("^[4][0-9]{2}$")) {
			command = new ServerErrorCommand();
		} else if (commandString.matches("^[23][0-9]{2}$")) {
			command = new ServerCommand(Integer.parseInt(commandString));
		} else {
			command = new OtherCommand();
		}
		return command;
	}
}
