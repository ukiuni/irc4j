package org.ukiuni.irc4j;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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

import org.ukiuni.irc4j.client.DCCMessage;
import org.ukiuni.irc4j.gui.ConnectingManager;
import org.ukiuni.irc4j.gui.HostSetting;
import org.ukiuni.irc4j.gui.Settings;
import org.ukiuni.irc4j.gui.UIFolder;
import org.ukiuni.irc4j.server.command.FileRecieveThread;

public class Test {
	public static TreeNode currentSelectHostNode;
	public static TreeNode currentSelectChannelNode;
	public static String myNickName;
	public static String myHostName = "localhost";
	public static String myRealName;

	public static void main(String[] args) throws IOException {
		final JFrame frame = new JFrame("aris");
		final UIFolder uiFolder = new UIFolder();

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
		myNickName = settings.getMyNickName();
		myRealName = settings.getMyRealName();
		myHostName = settings.getMyHostName();
		while (null == myNickName || "".equals(myNickName)) {
			myNickName = JOptionPane.showInputDialog("input nick name");
			myRealName = myNickName;
			settings.setMyNickName(myNickName);
			settings.setMyRealName(myRealName);
			settings.save();
		}
		inputField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String input = inputField.getText();
				if (null != currentSelectChannelNode && !"".equals(input)) {
					UISet currentUISet = uiFolder.get(currentSelectChannelNode);
					SimpleAttributeSet attribute = new SimpleAttributeSet();
					try {
						connectingManager.sendMessage(currentSelectHostNode, currentSelectChannelNode.toString(), input);
						currentUISet.messageDocument.insertString(currentUISet.messageDocument.getLength(), String.format("%1$-12s", myNickName) + ":" + input + "\n", attribute);
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
				if (channelName.equals(client.getNickName())) {
					channelName = from;
				}
				UISet recievedUISet = uiFolder.get(client.getHost(), client.getPort(), channelName);
				if (null == recievedUISet) {
					recievedUISet = appendNewChannelNode(uiFolder, hostAndChannelTreeRootNode, hostAndChannelTree, client, channelName);
				}
				try {
					recievedUISet.messageDocument.insertString(recievedUISet.messageDocument.getLength(), String.format("%1$-12s", from) + ":" + message + "\n", attribute);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onDisconnectedOnce(IRCClient client) {

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

			private boolean ifNext353AcceptedCleanUserList = true;;

			@Override
			public void onServerMessage(IRCClient client, int id, String message) {
				System.out.println("s:[" + id + "]" + message);
				SimpleAttributeSet attribute = new SimpleAttributeSet();
				try {
					logDocument.insertString(logDocument.getLength(), id + ":" + message + "\n", attribute);
					if (353 == id) {
						if (ifNext353AcceptedCleanUserList) {
							// connectingManager.clearAllUsers();
							ifNext353AcceptedCleanUserList = false;
						}

						Matcher m = Pattern.compile("(#.*):").matcher(message);
						m.find();
						String channelName = m.group(1).trim();
						String[] respondUsers = message.substring(message.lastIndexOf(":") + 1).split(" ");

						Set<String> users = connectingManager.getUsers(client.getHost(), client.getPort(), channelName);

						for (String userName : respondUsers) {
							if (!users.contains(userName) && users.contains("@" + userName)) {
								users.add(userName);
							}
						}

						UISet targetUISet = UISet.findUISet(client.getHost(), client.getPort(), channelName);
						targetUISet.userlist.removeAllElements();
						for (String userName : users) {
							targetUISet.userlist.addElement(userName);
						}
					} else if (366 == id) {
						ifNext353AcceptedCleanUserList = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onJoinToChannel(IRCClient client, String channelName, String nickName) {
				Set<String> users = connectingManager.getUsers(client.getHost(), client.getPort(), channelName);
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
				Set<String> users = connectingManager.getUsers(client.getHost(), client.getPort(), channelName);
				users.remove(nickName);
				UISet targetUISet = UISet.findUISet(client.getHost(), client.getPort(), channelName);
				targetUISet.userlist.removeAllElements();
				for (String userName : users) {
					targetUISet.userlist.addElement(userName);
				}
			}

			@Override
			public void onDCC(IRCClient client, String channelName, String from, DCCMessage dcc) {
				int selectedOption = JOptionPane.showConfirmDialog(frame, "file received from " + from + "\nname:" + dcc.fileName, "", JOptionPane.YES_NO_OPTION);
				if (0 == selectedOption) {
					FileDialog dialog = new FileDialog(frame, "save file", FileDialog.SAVE);
					dialog.setFile(dcc.fileName);
					dialog.setModal(true);
					dialog.setVisible(true);
					final String fullPath = dialog.getDirectory() + dialog.getFile();
					dialog.dispose();
					FileRecieveThread fileRecieveThread = new FileRecieveThread(dcc.targetHost, dcc.portNum, fullPath, dcc.fileSize, new FileRecieveThread.OnCompleteListener() {
						@Override
						public void onError(Throwable e) {
							e.printStackTrace();
						}

						@Override
						public void onComplete(String uploadedUri) {
							System.out.println("completed = "+uploadedUri);
						}
					});
					fileRecieveThread.start();
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

										connectingManager.connect(hostNode, serverPath, port, myNickName, myHostName, myRealName, handler);
										hostAndChannelTreeRootNode.add(hostNode);
										((DefaultTreeModel) hostAndChannelTree.getModel()).reload();
										currentSelectHostNode = hostNode;

										settings.addHost(serverPath, port, myNickName, myHostName);
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

											uiFolder.put(channelNode, uiSet, client.getHost(), client.getPort(), channelName);

											UISet currentUISet = uiFolder.get(currentSelectChannelNode);
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
							currentSelectHostNode = selectedNode.getParent();
							currentSelectChannelNode = selectedNode;
							UISet uiSet = uiFolder.get(currentSelectChannelNode);
							messageArea.setDocument(uiSet.messageDocument);
							userList.setModel(uiSet.userlist);
						}
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		userList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					String selectedUserNickName = (String) userList.getSelectedValue();
					if (null != selectedUserNickName) {
						if (0 == JOptionPane.showConfirmDialog(frame, "start talk?")) {
							appendNewChannelNode(uiFolder, hostAndChannelTreeRootNode, hostAndChannelTree, connectingManager.getClient(currentSelectHostNode), selectedUserNickName);
						}
					}
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
				uiFolder.put(channelNode, uiSet, hostSettings.getHostUrl(), hostSettings.getPort(), channelName);
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

	private static DefaultMutableTreeNode findHostTreeNode(final DefaultMutableTreeNode hostAndChannelTreeRootNode, String hostName) {
		Enumeration<DefaultMutableTreeNode> nodes = hostAndChannelTreeRootNode.children();
		DefaultMutableTreeNode hostNode = null;
		while (nodes.hasMoreElements()) {
			DefaultMutableTreeNode node = nodes.nextElement();
			if (node.toString().equals(hostName)) {
				hostNode = node;
				break;
			}
		}
		return hostNode;
	}

	private static UISet appendNewChannelNode(final UIFolder uiFolder, final DefaultMutableTreeNode hostAndChannelTreeRootNode, final JTree hostAndChannelTree, IRCClient client, String channelName) {
		UISet recievedUISet;
		DefaultMutableTreeNode channelNode = new DefaultMutableTreeNode(channelName);
		DefaultMutableTreeNode hostNode = null;

		hostNode = findHostTreeNode(hostAndChannelTreeRootNode, client.getHost());
		hostNode.add(channelNode);
		DefaultStyledDocument messageDocument = new DefaultStyledDocument();
		DefaultListModel userlist = new DefaultListModel();

		UISet uiSet = new UISet(client.getHost(), client.getPort(), channelName, messageDocument, userlist);
		uiFolder.put(channelNode, uiSet, client.getHost(), client.getPort(), channelName);
		recievedUISet = uiSet;

		((DefaultTreeModel) hostAndChannelTree.getModel()).reload();

		hostAndChannelTree.expandRow(hostAndChannelTreeRootNode.getIndex(hostNode));
		return recievedUISet;
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
