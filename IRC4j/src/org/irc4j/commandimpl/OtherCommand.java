package org.irc4j.commandimpl;

import java.util.List;

import org.irc4j.Command;
import org.irc4j.IRCClient;
import org.irc4j.IRCEventHandler;

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
 * This class is work with server response that is NOT implements in irc4j.
 * @author ukiuni
 *
 */
public class OtherCommand extends Command {
	private String message;

	public OtherCommand(String message) {
		this.message = message;
	}

	@Override
	public void execute(IRCClient ircClient, List<IRCEventHandler> handlers) throws Throwable {
		//System.out.println("otherCommand(" + message + ")");
	}

}
