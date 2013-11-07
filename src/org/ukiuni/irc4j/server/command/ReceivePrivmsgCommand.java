package org.ukiuni.irc4j.server.command;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import org.ukiuni.irc4j.Conf;
import org.ukiuni.irc4j.IRCEventHandler;
import org.ukiuni.irc4j.server.ClientConnection;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.server.ServerChannel;
import org.ukiuni.irc4j.server.ServerCommand;
import org.ukiuni.irc4j.storage.Storage;
import org.ukiuni.irc4j.storage.Storage.WriteHandle;
import org.ukiuni.lighthttpserver.util.FileUtil;

public class ReceivePrivmsgCommand extends ServerCommand {

	@Override
	public void execute(IRCServer ircServer, ClientConnection selfClientConnection, List<IRCEventHandler> handlers) throws Throwable {
		String[] targets = getCommandParameters()[0].split(",");
		String message = getCommandParametersString().split(" ", 2)[1];
		if (message.startsWith(":")) {
			message = message.substring(1);
		}
		for (String target : targets) {
			if (target.startsWith("#")) {
				ServerChannel channel = ircServer.getChannel(target);
				if (channel == null) {
					selfClientConnection.sendPrivateCommand("No such channel " + target);
				} else if (!channel.joins(selfClientConnection)) {
					selfClientConnection.sendPrivateCommand("You are not joined to " + target);
				} else {
					channel.sendMessage(getCommandString(), selfClientConnection, message);
				}
			} else if (target.equals(ircServer.getServerName())) {
				String parameterString = getCommandParametersString().replace(new String(new char[] { new Character((char) 1).charValue() }), "");
				if (parameterString.startsWith(ircServer.getServerName() + " :DCC")) {
					String[] param = parameterString.split(" ");
					System.out.println("/////////////////// socket = " + param[4] + ":" + Integer.valueOf(param[5]));
					Socket socket = new Socket("localhost", Integer.valueOf(param[5]));
					long fileSize = Long.valueOf(Integer.valueOf(param[6]));
					InputStream in = socket.getInputStream();
					byte[] buffer = new byte[1024];
					long totalReaded = 0;
					int readed = in.read(buffer);
					WriteHandle writeHandle = Storage.getInstance().createWriteHandle(param[3], FileUtil.getMimeType(param[6]));
					OutputStream out = writeHandle.getOutputStream();
					while (totalReaded < fileSize && readed > 0) {
						out.write(buffer, 0, readed);
						totalReaded += readed;
						if (totalReaded + buffer.length > fileSize) {
							buffer = new byte[(int) (fileSize - totalReaded)];
						}
						readed = in.read(buffer);
					}
					socket.close();
					out.close();
					writeHandle.save();
					System.out.println("/////////////////// upload complete");
					ServerChannel channel = selfClientConnection.getCurrentFileUploadChannel();
					selfClientConnection.setCurrentFileUploadChannel(null);
					String responseMessage = "file upload to " + Conf.getHttpServerURL() + "/file?k=" + writeHandle.getKey();
					channel.sendMessage("PRIVMSG", selfClientConnection, responseMessage);
					selfClientConnection.sendMessage("PRIVMSG", selfClientConnection, channel, responseMessage);
					selfClientConnection.sendPartCommand(ircServer.getFQSN(), channel.getName());// TODO
				}
			} else {
				ClientConnection clientConnection = ircServer.findConnection(target);
				if (clientConnection == null) {
					selfClientConnection.sendPrivateCommand("The user " + target + " is not online.");
				} else {
					clientConnection.sendPrivateMessage(getCommandString(), selfClientConnection, message);
				}
			}
		}
	}

	protected String getCommandString() {
		return "PRIVMSG";
	}
}
