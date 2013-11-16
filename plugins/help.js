function getCommand() {
	return "HELP";
}
function execute(server, client) {
	client.sendPrivateCommand(server.getServerName() + " ----------");
	client.sendPrivateCommand("/FILEUPLOAD {channelName}");
	client.sendPrivateCommand("    upload file to channel.");
	client.sendPrivateCommand("    After send command, You can find AIR_IRC user on specify channel.");
	client.sendPrivateCommand("    You send file via DDC to AIR_IRC, file to be uploaded.");
	client.sendPrivateCommand("");
	client.sendPrivateCommand("/HISTORY {channelName} [count]");
	client.sendPrivateCommand("    It\'s Send you history of specify channel. [count] is amount of log.");
	client.sendPrivateCommand("");
	client.sendPrivateCommand("/WLOG {channelName} [count]");
	client.sendPrivateCommand("    You get URL to show log of specify channel. [count] is amount of log.");
	client.sendPrivateCommand("---------------");
}