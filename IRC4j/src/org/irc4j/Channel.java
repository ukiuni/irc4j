package org.irc4j;

import java.io.IOException;
import java.util.List;

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
 * Channel of server. You have to join Channel before you send message. You can
 * join to channel to use IRCClient.join();
 * 
 * @author ukiuni
 */
public class Channel {
	private IRCClient ircClient;
	private String name;
	private List<User> userList;

	protected Channel(IRCClient ircClient, String name) {
		this.ircClient = ircClient;
		this.name = name;
	}

	public void join() throws IOException {
		ircClient.sendJoin(name);
	}

	public void sendMessage(String message) throws IOException {
		ircClient.sendMessage(name, message);
	}

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}
}
