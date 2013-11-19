package org.ukiuni.irc4j.server.plugin;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.ukiuni.irc4j.Log;
import org.ukiuni.irc4j.db.Database;
import org.ukiuni.irc4j.server.ServerCommand;
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
			} catch (ScriptException e) {
				Log.log("error on load Plugin from db", e);
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

	private void appendPlugin(Reader reader, String name) throws ScriptException {
		Log.log("plugin loading :" + name);
		ScriptEngine engine = new ScriptEngineManager().getEngineByExtension(FileUtil.getExt(name));
		engine.eval("Package = importPackage = java = javax = org = edu = com = net = null;");
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
