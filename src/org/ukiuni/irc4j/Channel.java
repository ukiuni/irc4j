package org.ukiuni.irc4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	private Set<User> userList = new HashSet<User>();

	public Channel(String name) {
		this.name = name;
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
		return !(channelName.startsWith("#") && !channelName.contains(" ") && !channelName.contains(">") && !channelName.contains("<") && !channelName.contains("&") && !channelName.contains("\"") && !channelName.contains(","));
	}

	public void addUserAll(List<User> userList) {
		this.userList.addAll(userList);
	}

	/**
	 * This method call via child class, caz You have to do many with remove
	 * User.
	 * 
	 * @param nickName
	 */
	protected void removeUser(User user) {
		userList.remove(user);
	}

	public List<User> getCurrentUserList() {
		return new ArrayList<User>(userList);
	}

	public void clearUsers() {
		userList.clear();
	}

	protected void addUser(User user) {
		userList.add(user);
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Channel)) {
			return false;
		}
		return getName().equals(((Channel) obj).getName());
	}
}
