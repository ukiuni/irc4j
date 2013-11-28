package org.ukiuni.irc4j.server.worker.webworker;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.arnx.jsonic.JSON;

import org.ukiuni.irc4j.Channel;
import org.ukiuni.irc4j.User;
import org.ukiuni.irc4j.db.Database;
import org.ukiuni.irc4j.entity.Message;
import org.ukiuni.irc4j.server.IRCServer;

public class ResponseRejoin extends AIRCResponse {

	public ResponseRejoin(IRCServer ircServer) {
		super(ircServer);
	}

	@Override
	public void onResponseSecure(OutputStream out) throws Throwable {
		WebWorkerClientConnection clientConnection = getAccessConnection();
		if (null != clientConnection) {
			write(out, 200, "{\"status\":\"aleady join\"}", "application/json; charset=utf-8", "UTF-8");
			return;
		}
		String channelNames = getRequest().getParameter("channelNames");
		if (null == channelNames || null == getNickName()) {
			write(out, 400, "{\"status\":\"wrong channelName\"}", "application/json; charset=utf-8", "UTF-8");
			return;
		}
		User user = Database.getInstance().loadUser(getNickName());
		if (null == user) {
			clientConnection = new WebWorkerClientConnection(ircServer);
			clientConnection.setNickName(getNickName());
			clientConnection.getUser().setHostName("webIF");
			clientConnection.getUser().setName(getNickName());
			clientConnection.getUser().setRealName(getNickName());
		} else {
			if (user.getId() != getUserId()) {
				write(out, 403, "{\"status\":\"wrong UserId\"}", "application/json; charset=utf-8", "UTF-8");
				return;
			}
			clientConnection = new WebWorkerClientConnection(ircServer);
			clientConnection.setNickName(getNickName());
			clientConnection.getUser().setHostName("webIF");
			clientConnection.getUser().setRealName(user.getRealName());
			clientConnection.getUser().setCreatedAt(user.getCreatedAt());
			clientConnection.getUser().setDescription(user.getDescription());
			clientConnection.getUser().setEmail(user.getEmail());
			clientConnection.getUser().setIconImage(user.getIconImage());
			clientConnection.getUser().setName(user.getName());
			clientConnection.getUser().setId(user.getId());
			clientConnection.getUser().setNickName(user.getNickName());
			clientConnection.getUser().setPasswordHashed(user.getPasswordHashed());
			clientConnection.getUser().setNotify(user.isNotify());
			clientConnection.getUser().setNotificationKeyword(user.getNotificationKeyword());
			clientConnection.getUser().setRealName(user.getRealName());
			clientConnection.getUser().setUpdatedAt(user.getUpdatedAt());
		}
		ircServer.putConnection(clientConnection);
		String[] channelNameArray = channelNames.split(",");
		for (String channelName : channelNameArray) {
			if (channelName.startsWith("#")) {
				if (Channel.wrongName(channelName)) {
					write(out, 400, "{\"status\":\"wrong channelName\"}", "application/json; charset=utf-8", "UTF-8");
					return;
				}
				// TODO password
				ircServer.joinToChannel(clientConnection, channelName);
			}
		}
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			long maxMessageId = Long.parseLong(getRequest().getParameter("maxMessageId"));
			Map<String, List<Message>> messageMap = new HashMap<String, List<Message>>();
			for (String channelName : channelNameArray) {
				messageMap.put(channelName, Database.getInstance().loadMessageNewerThan(channelName, maxMessageId, Integer.MAX_VALUE));
			}
			returnMap.put("messages", messageMap);
		} catch (Exception e) {
		}
		returnMap.put("status", "rejoined");
		write(out, 200, JSON.encode(returnMap), "application/json; charset=utf-8", "UTF-8");
	}
}
