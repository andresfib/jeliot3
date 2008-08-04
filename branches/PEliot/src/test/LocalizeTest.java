package test;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class LocalizeTest extends JFrame {

	public static void main(String[] args) {
		new LocalizeTest();
	}
	
	public LocalizeTest()
	{
		ResourceBundle bundle = ResourceBundle.getBundle("jeliot.gui.resources.messages", Locale.getDefault());  //$NON-NLS-1$
                   
		JLabel hello = new JLabel(bundle.getString("LocalizeTest.1"));  //$NON-NLS-1$
		add(hello);
		
		JButton btn = new JButton(Messages.getString("LocalizeTest.0")); 
		add(btn);
		this.setSize(100, 100);
		setVisible(true);
	}
}
