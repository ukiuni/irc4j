package org.ukiuni.irc4j;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.plaf.metal.MetalSplitPaneUI;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants.CharacterConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.ukiuni.irc4j.gui.ConnectingManager;
import org.ukiuni.irc4j.gui.HostSetting;
import org.ukiuni.irc4j.gui.Settings;

public class Test {
	public static TreeNode currentSelectHostNode;
	public static TreeNode currentSelectChannelNode;

	public static void main(String[] args) throws IOException {
		final JFrame frame = new JFrame("aris");

		final Map<TreeNode, UISet> uiSetMap = new HashMap<TreeNode, UISet>();

		JSplitPane splitpane = new JSplitPane();

		JSplitPane leftPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		leftPanel.setUI(new MetalSplitPaneUI());
		JPanel messagePane = new JPanel();
		messagePane.setLayout(new BorderLayout());

		final JTextPane messageArea = new JTextPane();
		messageArea.setEditable(false);
		messagePane.add(new JScrollPane(messageArea), BorderLayout.CENTER);
		final JTextField inputField = new JTextField();
		messagePane.add(inputField, BorderLayout.SOUTH);

		final DefaultStyledDocument logDocument = new DefaultStyledDocument();
		final JTextPane logPane = new JTextPane();
		logPane.setDocument(logDocument);

		leftPanel.setTopComponent(messagePane);
		leftPanel.setBottomComponent(new JScrollPane(logPane));

		JSplitPane rightPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		MetalSplitPaneUI rightVerticalSprit = new MetalSplitPaneUI();
		rightPanel.setUI(rightVerticalSprit);

		final JList userList = new JList();
		rightPanel.setTopComponent(new JScrollPane(userList));

		final DefaultMutableTreeNode hostAndChannelTreeRootNode = new DefaultMutableTreeNode("rootNode");
		final JTree hostAndChannelTree = new JTree(hostAndChannelTreeRootNode);
		hostAndChannelTree.setRootVisible(false);
		rightPanel.setBottomComponent(new JScrollPane(hostAndChannelTree));

		splitpane.setUI(new MetalSplitPaneUI());
		splitpane.setLeftComponent(leftPanel);
		splitpane.setRightComponent(rightPanel);

		frame.getContentPane().add(splitpane);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final ConnectingManager connectingManager = ConnectingManager.getInstance();
		final Settings settings = Settings.load();

		inputField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String input = inputField.getText();
				if (null != currentSelectChannelNode && !"".equals(input)) {
					UISet currentUISet = uiSetMap.get(currentSelectChannelNode);
					SimpleAttributeSet attribute = new SimpleAttributeSet();
					try {
						System.out.println("send to" + currentSelectHostNode + ":" + currentSelectChannelNode);
						connectingManager.sendMessage(currentSelectHostNode, currentSelectChannelNode.toString(), input);
						currentUISet.messageDocument.insertString(currentUISet.messageDocument.getLength(), String.format("%1$-12s", "ukiunin") + ":" + input + "\n", attribute);
					} catch (Exception e) {
						e.printStackTrace();
					}
					inputField.setText("");
				}
			}
		});
		final ConnectingManager.IRCEventHandler handler = (new ConnectingManager.IRCEventHandler() {
			@Override
			public void onMessage(IRCClient client, String channelName, String from, String message) {
				SimpleAttributeSet attribute = new SimpleAttributeSet();
				UISet currentUISet = uiSetMap.get(currentSelectChannelNode);
				try {
					currentUISet.messageDocument.insertString(currentUISet.messageDocument.getLength(), String.format("%1$-12s", from) + ":" + message + "\n", attribute);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(IRCClient client, Throwable e) {
				SimpleAttributeSet attribute = new SimpleAttributeSet();
				attribute.addAttribute(CharacterConstants.Foreground, Color.red);
				try {
					logDocument.insertString(logDocument.getLength(), e + "\n", attribute);
				} catch (Exception e2) {
					e.printStackTrace();
				}
			}

			@Override
			public void onServerMessage(IRCClient client, int id, String message) {
				System.out.println("s:[" + id + "]" + message);
				SimpleAttributeSet attribute = new SimpleAttributeSet();
				try {
					logDocument.insertString(logDocument.getLength(), id + ":" + message + "\n", attribute);
					if (353 == id) {
						Matcher m = Pattern.compile("(#.*):").matcher(message);
						m.find();
						String channelName = m.group(1).trim();
						String[] respondUsers = message.substring(message.lastIndexOf(":") + 1).split(" ");

						List<String> users = connectingManager.getUsers(client.getHost(), client.getPort(), channelName);
						users.clear();
						for (String userName : respondUsers) {
							System.out.println("u = " + userName);
							users.add(userName);
						}

						UISet targetUISet = UISet.findUISet(client.getHost(), client.getPort(), channelName);
						targetUISet.userlist.removeAllElements();
						for (String userName : users) {
							targetUISet.userlist.addElement(userName);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onJoinToChannel(IRCClient client, String channelName, String nickName) {
				List<String> users = connectingManager.getUsers(client.getHost(), client.getPort(), channelName);
				if (!users.contains(nickName)) {
					users.add(nickName);
				}
				UISet targetUISet = UISet.findUISet(client.getHost(), client.getPort(), channelName);
				targetUISet.userlist.removeAllElements();
				for (String userName : users) {
					targetUISet.userlist.addElement(userName);
				}
			}

			@Override
			public void onPartFromChannel(IRCClient client, String channelName, String nickName, String message) {
				List<String> users = connectingManager.getUsers(client.getHost(), client.getPort(), channelName);
				users.remove(nickName);
				UISet targetUISet = UISet.findUISet(client.getHost(), client.getPort(), channelName);
				targetUISet.userlist.removeAllElements();
				for (String userName : users) {
					targetUISet.userlist.addElement(userName);
				}
			}
		});
		hostAndChannelTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					if (SwingUtilities.isRightMouseButton(e)) {
						final TreePath selectedPath = hostAndChannelTree.getPathForLocation(e.getX(), e.getY());

						if (null == selectedPath) {
							AddServerPopupCallback callback = new AddServerPopupCallback() {
								@Override
								public void onAddServerSelected() {
									try {
										String serverPath = JOptionPane.showInputDialog("input server path");
										if (null == serverPath) {
											return;
										}
										int port = 6667;
										if (serverPath.contains(":")) {
											String[] sprited = serverPath.split(":");
											serverPath = sprited[0];
											port = Integer.parseInt(sprited[1]);
										}
										DefaultMutableTreeNode hostNode = new DefaultMutableTreeNode(serverPath);

										connectingManager.connect(hostNode, serverPath, port, "ukiuni2", "localhost", "ukiuni2", handler);
										hostAndChannelTreeRootNode.add(hostNode);
										((DefaultTreeModel) hostAndChannelTree.getModel()).reload();
										System.out.println("hostNodePath = " + hostNode.toString());
										currentSelectHostNode = hostNode;

										settings.addHost(serverPath, port, "ukiuni2", "localhost");
										settings.save();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							};
							createAddServerPopup(callback, e.getXOnScreen(), e.getYOnScreen()).setVisible(true);
						} else {
							final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
							if (hostAndChannelTreeRootNode == selectedNode.getParent()) {
								ServerPopupCallback callback = new ServerPopupCallback() {
									public void onAddChannelSelected() {
										try {
											String channelName = JOptionPane.showInputDialog("input channelName");
											if (null == channelName) {
												return;
											}
											if (connectingManager.isJoining(selectedNode, channelName)) {
												return;
											}
											if (!channelName.startsWith("#")) {
												channelName = "#" + channelName;
											}
											connectingManager.joinToChannel(selectedNode, channelName);
											DefaultMutableTreeNode channelNode = new DefaultMutableTreeNode(channelName);
											selectedNode.add(channelNode);
											((DefaultTreeModel) hostAndChannelTree.getModel()).reload();
											hostAndChannelTree.expandRow(hostAndChannelTreeRootNode.getIndex(selectedNode));

											currentSelectHostNode = selectedNode;
											currentSelectChannelNode = channelNode;

											DefaultStyledDocument messageDocument = new DefaultStyledDocument();
											DefaultListModel userlist = new DefaultListModel();

											IRCClient client = connectingManager.getClient(currentSelectHostNode);

											UISet uiSet = new UISet(client.getHost(), client.getPort(), channelName, messageDocument, userlist);

											uiSetMap.put(currentSelectChannelNode, uiSet);
											System.out.println("regist = " + selectedNode.toString() + ":" + selectedNode.toString());

											UISet currentUISet = uiSetMap.get(currentSelectChannelNode);
											messageArea.setDocument(currentUISet.messageDocument);
											userList.setModel(currentUISet.userlist);

											settings.getHostSetting(client.getHost(), client.getPort()).addChannel(channelName);
											settings.save();
										} catch (Exception e) {
											e.printStackTrace();
										}
									};
								};
								createServerPopup(callback, e.getXOnScreen(), e.getYOnScreen()).setVisible(true);
							} else {
								ChannelPopupCallback callback = new ChannelPopupCallback() {
									public void onRemoveSelected() {
										int selectedOption = JOptionPane.showConfirmDialog(frame, "Remove Channel?", "", JOptionPane.YES_NO_OPTION);
										if (0 == selectedOption) {
											selectedNode.removeFromParent();
											try {
												connectingManager.partFromChannel(selectedNode.getParent(), selectedNode.toString());
											} catch (IOException e) {
												e.printStackTrace();
											}
										}
									};
								};
								createChannelPopup(callback, e.getXOnScreen(), e.getYOnScreen()).setVisible(true);
							}
						}
					} else {
						DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) hostAndChannelTree.getLastSelectedPathComponent();
						if (null != selectedNode && selectedNode.getParent() != hostAndChannelTreeRootNode) {
							System.out.println("load = " + selectedNode.getParent().toString() + ":" + selectedNode.toString());
							currentSelectHostNode = selectedNode.getParent();
							currentSelectChannelNode = selectedNode;
							UISet uiSet = uiSetMap.get(currentSelectChannelNode);
							messageArea.setDocument(uiSet.messageDocument);
							userList.setModel(uiSet.userlist);
						}
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});

		for (HostSetting hostSettings : settings.getHosts()) {

			DefaultMutableTreeNode hostNode = new DefaultMutableTreeNode(hostSettings.getHostShowName());
			hostAndChannelTreeRootNode.add(hostNode);
			connectingManager.connect(hostNode, hostSettings.getHostUrl(), hostSettings.getPort(), hostSettings.getNickname(), hostSettings.getMyHost(), hostSettings.getRealName(), handler);

			for (String channelName : hostSettings.getChannels()) {
				DefaultMutableTreeNode channelNode = new DefaultMutableTreeNode(channelName);
				hostNode.add(channelNode);
				DefaultStyledDocument messageDocument = new DefaultStyledDocument();
				DefaultListModel userlist = new DefaultListModel();

				UISet uiSet = new UISet(hostSettings.getHostUrl(), hostSettings.getPort(), channelName, messageDocument, userlist);
				uiSetMap.put(channelNode, uiSet);
				if (null == currentSelectChannelNode) {
					currentSelectChannelNode = channelNode;
				}
				if (null == currentSelectHostNode) {
					currentSelectHostNode = hostNode;
				}
				connectingManager.joinToChannel(hostNode, channelName);
			}
		}
		((DefaultTreeModel) hostAndChannelTree.getModel()).reload();

		frame.setBounds(10, 10, 650, 500);
		splitpane.setDividerLocation(500);
		leftPanel.setDividerLocation(350);
		rightPanel.setDividerLocation(350);
		frame.setVisible(true);
	}

	public static class UISet {
		public static UISet findUISet(String host, int port, String channel) {
			return createdMap.get(host + "\n" + port + "\n" + channel);
		}

		public UISet(String host, int port, String channel, DefaultStyledDocument messageDocument, DefaultListModel userlist) {
			this.host = host;
			this.port = port;
			this.channel = channel;
			this.messageDocument = messageDocument;
			this.userlist = userlist;
			createdMap.put(host + "\n" + port + "\n" + channel, this);
		}

		public static Map<String, UISet> createdMap = new HashMap<String, Test.UISet>();
		public final DefaultStyledDocument messageDocument;
		public final DefaultListModel userlist;
		public final String host;
		public final int port;
		public final String channel;
	}

	public static JPopupMenu createAddServerPopup(final AddServerPopupCallback callback, int x, int y) {
		final JPopupMenu popup = new JPopupMenu();
		popup.setLocation(x, y);
		JMenuItem item = new JMenuItem("Add Server");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				popup.setVisible(false);
				callback.onAddServerSelected();
			}
		});
		popup.add(item);
		return popup;
	}

	public static JPopupMenu createServerPopup(final ServerPopupCallback callback, int x, int y) {
		final JPopupMenu popup = new JPopupMenu();
		popup.setLocation(x, y);
		JMenuItem item = new JMenuItem("Add Channel");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				popup.setVisible(false);
				callback.onAddChannelSelected();
			}
		});
		popup.add(item);
		return popup;
	}

	public static JPopupMenu createChannelPopup(final ChannelPopupCallback callback, int x, int y) {
		final JPopupMenu popup = new JPopupMenu();
		popup.setLocation(x, y);
		JMenuItem item = new JMenuItem("Remove Channel");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				popup.setVisible(false);
				callback.onRemoveSelected();
			}
		});
		popup.add(item);
		return popup;
	}

	public static class AddServerPopupCallback {
		public void onAddServerSelected() {
		}
	}

	public static class ServerPopupCallback {
		public void onAddChannelSelected() {
		}
	}

	public static class ChannelPopupCallback {
		public void onRemoveSelected() {
		}
	}
}
