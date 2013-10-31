package org.irc4j;

import java.io.IOException;
import java.util.Properties;

public class Conf {
	private static Properties prop = new Properties();
	static {
		try {
			prop.load(Conf.class.getResourceAsStream("/config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int getHttpServerPort() {
		try {
			return Integer.valueOf(prop.getProperty("http.server.port"));
		} catch (Throwable e) {
			return 1080;
		}
	}

	public static String getHttpServerURL() {
		return prop.getProperty("http.server.url");
	}

	public static String getLogCipherKey() {
		return prop.getProperty("log.cipher.key");
	}
}
