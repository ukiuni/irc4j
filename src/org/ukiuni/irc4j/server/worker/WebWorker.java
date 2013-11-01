package org.ukiuni.irc4j.server.worker;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.arnx.jsonic.JSON;

import org.ukiuni.irc4j.Conf;
import org.ukiuni.irc4j.Log;
import org.ukiuni.irc4j.db.Database;
import org.ukiuni.irc4j.entity.Message;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.util.CipherUtil;
import org.ukiuni.lighthttpserver.HttpServer;
import org.ukiuni.lighthttpserver.response.Response;

public class WebWorker implements Worker {

	private HttpServer httpServer;
	private final File baseDir = null == System.getenv("WEB_BASE_PATH") ? new File("./webContents") : new File(System.getenv("WEB_BASE_PATH"));

	@Override
	public void work(IRCServer ircServer) {
		Log.log("webWorker start basedir = " + baseDir.getAbsolutePath());
		httpServer = new HttpServer(Conf.getHttpServerPort());
		httpServer.getDefaultHandler().addStaticBaseDir(baseDir);
		httpServer.getDefaultHandler().addResponse("/logs", new Response() {
			@Override
			public void onResponse(OutputStream out) throws Throwable {
				String key = getRequest().getParameter("k");
				String[] param = CipherUtil.decode(key).split(" ");
				String channelName = param[0];
				long maxId = Long.valueOf(param[1]);
				int limit = Integer.valueOf(param[2]);
				String user = param[3];
				long dateLong = Long.valueOf(param[4]);
				List<Message> messageList = Database.getInstance().loadMessage(channelName, maxId, limit);
				Map<String, Object> responseMap = new HashMap<String, Object>();
				if (!messageList.isEmpty()) {
					responseMap.put("date", dateLong);
					responseMap.put("user", user);
					responseMap.put("channelName", channelName);
					responseMap.put("logs", messageList);
					write(out, 200, JSON.encode(responseMap), "application/json; charset=utf-8", "UTF-8");
				} else {
					write(out, 400, "error", "application/json; charset=utf-8", "UTF-8");
				}
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
