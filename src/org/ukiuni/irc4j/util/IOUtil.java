package org.ukiuni.irc4j.util;

import java.io.Closeable;
import java.net.ServerSocket;
import java.net.Socket;

public class IOUtil {
	public static void close(Closeable target) {
		if (null != target) {
			try {
				target.close();
			} catch (Throwable e) {
			}
		}
	}

	public static void close(ServerSocket target) {
		if (null != target) {
			try {
				target.close();
			} catch (Throwable e) {
			}
		}
	}

	public static void close(Socket target) {
		if (null != target) {
			try {
				target.close();
			} catch (Throwable e) {
			}
		}
	}
}
