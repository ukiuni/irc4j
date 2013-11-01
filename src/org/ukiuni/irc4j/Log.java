package org.ukiuni.irc4j;

public class Log {
	public static void log(String message) {
		System.out.println(message);
	}

	public static void log(Throwable e) {
		e.printStackTrace();
	}

	public static void log(String message, Throwable e) {
		log(message);
		log(e);
	}
}
