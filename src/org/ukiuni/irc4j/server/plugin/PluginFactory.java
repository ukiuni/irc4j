package org.ukiuni.irc4j.server.plugin;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.ukiuni.irc4j.Log;
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
		try {
			File[] files = pluginBaseDir.listFiles();
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				FileReader reader = null;
				try {
					Log.log("plugin loading :" + file.getAbsolutePath());
					ScriptEngine engine = new ScriptEngineManager().getEngineByExtension(FileUtil.getExt(file));
					engine.eval("Package = importPackage = java = javax = org = edu = com = net = null;");
					Invocable inv = (Invocable) engine;
					reader = new FileReader(file);
					engine.eval(reader);
					if (fitsInterface(engine, CommandPlugin.class)) {
						CommandPlugin commandPlugin = inv.getInterface(CommandPlugin.class);
						commandPluginMap.put(commandPlugin.getCommand().toUpperCase(), commandPlugin);
					}
					Log.log("plugin loaded :" + file.getAbsolutePath());
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
}
