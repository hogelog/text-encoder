package org.hogel;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class MainWindow {
	
	private static final Logger LOG = LoggerFactory.getLogger(MainWindow.class);

	private JFrame frame;

	private JTextArea logTextArea;
	
	private JTable replaceTable;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel logPanel = new JPanel();
		logPanel.setToolTipText("");
		tabbedPane.addTab("変換ログ", null, logPanel, null);
		logPanel.setLayout(new BorderLayout(0, 0));
		
		logTextArea = new JTextArea();
		logPanel.add(logTextArea);
		logTextArea.setEditable(false);
		
		JPanel settingPanel = new JPanel();
		tabbedPane.addTab("設定", null, settingPanel, null);
		settingPanel.setLayout(new BorderLayout(0, 0));
		
		replaceTable = new JTable();
		settingPanel.add(replaceTable, BorderLayout.CENTER);
		
		JScrollPane scrollPane = new JScrollPane();
		settingPanel.add(scrollPane, BorderLayout.EAST);
	}

}
