/*

  Universal Java interface to a visualization
  Copyright 2010 by Moti Ben-Ari under GNU GPL

  This interface is intended to enable pedagogical software
  (such as learning objects, learning management systems,
  interactive learning environments) to control visualizations
  written in Java which will implement the interface.

  The details of the parameters, etc., are to be specified separately
  for each visualization implementing the interface.
  
*/
package loviz;
public interface Visualization {

  // Initialize the visualization, possibly with arguments
  // The visualization is to be displayed in the supplied frame
  // Alternatively, the visualization could supply the JFrame
  public abstract void initialize(
    javax.swing.JFrame frame, String args[]) throws Exception;
  public abstract javax.swing.JFrame initialize(
    String args[]) throws Exception;

  public abstract javax.swing.JComponent initializeVisualization(
		    String args[]) throws Exception;

  // Load a file such as a program or algorithm to visualize
  public abstract void load(String fileName) throws java.io.IOException;

  // Get/Set internal options
  public abstract String[] getOptions();
  public abstract void     setOptions(String args[]);

  // Run from start or run something, step, reset the visualization
  public abstract void runFromStart()   throws Exception;
  public abstract void run(String what) throws Exception;
  public abstract void step(int steps)  throws Exception;
  public abstract void reset()          throws Exception;

  // Query the visualization and return information
  //   such as the value of a variable
  // For an object, its toString would be returned
  public abstract int    getIntValue   (String name);
  public abstract double getDoubleValue(String name);
  public abstract String getStringValue(String name);
  public abstract String getObjectValue(String name);
}
