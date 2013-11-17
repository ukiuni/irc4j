package org.ukiuni.irc4j.util;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;

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

	public static void close(ResultSet rs) {
		if (null != rs) {
			try {
				rs.close();
			} catch (Throwable e) {
			}
		}
	}

	public static ServerSocket createSSLServerSocket(int port, int serverWaitQueue, String keyStoreType, InputStream cert, char[] storePassword, char[] certPassword) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, UnrecoverableKeyException, KeyManagementException {
		KeyStore keyStore = KeyStore.getInstance(keyStoreType);
		keyStore.load(cert, storePassword);
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
		keyManagerFactory.init(keyStore, certPassword);
		SSLContext sSLContext = SSLContext.getInstance("TLS");
		sSLContext.init(keyManagerFactory.getKeyManagers(), null, null);
		SSLServerSocketFactory serverSocketFactory = sSLContext.getServerSocketFactory();
		ServerSocket serverSocket = serverSocketFactory.createServerSocket(port, serverWaitQueue);
		return serverSocket;
	}
}
