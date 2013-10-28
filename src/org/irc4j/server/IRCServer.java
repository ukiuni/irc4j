package org.irc4j.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.irc4j.Channel;
import org.irc4j.ExceptionHandler;
import org.irc4j.Log;
import org.irc4j.User;
import org.irc4j.server.worker.PingPongWorker;
import org.irc4j.server.worker.Worker;
import org.irc4j.util.IOUtil;

public class IRCServer implements Runnable {
	private Map<String, ClientConnection> connectionMap = new HashMap<String, ClientConnection>();
	private Map<String, ServerChannel> channelMap = new HashMap<String, ServerChannel>();
	private int portNum = 6667;
	private ServerSocket serverSocket;
	private boolean isRunning;
	private Thread runningThread;
	private ArrayList<Worker> workerList;

	public Map<String, ClientConnection> getConnectionMap() {
		return connectionMap;
	}

	public static void main(String[] args) throws IOException {
		new IRCServer().start();
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
		for (Worker worker : workerList) {
			worker.work(this);
		}
	}

	public void stop() throws IOException {
		isRunning = false;
		IOUtil.close(serverSocket);
		channelMap.clear();
		List<ClientConnection> closeCollections = new ArrayList<ClientConnection>(connectionMap.values());
		for (ClientConnection clientConnection : closeCollections) {
			IOUtil.close(clientConnection);
		}
		connectionMap.clear();
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
			// Log.log("Exception with " + clientConnection.getNickName());
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
		Map<String, ServerChannel> channelMap = selfClientConnection.getJoinedChannels();
		for (Channel channel : channelMap.values()) {
			for (User user : channel.getUserList()) {
				ClientConnection clientConnection = connectionMap.get(user.getNickName());
				clientConnection.send(newNickCommand);
			}
		}
	}

	public void joinToChannel(final ClientConnection con, String channelName) throws IOException {
		joinToChannel(con, channelName, null);
	}

	public synchronized void joinToChannel(final ClientConnection con, String channelName, String password) throws IOException {
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
		for (User userInChannel : channel.getUserList()) {
			if (userInChannel.getNickName().equals(con.getNickName())) {
				con.sendCommand("You are already a member of " + channelName);
				return;
			}
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
	}

	public ServerChannel getChannel(String channelName) {
		return channelMap.get(channelName);
	}

	public void removeChannel(String name) {
		channelMap.remove(name);
	}

	public synchronized void removeConnection(String nickName) {
		ClientConnection clientConnection = connectionMap.remove(nickName);
		for (ServerChannel channel : clientConnection.getJoinedChannels().values()) {
			try {
				channel.part(clientConnection);
			} catch (IOException e) {
			}
		}
		IOUtil.close(clientConnection);

		Log.log("connection removed [" + nickName + "]");
	}
}
