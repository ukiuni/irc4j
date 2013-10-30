package org.irc4j.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

public class CipherUtil {
	private static byte[] PRIVATE_KEY = "123456789012345678901234".getBytes(); //TODO out

	public static String encode(String src) {
		try {
			byte[] rawText = src.getBytes("UTF-8");
			SecretKey secretKey = SecretKeyFactory.getInstance("DESEde").generateSecret(new DESedeKeySpec(PRIVATE_KEY));
			Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm() + "/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] encryptedText = cipher.doFinal(rawText);;
			return toHex(encryptedText);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String decode(String src) {
		try {
			byte[] rawText = toByteArray(src);
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

	public static byte[] toByteArray(String hex) {
		byte[] bytes = new byte[hex.length() / 2];
		for (int index = 0; index < bytes.length; index++) {
			bytes[index] = (byte) Integer.parseInt(hex.substring(index * 2, (index + 1) * 2), 16);
		}
		return bytes;
	}
}
