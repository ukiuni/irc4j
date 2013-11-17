package org.ukiuni.irc4j.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.ukiuni.irc4j.Conf;

public class CipherUtil {
	private static byte[] PRIVATE_KEY = Conf.getLogCipherKey().getBytes();

	public static String encode(String src) {
		try {
			byte[] rawText = src.getBytes("UTF-8");
			SecretKey secretKey = SecretKeyFactory.getInstance("DESEde").generateSecret(new DESedeKeySpec(PRIVATE_KEY));
			Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm() + "/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] encryptedText = cipher.doFinal(rawText);
			return toHex(zip(encryptedText));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String decode(String src) {
		try {
			byte[] rawText = unzip(toByteArray(src));
			SecretKey secretKey = SecretKeyFactory.getInstance("DESEde").generateSecret(new DESedeKeySpec(PRIVATE_KEY));
			Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm() + "/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] encryptedText = cipher.doFinal(rawText);
			return new String(encryptedText, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String toHex(byte bytes[]) {
		StringBuffer strbuf = new StringBuffer(bytes.length * 2);
		for (int index = 0; index < bytes.length; index++) {
			int bt = bytes[index] & 0xff;
			if (bt < 0x10) {
				strbuf.append("0");
			}
			strbuf.append(Integer.toHexString(bt));
		}
		return strbuf.toString();
	}

	private static byte[] zip(byte[] src) {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DeflaterOutputStream zout = new DeflaterOutputStream(bout);
			zout.write(src);
			zout.finish();
			return bout.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static byte[] unzip(byte[] src) {
		try {
			InflaterInputStream in = new InflaterInputStream(new ByteArrayInputStream(src));
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int readed = in.read(buffer);
			while (readed > 0) {
				bout.write(buffer, 0, readed);
				readed = in.read(buffer);
			}
			return bout.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] toByteArray(String hex) {
		byte[] bytes = new byte[hex.length() / 2];
		for (int index = 0; index < bytes.length; index++) {
			bytes[index] = (byte) Integer.parseInt(hex.substring(index * 2, (index + 1) * 2), 16);
		}
		return bytes;
	}
}
