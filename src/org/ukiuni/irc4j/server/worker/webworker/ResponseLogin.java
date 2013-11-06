package org.ukiuni.irc4j.server.worker.webworker;

import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.arnx.jsonic.JSON;

import org.ukiuni.irc4j.User;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.lighthttpserver.response.Response;

public class ResponseLogin extends Response {
	private IRCServer ircServer;

	public ResponseLogin(IRCServer ircServer) {
		this.ircServer = ircServer;
	}

	@Override
	public void onResponse(OutputStream out) throws Throwable {
		String nickName = getRequest().getParameter("nickName");
		String password = getRequest().getParameter("password");
		if (null == nickName || User.isWrongNickName(nickName) || ircServer.hasConnection(nickName)) {
			write(out, 409, "{\"error\":\"" + nickName + " is wrone or duplicate\"}", "application/json; charset=utf-8", "UTF-8");
			return;
		}
		WebWorkerClientConnection clientConnection = new WebWorkerClientConnection(ircServer);

		clientConnection.setNickName(nickName);
		clientConnection.getUser().setHostName("webIF");
		clientConnection.getUser().setName(nickName);
		clientConnection.getUser().setRealName(nickName);
		clientConnection.getUser().setPassword(password);

		ircServer.putConnection(clientConnection);

		String channels = getRequest().getParameter("channels");
		if (null != channels && channels.equals("")) {
			String[] channelArray = channels.split(",");
			for (String channelName : channelArray) {
				ircServer.joinToChannel(clientConnection, channelName);
			}
		}
		String sessionId = UUID.randomUUID().toString();
		long loginTime = new Date().getTime();
		String sessionKey = AIRCResponse.createSessionKey(nickName, sessionId, loginTime);
		Map<String, String> responseData = new HashMap<String, String>();
		responseData.put("sessionId", sessionId);
		responseData.put("sessionKey", sessionKey);
		responseData.put("nickName", nickName);
		write(out, 200, JSON.encode(responseData), "application/json; charset=utf-8", "UTF-8");
	}
}
