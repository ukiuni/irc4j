package org.irc4j;

import java.util.ArrayList;
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
	private String name;
	private String topic;
	private List<User> userList = new ArrayList<User>();

	public Channel(String name) {
		this.name = name;
	}

	public List<User> getUserList() {
		return userList;
	}

	public String getName() {
		return name;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public static boolean wrongName(String channelName) {
		return !(channelName.startsWith("#") && !channelName.contains(" "));
	}

	public void addUserAll(List<User> userList) {
		this.userList.addAll(userList);
	}

	/**
	 * This method call via child class, caz You have to do many with remove User.
	 * @param nickName
	 */
	protected void removeUser(String nickName) {
		List<User> removeUsers = new ArrayList<User>();// for duplicate bug. it
														// may not be better
														// than single value.
		for (User user : userList) {
			if (nickName.equals(user.getNickName())) {
				removeUsers.add(user);
			}
		}
		userList.removeAll(removeUsers);
	}
}
