package org.irc4j;

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
 * You don't use this class. This class knows how to handle server response. If
 * you want to add implementation of new server response handler, create new
 * Command class and add line into CommandFactory.loadCommand();
 * 
 * @author ukiuni
 */
public abstract class CommandFactory<T extends Command> {

	public Command loadCommand(String line) {
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
		T command = createCommandInstance(commandString);

		command.setLine(line);
		command.setPrefix(prefix);
		command.setCommand(commandString);
		command.setCommandParametersString(commandParametersString);
		return command;
	}

	protected abstract T createCommandInstance(String commandString);
}
