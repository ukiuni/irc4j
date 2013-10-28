package org.irc4j;

public class Log {
	public static void log(String message) {
		System.out.println(message);
	}

	public static void log(Exception e) {
		e.printStackTrace();
	}
}
