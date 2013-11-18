package org.ukiuni.irc4j.server.worker.webworker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.ukiuni.irc4j.Channel;
import org.ukiuni.irc4j.Conf;
import org.ukiuni.irc4j.Log;
import org.ukiuni.irc4j.server.ClientConnection;
import org.ukiuni.irc4j.server.Event;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.server.command.FileRecieveThread;
import org.ukiuni.irc4j.server.command.FileRecieveThread.OnCompleteListener;

public class WebWorkerClientConnection extends ClientConnection {
	List<Event> eventList = Collections.synchronizedList(new LinkedList<Event>());

	public WebWorkerClientConnection(IRCServer ircServer) throws IOException {
		super(ircServer, null);
	}

	@Override
	public synchronized void send(String lowCommand) throws IOException {
		Log.log("## WebWorkerClientConnection#send(dummy) > " + lowCommand);
	}

	public List<Event> removeAllEvent() {
		List<Event> returnList = new ArrayList<Event>(eventList);
		eventList.removeAll(returnList);
		return returnList;
	}

	@Override
	public void sendPartCommand(ClientConnection partConnection, Channel channel, String message) throws IOException {
		send(Event.createPart(channel.getName(), partConnection.getNickName()));
	}

	@Override
	public void sendJoin(ClientConnection joiner, Channel channel) throws IOException {
		send(Event.createJoin(channel.getName(), joiner.getNickName()));
	}

	@Override
	public void sendMessage(String type, ClientConnection senderConnection, Channel channel, String message) throws IOException {
		send(Event.createMessage(channel.getName(), senderConnection.getNickName(), message));
	}

	@Override
	public void sendPrivateMessage(String type, final ClientConnection senderClientConnection, String message) throws IOException {
		String replacedMessage = message.replace(new String(new char[] { new Character((char) 1).charValue() }), "");
		if (!replacedMessage.startsWith("DCC SEND ")) {
			send(Event.createMessage(senderClientConnection.getNickName(), senderClientConnection.getNickName(), message));
		} else {
			final String[] param = replacedMessage.split(" ");
			String fileName = param[2];
			int portNum = Integer.valueOf(param[4]);
			long fileSize = Long.valueOf(param[5]);
			new FileRecieveThread(senderClientConnection.getUser().getHostName(), portNum, fileName, fileSize, new OnCompleteListener() {
				@Override
				public void onComplete(String uploadedUri) {
					try {
						String responseMessage = Conf.getHttpServerURL() + uploadedUri;
						send(Event.createMessage(senderClientConnection.getNickName(), senderClientConnection.getNickName(), responseMessage));
					} catch (Exception e) {
						Log.log("have fault after upload");
						try {
							senderClientConnection.sendNotice(":uploaded but some fault " + e);
						} catch (IOException e1) {
							Log.log(e);
						}
					}
				}

				@Override
				public void onError(Throwable e) {
					Log.log("Upload fault", e);
					try {
						senderClientConnection.sendNotice(":upload fault " + e);
					} catch (IOException e1) {
						Log.log(e);
					}
				}
			}).start();
		}
	}

	public void sendPrivateSelfMessage(String type, ClientConnection tailingClientConnection, String message) throws IOException {
		send(Event.createMessage(tailingClientConnection.getNickName(), getNickName(), message));
	}

	private void send(Event event) {
		eventList.add(event);
		synchronized (this) {
			notifyAll();
		}
	}
}
