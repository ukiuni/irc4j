package org.ukiuni.irc4j.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.ukiuni.irc4j.entity.JoinLog;
import org.ukiuni.irc4j.entity.Message;
import org.ukiuni.irc4j.util.IOUtil;

public class Database {
	private static Database instance;
	private Connection con;

	public static Database getInstance() {
		if (null == instance) {
			synchronized (Database.class) {
				if (null == instance) {
					instance = new Database();
				}
			}
		}
		return instance;
	}

	public Database() {
		try {
			new File(System.getProperty("user.home") + "/.airc").mkdirs();
			this.con = DriverManager.getConnection("jdbc:h2:file:" + System.getProperty("user.home") + "/.airc/.db");
			Statement st = null;
			try {
				st = this.con.createStatement();
				st.executeQuery("select * from message limit 1");
			} catch (SQLException e) {
				st.execute("create table message (id bigint auto_increment primary key, type varchar, senderFQUN varchar, sender_nick_name varchar,  target_channel varchar, message varchar,created_at timestamp)");
			} finally {
				IOUtil.close(st);
			}
			try {
				st = this.con.createStatement();
				st.executeQuery("select * from join_log limit 1");
			} catch (SQLException e) {
				st.execute("create table join_log (id bigint auto_increment primary key, channel_name varchar,  event varchar, user_id bigint, nickname varchar, created_at timestamp)");
			} finally {
				IOUtil.close(st);
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public void regist(Message message) {
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement("insert into message (type, senderFQUN, sender_nick_name, target_channel, message, created_at) values ( ?, ?, ?, ?, ?, now())");
			stmt.setString(1, message.getType());
			stmt.setString(2, message.getSenderFQUN());
			stmt.setString(3, message.getSenderNickName());
			stmt.setString(4, message.getTargetChannel());
			stmt.setString(5, message.getMessage());
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtil.close(stmt);
		}
	}

	public void regist(JoinLog joinLog) {
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement("insert into join_log (channel_name, event, user_id, nickname, created_at) values (?, ?, ?, ?, now())");
			stmt.setString(1, joinLog.getChannelName());
			stmt.setString(2, joinLog.getEvent().toString());
			stmt.setLong(3, joinLog.getUserId());
			stmt.setString(4, joinLog.getNickName());
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtil.close(stmt);
		}
	}

	public List<Message> loadMessage(String channel, int limit) {
		return loadMessage(channel, limit, true);
	}

	public List<Message> loadMessage(String channel, int limit, boolean oldToNew) {
		List<Message> messageList = loadMessage(channel, limit, "", oldToNew);
		return messageList;
	}

	private List<Message> loadMessage(String channel, int limit, String where, boolean oldToNew) {
		PreparedStatement stmt = null;
		try {
			String sql = "select id, type, senderFQUN, sender_nick_name, target_channel, message, created_at from message where target_channel = ? " + where + " order by id desc limit ?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, channel);
			stmt.setInt(2, limit);
			ResultSet rs = stmt.executeQuery();
			List<Message> messageList = rsToMessage(rs);
			if (oldToNew) {
				Collections.reverse(messageList);
			}
			return messageList;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtil.close(stmt);
		}
	}

	private List<Message> rsToMessage(ResultSet rs) throws SQLException {
		List<Message> messageList = new ArrayList<Message>();
		while (rs.next()) {
			Message message = new Message();
			message.setId(rs.getLong("id"));
			message.setType(rs.getString("type"));
			message.setSenderFQUN(rs.getString("senderFQUN"));
			message.setSenderNickName(rs.getString("sender_nick_name"));
			message.setTargetChannel(rs.getString("target_channel"));
			message.setMessage(rs.getString("message"));
			message.setCreatedAt(new Date(rs.getTimestamp("created_at").getTime()));
			messageList.add(message);
		}
		return messageList;
	}

	public List<Message> loadMessage(String channel, long maxId, int limit) {
		return loadMessage(channel, limit, "and id <= " + maxId, true);
	}

	public List<Message> loadMessageOlderThan(String channel, long maxId, int limit) {
		return loadMessage(channel, limit, "and id < " + maxId, false);
	}

	public List<Message> loadMessageNewerThan(String channel, long maxId, int limit) {
		return loadMessage(channel, limit, "and id > " + maxId, false);
	}

	public long loadMaxId(String channel) {
		List<Message> messageList = loadMessage(channel, 1, "", false);
		return messageList.isEmpty() ? 0 : messageList.get(0).getId();
	}
}
