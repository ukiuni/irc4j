package org.ukiuni.irc4j.entity;

import java.util.Date;

public class Message {
	private long id;
	private String type;
	private String senderFQUN;
	private String senderNickName;
	private String targetChannel;
	private String message;
	private Date createdAt;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSenderFQUN() {
		return senderFQUN;
	}

	public void setSenderFQUN(String senderFQUN) {
		this.senderFQUN = senderFQUN;
	}

	public String getTargetChannel() {
		return targetChannel;
	}

	public void setTargetChannel(String targetChannel) {
		this.targetChannel = targetChannel;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSenderNickName() {
		return senderNickName;
	}

	public void setSenderNickName(String senderNickName) {
		this.senderNickName = senderNickName;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
