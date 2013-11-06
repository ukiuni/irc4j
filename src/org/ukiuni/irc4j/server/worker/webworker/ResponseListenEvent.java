package org.ukiuni.irc4j.server.worker.webworker;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import net.arnx.jsonic.JSON;

import org.ukiuni.irc4j.server.Event;
import org.ukiuni.irc4j.server.IRCServer;

public class ResponseListenEvent extends AIRCResponse {
	private static final int WAITING_TIME = 30000;

	public ResponseListenEvent(IRCServer ircServer) {
		super(ircServer);
	}

	@Override
	public void onResponseSecure(OutputStream out) throws Throwable {
		WebWorkerClientConnection connection = getAccessConnection();
		if (null == connection) {
			write(out, 200, JSON.encode(Arrays.asList(Event.createRejoin())), "application/json; charset=utf-8", "UTF-8");
			return;
		}
		connection.recievePong();
		List<Event> eventList = connection.removeAllEvent();
		if (eventList.isEmpty()) {
			synchronized (connection) {
				connection.wait(WAITING_TIME);
				eventList = connection.removeAllEvent();
			}
		}
		write(out, 200, JSON.encode(eventList), "application/json; charset=utf-8", "UTF-8");
	}
}
