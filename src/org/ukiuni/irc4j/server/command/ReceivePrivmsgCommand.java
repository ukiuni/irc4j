package org.ukiuni.irc4j.server.command;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;

import org.ukiuni.irc4j.IRCEventHandler;
import org.ukiuni.irc4j.server.ClientConnection;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.server.ServerChannel;
import org.ukiuni.irc4j.server.ServerCommand;

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
				if (getCommandParametersString().startsWith(ircServer.getServerName() + " :" + new Character((char) 1) + "DCC")) {
					System.out.println("*********** socket = " + getCommandParameters()[4] + ":" + Integer.valueOf(getCommandParameters()[5]));
					Socket socket = new Socket("localhost", Integer.valueOf(getCommandParameters()[5]));
					long fileSize = Long.valueOf(Integer.valueOf(getCommandParameters()[5]));
					InputStream in = socket.getInputStream();
					byte[] buffer = new byte[1024];
					long totalReaded = 0;
					int readed = in.read(buffer);
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					while (totalReaded < fileSize && readed > 0) {
						out.write(buffer, 0, readed);
						totalReaded += readed;
						if (totalReaded + buffer.length > fileSize) {
							buffer = new byte[(int) (fileSize - totalReaded)];
						}
						readed = in.read(buffer);
					}
					socket.close();
					System.out.println("///////////////////");
					System.out.println(new String(out.toByteArray()));
					System.out.println("///////////////////");
					selfClientConnection.sendPartCommand(ircServer.getFQSN(), "#home");// TODO
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
