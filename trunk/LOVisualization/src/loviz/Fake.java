/*
  Fake visualization that implements the Visualization interface
  Copyright 2010 by Moti Ben-Ari under GNU GPL
*/
package loviz;
import javax.swing.*;

public class Fake implements Visualization {

  static JTextArea   area    = new JTextArea(20, 80);
  static JScrollPane scrollPane = new JScrollPane(area);
  static JFrame      frame = new JFrame();

  // Initialize the visualization, possibly with arguments
  // The visualization is to be displayed in the supplied frame
  // Alternatively, the visualization could supply the JFrame
  public void initialize(
    javax.swing.JFrame frame, String args[]) throws Exception {
  }

  public javax.swing.JFrame initialize(
      String args[]) throws Exception {
    String s = "";
    for (int i = 0; i < args.length; i++)
      s = s + args[i];
    frame.getContentPane().add(scrollPane);
    area.setFont(LOJel.font);
    area.setText("Initializing Jeliot with default options\n" + s);
    return frame;
  }

  // Load a file such as a program or algorithm to visualize
  public void load(String fileName) throws java.io.IOException {
   	JOptionPane.showMessageDialog(null, 
   	  "Start a Jeliot animation for program " + fileName);
  }

  // Get/Set internal options
  public String[] getOptions() { return new String[]{"", ""}; }

  public void     setOptions(String args[]) {
    String s = "";
    for (int i = 0; i < args.length; i++)
      s = s + args[i];
    JOptionPane.showMessageDialog(null,
      "Set the Jeliot options to " + s);
}

  // Run from start or run something, step, reset the visualization
  public void runFromStart()   throws Exception {
    JOptionPane.showMessageDialog(null,
      "Run Jeliot from the start");
  }

  public void run(String what) throws Exception {
  }

  public void step(int steps)  throws Exception {
    JOptionPane.showMessageDialog(null,
      "Run Jeliot for " + steps + " steps");
  }

  public void reset()          throws Exception {
    JOptionPane.showMessageDialog(null,
      "Reset Jeliot run");
  }

  // Query the visualization and return information
  //   such as the value of a variable
  // For an object, its toString would be returned
  public int    getIntValue   (String name) { return 0; }
  public double getDoubleValue(String name) { return 0.0; }
  public String getStringValue(String name) { return ""; }
  public String getObjectValue(String name) { return null; }
}
