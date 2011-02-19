// Copyright 2010 by Mordechai (Moti) Ben-Ari. See VN.java. */
package loviz;

import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/*
 * Config - configuration:
 *   Directories and file names and extensions
 *   Fonts, colors and sizes
 *   Button names and mnemonics
 *   Texts for messages and errors
 */

public class Config {

	static final String[] DEFAULT_ARGS = new String[] { "Use Null Parameter to Call Main" };

	static final String VERSION = " V0.1";
	static final String TITLE = "LO-Jel: Learning Objects for Jeliot" + VERSION;

	static private Properties properties = new Properties();
	static private final String FILE_NAME = "config.cfg";

	// Verbose output in progress pane
	static final boolean VERBOSE = true;

	private static void setDefaultProperties() {
		properties.put("SOURCE_DIRECTORY", "examples");
		properties.put("HELP_FILE_NAME", "txt\\help.txt");
		properties.put("ABOUT_FILE_NAME", "txt\\copyright.txt");

		properties.put("WIDTH", Integer.toString(1000));
		properties.put("HEIGHT", Integer.toString(720));
		properties.put("LEFT_WIDTH", Integer.toString(250));
		properties.put("TB_DIVIDER", Integer.toString(570));
	}

	static final String javaExt = ".java";

	static final int BUTTON_WIDTH = 70;
	static final int BUTTON_HEIGHT = 40;
	static final int TEXT_WIDTH = 150;

	static final String FONT_FAMILY = "Lucida Sans Typewriter";
	static final int FONT_STYLE = java.awt.Font.PLAIN;
	static final int FONT_SIZE = 12;

	static final String INPUT = "String or length: ";
	static final String OPEN = "Open";
	static final int OPENMN = KeyEvent.VK_O;
	static final String RUN = "Run";
	static final int RUNMN = KeyEvent.VK_R;
	static final String NEXT = "Next";
	static final int NEXTMN = KeyEvent.VK_N;
	static final String RESET = "Reset";
	static final int RESETMN = KeyEvent.VK_E;
	static final String HELP = "Help";
	static final int HELPMN = KeyEvent.VK_H;
	static final String ABOUT = "About";
	static final int ABOUTMN = KeyEvent.VK_A;
	static final String EXIT = "Exit";
	static final int EXITMN = KeyEvent.VK_X;

	static final String OPTIONS = "Options";
	static final int OPTIONSMN = KeyEvent.VK_P;
	static final String OK = "OK";
	static final int OKMN = KeyEvent.VK_O;
	static final String CANCEL = "Cancel";
	static final int CANCELMN = KeyEvent.VK_A;

	static final String FILE_ERROR = "File error ";
	static final String NO_JAVA_FILE = "First open a Java source file ";
	static final String READING = "Reading ";

	// Initialize configuration file
	static void init() {
		setDefaultProperties();
		FileInputStream in = null;
		try {
			in = new FileInputStream(FILE_NAME);
		} catch (FileNotFoundException e1) {
			System.out.println("Cannot open config file, creating new file");
			try {
				saveFile();
				in = new FileInputStream(FILE_NAME);
			} catch (IOException e2) {
				System.err.println("Cannot write config file");
			}
		}
		try {
			properties.load(in);
			in.close();
		} catch (IOException e3) {
			System.err.println("Cannot read config file");
		}
	}

	// Save configuration file
	static void saveFile() {
		try {
			FileOutputStream out = new FileOutputStream(FILE_NAME);
			properties.store(out, "Configuration file");
			out.close();
			System.out.println("Saved config file " + FILE_NAME);
		} catch (IOException e2) {
			System.err.println("Cannot write config file");
		}
	}

	// Interface to get/set propertyies of various types
	public static String getStringProperty(String s) {
		return properties.getProperty(s);
	}

	public static void setStringProperty(String s, String newValue) {
		properties.setProperty(s, newValue);
	}

	public static boolean getBooleanProperty(String s) {
		return Boolean.valueOf(properties.getProperty(s)).booleanValue();
	}

	public static void setBooleanProperty(String s, boolean newValue) {
		properties.setProperty(s, Boolean.toString(newValue));
	}

	public static int getIntProperty(String s) {
		return Integer.valueOf(properties.getProperty(s)).intValue();
	}

	public static void setIntProperty(String s, int newValue) {
		properties.setProperty(s, Integer.toString(newValue));
	}
}
