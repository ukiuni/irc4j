package org.ukiuni.irc4j.server.worker;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.ukiuni.irc4j.Log;
import org.ukiuni.irc4j.server.ClientConnection;
import org.ukiuni.irc4j.server.IRCServer;

public class PingPongWorker extends TimerTask implements Worker {
	private IRCServer ircServer;
	private static final int PONG_WAIT_TIME = 60000;
	private static final int PERIOD = 3000;
	private Timer timer;

	@Override
	public void run() {
		try {
			Log.log(this + " run");
			List<ClientConnection> doPingConnectionList = new ArrayList<ClientConnection>(this.ircServer.getConnectionList());
			Calendar waitTimeAgoCalendar = Calendar.getInstance();
			Date waitTimeAgo = waitTimeAgoCalendar.getTime();
			waitTimeAgoCalendar.add(Calendar.SECOND, PONG_WAIT_TIME * -1);
			for (ClientConnection clientConnection : doPingConnectionList) {
				try {
					if(null == clientConnection.getLastRecievePongDate() || waitTimeAgoCalendar.before(waitTimeAgoCalendar))
					clientConnection.sendPing("hello");
				} catch (IOException e) {
					this.ircServer.removeConnection(clientConnection.getNickName());
				}
			}
			try {
				Thread.sleep(PONG_WAIT_TIME);
			} catch (InterruptedException e) {
			}
			waitTimeAgoCalendar = Calendar.getInstance();
			waitTimeAgoCalendar.add(Calendar.MILLISECOND, PONG_WAIT_TIME * -1);
			waitTimeAgo = waitTimeAgoCalendar.getTime();
			List<ClientConnection> checkPongList = new ArrayList<ClientConnection>(this.ircServer.getConnectionList());
			for (ClientConnection clientConnection : checkPongList) {
				Date lastPongDate = clientConnection.getLastRecievePongDate();
				Date lastPingDate = clientConnection.getLastSendPingDate();
				if (null != lastPingDate && lastPingDate.before(waitTimeAgo)) {
					if (null == lastPongDate || lastPongDate.before(lastPingDate)) {
						Log.log("remove [" + clientConnection.getNickName() + "] lastPing " + new SimpleDateFormat("hh:mm").format(lastPingDate) + " lastPong " + (null == lastPongDate ? "null" : new SimpleDateFormat("hh:mm").format(lastPongDate)));
						this.ircServer.removeConnection(clientConnection.getNickName());
					}
				}
			}
			ircServer.dumpUsers();
		} catch (Throwable e) {
			e.printStackTrace();
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
