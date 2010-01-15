/*Refer to <Swing Hacks>,Chapter 5,Hack 39 Spin Open a Detail Pane.
 * The writers of this book are Chris Adamson and Joshua Marinacci.*/
package jeliot.annotation;

import java.awt.*; 
import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import jeliot.util.ResourceBundles;

public class InfoPanel extends JPanel {
        static private ResourceBundle messageBundle = ResourceBundles.getTheaterMessageResourceBundle();
	    private boolean show = true; 
        public Component first;
        public Component second;
        private JButton button = new JButton(messageBundle.getString("message.button1"));
        
       
        public InfoPanel (Component top, Component bottom) {
            first = top;
            second = bottom;
            //ControlDetails control = new ControlDetails(button,second);
            ActionListener al = new ActionListener(){
           	 
                public void actionPerformed(ActionEvent e) { 
             	            
             	     String label = e.getActionCommand();
             	     if(label.equals(messageBundle.getString("message.button1")))
             	    {
             	     second.setVisible(!show);
             	     button.setText(messageBundle.getString("message.button2"));
            	     if (isShowing()) 
            	     {
                         Container ancestor = getTopLevelAncestor( ); 
                         if ((ancestor != null) && (ancestor instanceof Window))        
                             ((Window) ancestor).pack( );
                         repaint( );
                      }
             	    }
             	     else 
             	 	{    

             	 	     button.setText(messageBundle.getString("message.button1"));
             	 	     //show the bottom part again
             	 	     second.setVisible(show);
                	     if (isVisible()) 
                	     {
                             Container ancestor = getTopLevelAncestor( ); 
                             if ((ancestor != null) && (ancestor instanceof Window))        
                                 ((Window) ancestor).pack( );
                             repaint( );
                          }

             	 	}	 
                }
                };
            button.addActionListener(al);
            myLayout( );
        }

        protected void myLayout() {
            setLayout (new BoxLayout (this, BoxLayout.Y_AXIS));
            add (first);
            add (button);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            add (second);   
        }
        
 

}




