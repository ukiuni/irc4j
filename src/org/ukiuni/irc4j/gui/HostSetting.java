package org.ukiuni.irc4j.gui;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class HostSetting {
	private String hostShowName;
	private String hostUrl;
	private int port;
	private String nickname;
	private String myHost = "localhost";
	private String realName;

	@XmlElement
	List<String> channels = new ArrayList<String>();

	public HostSetting() {
	}

	public HostSetting(String hostUrl, int port, String hostShowName, String nickname, String myHost, String realName) {
		this.hostShowName = hostShowName;
		this.hostUrl = hostUrl;
		this.port = port;
		this.nickname = nickname;
		this.myHost = myHost;
		this.realName = realName;
	}

	public String getHostShowName() {
		return hostShowName;
	}

	public void setHostShowName(String hostShowName) {
		this.hostShowName = hostShowName;
	}

	public String getHostUrl() {
		return hostUrl;
	}

	public void setHostUrl(String hostUrl) {
		this.hostUrl = hostUrl;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getMyHost() {
		return myHost;
	}

	public void setMyHost(String myHost) {
		this.myHost = myHost;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public List<String> getChannels() {
		return channels;
	}

	public void addChannel(String channel) {
		channels.add(channel);
	}
}
