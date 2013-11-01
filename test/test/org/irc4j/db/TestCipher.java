package test.org.irc4j.db;

import org.junit.Assert;
import org.junit.Test;
import org.ukiuni.irc4j.util.CipherUtil;

public class TestCipher {
	@Test
	public void testCipher() {
		String src = "返還前 asdfas";
		String encrypted = CipherUtil.encode(src);
		System.out.println(encrypted);
		Assert.assertEquals(src, CipherUtil.decode(encrypted));
	}
}
