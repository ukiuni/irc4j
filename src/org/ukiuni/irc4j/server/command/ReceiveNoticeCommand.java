package org.ukiuni.irc4j.server.command;

public class ReceiveNoticeCommand extends ReceivePrivmsgCommand {

	@Override
	public String getCommandParametersString() {
		return "NOTICE";
	}

}
