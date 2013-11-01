package test.org.irc4j.db;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.ukiuni.irc4j.db.Database;
import org.ukiuni.irc4j.entity.Message;

public class TestDatabase {
	@Test
	public void testStart() {
		Message message = new Message();
		message.setCreatedAt(new Date());
		message.setMessage("testMessage");
		message.setSenderFQUN("client1NickName__!client1RealName@127.0.0.1");
		message.setSenderNickName("client1NickName__");
		message.setTargetChannel("#testChannel");
		message.setType("PRVMSG");
		Database.getInstance().regist(message);
		List<Message> messageList = Database.getInstance().loadMessage("#testChannel", 1);
		Assert.assertEquals(1, messageList.size());
		Assert.assertEquals(message.getMessage(), messageList.get(0).getMessage());
		Assert.assertEquals(message.getSenderFQUN(), messageList.get(0).getSenderFQUN());
		Assert.assertEquals(message.getSenderNickName(), messageList.get(0).getSenderNickName());
		Assert.assertEquals(message.getTargetChannel(), messageList.get(0).getTargetChannel());
		Assert.assertEquals(message.getType(), messageList.get(0).getType());
	}
}
