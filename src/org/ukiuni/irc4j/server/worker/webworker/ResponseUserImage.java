package org.ukiuni.irc4j.server.worker.webworker;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.ukiuni.irc4j.storage.Storage;
import org.ukiuni.irc4j.storage.Storage.ReadHandle;
import org.ukiuni.lighthttpserver.response.Response;
import org.ukiuni.lighthttpserver.util.FileUtil;
import org.ukiuni.lighthttpserver.util.StreamUtil;

public class ResponseUserImage extends Response {

	@Override
	public void onResponse(OutputStream out) throws Throwable {
		String key = getRequest().getPath().substring("/user/iconImage/".length());
		if (key == null || key.contains("..")) {
			write(out, 400, "no data found", "application/json; charset=utf-8", "UTF-8");
			return;
		}
		ReadHandle readHandle = Storage.getInstance().createUserIconImageReadHandle(key);
		if (null == readHandle) {
			write(out, 400, "no data found", "application/json; charset=utf-8", "UTF-8");
			return;
		}
		Map<String, String> additionalHeader = new HashMap<String, String>();
		additionalHeader.put("Content-Type", FileUtil.getMimeType(readHandle.getName()));
		additionalHeader.put("Content-Length", String.valueOf(readHandle.getContentLength()));
		setAdditionalHeader(additionalHeader);
		writeHeader(out, 200, "application/octet-stream", readHandle.getContentLength());
		StreamUtil.copy(readHandle.getInputStream(), out);
		out.close();
	}
}
