package org.ukiuni.irc4j;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class GUIClient {
	public static void main(String[] args) throws UnsupportedEncodingException, ScriptException {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
		engine.put("loader", new JSLoader(engine));
		engine.eval(new InputStreamReader(GUIClient.class.getResourceAsStream("/GuiClient.js"), "UTF-8"));
	}

	public static class JSLoader {
		private ScriptEngine engine;

		public JSLoader(ScriptEngine engine) {
			this.engine = engine;
		}

		public void load(String file) throws UnsupportedEncodingException, ScriptException {
			engine.eval(new InputStreamReader(GUIClient.class.getResourceAsStream("/" + file), "UTF-8"));
		}
	}
}
