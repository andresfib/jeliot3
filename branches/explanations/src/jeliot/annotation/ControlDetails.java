package jeliot.annotation;
import java.awt.*;
import java.awt.event.*;

public class ControlDetails implements ActionListener{
	   private boolean show = true;
	   private Component component;
		public ControlDetails(Component c)
		{
		  component = c;	
		}
		public void actionPerformed(ActionEvent a) { 
	            
	     String label = a.getActionCommand();
	     if(label.equals("Less details"))
	{
	     component.setVisible(!show);
	   
	}
	     else if(label.equals("More details"))
	 	{
	 	     component.setVisible(show);
	 	   
	 	}
		}
}
