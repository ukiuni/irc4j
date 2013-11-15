package org.ukiuni.irc4j.server.worker.webworker;

import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.arnx.jsonic.JSON;

import org.ukiuni.irc4j.User;
import org.ukiuni.irc4j.db.Database;
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
		if (null == nickName || User.isWrongNickName(nickName)) {
			write(out, 400, "{\"error\":\"" + nickName + " is wrone\"}", "application/json; charset=utf-8", "UTF-8");
			return;
		} else if (ircServer.hasConnection(nickName)) {
			write(out, 409, "{\"error\":\"" + nickName + " is duplicate\"}", "application/json; charset=utf-8", "UTF-8");
			return;
		}
		User user = Database.getInstance().loadUser(nickName);
		if (user != null && (null == password || !user.getPasswordHashed().equals(User.toHash(password)))) {
			write(out, 403, "{\"error\":\"" + nickName + " has password\"}", "application/json; charset=utf-8", "UTF-8");
			return;
		}
		WebWorkerClientConnection clientConnection = new WebWorkerClientConnection(ircServer);
		if (user != null) {
			clientConnection.setNickName(user.getNickName());
			clientConnection.getUser().setHostName("webIF");
			clientConnection.getUser().setName(user.getName());
			clientConnection.getUser().setRealName(user.getRealName());
			clientConnection.getUser().setPasswordHashed(user.getPasswordHashed());
			clientConnection.getUser().setIconImage(user.getIconImage());
			clientConnection.getUser().setEmail(user.getEmail());
			clientConnection.getUser().setId(user.getId());
			clientConnection.getUser().setDescription(user.getDescription());
		} else {
			clientConnection.setNickName(nickName);
			clientConnection.getUser().setHostName("webIF");
			clientConnection.getUser().setName(nickName);
			clientConnection.getUser().setRealName(nickName);
			clientConnection.getUser().setPasswordHashed(User.toHash(password));

		}
		ircServer.putConnection(clientConnection);
		if (null != user) {
			List<String> loginChannels = Database.getInstance().loadJoinedChannelNames(user);
			for (String channelName : loginChannels) {
				ircServer.joinToChannel(clientConnection, channelName);
			}
		}

		String sessionId = UUID.randomUUID().toString();
		long loginTime = new Date().getTime();
		String sessionKey = AIRCResponse.createSessionKey(nickName, sessionId, loginTime);
		Map<String, Object> responseData = new HashMap<String, Object>();
		responseData.put("sessionId", sessionId);
		responseData.put("sessionKey", sessionKey);
		responseData.put("nickName", nickName);
		if (null != clientConnection.getUser().getIconImage()) {
			responseData.put("iconImage", user.getIconImage());
		}
		if (null != user) {
			List<String> channelNames = Database.getInstance().loadJoinedChannelNames(user);
			responseData.put("channelNames", channelNames);
		}
		write(out, 200, JSON.encode(responseData), "application/json; charset=utf-8", "UTF-8");
	}
}
