package org.ukiuni.irc4j.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class Storage {
	private static Storage instance;
	private File storageDirectory;

	public static Storage getInstance() {
		if (null == instance) {
			synchronized (Storage.class) {
				if (null == instance) {
					instance = new Storage();
				}
			}
		}
		return instance;
	}

	public Storage() {
		try {
			storageDirectory = new File(System.getProperty("user.home") + "/.airc/storage");
			storageDirectory.mkdirs();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public ReadHandle createReadHandle(final String key) {
		final File file = new File(storageDirectory, key);
		if (!file.isFile()) {
			return null;
		}
		return new ReadHandle() {
			@Override
			public String getKey() {
				return key;
			}

			@Override
			public InputStream getInputStream() throws IOException {
				return new FileInputStream(file);
			}

			@Override
			public String getContentType() {
				return null;
			}

			@Override
			public void delete() {
				file.delete();
			}

			@Override
			public long getContentLength() {
				return file.length();
			}

			@Override
			public String getName() {
				return file.getName();
			}
		};
	}

	public WriteHandle createWriteHandle(final String name, final String contentType) {
		final String uuid = UUID.randomUUID().toString().replace("-", "");
		final File uploadDir = new File(storageDirectory, uuid);
		uploadDir.mkdirs();
		File file = new File(uploadDir, name);
		return createWriteHandle(file, uuid + "/" + name);
	}

	public WriteHandle createUserIconImageWriteHandle(final String nickname) {
		final File uploadDir = new File(storageDirectory, "userIconImage");
		uploadDir.mkdirs();
		File file = new File(uploadDir, nickname + ".gif");
		return createWriteHandle(file, "userIconImage/" + nickname + ".gif");
	}

	public ReadHandle createUserIconImageReadHandle(final String fileName) {
		return createReadHandle("userIconImage/" + fileName);
	}

	public WriteHandle createWriteHandle(final File file, final String key) {
		return new WriteHandle() {
			OutputStream out;

			@Override
			public void save() throws IOException {
				try {
					out.close();
				} catch (Exception e) {
					// do nothing
				}
			}

			@Override
			public OutputStream getOutputStream() throws IOException {
				this.out = new FileOutputStream(file);
				return this.out;
			}

			@Override
			public String getKey() {
				return key;
			}
		};
	}

	public interface WriteHandle {
		public OutputStream getOutputStream() throws IOException;

		public void save() throws IOException;

		public String getKey();
	}

	public interface ReadHandle {
		public InputStream getInputStream() throws IOException;

		public void delete();

		public String getKey();

		public long getContentLength();

		public String getContentType();

		public String getName();
	}

	public boolean exists(String key) {
		return new File(storageDirectory, key).isFile();
	}
}
