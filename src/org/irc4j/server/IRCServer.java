package org.irc4j.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.irc4j.Channel;
import org.irc4j.ExceptionHandler;
import org.irc4j.Log;
import org.irc4j.User;
import org.irc4j.server.worker.PingPongWorker;
import org.irc4j.server.worker.WebWorker;
import org.irc4j.server.worker.Worker;
import org.irc4j.util.IOUtil;

public class IRCServer implements Runnable {
	private List<ClientConnection> connectionList = new ArrayList<ClientConnection>();
	private Map<String, ServerChannel> channelMap = new HashMap<String, ServerChannel>();
	private int portNum = 6667;
	private ServerSocket serverSocket;
	private boolean isRunning;
	private Thread runningThread;
	private ArrayList<Worker> workerList;

	public static void main(String[] args) throws IOException {
		final IRCServer server = new IRCServer();
		server.start();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					server.stop();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		});
	}

	public IRCServer() {
	}

	public IRCServer(int portNum) {
		this.portNum = portNum;
	}

	public void start() {
		runningThread = new Thread(this);
		runningThread.start();
		this.workerList = new ArrayList<Worker>();
		this.workerList.add(new PingPongWorker());
		this.workerList.add(new WebWorker());
		for (Worker worker : workerList) {
			worker.work(this);
		}
	}

	public void stop() throws IOException {
		isRunning = false;
		IOUtil.close(serverSocket);
		channelMap.clear();
		List<ClientConnection> closeCollections = new ArrayList<ClientConnection>(connectionList);
		for (ClientConnection clientConnection : closeCollections) {
			IOUtil.close(clientConnection);
		}
		connectionList.clear();
		runningThread = null;
		if (null != workerList) {
			for (Worker worker : workerList) {
				worker.stop();
			}
		}
	}

	@Override
	public void run() {
		try {
			isRunning = true;
			serverSocket = new ServerSocket(portNum);
			while (isRunning) {
				Socket socket = serverSocket.accept();
				ClientConnectionExceptionHandler exceptionHandler = new ClientConnectionExceptionHandler();
				ClientConnection clientConnection = new ClientConnection(this, socket, exceptionHandler);
				exceptionHandler.setClinetConnection(clientConnection);
				exceptionHandler.setIRCServer(this);
				clientConnection.start();
			}
		} catch (Throwable e) {
			if (isRunning) {
				e.printStackTrace();// todo
			}
		}
	}

	public static class ClientConnectionExceptionHandler implements ExceptionHandler {

		private ClientConnection clientConnection;
		private IRCServer ircServer;

		@Override
		public void handle(Throwable e) {
			Log.log("Exception with " + clientConnection.getNickName(), e);
			// e.printStackTrace();
			// ircServer.connectionMap.remove(clientConnection.getNickName());
		}

		public void setIRCServer(IRCServer ircServer) {
			this.ircServer = ircServer;
		}

		public void setClinetConnection(ClientConnection clientConnection) {
			this.clientConnection = clientConnection;
		}
	}

	public String getServerName() {
		return "AIR_IRC";
	}

	public void sendToSameChannelUser(ClientConnection selfClientConnection, String newNickCommand) throws IOException {
		for (Channel channel : selfClientConnection.getJoinedChannels()) {
			for (User user : channel.getUserList()) {
				ClientConnection clientConnection = findConnection(user.getNickName());
				clientConnection.send(newNickCommand);
			}
		}
	}

	public ClientConnection findConnection(String nickName) {
		for (ClientConnection connection : connectionList) {
			if (nickName.equals(connection.getNickName())) {
				return connection;
			}
		}
		return null;
	}

	public void joinToChannel(final ClientConnection con, String channelName) throws IOException {
		joinToChannel(con, channelName, null);
	}

	public synchronized void joinToChannel(final ClientConnection con, String channelName, String password) throws IOException {
		Log.log("joinToChannel start");
		if (Channel.wrongName(channelName)) {
			con.sendCommand("Wrong channel name " + channelName);
			return;
		}
		ServerChannel channel = channelMap.get(channelName);
		boolean createdNew = false;
		if (null == channel) {
			createdNew = true;
			channel = new ServerChannel(this, channelName);
			channelMap.put(channelName, channel);
			channel.setPassword(password);
		} else {
			if (!channel.judgePassword(password)) {
				con.sendCommand("This channel has password " + channelName);
				return;
			}
		}
		if (channel.getUserList().contains(con.getUser())) {
			con.sendCommand("You are already a member of " + channelName);
			return;
		}
		channel.getUserList().add(con.getUser());
		channel.addConnection(con);
		channel.send(":" + con.getUser().getFQUN() + " JOIN " + channelName);
		if (createdNew) {
			con.sendCommand("MODE " + channelName + " +nt");
		}
		con.send(":" + con.getUser().getFQUN() + " JOIN " + channelName);
		if (channel.getTopic() != null) {
			con.sendCommand("332 " + con.getUser().getNickName() + " " + channel.getName() + " :" + channel.getTopic());
		} else {
			con.sendCommand("331 " + con.getUser().getNickName() + " " + channel.getName() + " :No topic is set");
		}
		for (User user : channel.getUserList()) {
			con.sendCommand("353 " + con.getUser().getNickName() + " = " + channel.getName() + " :" + user.getNickName());
		}
		con.sendCommand("366 " + con.getUser().getNickName() + " " + channelName + " :End of /NAMES list");
		Log.log("joinToChannel end");
	}

	public ServerChannel getChannel(String channelName) {
		return channelMap.get(channelName);
	}

	public void removeChannel(String name) {
		channelMap.remove(name);
	}

	public synchronized void removeConnection(String nickName) {
		ClientConnection clientConnection = findConnection(nickName);
		if (null == clientConnection) {
			return;
		}
		connectionList.remove(clientConnection);
		for (ServerChannel channel : clientConnection.getJoinedChannels()) {
			try {
				channel.part(clientConnection);
			} catch (IOException e) {
			}
		}
		IOUtil.close(clientConnection);

		Log.log("connection removed [" + nickName + "]");
	}

	public void sendServerHelloAndPutConnection(ClientConnection clientConnection) throws IOException {
		String nickName = clientConnection.getNickName();
		clientConnection.setServerHelloSended(true);
		clientConnection.sendCommand("001 " + nickName + " :Welcome to " + this.getServerName() + ", Multi-Communication server IRC interface.");
		clientConnection.sendCommand("004 " + nickName + " " + this.getServerName() + " ");
		clientConnection.sendCommand("375 " + nickName + " :- " + this.getServerName() + " Message of the Day -");
		clientConnection.sendCommand("372 " + nickName + " :- Hello. Welcome to " + this.getServerName() + ", a test.");
		clientConnection.sendCommand("372 " + nickName + " :- forsome " + "for more in.");
		clientConnection.sendCommand("376 " + nickName + " :End of MOTD command. is what");
		this.putConnection(nickName, clientConnection);
	}

	public void putConnection(String nickName, ClientConnection clientConnection) {
		this.connectionList.add(clientConnection);
	}

	public boolean hasConnection(String nickName) {
		return null != findConnection(nickName);
	}

	public void dumpUsers() {
		System.out.println("start////////");
		for (ClientConnection connection : connectionList) {
			System.out.println(connection.getUser().getFQUN());
		}
		System.out.println("/////////////");

	}

	public Collection<ClientConnection> getConnectionList() {
		return Collections.unmodifiableList(connectionList);
	}
}
