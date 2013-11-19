package org.ukiuni.irc4j.server.worker.webworker;

import java.io.OutputStream;
import java.util.List;

import net.arnx.jsonic.JSON;

import org.ukiuni.irc4j.db.Database;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.server.plugin.Plugin;

public class ResponsePluginMyList extends AIRCResponse {

	public ResponsePluginMyList(IRCServer ircServer) {
		super(ircServer);
	}

	@Override
	public void onResponseSecure(OutputStream out) throws Throwable {
		long userId = getAccessConnection().getUser().getId();
		if (0 == userId) {
			write(out, 403, "{\"message\":\"You must regist account before get plugin.\"}", "application/json; charset=utf-8", "UTF-8");
			return;
		}
		List<Plugin> pluginList = Database.getInstance().loadPlugin(userId);
		write(out, 200, JSON.encode(pluginList), "application/json; charset=utf-8", "UTF-8");
	}
}
