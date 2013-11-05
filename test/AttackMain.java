import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ukiuni.irc4j.IRCClient;

public class AttackMain {
	public static void main(String[] args) throws Exception {
		List<IRCClient> clientList = new ArrayList<IRCClient>();

		for (int j = 0; j < 3; j++) {
			for (int i = 0; i < 200; i++) {
				IRCClient client = createClient("client" + j + "%" + i + "NickName", "client" + i + "RealName");
				clientList.add(client);
			}
			Thread.sleep(3000);
			for (IRCClient ircClient : clientList) {
				try {
					ircClient.sendJoin("#testChannel");
					// ircClient.sendJoin("#gasgas");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Thread.sleep(3000);
			for (final IRCClient ircClient : clientList) {
				new Thread() {
					public void run() {
						for (int i = 0; i < 3000; i++) {
							try {
								ircClient.sendMessage("#testChannel", "I am " + ircClient.getNickName() + " and index " + i);
								Thread.sleep(500);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					};
				}.start();
			}
			Thread.sleep(10000);
			for (final IRCClient ircClient : clientList) {

				try {
					ircClient.sendQuit();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static IRCClient createClient(String nickName, String realName) throws IOException {
		IRCClient client1 = new IRCClient();
		client1.setHost("localhost");
		client1.setPort(6667);
		client1.setNickName(nickName);
		client1.setRealName(realName);
		client1.connect();
		return client1;
	}
}
