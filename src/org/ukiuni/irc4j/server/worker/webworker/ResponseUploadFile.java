package org.ukiuni.irc4j.server.worker.webworker;

import java.io.IOException;
import java.io.OutputStream;

import org.ukiuni.irc4j.Conf;
import org.ukiuni.irc4j.server.ClientConnection;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.server.ServerChannel;
import org.ukiuni.irc4j.storage.Storage;
import org.ukiuni.irc4j.storage.Storage.WriteHandle;
import org.ukiuni.lighthttpserver.request.ParameterFile;
import org.ukiuni.lighthttpserver.util.StreamUtil;

public class ResponseUploadFile extends AIRCResponse {

	public ResponseUploadFile(IRCServer ircServer) {
		super(ircServer);
	}

	@Override
	public void onResponseSecure(OutputStream out) throws Throwable {
		String channelName = getRequest().getParameter("channelName");
		ParameterFile file = getRequest().getParameterFile("file");
		if (null == file) {
			writeError(out, 400, "message must be not null or empty");
			return;
		}
		if (channelName.startsWith("#")) {
			if (null == channelName || !ircServer.hasChannel(channelName)) {
				writeError(out, 404, "channelNotFound");
				return;
			}
			WriteHandle writeHandle = registFIle(file);
			ServerChannel channel = ircServer.getChannel(channelName);
			String message = "file upload to " + Conf.getHttpServerURL() + "/file/" + writeHandle.getKey();
			channel.sendMessage("PRIVMSG", getAccessConnection(), message);
			getAccessConnection().sendMessage("PRIVMSG", getAccessConnection(), channel, message);
		} else {
			ClientConnection connection = ircServer.findConnection(channelName);
			if (null == connection) {
				writeError(out, 404, "user not found");
				return;
			}
			WriteHandle writeHandle = registFIle(file);
			String message = "file upload to " + Conf.getHttpServerURL() + "/file/" + writeHandle.getKey();
			connection.sendPrivateMessage("PRIVMSG", getAccessConnection(), message);
			getAccessConnection().sendPrivateSelfMessage("PRIVMSG", connection, message);
		}
		write(out, 201, "{\"status\":\"success\"}", "application/json; charset=utf-8", "UTF-8");
	}

	private WriteHandle registFIle(ParameterFile file) throws IOException {
		WriteHandle writeHandle = Storage.getInstance().createWriteHandle(file.getFileName(), file.getContentType());
		OutputStream writeHandleOut = writeHandle.getOutputStream();
		StreamUtil.copy(file.getInputStream(), writeHandleOut);
		writeHandle.save();
		return writeHandle;
	}
}
