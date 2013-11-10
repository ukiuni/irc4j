package org.ukiuni.irc4j.entity;

import java.util.Date;

public class JoinLog {
	private String channelName;
	private Event event;
	private long userId;
	private String nickName;
	private Date createdAt;

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public enum Event {
		JOIN, PART
	}

	public static JoinLog create(String channelName, Event event, String nickName, long userId) {
		JoinLog joinLog = new JoinLog();
		joinLog.setChannelName(channelName);
		joinLog.setEvent(event);
		joinLog.setUserId(userId);
		joinLog.setNickName(nickName);
		joinLog.setCreatedAt(new Date());
		return joinLog;
	}
}
