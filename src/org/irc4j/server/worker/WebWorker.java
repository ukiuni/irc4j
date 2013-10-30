package org.irc4j.server.worker;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import net.arnx.jsonic.JSON;

import org.irc4j.Log;
import org.irc4j.db.Database;
import org.irc4j.entity.Message;
import org.irc4j.server.IRCServer;
import org.irc4j.util.CipherUtil;
import org.ukiuni.lighthttpserver.HttpServer;
import org.ukiuni.lighthttpserver.response.Response;

public class WebWorker implements Worker {

	private HttpServer httpServer;
	private final File baseDir = null == System.getenv("WEB_BASE_PATH") ? new File("./webContents") : new File(System.getenv("WEB_BASE_PATH"));

	@Override
	public void work(IRCServer ircServer) {
		Log.log("webWorker start basedir = " + baseDir.getAbsolutePath());
		httpServer = new HttpServer(1080);
		httpServer.getDefaultHandler().addResponseAll(baseDir);
		httpServer.getDefaultHandler().addResponse("/logs", new Response() {
			@Override
			public void onResponse(OutputStream out) throws Throwable {
				String key = getRequest().getParameter("k");
				String[] param = CipherUtil.decode(key).split(" ");
				String channel = param[0];
				long maxId = Long.valueOf(param[1]);
				int limit = Integer.valueOf(param[2]);
				List<Message> messageList = Database.getInstance().loadMessage(channel, maxId, limit);
				write(out, 200, JSON.encode(messageList), "application/json; charset=utf-8", "UTF-8");
			}
		});
		try {
			httpServer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		try {
			httpServer.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
