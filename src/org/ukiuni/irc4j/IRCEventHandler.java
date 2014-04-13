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
 * Instance of this class is callback object will callback from IRCClient when on server callback and on error.
 * @see org.irc4j.IRCClient.addHandler();
 * @author ukiuni
 */
public interface IRCEventHandler {
	/**
	 * Callback on recieve message from server.
	 * @param channelName channel
	 * @param from from user. nickName.
	 * @param message message
	 */
	public void onMessage(String channelName, String from, String message);

	/**
	 * Callback on recieve server message(Server command) from server.
	 * I think you don't use it, because org.irc4j.IRCClient work automatically with server command as ping or other.
	 * @see org.irc4j.commandimpl.ServerCommand.
	 * @param id
	 * @param message
	 */
	public void onServerMessage(int id, String message);

	/**
	 * Callback on rise error.
	 * @param e
	 */
	public void onError(Throwable e);

	/**
	 * Callback on someone join to channel
	 * @param channelName
	 * @param nickName
	 * @param message
	 */
	public void onJoinToChannel(String channelName, String nickName);

	/**
	 * Callback on someone part from channel
	 * @param channelName
	 * @param nickName
	 * @param message
	 */
	public void onPartFromChannel(String channelName, String nickName, String message);
}
