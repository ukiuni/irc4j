package org.ukiuni.irc4j.server.worker.webworker;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.ukiuni.irc4j.storage.Storage;
import org.ukiuni.irc4j.storage.Storage.ReadHandle;
import org.ukiuni.lighthttpserver.response.Response;
import org.ukiuni.lighthttpserver.util.StreamUtil;

public class ResponseFile extends Response {

	@Override
	public void onResponse(OutputStream out) throws Throwable {
		String key = getRequest().getPath().substring("/file/".length());
		if (key == null || !Storage.getInstance().exists(key)) {
			write(out, 400, "no data found", "application/json; charset=utf-8", "UTF-8");
			return;
		}
		ReadHandle readHandle = Storage.getInstance().createReadandle(key);
		Map<String, String> additionalHeader = new HashMap<String, String>();
		additionalHeader.put("Content-Disposition", "attachment; filename=" + readHandle.getName());
		additionalHeader.put("Content-Length", String.valueOf(readHandle.getContentLength()));
		setAdditionalHeader(additionalHeader);
		writeHeader(out, 200, "application/octet-stream", readHandle.getContentLength());
		StreamUtil.copy(readHandle.getInputStream(), out);
		out.close();
	}
}
