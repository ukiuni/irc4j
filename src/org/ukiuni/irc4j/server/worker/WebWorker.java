package org.ukiuni.irc4j.server.worker;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

import org.ukiuni.irc4j.Conf;
import org.ukiuni.irc4j.Log;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.server.worker.webworker.ResponseFile;
import org.ukiuni.irc4j.server.worker.webworker.ResponseJoin;
import org.ukiuni.irc4j.server.worker.webworker.ResponseListenEvent;
import org.ukiuni.irc4j.server.worker.webworker.ResponseLoadMessage;
import org.ukiuni.irc4j.server.worker.webworker.ResponseLogin;
import org.ukiuni.irc4j.server.worker.webworker.ResponseLogs;
import org.ukiuni.irc4j.server.worker.webworker.ResponsePart;
import org.ukiuni.irc4j.server.worker.webworker.ResponsePostMessage;
import org.ukiuni.irc4j.server.worker.webworker.ResponseRejoin;
import org.ukiuni.lighthttpserver.HttpServer;
import org.ukiuni.lighthttpserver.request.DefaultHandler;
import org.ukiuni.lighthttpserver.request.Request;
import org.ukiuni.lighthttpserver.response.Response;

public class WebWorker implements Worker {

	private HttpServer httpServer;
	private final File baseDir = null == System.getenv("WEB_BASE_PATH") ? new File("./webContents") : new File(System.getenv("WEB_BASE_PATH"));

	@Override
	public void work(final IRCServer ircServer) {
		Log.log("webWorker start basedir = " + baseDir.getAbsolutePath());
		httpServer = new HttpServer(Conf.getHttpServerPort());
		httpServer.setExecutorService(Executors.newCachedThreadPool());
		httpServer.setHandler(new DefaultHandler() {
			@Override
			public Response onRequest(Request request) {
				if ("/logs".equals(request.getPath())) {
					return new ResponseLogs();
				} else if ("/listenEvent".equals(request.getPath())) {
					return new ResponseListenEvent(ircServer);
				} else if (request.getPath().startsWith("/file/") && !request.getPath().contains("../")) {
					return new ResponseFile();
				} else if ("/channel/post".equals(request.getPath())) {
					return new ResponsePostMessage(ircServer);
				} else if ("/rejoin".equals(request.getPath())) {
					return new ResponseRejoin(ircServer);
				} else if ("/login".equals(request.getPath())) {
					return new ResponseLogin(ircServer);
				} else if ("/channel/join".equals(request.getPath())) {
					return new ResponseJoin(ircServer);
				} else if ("/channel/part".equals(request.getPath())) {
					return new ResponsePart(ircServer);
				} else if ("/channel/message".equals(request.getPath())) {
					return new ResponseLoadMessage(ircServer);
				}
				return super.onRequest(request);
			}
		});
		httpServer.getDefaultHandler().addStaticBaseDir(baseDir);
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
