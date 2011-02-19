package loviz;

import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFrame;

import jeliot.Jeliot;
import jeliot.gui.LoadJeliot;

public class JeliotLOVisualization extends Jeliot implements Visualization {

	public JeliotLOVisualization() {
		super("jeliot.io.*");
	}

	public JFrame initialize(String[] args) throws Exception {
		LoadJeliot.simpleStart(this);
		handleArgs(args);
		return gui.getFrame();
	}

	public JComponent initializeVisualization(String[] args) throws Exception {
		LoadJeliot.simpleStart(this);
		handleArgs(args);
		return gui.getTopSplitPane();
	}

	public void load(String fileName) throws IOException {
		final File programFile = new File(fileName);
		if (programFile.exists()) {
		//	SwingUtilities.invokeLater(new Runnable() {
		//		public void run() {
					setProgram(programFile);
		//		}
		//	});
		}
	}

	public void reset() throws Exception {
		compile(null);
	}

	public void runFromStart() throws Exception {
		compile(null);
		playAnimation();
	}

	public void step(int steps) throws Exception {
		int count = 0;
		while (count < steps) {
			if (playStepAnimation()) {
				count++;
			}
			Thread.sleep(10);
		}
		// TODO: add here a check if the animation has ended when this should end as well even though there are steps
		// left
	}

	public void run(String what) throws Exception {
		// TODO Auto-generated method stub
	}

	public void setOptions(String[] args) {
		// TODO Auto-generated method stub
	}

	public double getDoubleValue(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getIntValue(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getObjectValue(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getStringValue(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public void initialize(JFrame frame, String[] args) throws Exception {
		// TODO Auto-generated method stub
	}
}