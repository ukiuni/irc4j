package org.irc4j.server.worker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.irc4j.Log;
import org.irc4j.server.ClientConnection;
import org.irc4j.server.IRCServer;

public class PingPongWorker extends TimerTask implements Worker {
	private IRCServer ircServer;
	private static final int PONG_WAIT_TIME = 10000;
	private static final int PERIOD = 30000;
	private Timer timer;

	@Override
	public void run() {
		Log.log(this + " run");
		List<ClientConnection> doPingConnectionList = new ArrayList<ClientConnection>(this.ircServer.getConnectionMap().values());
		for (ClientConnection clientConnection : doPingConnectionList) {
			try {
				clientConnection.sendPing("hello");
			} catch (IOException e) {
				this.ircServer.removeConnection(clientConnection.getNickName());
			}
		}
		try {
			Thread.sleep(PONG_WAIT_TIME);
		} catch (InterruptedException e) {
		}
		Calendar waitTimeAgoCalendar = Calendar.getInstance();
		waitTimeAgoCalendar.add(Calendar.SECOND, PONG_WAIT_TIME * -1);
		Date waitTimeAgo = waitTimeAgoCalendar.getTime();
		List<ClientConnection> checkPongList = new ArrayList<ClientConnection>(this.ircServer.getConnectionMap().values());
		for (ClientConnection clientConnection : checkPongList) {
			Date lastPongDate = clientConnection.getLastRecievePongDate();
			Date lastPingDate = clientConnection.getLastSendPingDate();
			if ((null != lastPingDate && lastPingDate.before(waitTimeAgo)) && (null == lastPongDate || lastPongDate.before(lastPingDate))) {
				this.ircServer.removeConnection(clientConnection.getNickName());
			}
		}
	}

	@Override
	public void work(IRCServer ircServer) {
		this.ircServer = ircServer;
		timer = new Timer(true);
		timer.schedule(this, PERIOD, PERIOD + PONG_WAIT_TIME);
	}

	@Override
	public void stop() {
		if (null != timer) {
			timer.cancel();
		}
	}
}
