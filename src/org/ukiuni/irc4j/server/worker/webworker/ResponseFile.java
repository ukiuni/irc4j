package org.ukiuni.irc4j.server.worker.webworker;

import java.io.OutputStream;

import org.ukiuni.irc4j.storage.Storage;
import org.ukiuni.irc4j.storage.Storage.ReadHandle;
import org.ukiuni.lighthttpserver.response.Response;
import org.ukiuni.lighthttpserver.util.StreamUtil;

public class ResponseFile extends Response {

	@Override
	public void onResponse(OutputStream out) throws Throwable {
		String key = getRequest().getParameter("k");
		if (key == null || !Storage.getInstance().exists(key)) {
			write(out, 400, "no data found", "application/json; charset=utf-8", "UTF-8");
			return;
		}
		ReadHandle readHandle = Storage.getInstance().createReadandle(key);
		writeHeader(out, 200, "application/octet-stream", readHandle.getContentLength());
		StreamUtil.copy(readHandle.getInputStream(), out);
		out.close();
	}
}
