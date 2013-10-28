package org.irc4j;

import java.util.Date;

public class Message {
	private String type;
	private String senderFQUN;
	private String targetChannel;
	private String message;

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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	private String senderNickName;
	private Date date;
}
