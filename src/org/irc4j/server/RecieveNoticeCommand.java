package org.irc4j.server;

public class RecieveNoticeCommand extends RecievePrivmsgCommand {

	@Override
	public String getCommandParametersString() {
		return "NOTICE";
	}

}
