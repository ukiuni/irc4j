package org.ukiuni.irc4j.server.command;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.ukiuni.irc4j.Log;
import org.ukiuni.irc4j.storage.Storage;
import org.ukiuni.irc4j.storage.Storage.WriteHandle;
import org.ukiuni.lighthttpserver.util.FileUtil;

public class FileRecieveThread extends Thread {
	public OnCompleteListener onCompleteListener;
	private String hostName;
	private String fileName;
	private Integer portNum;
	private Long fileSize;

	public FileRecieveThread(String hostName, int portNum, String fileName, long fileSize, OnCompleteListener onCompleteListener) {
		this.hostName = hostName;
		this.onCompleteListener = onCompleteListener;
		this.portNum = portNum;
		this.fileName = fileName;
		this.fileSize = fileSize;
	}

	public void run() {
		try {
			Log.log("/////////////////// socket = " + hostName + ":" + portNum);
			Socket socket = new Socket(hostName, portNum);
			InputStream in = socket.getInputStream();
			byte[] buffer = new byte[1024];
			long totalReaded = 0;
			int readed = in.read(buffer);
			WriteHandle writeHandle = Storage.getInstance().createWriteHandle(fileName, FileUtil.getMimeType(fileName));
			OutputStream out = writeHandle.getOutputStream();
			while (totalReaded < fileSize && readed > 0) {
				out.write(buffer, 0, readed);
				totalReaded += readed;
				if (totalReaded + buffer.length > fileSize) {
					buffer = new byte[(int) (fileSize - totalReaded)];
				}
				readed = in.read(buffer);
			}
			socket.close();
			out.close();
			writeHandle.save();
			Log.log("/////////////////// upload complete");
			if (null != onCompleteListener) {
				onCompleteListener.onComplete("/file/" + writeHandle.getKey());
			}
		} catch (Throwable e) {
			if (null != onCompleteListener) {
				onCompleteListener.onError(e);
			}
		}
	};

	public interface OnCompleteListener {
		public void onComplete(String uploadedUri);

		public void onError(Throwable e);
	}
}
