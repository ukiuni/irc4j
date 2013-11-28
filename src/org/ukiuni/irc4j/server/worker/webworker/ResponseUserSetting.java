package org.ukiuni.irc4j.server.worker.webworker;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import net.arnx.jsonic.JSON;

import org.ukiuni.irc4j.User;
import org.ukiuni.irc4j.db.Database;
import org.ukiuni.irc4j.server.IRCServer;
import org.ukiuni.irc4j.storage.Storage;
import org.ukiuni.irc4j.storage.Storage.WriteHandle;
import org.ukiuni.lighthttpserver.request.ParameterFile;

public class ResponseUserSetting extends AIRCResponse {

	public ResponseUserSetting(IRCServer ircServer) {
		super(ircServer);
	}

	@Override
	public void onResponseSecure(OutputStream out) throws Throwable {
		String name = getRequest().getParameter("name");
		String realName = getRequest().getParameter("realName");
		String email = getRequest().getParameter("email");
		ParameterFile iconImage = getRequest().getParameterFile("iconImage");
		String password = getRequest().getParameter("password");
		String description = getRequest().getParameter("description");
		String notify = getRequest().getParameter("notify");
		String notificationKeyword = getRequest().getParameter("notificationKeyword");

		User user = getAccessConnection().getUser();
		user.setName(name);
		user.setRealName(realName);
		user.setEmail(email);
		user.setPasswordHashed(User.toHash(password));
		user.setDescription(description);
		user.setNotify(null != notify);
		user.setNotificationKeyword(notificationKeyword);
		if (null != iconImage) {
			BufferedImage uploadedImage = ImageIO.read(iconImage.getInputStream());
			BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
			int srcWidth = uploadedImage.getWidth();
			int srcHeight = uploadedImage.getHeight();
			int targetWidth = image.getWidth();
			int targetHeight = image.getHeight();
			double widthHi = (double) targetWidth / (double) srcWidth;
			double heightHi = (double) targetHeight / (double) srcHeight;
			double destHi = widthHi > heightHi ? widthHi : heightHi;
			Graphics g = image.getGraphics();
			int destWidth = (int) (srcWidth * destHi);
			int destHeight = (int) (srcHeight * destHi);
			g.drawImage(uploadedImage, (targetWidth - destWidth) / 2, (targetHeight - destHeight) / 2, destWidth, destHeight, null);
			g.dispose();
			WriteHandle writeHandle = Storage.getInstance().createUserIconImageWriteHandle(getAccessConnection().getNickName());
			OutputStream imageOut = writeHandle.getOutputStream();
			ImageIO.write(image, "gif", imageOut);
			imageOut.close();
			writeHandle.save();
			user.setIconImage("/user/iconImage/" + getAccessConnection().getNickName() + ".gif");
		}
		Database.getInstance().regist(user);
		write(out, 200, JSON.encode(getAccessConnection().getUser()), "application/json; charset=utf-8", "UTF-8");
	}
}
