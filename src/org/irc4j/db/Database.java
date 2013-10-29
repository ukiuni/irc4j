package org.irc4j.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.irc4j.Message;

public class Database {
	private static Database instance;
	private Connection con;

	public Database getInstance() {
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
			this.con = DriverManager.getConnection("jdbc:derby:db/sample;create=true");
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public void regist(Message message) {
		try {
			PreparedStatement stmt = con.prepareStatement("insert into ");
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
