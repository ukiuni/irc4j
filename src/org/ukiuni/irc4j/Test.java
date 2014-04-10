package org.ukiuni.irc4j;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.plaf.metal.MetalSplitPaneUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;

public class Test {
	public static void main(String[] args) {
		JFrame frame = new JFrame("test");

		JSplitPane splitpane = new JSplitPane();

		JSplitPane leftPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		leftPanel.setUI(new MetalSplitPaneUI());
		JPanel messagePane = new JPanel();
		messagePane.setLayout(new BorderLayout());
		final DefaultStyledDocument messageDocument = new DefaultStyledDocument();
		final JTextPane messageArea = new JTextPane(messageDocument);
		messagePane.add(new JScrollPane(messageArea), BorderLayout.CENTER);
		final JTextField inputField = new JTextField();
		messagePane.add(inputField, BorderLayout.SOUTH);

		final DefaultStyledDocument logDocument = new DefaultStyledDocument();
		final JTextPane logPane = new JTextPane(logDocument);

		leftPanel.setTopComponent(messagePane);
		leftPanel.setBottomComponent(new JScrollPane(logPane));

		inputField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String input = inputField.getText();
				if (!"".equals(input)) {
					SimpleAttributeSet attribute = new SimpleAttributeSet();
					try {
						messageDocument.insertString(messageDocument.getLength(), input + "\n", attribute);
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
					inputField.setText("");
				}
			}
		});

		JSplitPane rightPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		MetalSplitPaneUI rightVerticalSprit = new MetalSplitPaneUI();
		rightPanel.setUI(rightVerticalSprit);
		JList userList = new JList();
		rightPanel.setTopComponent(new JScrollPane(userList));

		JTree hostAndChannelTree = new JTree();
		hostAndChannelTree.setRootVisible(false);
		rightPanel.setBottomComponent(new JScrollPane(hostAndChannelTree));

		splitpane.setUI(new MetalSplitPaneUI());
		splitpane.setLeftComponent(leftPanel);
		splitpane.setRightComponent(rightPanel);

		frame.getContentPane().add(splitpane);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(10, 10, 500, 200);
		splitpane.setDividerLocation(400);

		frame.setVisible(true);
	}
}
