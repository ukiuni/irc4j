package org.ukiuni.irc4j.server;

import java.util.Date;

import org.ukiuni.irc4j.Conf;

public class Event {
	public static final String TYPE_USER_PART = "user.part";
	public static final String TYPE_USER_JOIN = "user.join";

	public static Event createPart(String channelName, String userNickName) {
		Event event = new Event();
		event.setChannelName(channelName);
		event.setUserNickName(userNickName);
		event.setType("user.part");
		event.setCreatedAt(new Date());
		return event;
	}

	public static Event createJoin(String channelName, String userNickName) {
		Event event = new Event();
		event.setChannelName(channelName);
		event.setUserNickName(userNickName);
		event.setType("user.join");
		event.setCreatedAt(new Date());
		return event;
	}

	public static Event createMessage(String channelName, String userNickName, String message) {
		Event event = new Event();
		event.setChannelName(channelName);
		event.setUserNickName(userNickName);
		event.setMessage(message);
		event.setType("message");
		event.setCreatedAt(new Date());
		return event;
	}

	public static Event createReload() {
		Event event = new Event();
		event.setUrl(Conf.getHttpServerURL());
		event.setType("reload");
		event.setCreatedAt(new Date());
		return event;
	}

	public static Event createRejoin() {
		Event event = new Event();
		event.setUrl(Conf.getHttpServerURL());
		event.setType("rejoin");
		event.setCreatedAt(new Date());
		return event;
	}

	private String type;
	private String channelName;
	private String userNickName;
	private String message;
	private String url;
	private Date createdAt;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getUserNickName() {
		return userNickName;
	}

	public void setUserNickName(String userNickName) {
		this.userNickName = userNickName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
