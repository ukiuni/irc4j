package org.ukiuni.irc4j.gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ukiuni.irc4j.IRCClient;
import org.ukiuni.irc4j.client.DCCMessage;

public class ConnectingManager {
	private Map<String, Map<String, Set<String>>> hostAndChannelAndUsers = new HashMap<String, Map<String, Set<String>>>();
	private Map<Object, IRCClient> clientMap = new HashMap<Object, IRCClient>();
	private Map<Object, Set<String>> joindedChannel = new HashMap<Object, Set<String>>();
	private static ConnectingManager instance;

	private ConnectingManager() {
	}

	public static ConnectingManager getInstance() {
		if (null == instance) {
			synchronized (ConnectingManager.class) {
				if (null == instance) {
					instance = new ConnectingManager();
				}
			}
		}
		return instance;
	}

	public Map<String, Set<String>> getChannelsAndUsers(String host, int port) {
		String key = host + ":" + port;
		if (!hostAndChannelAndUsers.containsKey(key)) {
			hostAndChannelAndUsers.put(key, new HashMap<String, Set<String>>());
		}
		return hostAndChannelAndUsers.get(key);
	}

	public IRCClient getClient(Object registerKey) {
		return clientMap.get(registerKey);
	}

	public Set<String> getUsers(String host, int port, String channel) {
		Map<String, Set<String>> channelAndUsers = getChannelsAndUsers(host, port);

		if (!channelAndUsers.containsKey(channel)) {
			channelAndUsers.put(channel, new HashSet<String>());
		}
		return channelAndUsers.get(channel);
	}

	public void clearAllUsers() {
		for (Map<String, Set<String>> channelAndUsers : hostAndChannelAndUsers.values()) {
			for (Set<String> users : channelAndUsers.values()) {
				users.clear();
			}
		}
	}

	public void connect(Object registerKey, final String host, final int port, final String nickName, final String myHost, final String realName, final IRCEventHandler handler) throws IOException {
		final IRCClient client = new IRCClient(host, port, nickName, myHost, realName);
		client.addHandler(new org.ukiuni.irc4j.IRCEventHandler() {

			@Override
			public void onServerMessage(int id, String message) {
				handler.onServerMessage(client, id, message);
			}

			@Override
			public void onMessage(String channelName, String from, String message) {
				handler.onMessage(client, channelName, from, message);
			}

			@Override
			public void onJoinToChannel(String channelName, String nickName) {
				handler.onJoinToChannel(client, channelName, nickName);
			}

			@Override
			public void onPartFromChannel(String channelName, String nickName, String message) {
				handler.onPartFromChannel(client, channelName, nickName, message);
			}

			@Override
			public void onError(Throwable e) {
				handler.onError(client, e);
			}

			@Override
			public void onDisconnectedOnce() {
				handler.onDisconnectedOnce(client);
				
			}

			@Override
			public void onDCC(String channelName, String from, DCCMessage dcc) {
				handler.onDCC(client, channelName, from, dcc);
			}
		});
		client.connect();
		clientMap.put(registerKey, client);
	}

	public boolean isJoining(Object registerKey, String channelName) {
		return joindedChannel.containsKey(registerKey) && joindedChannel.get(registerKey).contains(channelName);
	}

	public void joinToChannel(Object registerKey, String channelName) throws IOException {
		clientMap.get(registerKey).sendJoin(channelName);
		if (!joindedChannel.containsKey(registerKey)) {
			joindedChannel.put(registerKey, new HashSet<String>());
		}
		joindedChannel.get(registerKey).add(channelName);
	}

	public void partFromChannel(Object registerKey, String channelName) throws IOException {
		clientMap.get(registerKey).sendPART(channelName);
		if (!joindedChannel.containsKey(registerKey)) {
			joindedChannel.put(registerKey, new HashSet<String>());
		}
		joindedChannel.get(registerKey).remove(channelName);
	}

	public void sendMessage(Object registerKey, String channelName, String message) throws IOException {
		clientMap.get(registerKey).sendMessage(channelName, message);
	}

	public static class IRCEventHandler {
		/**
		 * Callback on recieve message from server.
		 * 
		 * @param channelName
		 *            channel
		 * @param from
		 *            from user. nickName.
		 * @param message
		 *            message
		 */
		public void onMessage(IRCClient client, String channelName, String from, String message) {
		}

		public void onDCC(IRCClient client, String channelName, String from, DCCMessage dcc) {
		}

		public void onDisconnectedOnce(IRCClient client) {
		}

		/**
		 * Callback on recieve server message(Server command) from server. I
		 * think you don't use it, because org.irc4j.IRCClient work
		 * automatically with server command as ping or other.
		 * 
		 * @see org.irc4j.commandimpl.ServerCommand.
		 * @param id
		 * @param message
		 */
		public void onServerMessage(IRCClient client, int id, String message) {
		}

		/**
		 * Callback on rise error.
		 * 
		 * @param e
		 */
		public void onError(IRCClient client, Throwable e) {
		}

		/**
		 * Callback on someone join to channel
		 * 
		 * @param channelName
		 * @param nickName
		 * @param message
		 */
		public void onJoinToChannel(IRCClient client, String channelName, String nickName) {
		}

		/**
		 * Callback on someone part from channel
		 * 
		 * @param channelName
		 * @param nickName
		 * @param message
		 */
		public void onPartFromChannel(IRCClient client, String channelName, String nickName, String message) {
		}
	}
}
