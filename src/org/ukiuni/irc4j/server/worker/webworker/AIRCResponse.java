package org.ukiuni.irc4j.server.worker.webworker;

import java.io.IOException;
import java.io.OutputStream;

import org.ukiuni.irc4j.server.ClientConnection;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.util.CipherUtil;
import org.ukiuni.lighthttpserver.response.Response;

public abstract class AIRCResponse extends Response {
	private String nickname;
	private WebWorkerClientConnection accessConnection;
	protected IRCServer ircServer;

	public AIRCResponse(IRCServer ircServer) {
		this.ircServer = ircServer;
	}

	@Override
	public final void onResponse(OutputStream out) throws Throwable {
		String sessionId = getRequest().getParameter("sessionId");
		String sessionKey = getRequest().getParameter("sessionKey");
		if (null == sessionId || null == sessionKey) {
			writeError(out, 403, "security");
			return;
		}
		String decorded = CipherUtil.decode(sessionKey);
		String[] decordedSprit = decorded.split(" ");
		String nickName = decordedSprit[0];
		String encordedSessionKey = decordedSprit[1];
		String loginTime = decordedSprit[2];
		if (!sessionId.equals(encordedSessionKey)) {
			writeError(out, 403, "security");
			return;
		}
		accessConnection = (WebWorkerClientConnection) ircServer.findConnection(nickName);
		onResponseSecure(out);
	}

	public void writeError(OutputStream out, int status, String message) throws IOException {
		write(out, 403, "{\"error\":\"" + message + "\"}", "application/json; charset=utf-8", "UTF-8");
	}

	public abstract void onResponseSecure(OutputStream out) throws Throwable;

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public WebWorkerClientConnection getAccessConnection() {
		return accessConnection;
	}

	protected static String createSessionKey(String nickName, String sessionId, long time) {
		return CipherUtil.encode(nickName + " " + sessionId + " " + time);
	}
}
