import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.irc4j.IRCClient;
import org.irc4j.IRCEventHandler;

public class Main {
	public static void main(String[] args) throws Exception {
		String host = null;
		int port = 0;
		String nickName = null;
		String myHost = null;
		String realName = null;
		String channel = null;
		String message = null;
		if (args.length >= 7) {
			host = args[0];
			port = Integer.valueOf(args[1]).intValue();
			nickName = args[2];
			myHost = args[3];
			realName = args[4];
			channel = args[5];
			message = "";
			for (int i = 6; i < args.length; i++) {
				message += args[i];
				if (i < args.length - 1) {
					message += " ";
				}
			}
		} else if (args.length > 0 && args.length < 7) {

			System.out.println(args[4] + " " + args.length + " args [host, port, nickName, myHost, realName, channel, message]\nchannel name may be \"not\" starts with #");
			return;
		}
		IRCClient ircClient = new IRCClient(host, port, nickName, myHost, realName);
		ircClient.addHandler(new IRCEventHandler() {
			@Override
			public void onServerMessage(int id, String message) {
				System.out.println("onServerMessage(" + id + ", " + message + ")");
			}

			@Override
			public void onMessage(String channelName, String from, String message) {
				System.out.println("onMessage(" + channelName + "," + from + "," + message + ")");
			}

			@Override
			public void onError(Throwable e) {
				e.printStackTrace();
			}
		});
		ircClient.setDaemon(true);
		ircClient.connect();
		ircClient.sendJoin(channel);
		ircClient.sendMessage(channel, message);
		// some irc server wait some second for complete message;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String line;
		while(!"".equals(line = in.readLine())){
			ircClient.sendMessage(channel, line);
		}
		ircClient.sendQuit();
		
	}
}
