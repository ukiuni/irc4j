package org.ukiuni.irc4j.server.worker.webworker;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.arnx.jsonic.JSON;

import org.ukiuni.irc4j.User;
import org.ukiuni.irc4j.db.Database;
import org.ukiuni.irc4j.entity.Message;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.server.ServerChannel;

public class ResponseLoadMessage extends AIRCResponse {

	public ResponseLoadMessage(IRCServer ircServer) {
		super(ircServer);
	}

	@Override
	public void onResponseSecure(OutputStream out) throws Throwable {
		String channelName = getRequest().getParameter("channelName");
		String limitString = getRequest().getParameter("limit");
		int limit;
		try {
			limit = Integer.valueOf(limitString);
		} catch (NumberFormatException e) {
			limit = 10;
		}
		List<Message> messages;
		List<String> userNameList;
		if (channelName.startsWith("#")) {
			if (null == channelName || !ircServer.hasChannel(channelName)) {
				writeError(out, 404, "channelNotFound");
				return;
			}
			ServerChannel channel = ircServer.getChannel(channelName);
			List<User> userList = channel.getCurrentUserList();
			userNameList = new ArrayList<String>(userList.size());
			for (User user : userList) {
				userNameList.add(user.getNickName());
			}
			if (null != getRequest().getParameter("olderThan") && null != getRequest().getParameter("newerThan")) {
				long olderThan = Long.valueOf(getRequest().getParameter("olderThan"));
				long newerThan = Long.valueOf(getRequest().getParameter("newerThan"));
				messages = Database.getInstance().loadMessageBetween(channelName, olderThan, newerThan, limit);
			} else if (null != getRequest().getParameter("olderThan")) {
				long olderThan = Long.valueOf(getRequest().getParameter("olderThan"));
				messages = Database.getInstance().loadMessageOlderThan(channelName, olderThan, limit);
			} else if (null != getRequest().getParameter("newerThan")) {
				long newerThan = Long.valueOf(getRequest().getParameter("newerThan"));
				messages = Database.getInstance().loadMessageNewerThan(channelName, newerThan, limit);
			} else {
				messages = Database.getInstance().loadMessage(channelName, limit, false);
			}
		} else {
			// TODO roadPrivate message? is still regist;
			messages = Collections.emptyList();
			userNameList = Arrays.asList(channelName);
		}
		Map<String, Object> responseMap = new HashMap<String, Object>();
		responseMap.put("users", userNameList);
		responseMap.put("messages", messages);
		write(out, 200, JSON.encode(responseMap), "application/json; charset=utf-8", "UTF-8");
	}
}
