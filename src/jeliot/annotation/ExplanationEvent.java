package jeliot.annotation;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.ResourceBundle;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import sun.java2d.pipe.SpanShapeRenderer.Composite;

import jeliot.util.ResourceBundles;

import com.sun.awt.AWTUtilities;



public class ExplanationEvent {
	static private ResourceBundle messageBundle = ResourceBundles.getAnnotationMessageResourceBundle();
	Dimension d_screen = Toolkit.getDefaultToolkit ().getScreenSize();
	/*
	 *Set coordinates for message dialog to make it closed to the top-right of the screen.After
	 *trying several fractions for the width of the screen,it is proved the fraction should be bigger than 
	 *3/5,and smaller than 2/3.
	 */ 
	double x1= 5*d_screen.getWidth()/8;
	double y1= d_screen.getHeight()/22;
	int x= new Double(x1).intValue();
	int y= new Double(y1).intValue();
	
	//private JButton button = new JButton(messageBundle.getString("message.button1"));

	public void explanationDisplay(String name,String explanation){
		//First part:dialog part
		Object[] options = {messageBundle.getString("message.button")};
		//Object[] options = {messageBundle.getString("message.button"),messageBundle.getString("message.button1")};
		JOptionPane pane = new JOptionPane (name, JOptionPane.INFORMATION_MESSAGE, JOptionPane.OK_OPTION, null, options){
	        /* try to make JoptionPane translucent,but the following codes 
		     * make the whole part transparent."0.4f" doesn't work.
			protected void paintComponent(Graphics g) {  
     
		        The following codes comes from http://marioyohanes.wordpress.com/
		        Graphics2D g2 = (Graphics2D)g.create();
		        Paint p = new GradientPaint(
		                0, 0, new Color(0,0,0,0),
		                getWidth(), 0, new Color(0x272B39),
		                true
		            );
		        g2.setPaint(p);
		        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		        g2.dispose();
		        
			* Using the second way,it still doesn't work.
				if(isOpaque()){  
			    g.setColor(getBackground());
		        g.fillRect(0,0, getWidth(),getHeight());
			   }
		        
		    * Using the third way,still doesn't work.  
		        setOpaque(false);
		        Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                //g2d.setColor(new Color(0,0,0,0));
		        g2d.setBackground(new Color(0,0,0,0));
		        java.awt.Composite old = g2d.getComposite();
		        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
		        g2d.fillRect(0,0, getWidth(),getHeight());
		        g2d.setComposite(old);
		        g2d.dispose();
			}*/
		}; 

		JDialog dialog = pane.createDialog(null,messageBundle.getString("dialog.message.title"));
		Container content = dialog.getContentPane( );

		//Second part:text area for more information
		JTextArea text = new JTextArea (explanation, 8, 40){

			/*try to make JtextArea translucent,but the following codes 
			 * make the whole part transparent."derive(0.75f)" doesn't work.
			 
			    protected void paintComponent(Graphics g) {  
		        Graphics2D g2 = (Graphics2D)g.create();
		        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		        Composite old = (Composite) g2.getComposite();
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER).derive(0.75f));
				g2.fillRect(0,0, getColumns(),getRows());
				g2.setComposite((java.awt.Composite) old);
				g2.dispose();
			}*/	
		};
		
		//allow Wrapped text to fit in width
		text.setLineWrap (true);
		//wrapped at word boundaries
		text.setWrapStyleWord (true);
		text.setEditable(false);
		

		JScrollPane scroller =
			new JScrollPane (text, 
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		   /* try to make JscrollPane translucent.There are three steps:
		    * --set scroller translucent;
		    * --set getviewport translucent;
		    * --set text on the scroller translucent;
		    * However,these don't work.
		    scroller.setOpaque(false);
            scroller.getViewport().setOpaque(false);
		    text.setOpaque(false);*/
		
		//mix the first and second part 
		InfoPanel mix = new InfoPanel(content,scroller);
		dialog.setContentPane(mix);
		
		dialog.pack();
        /* Setlocation() method only has been put before Setvisible() method 
        because the size of the dialog has not been determined.*/
		dialog.setLocation(x, y);
		dialog.setVisible(true);


		/* This is for making Window translucent.Unfortunately,I use Jdialog as the 
		if (AWTUtilities.isTranslucencySupported(AWTUtilities.Translucency.TRANSLUCENT)) {
			//perform translucency operations here
		}*/
    } 


	}

