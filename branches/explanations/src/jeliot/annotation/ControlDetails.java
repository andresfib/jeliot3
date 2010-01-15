package jeliot.annotation;
import java.awt.*;
import java.awt.event.*;

import javax.swing.JButton;


public class ControlDetails implements ActionListener{
	   private boolean show = true;
	   private Component component;
	   private JButton button;

		public ControlDetails(JButton b,Component c)
		{
		  component = c;
		  button = b;
	
		}
		public void actionPerformed(ActionEvent e) { 
	            
	     String label = e.getActionCommand();
	     if(label.equals("Less details"))
	{
	     component.setVisible(!show);
	     button.setText("More details");
	     
	}
	     else 
	 	{
	 	     component.setVisible(show);
	 	     button.setText("Less details");
	 	}
		}
}
