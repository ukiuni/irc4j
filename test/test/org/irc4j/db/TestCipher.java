package test.org.irc4j.db;

import org.irc4j.util.CipherUtil;
import org.junit.Assert;
import org.junit.Test;

public class TestCipher {
	@Test
	public void testCipher() {
		String src = "返還前 asdfas";
		String encrypted = CipherUtil.encode(src);
		System.out.println(encrypted);
		Assert.assertEquals(src, CipherUtil.decode(encrypted));
	}
}
