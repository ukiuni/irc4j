function getCommand() {
	return "HELLO";
}
function execute(server, client, params) {
	client.sendPrivateCommand(server.getServerName() + " ----------");
	client.sendPrivateCommand("Hello " + client.getNickName() + " !!!");
	for ( var i in params) {
		client.sendPrivateCommand("    " + params[i]);
	}
	client.sendPrivateCommand("---------------");
}