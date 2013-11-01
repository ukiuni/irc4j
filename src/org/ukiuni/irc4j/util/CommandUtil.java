package org.ukiuni.irc4j.util;

public class CommandUtil {
	private CommandUtil() {
	}

	public static String escape(String src) {
		src = src.replace("\n", "").replace("\r", "");
		return src;
	}
}
