package org.ukiuni.irc4j.server.command;

import java.io.IOException;
import java.util.List;

import org.ukiuni.irc4j.Conf;
import org.ukiuni.irc4j.IRCEventHandler;
import org.ukiuni.irc4j.Log;
import org.ukiuni.irc4j.server.ClientConnection;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.server.ServerChannel;
import org.ukiuni.irc4j.server.ServerCommand;
import org.ukiuni.irc4j.server.command.FileRecieveThread.OnCompleteListener;

public class ReceivePrivmsgCommand extends ServerCommand {

	@Override
	public void execute(final IRCServer ircServer, final ClientConnection selfClientConnection, List<IRCEventHandler> handlers) throws Throwable {
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
					final String[] param = parameterString.split(" ");
					String fileName = param[3];
					int portNum = Integer.valueOf(param[5]);
					long fileSize = Long.valueOf(param[6]);
					final ServerChannel channel = selfClientConnection.getCurrentFileUploadChannel();
					new FileRecieveThread(selfClientConnection.getUser().getHostName(), portNum, fileName, fileSize, new OnCompleteListener() {
						@Override
						public void onComplete(String uploadedUri) {
							try {
								selfClientConnection.setCurrentFileUploadChannel(null);
								String responseMessage = "file upload to " + Conf.getHttpServerURL() + uploadedUri;
								channel.sendMessage("PRIVMSG", selfClientConnection, responseMessage);
								selfClientConnection.sendMessage("PRIVMSG", selfClientConnection, channel, responseMessage);
								selfClientConnection.sendPartCommand(ircServer.getFQSN(), channel.getName(), "upload is completed.");
							} catch (IOException e) {
								Log.log("fail after upload", e);
								try {
									selfClientConnection.sendMessage("PRIVMSG", selfClientConnection, channel, ":uploaded. but have some fault " + e);
								} catch (IOException e1) {
								}
							}
						}

						@Override
						public void onError(Throwable e) {
							Log.log("Upload fault", e);
							try {
								selfClientConnection.sendMessage("PRIVMSG", selfClientConnection, channel, ":uploaded. but have some fault " + e);
							} catch (IOException e1) {
								Log.log(e);
							}
						}
					}).start();
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
