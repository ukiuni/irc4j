package org.ukiuni.irc4j.client;

public class DCCMessage {
	public final String targetHost;
	public final String fileName;
	public final int portNum;
	public final long fileSize;

	public DCCMessage(String targetHost, String fileName, int portNum, long fileSize) {
		this.targetHost = targetHost;
		this.fileName = fileName;
		this.portNum = portNum;
		this.fileSize = fileSize;
	}
}
