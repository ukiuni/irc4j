package test.org.irc4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ukiuni.irc4j.IRCClient;
import org.ukiuni.irc4j.IRCEventAdapter;
import org.ukiuni.irc4j.server.IRCServer;

public class TestAuthodocs {
	IRCServer ircServer;

	@Before
	public void setupServer() throws IOException {
		ircServer = new IRCServer();
		ircServer.start();
	}

	@After
	public void stopServer() throws IOException, InterruptedException {
		if (null != ircServer) {
			ircServer.stop();
		}
		Thread.sleep(2000);
	}

	@Test
	public void test() throws Exception {
		final Map<String, Object> resultMap = new HashMap<String, Object>();
		IRCClient client1 = createClient("client1NickName", "client1RealName");
		IRCClient client2 = createClient("client2NickName", "client2RealName");
		client2.addHandler(new IRCEventAdapter() {
			@Override
			public void onMessage(String channelName, String from, String message) {
				resultMap.put("channel", channelName);
				resultMap.put("from", from);
				resultMap.put("message", message);
			}
		});
		client2.sendJoin("#testChannel");
		client1.sendJoin("#testChannel");
		Thread.sleep(1000);
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

		IRCClient client1 = createClient("client1NickName", "client1RealName");
		IRCClient client2 = createClient("client2NickName", "client2RealName");
		client2.addHandler(new IRCEventAdapter() {
			@Override
			public void onMessage(String channelName, String from, String message) {
				resultMap.put("channel", channelName);
				resultMap.put("from", from);
				resultMap.put("message", message);
			}
		});
		client2.sendJoin("#testChannel");
		client1.sendJoin("#testChannel");
		Thread.sleep(1000);
		client1.sendMessage("client2NickName", "testMessage");
		Thread.sleep(1000);
		assertEquals("client2NickName", resultMap.get("channel"));
		assertEquals("client1NickName", resultMap.get("from"));
		assertEquals("testMessage", resultMap.get("message"));
		client1.sendQuit();
		client2.sendQuit();
	}

	@Test
	public void testThree() throws Exception {
		final Map<String, Object> resultMap = new HashMap<String, Object>();
		IRCClient client1 = createClient("client1NickName", "client1RealName");
		IRCClient client2 = createClient("client2NickName", "client2RealName");
		IRCClient client3 = createClient("client3NickName", "client3RealName");
		client2.addHandler(new IRCEventAdapter() {
			@Override
			public void onMessage(String channelName, String from, String message) {
				resultMap.put("channel2", channelName);
				resultMap.put("from2", from);
				resultMap.put("message2", message);
			}
		});
		client3.addHandler(new IRCEventAdapter() {
			@Override
			public void onMessage(String channelName, String from, String message) {
				resultMap.put("channel3", channelName);
				resultMap.put("from3", from);
				resultMap.put("message3", message);
			}
		});
		client1.sendJoin("#testChannel");
		client2.sendJoin("#testChannel");
		client3.sendJoin("#testChannel");
		Thread.sleep(1000);
		client1.sendMessage("#testChannel", "testMessage");
		Thread.sleep(2000);
		assertEquals("#testChannel", resultMap.get("channel2"));
		assertEquals("client1NickName", resultMap.get("from2"));
		assertEquals("testMessage", resultMap.get("message2"));
		assertEquals("#testChannel", resultMap.get("channel3"));
		assertEquals("client1NickName", resultMap.get("from3"));
		assertEquals("testMessage", resultMap.get("message3"));
		client1.sendQuit();
		client2.sendQuit();
		client3.sendQuit();
	}
	@Test
	public void testOtherChannelDontRecieveMessage() throws Exception {
		final Map<String, Object> resultMap = new HashMap<String, Object>();
		IRCClient client1 = createClient("client1NickName", "client1RealName");
		IRCClient client2 = createClient("client2NickName", "client2RealName");
		IRCClient client3 = createClient("client3NickName", "client3RealName");
		client2.addHandler(new IRCEventAdapter() {
			@Override
			public void onMessage(String channelName, String from, String message) {
				resultMap.put("channel2", channelName);
				resultMap.put("from2", from);
				resultMap.put("message2", message);
			}
		});
		client3.addHandler(new IRCEventAdapter() {
			@Override
			public void onMessage(String channelName, String from, String message) {
				resultMap.put("channel3", channelName);
				resultMap.put("from3", from);
				resultMap.put("message3", message);
			}
		});
		client1.sendJoin("#testChannel");
		client2.sendJoin("#testChannel");
		client3.sendJoin("#otherTestChannel");
		Thread.sleep(1000);
		client1.sendMessage("#testChannel", "testMessage");
		Thread.sleep(2000);
		assertEquals("#testChannel", resultMap.get("channel2"));
		assertEquals("client1NickName", resultMap.get("from2"));
		assertEquals("testMessage", resultMap.get("message2"));
		assertNull("#testChannel", resultMap.get("channel3"));
		assertNull("client1NickName", resultMap.get("from3"));
		assertNull("testMessage", resultMap.get("message3"));
		client1.sendQuit();
		client2.sendQuit();
		client3.sendQuit();
	}

	@Test
	public void testNotRecieveIfJoinOtherChannel() throws Exception {
		final Map<String, Object> resultMap = new HashMap<String, Object>();
		IRCClient client1 = createClient("client1NickName", "client1RealName");
		IRCClient client2 = createClient("client2NickName", "client2RealName");
		IRCClient client3 = createClient("client3NickName", "client3RealName");
		client1.addHandler(new IRCEventAdapter() {
			@Override
			public void onServerMessage(int id, String message) {
				resultMap.put("id", id);
				resultMap.put("message1", message);
			}
		});
		client2.addHandler(new IRCEventAdapter() {
			@Override
			public void onMessage(String channelName, String from, String message) {
				resultMap.put("channel2", channelName);
				resultMap.put("from2", from);
				resultMap.put("message2", message);
			}
		});
		client3.addHandler(new IRCEventAdapter() {
			@Override
			public void onMessage(String channelName, String from, String message) {
				resultMap.put("channel3", channelName);
				resultMap.put("from3", from);
				resultMap.put("message3", message);
			}
		});
		client1.sendJoin("#testChannel");
		client2.sendJoin("#testChannel");
		client3.sendJoin("#otherTestChannel");
		client1.sendMessage("#testChannel", "testMessage");
		Thread.sleep(1000);
		assertEquals("#testChannel", resultMap.get("channel2"));
		assertEquals("client1NickName", resultMap.get("from2"));
		assertEquals("testMessage", resultMap.get("message2"));
		assertNull("#otherChannel", resultMap.get("channel3"));
		assertNull("client1NickName3", resultMap.get("from3"));
		assertNull("testMessage3", resultMap.get("message3"));
		client1.sendQuit();
		client2.sendQuit();
		client3.sendQuit();
	}

	@Test
	public void testNotRecieveIfClose() throws Exception {
		final Map<String, Object> resultMap = new HashMap<String, Object>();
		IRCClient client1 = createClient("client1NickName", "client1RealName");
		IRCClient client2 = createClient("client2NickName", "client2RealName");
		IRCClient client3 = createClient("client3NickName", "client3RealName");
		resultMap.put("352Count", 0);

		client1.addHandler(new IRCEventAdapter() {
			@Override
			public void onServerMessage(int id, String message) {
				resultMap.put("id", id);
				resultMap.put("message1", message);
				if (352 == id) {
					resultMap.put("352Count", ((Integer) resultMap.get("352Count")) + 1);
				}
			}
		});
		client2.addHandler(new IRCEventAdapter() {
			@Override
			public void onMessage(String channelName, String from, String message) {
				resultMap.put("channel2", channelName);
				resultMap.put("from2", from);
				resultMap.put("message2", message);
			}
		});
		client3.addHandler(new IRCEventAdapter() {
			@Override
			public void onMessage(String channelName, String from, String message) {
				resultMap.put("channel3", channelName);
				resultMap.put("from3", from);
				resultMap.put("message3", message);
			}
		});
		client1.sendJoin("#testChannel");
		client2.sendJoin("#testChannel");
		client3.sendJoin("#testChannel");

		Thread.sleep(3000);
		client1.sendWho("#testChannel");
		Thread.sleep(1000);
		client3.sendQuit();
		Thread.sleep(2000);
		assertEquals(3, ((Integer) resultMap.get("352Count")).intValue());
		client1.sendMessage("#testChannel", "testMessage");
		Thread.sleep(1000);
		client1.sendWho("#testChannel");
		Thread.sleep(1000);
		assertEquals(5, ((Integer) resultMap.get("352Count")).intValue());
		assertEquals("#testChannel", resultMap.get("channel2"));
		assertEquals("client1NickName", resultMap.get("from2"));
		assertEquals("testMessage", resultMap.get("message2"));
		assertNull("#otherChannel", resultMap.get("channel3"));
		assertNull("client1NickName3", resultMap.get("from3"));
		assertNull("testMessage3", resultMap.get("message3"));
		client1.sendQuit();
		client2.sendQuit();
	}

	private IRCClient createClient(String nickName, String realName) throws IOException {
		IRCClient client1 = new IRCClient();
		client1.setHost("localhost");
		client1.setPort(6667);
		client1.setNickName(nickName);
		client1.setRealName(realName);
		client1.connect();
		return client1;
	}

	@Test
	public void testHistory() throws Exception {
		final Map<String, Object> resultMap = new HashMap<String, Object>();
		IRCClient client1 = createClient("client1NickName", "client1RealName");
		IRCClient client2 = createClient("client2NickName", "client2RealName");
		client2.addHandler(new IRCEventAdapter() {
			@Override
			public void onMessage(String channelName, String from, String message) {
				resultMap.put("channel", channelName);
				resultMap.put("from", from);
				resultMap.put("message", message);
				System.out.println("+++++++++++++:" + channelName + ":" + from + ":" + message);
			}

			@Override
			public void onServerMessage(int id, String message) {
				resultMap.put("serverMessage", message);
			}
		});
		client1.sendJoin("#testChannel");
		Thread.sleep(1000);
		client1.sendMessage("#testChannel", "testMessage");
		Thread.sleep(1000);

		client2.sendJoin("#testChannel");
		client2.write("HISTORY :#testChannel");
		Thread.sleep(1000);
		assertEquals("client2NickName", resultMap.get("channel"));
		assertEquals("AIR_IRC", resultMap.get("from"));
		assertTrue(((String) resultMap.get("message")).endsWith("client1NickName: testMessage"));
		client1.sendQuit();
		client2.sendQuit();
	}

}
