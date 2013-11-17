package org.ukiuni.irc4j;

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

	public static int getIrcServerPort() {
		try {
			return Integer.valueOf(prop.getProperty("irc.server.port"));
		} catch (Throwable e) {
			return 6667;
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

	public static char[] getIRCCertPassword() {
		String arg = null;
		try {
			arg = prop.getProperty("irc.ssl.certPassword");
		} catch (Throwable e) {
		}
		return null == arg ? null : arg.toCharArray();
	}

	public static char[] getIRCStorePassword() {
		String arg = null;
		try {
			arg = prop.getProperty("irc.ssl.storePassword");
		} catch (Throwable e) {
		}
		return null == arg ? null : arg.toCharArray();
	}

	public static boolean isIRCSSL() {
		String arg = null;
		try {
			arg = prop.getProperty("irc.ssl");
		} catch (Throwable e) {
		}
		return "true".equals(arg);
	}

	public static int getIRCServerWaitQueue() {
		int queueSize = 100;
		try {
			queueSize = Integer.valueOf(prop.getProperty("irc.serverWaitQueueSize"));
		} catch (Throwable e) {
		}
		return queueSize;
	}

	public static String getIRCKeyStoreType() {
		String arg = null;
		try {
			arg = prop.getProperty("irc.ssl.keyStoreType");
		} catch (Throwable e) {
		}
		return null == arg ? "JKS" : arg;
	}

	public static String getIRCCertPath() {
		String arg = null;
		try {
			arg = prop.getProperty("irc.ssl.keyStorePath");
		} catch (Throwable e) {
		}
		return arg;
	}

	public static char[] getHttpCertPassword() {
		String arg = null;
		try {
			arg = prop.getProperty("http.ssl.certPassword");
		} catch (Throwable e) {
		}
		return null == arg ? null : arg.toCharArray();
	}

	public static char[] getHttpStorePassword() {
		String arg = null;
		try {
			arg = prop.getProperty("http.ssl.storePassword");
		} catch (Throwable e) {
		}
		return null == arg ? null : arg.toCharArray();
	}

	public static boolean isHttpSSL() {
		String arg = null;
		try {
			arg = prop.getProperty("http.ssl");
		} catch (Throwable e) {
		}
		return "true".equals(arg);
	}

	public static int getHttpServerWaitQueue() {
		int queueSize = 100;
		try {
			queueSize = Integer.valueOf(prop.getProperty("http.serverWaitQueueSize"));
		} catch (Throwable e) {
		}
		return queueSize;
	}

	public static String getHttpKeyStoreType() {
		String arg = null;
		try {
			arg = prop.getProperty("http.ssl.keyStoreType");
		} catch (Throwable e) {
		}
		return null == arg ? "JKS" : arg;
	}

	public static String getHttpCertPath() {
		String arg = null;
		try {
			arg = prop.getProperty("http.ssl.keyStorePath");
		} catch (Throwable e) {
		}
		return arg;
	}
}
