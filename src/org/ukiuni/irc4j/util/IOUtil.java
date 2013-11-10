package org.ukiuni.irc4j.util;

import java.io.Closeable;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Statement;

public class IOUtil {
	public static void close(Statement st) {
		if (null != st) {
			try {
				st.close();
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

	public static void close(Closeable closeable) {
		if (null != closeable) {
			try {
				closeable.close();
			} catch (Throwable e) {
			}
		}
	}
}
