package org.ukiuni.irc4j.server.plugin;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.ukiuni.irc4j.Log;
import org.ukiuni.irc4j.db.Database;
import org.ukiuni.irc4j.server.ServerCommand;
import org.ukiuni.irc4j.server.plugin.Plugin.Status;
import org.ukiuni.irc4j.util.IOUtil;
import org.ukiuni.lighthttpserver.util.FileUtil;

public class PluginFactory {
	private static PluginFactory instance;
	private final File pluginBaseDir = null == System.getenv("PLUGIN_BASE_PATH") ? new File("./plugins") : new File(System.getenv("PLUGIN_BASE_PATH"));
	private final Map<String, CommandPlugin> commandPluginMap = new HashMap<String, CommandPlugin>();

	public static PluginFactory getInstance() {
		if (null == instance) {
			synchronized (PluginFactory.class) {
				if (null == instance) {
					instance = new PluginFactory();
				}
			}
		}
		return instance;
	}

	private PluginFactory() {
		loadPlugin();
	}

	private void loadPlugin() {
		loadPluginFromDB();
		loadPluginFromFile();
	}

	private void loadPluginFromDB() {
		List<Plugin> pluginList = Database.getInstance().loadMovingPlugin();
		for (Plugin plugin : pluginList) {
			try {
				appendPlugin(new StringReader(plugin.getScript() + "\n function getCommand() {return \"" + plugin.getCommand() + "\";}"), plugin.getName() + ".js");
			} catch (Throwable e) {
				Log.log("error on load Plugin from db plugin = " + plugin.getName(), e);
				plugin.setStatus(Status.SUSPENDED);
				Database.getInstance().regist(plugin);
			}
		}
	}

	private void loadPluginFromFile() {
		try {
			File[] files = pluginBaseDir.listFiles();
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				Reader reader = null;
				try {
					reader = new FileReader(file);
					String name = file.getAbsolutePath();
					appendPlugin(reader, name);
				} catch (Throwable e) {
					Log.log("Plugin " + file.getAbsolutePath() + " boot failed.", e);
				} finally {
					IOUtil.close(reader);
				}
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public class ScriptUtil {
		public void exec(final Runnable runnable, Object timeObj) {
			long time;
			System.out.println("timeObj = " + timeObj.getClass());
			if (timeObj instanceof Long) {
				time = (Long) timeObj;
			} else if (timeObj instanceof Integer) {
				time = (Integer) timeObj;
			} else if (timeObj instanceof String) {
				time = Long.valueOf((String) timeObj);
			} else if (timeObj instanceof Date) {
				time = ((Date) timeObj).getTime() - System.currentTimeMillis();
			} else {
				throw new IllegalArgumentException("time must be number or string or date.");
			}
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					runnable.run();
				}
			}, time);
		}

		public long parseInt(String value) {
			return Long.parseLong(value);
		}
	}

	private void appendPlugin(Reader reader, String name) throws ScriptException {
		Log.log("plugin loading :" + name);
		ScriptEngine engine = new ScriptEngineManager().getEngineByExtension(FileUtil.getExt(name));
		engine.put("SCRIPTINGFRAMEWORK_Util", new ScriptUtil());
		engine.eval("var SCRIPTINGFRAMEWORK_Runnable = java.lang.Runnable;");
		// engine.eval("function parseInt(value){return SCRIPTINGFRAMEWORK_Util.parseInt(value)}");
		engine.eval("function setTimeout(execFunction, delayTime){SCRIPTINGFRAMEWORK_Util.exec(new SCRIPTINGFRAMEWORK_Runnable({run:execFunction}), delayTime)}");
		engine.eval("Package = importPackage = Java = java = javax = org = edu = com = net = null;");
		Invocable inv = (Invocable) engine;
		engine.eval(reader);
		if (fitsInterface(engine, CommandPlugin.class)) {
			CommandPlugin commandPlugin = inv.getInterface(CommandPlugin.class);
			commandPluginMap.put(commandPlugin.getCommand().toUpperCase(), commandPlugin);
			Log.log("plugin loaded :" + name);
		} else {
			Log.log("plugin throw :" + name);
		}
	}

	private boolean fitsInterface(ScriptEngine engine, Class<?> interfaceClass) throws ScriptException {
		Method[] methods = interfaceClass.getDeclaredMethods();
		for (Method method : methods) {
			String functionName = method.getName();
			Object obj = engine.eval("typeof(" + functionName + ") == \"undefined\" ? false : true");
			if (!(Boolean) obj) {
				return false;
			}
		}
		return true;
	}

	public boolean containsCommand(String commandString) {
		return commandPluginMap.containsKey(commandString.toUpperCase());
	}

	public ServerCommand loadCommand(String commandString) {
		if (containsCommand(commandString)) {
			return new ScriptPluginExecuteServerCommand(commandPluginMap.get(commandString.toUpperCase()));
		} else {
			return null;
		}
	}

	public void flush() {
		synchronized (PluginFactory.class) {
			instance = null;
		}
	}
}
