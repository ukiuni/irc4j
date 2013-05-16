package org.irc4j;

import org.irc4j.commandimpl.MessageCommand;
import org.irc4j.commandimpl.OtherCommand;
import org.irc4j.commandimpl.RequestPingCommand;
import org.irc4j.commandimpl.ServerCommand;
import org.irc4j.commandimpl.ServerErrorCommand;

/*
 * Copyright [2013] [ukiuni]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
/**
 * You don't use this class. This class knows how to handle server response.
 * If you want to add implementation of new server response handler,
 * create new Command class and add line into CommandFactory.loadCommand();
 * @author ukiuni
 */
public class CommandFactory {

	protected static Command loadCommand(String line) {
		int lineCursor = 0;
		String prefix = null;
		String commandString = null;
		String commandParametersString = null;

		if (line.startsWith(":")) {
			lineCursor = line.indexOf(" ");
			prefix = line.substring(0, lineCursor);
			lineCursor++;
		}
		int commandEndPosition = line.indexOf(" ", lineCursor);
		if (0 > commandEndPosition) {
			commandEndPosition = line.length();
			commandString = line.substring(lineCursor, commandEndPosition);
		} else {
			commandString = line.substring(lineCursor, commandEndPosition);
			lineCursor = commandEndPosition + 1;
			commandParametersString = line.substring(lineCursor);
		}
		Command command = null;
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
			command = new OtherCommand(line);
		}
		command.setLine(line);
		command.setPrefix(prefix);
		command.setCommand(commandString);
		command.setCommandParametersString(commandParametersString);
		return command;
	}
}
