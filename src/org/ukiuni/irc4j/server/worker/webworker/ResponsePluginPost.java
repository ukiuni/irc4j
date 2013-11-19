package org.ukiuni.irc4j.server.worker.webworker;

import java.io.OutputStream;

import net.arnx.jsonic.JSON;

import org.ukiuni.irc4j.Log;
import org.ukiuni.irc4j.db.Database;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.server.plugin.Plugin;
import org.ukiuni.irc4j.server.plugin.Plugin.Status;
import org.ukiuni.irc4j.server.plugin.Plugin.Type;
import org.ukiuni.irc4j.server.plugin.PluginFactory;

public class ResponsePluginPost extends AIRCResponse {

	public ResponsePluginPost(IRCServer ircServer) {
		super(ircServer);
	}

	@Override
	public void onResponseSecure(OutputStream out) throws Throwable {
		long id = 0;
		try {
			id = Long.valueOf(getRequest().getParameter("id"));
		} catch (Exception e) {
			// Do nothing.
		}
		String name = getRequest().getParameter("name");
		String command = getRequest().getParameter("command");
		String description = getRequest().getParameter("description");
		String script = getRequest().getParameter("script");
		String effective = getRequest().getParameter("effective");
		if (null == name || "".equals(name.trim())) {
			writeError(out, 400, "name must be not null or empty");
			return;
		}
		if (null == command || "".equals(command.trim())) {
			writeError(out, 400, "command must be not null or empty");
			return;
		}
		if (null == description || "".equals(description.trim())) {
			writeError(out, 400, "description must be not null or empty");
			return;
		}
		if (null == script || "".equals(script.trim())) {
			writeError(out, 400, "description must be not null or empty");
			return;
		}
		if (0 == getAccessConnection().getUser().getId()) {
			writeError(out, 403, "first setting account");
			return;
		}
		Plugin plugin = new Plugin();
		if (0 != id) {
			plugin.setId(id);
		}
		plugin.setCommand(command);
		plugin.setCreatedUserId(getAccessConnection().getUser().getId());
		plugin.setDescription(description);
		plugin.setEngineName("javascript");
		plugin.setName(name);
		plugin.setScript(script);
		if (null == effective) {
			plugin.setStatus(Status.STOPED);
		} else {
			plugin.setStatus(Status.MOVING);
		}
		plugin.setType(Type.COMMAND);
		try {
			Database.getInstance().regist(plugin);
		} catch (Throwable e) {
			Log.log("Error on Regist. may be duplicate Name", e);
			write(out, 409, JSON.encode(plugin), "application/json; charset=utf-8", "UTF-8");
		}
		write(out, 201, JSON.encode(plugin), "application/json; charset=utf-8", "UTF-8");
		PluginFactory.getInstance().flush();
		PluginFactory.getInstance();// init
	}
}
