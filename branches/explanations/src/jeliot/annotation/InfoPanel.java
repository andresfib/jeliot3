/*Refer to <Swing Hacks>,Chapter 5,Hack 39 Spin Open a Detail Pane.
 The writers of this book are Chris Adamson and Joshua Marinacci.*/
package jeliot.annotation;

import java.awt.*; 

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import jeliot.util.ResourceBundles;

public class InfoPanel extends JPanel {
	
	ImageIcon icon = new ImageIcon("misc040.jpg");
	static private ResourceBundle messageBundle = ResourceBundles.getAnnotationMessageResourceBundle();
	public boolean show = false;
	public Component first;
	public Component second;
	private JButton button = new JButton(messageBundle.getString("message.button1"));

	public InfoPanel (Component top,Component bottom) {
		first = top;
		second = bottom;

		//ControlDetails control = new ControlDetails(button,second);
		ActionListener al = new ActionListener(){

			public void actionPerformed(ActionEvent e) { 

				show =! show;
				if(show)
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
					second.setVisible(!show);
					button.setText(messageBundle.getString("message.button1"));
					//show the bottom part again

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
	
	//make background of Jbutton grey
	protected void paintComponent(Graphics g){  
		Graphics2D g2d = (Graphics2D)g.create();
		Paint p = new GradientPaint(
                0, 0, new Color(0,0,0,0),
                getWidth()/2, 0, new Color(66,66,66),
                true
            );
        g2d.setPaint(p);
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        g2d.dispose();
		/*setOpaque(false);
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,            
				RenderingHints.VALUE_ANTIALIAS_ON);
		//g2d.setColor(new Color(0,0,0,0));
		//;
		Composite old = (Composite) g2d.getComposite();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
		g2d.fillRect(0,0, this.getWidth(),this.getHeight());
		g2d.setComposite((java.awt.Composite) old);
		g2d.dispose();*/
	}
}




