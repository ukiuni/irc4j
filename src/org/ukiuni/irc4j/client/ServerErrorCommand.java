package org.ukiuni.irc4j.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ukiuni.irc4j.IRCClient;
import org.ukiuni.irc4j.IRCEventHandler;
import org.ukiuni.irc4j.RecievedFromIRCServerException;

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
public class ServerErrorCommand extends ClientCommand {
	private static final Map<String, String> errorIdStringMap = new HashMap<String, String>();
	static {
		errorIdStringMap.put("401", "ERR_NOSUCHNICK");
		errorIdStringMap.put("402", "ERR_NOSUCHSERVE");
		errorIdStringMap.put("403", "ERR_NOSUCHCHANNEL");
		errorIdStringMap.put("404", "ERR_CANNOTSENDTOCHAN");
		errorIdStringMap.put("405", "ERR_TOOMANYCHANNELS");
		errorIdStringMap.put("406", "ERR_WASNOSUCHNICK");
		errorIdStringMap.put("407", "ERR_TOOMANYTARGETS");
		errorIdStringMap.put("409", "ERR_NOORIGIN");
		errorIdStringMap.put("412", "ERR_NOTEXTTOSEND");
		errorIdStringMap.put("413", "ERR_NOTOPLEVE");
		errorIdStringMap.put("414", "ERR_WILDTOPLEVEL");
		errorIdStringMap.put("422", "ERR_NOMOTD");
		errorIdStringMap.put("424", "ERR_FILEERROR");
		errorIdStringMap.put("431", "ERR_NONICKNAMEGIVEN");
		errorIdStringMap.put("432", "ERR_ERRONEUSNICKNAME");
		errorIdStringMap.put("433", "ERR_NICKNAMEINUSE");
		errorIdStringMap.put("436", "ERR_NICKCOLLISION");
		errorIdStringMap.put("441", "ERR_USERNOTINCHANNEL");
		errorIdStringMap.put("442", "ERR_NOTONCHANNE");
		errorIdStringMap.put("443", "ERR_USERONCHANNEL");
		errorIdStringMap.put("444", "ERR_NOLOGIN");
		errorIdStringMap.put("445", "ERR_SUMMONDISABLED");
		errorIdStringMap.put("461", "ERR_NEEDMOREPARAM");
		errorIdStringMap.put("462", "ERR_ALREADYREGISTRE");
		errorIdStringMap.put("463", "ERR_NOPERMFORHOST");
		errorIdStringMap.put("464", "ERR_PASSWDMISMATCH");
		errorIdStringMap.put("465", "ERR_YOUREBANNEDCREEP");
		errorIdStringMap.put("467", "ERR_KEYSET");
		errorIdStringMap.put("471", "ERR_CHANNELISFULL");
		errorIdStringMap.put("472", "ERR_UNKNOWNMODE");
		errorIdStringMap.put("473", "ERR_INVITEONLYCHAN");
		errorIdStringMap.put("474", "ERR_BANNEDFROMCHAN");
		errorIdStringMap.put("475", "ERR_BADCHANNELKEY");
		errorIdStringMap.put("481", "ERR_NOPRIVILEGES");
		errorIdStringMap.put("482", "ERR_CHANOPRIVSNEEDED");
		errorIdStringMap.put("483", "ERR_CANTKILLSERVER");
		errorIdStringMap.put("491", "ERR_NOOPERHOST");
		errorIdStringMap.put("501", "ERR_UMODEUNKNOWNFLAG");
		errorIdStringMap.put("502", "ERR_USERSDONTMATCH");
	}

	public ServerErrorCommand() {
	}

	@Override
	public void execute(IRCClient ircClient, List<IRCEventHandler> handlers) throws Throwable {
		RecievedFromIRCServerException e = new RecievedFromIRCServerException(getCommand(), getErrorString(), getLine());
		for (int i = 0; i < handlers.size(); i++) {
			try {
				IRCEventHandler ircEventHandler = handlers.get(i);
				ircEventHandler.onError(e);
			} catch (Exception e1) {
				// TODO how handle e1? its cause exception
			}
		}
	}

	private String getErrorString() {
		return errorIdStringMap.get(getCommand());
	}
}
