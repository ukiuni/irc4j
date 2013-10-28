package test.org.irc4j;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.irc4j.IRCClient;
import org.irc4j.IRCEventAdapter;
import org.irc4j.server.IRCServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestAuthodocs {
	IRCServer ircServer;

	@Before
	public void setupServer() throws IOException {
		ircServer = new IRCServer();
		ircServer.start();
	}

	@After
	public void stopServer() throws IOException {
		if (null != ircServer) {
			ircServer.stop();
		}
	}

	@Test
	public void test() throws Exception {
		final Map<String, Object> resultMap = new HashMap<String, Object>();
		IRCClient client1 = new IRCClient();
		IRCClient client2 = new IRCClient();
		client2.addHandler(new IRCEventAdapter() {
			@Override
			public void onMessage(String channelName, String from, String message) {
				resultMap.put("channel", channelName);
				resultMap.put("from", from);
				resultMap.put("message", message);
			}
		});
		client1.setHost("localhost");
		client1.setPort(6667);
		client1.setNickName("client1NickName");
		client1.setRealName("client1RealName");
		client2.setHost("localhost");
		client2.setPort(6667);
		client2.setNickName("client2NickName");
		client2.setRealName("client2RealName");
		client1.connect();
		client2.connect();
		client2.sendJoin("#testChannel");
		client1.sendJoin("#testChannel");
		client1.sendMessage("#testChannel", "testMessage");
		Thread.sleep(1000);
		assertEquals("#testChannel", resultMap.get("channel"));
		assertEquals("client1NickName", resultMap.get("from"));
		assertEquals("testMessage", resultMap.get("message"));
		client1.sendQuit();
		client2.sendQuit();
	}
	@Test
	public void testPrivateMessage() throws Exception {
		final Map<String, Object> resultMap = new HashMap<String, Object>();
		IRCClient client1 = new IRCClient();
		IRCClient client2 = new IRCClient();
		client2.addHandler(new IRCEventAdapter() {
			@Override
			public void onMessage(String channelName, String from, String message) {
				resultMap.put("channel", channelName);
				resultMap.put("from", from);
				resultMap.put("message", message);
			}
		});
		client1.setHost("localhost");
		client1.setPort(6667);
		client1.setNickName("client1NickName");
		client1.setRealName("client1RealName");
		client2.setHost("localhost");
		client2.setPort(6667);
		client2.setNickName("client2NickName");
		client2.setRealName("client2RealName");
		client1.connect();
		client2.connect();
		client2.sendJoin("#testChannel");
		client1.sendJoin("#testChannel");
		client1.sendMessage("client2NickName", "testMessage");
		Thread.sleep(1000);
		assertEquals("client2NickName", resultMap.get("channel"));
		assertEquals("client1NickName", resultMap.get("from"));
		assertEquals("testMessage", resultMap.get("message"));
		client1.sendQuit();
		client2.sendQuit();
	}

}
