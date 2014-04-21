package org.ukiuni.irc4j.client;

import java.util.List;

import org.ukiuni.irc4j.IRCClient;
import org.ukiuni.irc4j.IRCEventHandler;

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
public class MessageCommand extends ClientCommand {

	public MessageCommand() {
	}

	@Override
	public void execute(IRCClient ircClient, List<IRCEventHandler> handlers) throws Throwable {
		String channelName = getCommandParameters()[0];
		String from = getPrefix();
		if (null != from) {
			if (from.startsWith(":")) {
				from = from.substring(1);
			}
			int excrametionIndex = from.indexOf("!");
			if (0 < excrametionIndex) {
				from = from.substring(0, excrametionIndex);
			}
		}
		String message = getCommandParametersString().substring(channelName.length() + 1 + 1);// space
																								// and//
																								// :
		DCCMessage dcc = parseDCC(message);
		if (null != dcc) {
			for (IRCEventHandler ircEventHandler : handlers) {
				ircEventHandler.onDCC(channelName, from, dcc);
			}
		} else {
			for (IRCEventHandler ircEventHandler : handlers) {
				ircEventHandler.onMessage(channelName, from, message);
			}
		}
	}

	private DCCMessage parseDCC(String message) {
		message = message.replace(new String(new char[] { new Character((char) 1).charValue() }), "");
		if (message.startsWith("DCC")) {
			String[] spritedData = message.split(" ");
			if (6 == spritedData.length) {
				String fileName = spritedData[2];
				int portNum = Integer.valueOf(spritedData[4]);
				long fileSize = Long.valueOf(spritedData[5]);
				String targetHost = getLine().substring(getLine().indexOf("@")+1,getLine().indexOf(" "));
				return new DCCMessage(targetHost, fileName, portNum, fileSize);
			}
		}
		return null;
	}

}
