package org.ukiuni.irc4j;

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
 * Command recieve from server. You don't use this class ordinary.
 * 
 * @author ukiuni
 */
public abstract class Command {
	public static final String COMMAND_JOIN = "JOIN";
	public static final String COMMAND_NOTICE = "NOTICE";
	public static final String COMMAND_PRIVMSG = "PRIVMSG";
	public static final String COMMAND_PING = "PING";
	public static final String COMMAND_PONG = "PONG";
	public static final String COMMAND_NICK = "NICK";
	public static final String COMMAND_USER = "USER";
	public static final String COMMAND_QUIT = "QUIT";
	public static final String COMMAND_ERROR = "ERROR";
	public static final String COMMAND_PASS = "PASS";
	public static final String COMMAND_SERVER = "SERVER";
	public static final String COMMAND_OPER = "OPER";
	public static final String COMMAND_PART = "PART";
	public static final String COMMAND_WHO = "WHO";
	public static final String COMMAND_USERHOST = "USERHOST";
	public static final String COMMAND_MODE = "MODE";
	public static final String COMMAND_TOPIC = "TOPIC";
	public static final String COMMAND_HISTORY = "HISTORY";
	public static final String COMMAND_WEBLOG = "WLOG";
	public static final String COMMAND_INVITE = "INVITE";
	public static final String COMMAND_LIST = "LIST";
	public static final String COMMAND_WHOIS = "WHOIS";
	private String line;
	private String prefix;
	private String command;
	private String commandParametersString;

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String[] getCommandParameters() {
		if (null == commandParametersString) {
			return null;
		}
		return commandParametersString.split(" ");
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public String getCommandParametersString() {
		return commandParametersString;
	}

	public void setCommandParametersString(String commandParametersString) {
		this.commandParametersString = commandParametersString;
	}
}
