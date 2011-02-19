/*
  LO-Jel: Learning Objects for Jeliot
  Copyright 2010 by Mordechai (Moti) Ben-Ari.
 
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
02111-1307, USA.
*/

package loviz;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

public class LOJel extends JFrame implements ActionListener {

	// Fake implementation of the Visualization interface
	Visualization viz = new JeliotLOVisualization();
	// Counter for steps of the LO
	int stepCount;

	static File file;
	static String fileName;
	static String fileRoot;

	// User interface components
	static JTextArea messageArea = new JTextArea();
	static JTextArea textArea = new JTextArea();
	static JScrollPane messageScrollPane = new JScrollPane(messageArea);
	static JScrollPane textScrollPane = new JScrollPane(textArea);
	static JSplitPane topSplitPane;
	static JSplitPane mainSplitPane;

	public static java.awt.Font font = new java.awt.Font(Config.FONT_FAMILY, Config.FONT_STYLE, Config.FONT_SIZE);

	static JToolBar toolBar = new JToolBar();
	static JButton toolOpen = new JButton(Config.OPEN);
	static JButton toolRun = new JButton(Config.RUN);
	static JButton toolNext = new JButton(Config.NEXT);
	static JButton toolReset = new JButton(Config.RESET);
	static JButton toolOptions = new JButton(Config.OPTIONS);
	static JButton toolHelp = new JButton(Config.HELP);
	static JButton toolAbout = new JButton(Config.ABOUT);
	static JButton toolExit = new JButton(Config.EXIT);

	// Display messages
	static void fileError(String ext) {
		progress(Config.FILE_ERROR + fileName + ext);
	}

	static void progress(String s) {
		messageArea.append(s + "\n");
	}

	static void setFileName(File f) {
		file = f;
		fileName = f.getName();
		if (Config.VERBOSE)
			progress(Config.READING + fileName);
		fileName = f.getName().substring(0, fileName.lastIndexOf('.'));
		try {
			fileRoot = file.getCanonicalPath();
			fileRoot = fileRoot.substring(0, fileRoot.lastIndexOf('.'));
		} catch (IOException e) {
		}
	}

	private void openFile(File f) {
		messageArea.setText("");
		textArea.setText("");
		setFileName(f);
		setTitle(Config.TITLE + " - " + fileName);
	}

	// Listener
	public void actionPerformed(ActionEvent e) {
		int inputLength;
		if ((e.getSource() == toolOpen)) {
			JFileChooser fileChooser = new JFileChooser(Config.getStringProperty("SOURCE_DIRECTORY"));
			if (file != null)
				fileChooser.setSelectedFile(file);
			fileChooser.setFileFilter(new JavaFileFilter());
			if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
				return;
			else {
				File f = fileChooser.getSelectedFile();
				if (!f.exists()) {
					progress(Config.FILE_ERROR);
					return;
				} else
					openFile(f);
				textArea.setText(ConstructorText.introText);
				try {
					viz.load(f.getCanonicalPath());
				} catch (IOException ioex) {
				}
				stepCount = 0;
			}
		}

		else if (e.getSource() == toolRun) {
			if (file == null) {
				progress(Config.NO_JAVA_FILE);
				return;
			}
			try {
				viz.runFromStart();
			} catch (Exception e1) {
			}
		}

		else if (e.getSource() == toolNext) {
			if (file == null) {
				progress(Config.NO_JAVA_FILE);
				return;
			}
			try {
				textArea.setText(ConstructorText.steps[stepCount]);
				viz.step(ConstructorText.number[stepCount++]);
			} catch (Exception e2) {
			}
		}

		else if (e.getSource() == toolReset) {
			if (file == null) {
				progress(Config.NO_JAVA_FILE);
				return;
			}
			try {
				viz.reset();
			} catch (Exception e3) {
			}
		}

		else if (e.getSource() == toolOptions)
			viz.setOptions(new String[] { "Save on Compilation" });

		else if (e.getSource() == toolHelp)
			new DisplayFile(font, messageArea, Config.getStringProperty("HELP_FILE_NAME"), Config.HELP);

		else if (e.getSource() == toolAbout)
			new DisplayFile(font, messageArea, Config.getStringProperty("ABOUT_FILE_NAME"), Config.ABOUT);

		else if (e.getSource() == toolExit) {
			System.exit(0);
		}
	}

	void initToolButton(JButton item, int mnemonic) {
		item.setMaximumSize(new java.awt.Dimension(Config.BUTTON_WIDTH, Config.BUTTON_HEIGHT));
		toolBar.add(item);
		item.setMnemonic(mnemonic);
		item.addActionListener(this);
	}

	// Initialize toolbar
	void initToolBar() {
		toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.X_AXIS));
		toolBar.setFloatable(false);
		toolBar.setBorder(new javax.swing.border.LineBorder(java.awt.Color.BLUE));
		initToolButton(toolOpen, Config.OPENMN);
		toolBar.addSeparator();
		initToolButton(toolRun, Config.RUNMN);
		initToolButton(toolNext, Config.NEXTMN);
		initToolButton(toolReset, Config.RESETMN);
		toolBar.addSeparator();
		initToolButton(toolOptions, Config.OPTIONSMN);
		initToolButton(toolHelp, Config.HELPMN);
		initToolButton(toolAbout, Config.ABOUTMN);
		toolBar.addSeparator();
		initToolButton(toolExit, Config.EXITMN);
	}

	void init() {
		// Set properties of text areas
		textArea.setFont(font);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		messageArea.setFont(font);

		// Create menus and toolbar
		initToolBar();

		// Set up frame with panes
		JPanel framePanel = new JPanel();
		//JFrame frame = null;
		JComponent visComp = null;
		try {
			//frame = viz.initialize(Config.DEFAULT_ARGS);
			visComp = viz.initializeVisualization(Config.DEFAULT_ARGS);
		} catch (Exception e) {
			progress("Can't initialize Jeliot");
		}
		//framePanel.add(frame.getContentPane());
		framePanel.add(visComp);
		topSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		topSplitPane.setLeftComponent(textScrollPane);
		topSplitPane.setRightComponent(framePanel);
		//frame.setVisible(false);
		//framePanel.setVisible(true);
		// topSplitPane = new JSplitPane(
		// JSplitPane.HORIZONTAL_SPLIT, textScrollPane, new JPanel());
		topSplitPane.setOneTouchExpandable(true);
		topSplitPane.setDividerLocation(Config.getIntProperty("LEFT_WIDTH"));

		mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplitPane, messageScrollPane);
		mainSplitPane.setOneTouchExpandable(true);
		mainSplitPane.setDividerLocation(Config.getIntProperty("TB_DIVIDER"));

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new java.awt.BorderLayout());
		topPanel.add(toolBar, java.awt.BorderLayout.NORTH);

		JPanel contentPane = new JPanel();
		contentPane.setLayout(new java.awt.BorderLayout());
		contentPane.add(topPanel, java.awt.BorderLayout.NORTH);
		contentPane.add(mainSplitPane, java.awt.BorderLayout.CENTER);
		setContentPane(contentPane);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Configuration JFrame and make visible
		setFont(font);
		setTitle(Config.TITLE);
		setSize(Config.getIntProperty("WIDTH"), Config.getIntProperty("HEIGHT"));
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public static void main(java.lang.String[] args) {
		final String s = ((args.length > 0) ? args[0] + Config.javaExt : "");
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				LOJel lojel = new LOJel();
				Config.init();
				lojel.init();
				if (s != "") {
					File f = new File(s);
					if (!f.exists()) {
						System.err.println(Config.FILE_ERROR + s);
						System.exit(1);
					}
					LOJel.setFileName(f);
					lojel.setTitle(Config.TITLE + " - " + f);
				}
			}
		});
	}
}
