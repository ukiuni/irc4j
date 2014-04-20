package org.ukiuni.irc4j.gui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.ukiuni.irc4j.Test.UISet;

public class UIFolder {

	private final Map<TreeNode, UISet> uiSetMap = new HashMap<TreeNode, UISet>();
	private final Map<String, UISet> uiSetMapRelationedWithHostAndPortAndChannel = new HashMap<String, UISet>();
	private static UIFolder instance;

	public static UIFolder getInstance() {
		if (null == instance) {
			synchronized (ConnectingManager.class) {
				if (null == instance) {
					instance = new UIFolder();
				}
			}
		}
		return instance;
	}

	public UISet get(TreeNode treeNode) {
		return uiSetMap.get(treeNode);
	}

	public UISet get(String host, int port, String channelName) {
		return uiSetMapRelationedWithHostAndPortAndChannel.get(host + "\n" + port + "\n" + channelName);
	}

	public void put(DefaultMutableTreeNode channelNode, UISet uiSet, String host, int port, String channelName) {
		uiSetMap.put(channelNode, uiSet);
		uiSetMapRelationedWithHostAndPortAndChannel.put(host + "\n" + port + "\n" + channelName, uiSet);
	}
}
