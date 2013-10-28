package org.irc4j.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class ServerCommand extends ClientCommand {
	public static final Map<String, String> idAndStringMap = new HashMap<String, String>();
	static {
		idAndStringMap.put("300", "RPL_NONE");
		idAndStringMap.put("302", "RPL_USERHOST");
		idAndStringMap.put("303", "RPL_ISON");
		idAndStringMap.put("301", "RPL_AWAY");
		idAndStringMap.put("305", "RPL_UNAWAY");
		idAndStringMap.put("306", "RPL_NOWAWAY");
		idAndStringMap.put("311", "RPL_WHOISUSER");
		idAndStringMap.put("312", "RPL_WHOISSERVER");
		idAndStringMap.put("313", "RPL_WHOISOPERATOR");
		idAndStringMap.put("317", "RPL_WHOISIDLE");
		idAndStringMap.put("318", "RPL_ENDOFWHOIS");
		idAndStringMap.put("319", "RPL_WHOISCHANNELS");
		idAndStringMap.put("314", "RPL_WHOWASUSER");
		idAndStringMap.put("369", "RPL_ENDOFWHOWAS");
		idAndStringMap.put("321", "RPL_LISTSTART");
		idAndStringMap.put("322", "RPL_LIST");
		idAndStringMap.put("323", "RPL_LISTEND");
		idAndStringMap.put("324", "RPL_CHANNELMODEIS");
		idAndStringMap.put("331", "RPL_NOTOPIC");
		idAndStringMap.put("332", "RPL_TOPIC");
		idAndStringMap.put("341", "RPL_INVITING");
		idAndStringMap.put("342", "RPL_SUMMONING");
		idAndStringMap.put("351", "RPL_VERSION");
		idAndStringMap.put("352", "RPL_WHOREPLY");
		idAndStringMap.put("315", "RPL_ENDOFWHO");
		idAndStringMap.put("353", "RPL_NAMREPLY");
		idAndStringMap.put("366", "RPL_ENDOFNAME");
		idAndStringMap.put("364", "RPL_LINKS");
		idAndStringMap.put("365", "RPL_ENDOFLINKS");
		idAndStringMap.put("367", "RPL_BANLIST");
		idAndStringMap.put("368", "RPL_ENDOFBANLIST");
		idAndStringMap.put("371", "RPL_INFO");
		idAndStringMap.put("374", "RPL_ENDOFINFO");
		idAndStringMap.put("375", "RPL_MOTDSTART");
		idAndStringMap.put("372", "RPL_MOTD");
		idAndStringMap.put("376", "RPL_ENDOFMOTD");
		idAndStringMap.put("382", "RPL_REHASHING");
		idAndStringMap.put("391", "RPL_TIME");
		idAndStringMap.put("393", "RPL_USERS");
		idAndStringMap.put("394", "RPL_ENDOFUSERS");
		idAndStringMap.put("395", "RPL_NOUSERS");
		idAndStringMap.put("200", "RPL_TRACELINK");
		idAndStringMap.put("201", "RPL_TRACECONNECTING");
		idAndStringMap.put("202", "RPL_TRACEHANDSHAKE");
		idAndStringMap.put("203", "RPL_TRACEUNKNOWN");
		idAndStringMap.put("204", "RPL_TRACEOPERATOR");
		idAndStringMap.put("205", "RPL_TRACEUSER");
		idAndStringMap.put("206", "RPL_TRACESERVER");
		idAndStringMap.put("208", "RPL_TRACENEWTYPE");
		idAndStringMap.put("261", "RPL_TRACELOG");
		idAndStringMap.put("211", "RPL_STATSLINKINF");
		idAndStringMap.put("212", "RPL_STATSCOMMANDS");
		idAndStringMap.put("213", "RPL_STATSCLINE");
		idAndStringMap.put("214", "RPL_STATSNLINE");
		idAndStringMap.put("215", "RPL_STATSILINE");
		idAndStringMap.put("216", "RPL_STATSKLINE");
		idAndStringMap.put("218", "RPL_STATSYLINE");
		idAndStringMap.put("219", "RPL_ENDOFSTATS");
		idAndStringMap.put("241", "RPL_STATSLLINE");
		idAndStringMap.put("242", "RPL_STATSUPTIME");
		idAndStringMap.put("243", "RPL_STATSOLINE");
		idAndStringMap.put("244", "RPL_STATSHLINE");
		idAndStringMap.put("221", "RPL_UMODEIS");
		idAndStringMap.put("251", "RPL_LUSERCLIENT");
		idAndStringMap.put("252", "RPL_LUSEROP");
		idAndStringMap.put("253", "RPL_LUSERUNKNOWN");
		idAndStringMap.put("254", "RPL_LUSERCHANNELS");
		idAndStringMap.put("255", "RPL_LUSERME");
		idAndStringMap.put("256", "RPL_ADMINME");
		idAndStringMap.put("257", "RPL_ADMINLOC1");
		idAndStringMap.put("258", "RPL_ADMINLOC2");
		idAndStringMap.put("259", "RPL_ADMINEMAIL");
	}
	public static final int RPL_NONE = 300;
	public static final int RPL_USERHOST = 302;
	public static final int RPL_ISON = 303;
	public static final int RPL_AWAY = 301;
	public static final int RPL_UNAWAY = 305;
	public static final int RPL_NOWAWAY = 306;
	public static final int RPL_WHOISUSER = 311;
	public static final int RPL_WHOISSERVER = 312;
	public static final int RPL_WHOISOPERATOR = 313;
	public static final int RPL_WHOISIDLE = 317;
	public static final int RPL_ENDOFWHOIS = 318;
	public static final int RPL_WHOISCHANNELS = 319;
	public static final int RPL_WHOWASUSER = 314;
	public static final int RPL_ENDOFWHOWAS = 369;
	public static final int RPL_LISTSTART = 321;
	public static final int RPL_LIST = 322;
	public static final int RPL_LISTEND = 323;
	public static final int RPL_CHANNELMODEIS = 324;
	public static final int RPL_NOTOPIC = 331;
	public static final int RPL_TOPIC = 332;
	public static final int RPL_INVITING = 341;
	public static final int RPL_SUMMONING = 342;
	public static final int RPL_VERSION = 351;
	public static final int RPL_WHOREPLY = 352;
	public static final int RPL_ENDOFWHO = 315;
	public static final int RPL_NAMREPLY = 353;
	public static final int RPL_ENDOFNAME = 366;
	public static final int RPL_LINKS = 364;
	public static final int RPL_ENDOFLINKS = 365;
	public static final int RPL_BANLIST = 367;
	public static final int RPL_ENDOFBANLIST = 368;
	public static final int RPL_INFO = 371;
	public static final int RPL_ENDOFINFO = 374;
	public static final int RPL_MOTDSTART = 375;
	public static final int RPL_MOTD = 372;
	public static final int RPL_ENDOFMOTD = 376;
	public static final int RPL_REHASHING = 382;
	public static final int RPL_TIME = 391;
	public static final int RPL_USERS = 393;
	public static final int RPL_ENDOFUSERS = 394;
	public static final int RPL_NOUSERS = 395;
	public static final int RPL_TRACELINK = 200;
	public static final int RPL_TRACECONNECTING = 201;
	public static final int RPL_TRACEHANDSHAKE = 202;
	public static final int RPL_TRACEUNKNOWN = 203;
	public static final int RPL_TRACEOPERATOR = 204;
	public static final int RPL_TRACEUSER = 205;
	public static final int RPL_TRACESERVER = 206;
	public static final int RPL_TRACENEWTYPE = 208;
	public static final int RPL_TRACELOG = 261;
	public static final int RPL_STATSLINKINF = 211;
	public static final int RPL_STATSCOMMANDS = 212;
	public static final int RPL_STATSCLINE = 213;
	public static final int RPL_STATSNLINE = 214;
	public static final int RPL_STATSILINE = 215;
	public static final int RPL_STATSKLINE = 216;
	public static final int RPL_STATSYLINE = 218;
	public static final int RPL_ENDOFSTATS = 219;
	public static final int RPL_STATSLLINE = 241;
	public static final int RPL_STATSUPTIME = 242;
	public static final int RPL_STATSOLINE = 243;
	public static final int RPL_STATSHLINE = 244;
	public static final int RPL_UMODEIS = 221;
	public static final int RPL_LUSERCLIENT = 251;
	public static final int RPL_LUSEROP = 252;
	public static final int RPL_LUSERUNKNOWN = 253;
	public static final int RPL_LUSERCHANNELS = 254;
	public static final int RPL_LUSERME = 255;
	public static final int RPL_ADMINME = 256;
	public static final int RPL_ADMINLOC1 = 257;
	public static final int RPL_ADMINLOC2 = 258;
	public static final int RPL_ADMINEMAIL = 259;
	private int id;

	public ServerCommand(int id) {
		this.id = id;
	}

	@Override
	public void execute(IRCClient ircClient, List<IRCEventHandler> handlers) throws Throwable {
		for (IRCEventHandler ircEventHandler : handlers) {
			ircEventHandler.onServerMessage(this.id, getLine());
		}
	}

	public String getErrorString() {
		return idAndStringMap.get(getCommand());
	}
}
