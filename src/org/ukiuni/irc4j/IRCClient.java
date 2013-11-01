package org.ukiuni.irc4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.ukiuni.irc4j.client.ClientChannel;
import org.ukiuni.irc4j.client.ClientCommand;
import org.ukiuni.irc4j.client.ClientCommandFactory;
import org.ukiuni.irc4j.client.ServerCommand;

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
 * This class is most important class in IRC4j. It is sample code below.
 * 
 * IRCClient ircClient = new IRCClient(host, port, nickName, myHost, realName);
 * ircClient.addHandler(new IRCEventHandler() {
 * 
 * @Override public void onServerMessage(int id, String message) {
 *           System.out.println("onServerMessage(" + id + ", " + message + ")");
 *           }
 * @Override public void onMessage(String channelName, String from, String
 *           message) { System.out.println("onMessage(" + channelName + "," +
 *           from + "," + message + ")"); }
 * @Override public void onError(Throwable e) { e.printStackTrace(); } });
 *           //ircClient.setDaemon(true); ircClient.connect();
 *           ircClient.sendJoin(channel); ircClient.sendMessage(channel,
 *           message);
 * 
 * @author ukiuni
 * 
 */
public class IRCClient {
	private Socket socket;
	private String host;
	private int port;
	private boolean ready;
	private boolean waitServerResponseOnConnect = false;
	private ClientCommandFactory commandFactory = new ClientCommandFactory();

	private List<IRCEventHandler> handlers = new ArrayList<IRCEventHandler>();
	private IRCEventHandler startHander;
	private OutputStream out;
	private ReadThread readThread;
	private Map<String, ClientChannel> channelMap = new HashMap<String, ClientChannel>();
	private String encode = "UTF-8";
	private String nickName;
	private int startTimeout = 10000;
	private boolean autoRecconect = true;
	private int reConnectSpan = 10000;
	private boolean daemon = false;
	private String myHost;
	private String realName;
	private boolean starting = false;

	/**
	 * Create instance with no properties. You have to set parameter
	 * "host, port, nickname, myHost, realName" before connect.
	 */
	public IRCClient() {
		init();
	}

	/**
	 * Create instance with properties.
	 * 
	 * @param host
	 *            host
	 * @param port
	 *            port
	 * @param nickName
	 *            nickname
	 * @param myHost
	 *            localhost name
	 * @param realName
	 *            realName
	 */
	public IRCClient(String host, int port, String nickName, String myHost, String realName) {
		this.setHost(host);
		this.setPort(port);
		this.setNickName(nickName);
		this.setMyHost(myHost);
		this.setRealName(realName);

		init();
	}

	private void init() {
		addHandler(new IRCEventAdapter() {
			@Override
			public void onError(Throwable ex) {
				if (ex instanceof RecievedFromIRCServerException) {
					RecievedFromIRCServerException e = (RecievedFromIRCServerException) ex;
					if ("433".equals(e.getId())) {
						IRCClient.this.nickName = IRCClient.this.nickName + "_";
						try {
							reconnect();
						} catch (IOException e1) {
							IRCClient.this.onError(e1);
						}
					}
				}
			}
		});
		addHandler(new IRCEventAdapter() {
			@Override
			public void onServerMessage(int id, String message) {
				if (ServerCommand.RPL_NAMREPLY == id) {
					int sharpIndex = message.indexOf("#");
					String channelName = message.substring(sharpIndex, message.indexOf(" ", sharpIndex));
					String[] userNames = message.substring(message.lastIndexOf(":") + 1).trim().split(" ");
					List<User> userList = new ArrayList<User>();
					for (String userName : userNames) {
						User user = new User();
						if (userName.startsWith("@")) {
							user.setNickName(userName.substring(1));
							user.setOwner(true);
						} else {
							user.setNickName(userName);
						}
						userList.add(user);
					}
					Channel channel = channelMap.get(channelName);
					if (null != channel) {
						channel.clearUsers();
						channel.addUserAll(userList);
					}
				}
			}
		});
	}

	/**
	 * Recconect to IRC server.
	 * 
	 * @throws IOException
	 */
	public void reconnect() throws IOException {
		connect();
		Set<String> channelNames = new TreeSet<String>(channelMap.keySet());
		channelMap.clear();
		try {
			for (String channelName : channelNames) {
				sendJoin(channelName);
			}
		} catch (Exception e) {
			for (String channelName : channelNames) {
				channelMap.put(channelName, null);
			}
		}
	}

	/**
	 * Connect to IRC server.
	 * 
	 * @throws IOException
	 */
	public void connect() throws IOException {
		if (isReady()) {
			sendQuit();
			ready = false;
		}
		final List<IOException> eList = new ArrayList<IOException>();
		startHander = new IRCEventAdapter() {
			@Override
			public void onServerMessage(int id, String message) {
				if (waitServerResponseOnConnect) {
					try {
						sendNickName(nickName);
						sendUser(nickName, myHost, host, realName);
						ready = true;
					} catch (IOException e) {
						eList.add(e);
					}
					synchronized (startHander) {
						startHander.notifyAll();
					}
				}
			}

			@Override
			public void onError(Throwable e) {
				eList.add(new IOException(e));
			}
		};
		this.addHandler(startHander);
		try {
			socket = new Socket(this.host, this.port);
		} catch (IOException e) {
			if (!isAutoRecconect()) {
				throw e;
			}
			autoRecconect();
			return;
		}
		this.out = socket.getOutputStream();
		this.readThread = new ReadThread(socket.getInputStream());
		starting = true;
		this.readThread.start();

		if (waitServerResponseOnConnect) {
			if (!isReady()) {
				synchronized (startHander) {
					try {
						startHander.wait(getStartTimeout());
					} catch (InterruptedException e1) {
						// nothing to do;
					}
				}
			}
		} else {
			ready = true;
			sendNickName(nickName);
			sendUser(nickName, myHost, host, realName);
		}
		removeHandler(startHander);

		starting = false;
		if (!isReady()) {
			if (!eList.isEmpty()) {
				throw eList.get(0);
			} else {
				throw new IOException("request time out");
			}
		}
	}

	private void autoRecconect() {
		Thread recconectThread = new Thread() {
			public void run() {
				try {
					Thread.sleep(reConnectSpan);
					reconnect();
				} catch (Exception e) {
					onError(e);
				}
			};
		};
		recconectThread.setDaemon(isDaemon());
		recconectThread.start();
	}

	/**
	 * Send userinfo to IRC server
	 * 
	 * @param nickName
	 *            nickName
	 * @param myHost
	 *            localhost name
	 * @param host
	 *            server host name
	 * @param realName
	 *            realName
	 * @throws IOException
	 */
	public void sendUser(String nickName, String myHost, String host, String realName) throws IOException {
		this.nickName = nickName;
		this.myHost = myHost;
		this.host = host;
		this.realName = realName;
		write(Command.COMMAND_USER, null, nickName, myHost, host, realName);
	}

	/**
	 * Send password to IRC Server
	 * 
	 * @param password
	 *            password
	 * @throws IOException
	 */
	public void sendPass(String password) throws IOException {
		write(Command.COMMAND_PASS, null, password);
	}

	/**
	 * Send server command to IRC server.
	 * 
	 * @param prefix
	 *            command prefix
	 * @param message
	 *            message
	 * @throws IOException
	 */
	public void sendServer(String prefix, String message) throws IOException {
		writeIRCFormat(prefix, Command.COMMAND_SERVER, message);
	}

	/**
	 * Send oper command to IRC server.
	 * 
	 * @param nickName
	 * @param password
	 * @throws IOException
	 */
	public void sendOper(String nickName, String password) throws IOException {
		write(Command.COMMAND_OPER, null, nickName, password);
	}

	/**
	 * Send nickname to IRC server;
	 * 
	 * @param nickName
	 * @throws IOException
	 */
	public void sendNickName(String nickName) throws IOException {
		this.nickName = nickName;
		write(Command.COMMAND_NICK, null, nickName);
	}

	/**
	 * Send quit command to IRC server, and close connection.
	 * 
	 * @throws IOException
	 */
	public void sendQuit() throws IOException {
		write(Command.COMMAND_QUIT);
		ready = false;
		close();
	}

	/**
	 * Send quit command to IRC server.
	 * 
	 * @throws IOException
	 */
	public void sendServerQuit() throws IOException {
		ready = false;
		write(Command.COMMAND_QUIT);
		close();
	}

	private void close() {
		if (null != readThread) {
			try {
				readThread.close();
			} catch (IOException e) {
			}
		}
		if (null != socket) {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}

	private class ReadThread extends Thread {
		private InputStream in;

		public ReadThread(InputStream in) {
			this.setDaemon(IRCClient.this.isDaemon());
			this.in = in;
		}

		public void run() {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in, IRCClient.this.getEncode()));
				String line = reader.readLine();
				while (line != null && IRCClient.this.isReady() || IRCClient.this.isStarting()) {
					dispatch(line);
					line = reader.readLine();
				}
			} catch (IOException e) {

				if (IRCClient.this.isReady()) {
					onError(e);
					if (isAutoRecconect()) {
						autoRecconect();
					}
				}
			}
		}

		public void close() throws IOException {
			in.close();
		}
	}

	/**
	 * Send join command to IRC server. You have to join to channel before send
	 * message. You can join plural channel.
	 * 
	 * @param channelName
	 * @return
	 * @throws IOException
	 */
	public ClientChannel sendJoin(String channelName) throws IOException {
		if (!channelName.startsWith("#")) {
			channelName = "#" + channelName;
		}
		ClientChannel channel = channelMap.get(channelName);
		if (null == channel) {
			channel = new ClientChannel(this, channelName);
			channelMap.put(channelName, channel);
			write(Command.COMMAND_JOIN, channelName);
		}
		return channel;
	}

	/**
	 * Is client handshaking with IRC server
	 * 
	 * @return Is client handshaking with IRC server
	 */
	public boolean isStarting() {
		return starting;
	}

	/**
	 * Send part command to IRC server.
	 * 
	 * @param channelName
	 * @throws IOException
	 */
	public void sendPART(String channelName) throws IOException {
		if (!channelName.startsWith("#")) {
			channelName = "#" + channelName;
		}
		write(Command.COMMAND_PART, channelName);
		channelMap.remove(channelName);
	}

	/**
	 * Send mode command to IRC server.
	 * 
	 * @param channelName
	 *            channelName
	 * @param mode
	 *            mode
	 * @throws IOException
	 */
	public void sendModeToChannel(String channelName, String mode) throws IOException {
		write(Command.COMMAND_PART, channelName, mode);
	}

	/**
	 * Send mode command to IRC server
	 * 
	 * @param nickName
	 *            target nickname
	 * @param mode
	 *            mode
	 * @throws IOException
	 */
	public void sendModeToNickname(String nickName, String mode) throws IOException {
		writeIRCFormat(nickName, Command.COMMAND_PART, nickName + " " + mode);
	}

	/**
	 * Send toppic command to IRC server.
	 * 
	 * @param nickName
	 *            nickName
	 * @param channelName
	 *            channelName
	 * @param topic
	 *            topic
	 * @throws IOException
	 */
	public void sendTopic(String nickName, String channelName, String topic) throws IOException {
		if (!channelName.startsWith("#")) {
			channelName = "#" + channelName;
		}
		writeIRCFormat(nickName, Command.COMMAND_PART, channelName + " " + topic);
	}

	/**
	 * Send message to IRC server. You have to connect and join to channel
	 * before send message.
	 * 
	 * @param channelName
	 *            channelName
	 * @param message
	 *            message.
	 * @throws IOException
	 */
	public void sendMessage(String channelName, String message) throws IOException {
		write(Command.COMMAND_PRIVMSG, channelName, ":" + message);
	}

	/**
	 * Send command to IRC Server User this method when you want to use a
	 * command irc4j is not implement.
	 * 
	 * @see org.ukiuni.irc4j.Command
	 * @param command
	 *            command
	 * @throws IOException
	 */
	public void write(String command) throws IOException {
		write(command, null);
	}

	/**
	 * Send command to channel on IRC server. User this method when you want to
	 * use a command irc4j is not implement.
	 * 
	 * @param command
	 *            command
	 * @param channelName
	 *            channelName
	 * @throws IOException
	 */
	public void write(String command, String channelName) throws IOException {
		write(command, channelName, new String[] {});
	}

	/**
	 * Send command to channel on IRC server. User this method when you want to
	 * use a command irc4j is not implement.
	 * 
	 * @param command
	 *            command
	 * @param channelName
	 *            channelName
	 * @param params
	 *            params
	 * @throws IOException
	 */
	public void write(String command, String channelName, String... params) throws IOException {
		String outMessage = command;
		if (null != channelName) {
			outMessage += " " + channelName;
		}
		if (null != params) {
			for (String param : params) {
				if (null != param) {
					outMessage += " " + param;
				}
			}
		}
		writeDirectry(outMessage);
	}

	/**
	 * Send command to IRC server. Ordinary IRC command is
	 * ":prefix command parameter"
	 * 
	 * @param prefix
	 * @param command
	 * @param parameter
	 * @throws IOException
	 */
	private void writeIRCFormat(String prefix, String command, String parameter) throws IOException {
		if (prefix != null && prefix.startsWith(":")) {
			prefix = ":" + prefix;
		}
		if (null == prefix) {
			prefix = "";
		} else {
			prefix = prefix + " ";
		}
		if (null == parameter) {
			parameter = "";
		} else {
			parameter = " " + parameter;
		}
		writeDirectry(prefix + command + parameter);
	}

	/**
	 * Send command to IRC server.
	 * 
	 * @param outMessage
	 * @throws IOException
	 */
	private void writeDirectry(String outMessage) throws IOException {
		if (!isReady()) {
			throw new IOException("client is not ready");
		}
		out.write((outMessage + "\r\n").getBytes(encode));
		out.flush();
	}

	private void dispatch(String line) {
		ClientCommand command = (ClientCommand) commandFactory.loadCommand(line);
		try {
			command.execute(this, handlers);
		} catch (Throwable e) {
			onError(e);
		}
	}

	/**
	 * Callback error.
	 * 
	 * @param e
	 */
	private void onError(Throwable e) {
		for (IRCEventHandler handler : handlers) {
			handler.onError(e);
		}
	}

	/**
	 * Add callback handler.
	 * 
	 * @see org.ukiuni.irc4j.IRCEventHandler
	 * @param handler
	 */
	public void addHandler(IRCEventHandler handler) {
		handlers.add(handler);
	}

	/**
	 * Remove callback handler.
	 * 
	 * @see org.ukiuni.irc4j.IRCEventHandler
	 * @param handler
	 * @return
	 */
	public boolean removeHandler(IRCEventHandler handler) {
		return handlers.remove(handler);
	}

	public String getMyHost() {
		return myHost;
	}

	/**
	 * Set localhost name send to server on connect.
	 * 
	 * @param myHost
	 */
	public void setMyHost(String myHost) {
		this.myHost = myHost;
	}

	public String getRealName() {
		return realName;
	}

	/**
	 * Set realName send to server on connect.
	 * 
	 * @param realName
	 */
	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getHost() {
		return host;
	}

	/**
	 * Set IRC server host name.
	 * 
	 * @param host
	 */
	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	/**
	 * Set IRC server port. Default is 6667.
	 * 
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	public String getEncode() {
		return encode;
	}

	/**
	 * Set encode to treat message. Default is UTF-8.
	 * 
	 * @param encode
	 */
	public void setEncode(String encode) {
		this.encode = encode;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public int getStartTimeout() {
		return startTimeout;
	}

	/**
	 * Some IRC Server can't accept message before first server response. this
	 * time(millisecond) is time of wait to response.
	 * 
	 * @param startTimeout
	 */
	public void setStartTimeout(int startTimeout) {
		this.startTimeout = startTimeout;
	}

	/**
	 * Is ready to work with server.
	 * 
	 * @return
	 */
	public boolean isReady() {
		return ready;
	}

	public boolean isDaemon() {
		return daemon;
	}

	/**
	 * Set "true" if you want shutdown application when main method is end.
	 * 
	 * @param daemon
	 */
	public void setDaemon(boolean daemon) {
		this.daemon = daemon;
	}

	public boolean isWaitServerResponseOnConnect() {
		return waitServerResponseOnConnect;
	}

	/**
	 * Some IRC Server can't accept message before first server response. if you
	 * want to use this kind of server, set "true" before connect.
	 * 
	 * @param waitServerResponseOnConnect
	 */
	public void setWaitServerResponseOnConnect(boolean waitServerResponseOnConnect) {
		this.waitServerResponseOnConnect = waitServerResponseOnConnect;
	}

	public int getRecconectSpan() {
		return reConnectSpan;
	}

	/**
	 * Recconect span(millisecond) when client network disconnect between
	 * server. Use this parameter, you also have to setAutoConnect(true).
	 * 
	 * @param recconectSpan
	 */
	public void setRecconectSpan(int recconectSpan) {
		this.reConnectSpan = recconectSpan;
	}

	public boolean isAutoRecconect() {
		return autoRecconect;
	}

	/**
	 * Recconect when client network disconnect between server.
	 * 
	 * @param autoRecconect
	 */
	public void setAutoRecconect(boolean autoRecconect) {
		this.autoRecconect = autoRecconect;
	}

	public void sendWho(String channelName) throws IOException {
		write(Command.COMMAND_WHO, channelName);
	}

}
