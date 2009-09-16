package jeliot.annotation;

import javax.swing.JOptionPane;
 
public class AnnotationEngine {
	public void explanationMCDisplay(){
		JOptionPane.showMessageDialog(null,"The following step is a method call,including main method,super method and etc.","Explanation",JOptionPane.PLAIN_MESSAGE);  }

	public void explanationVDDisplay(){
		JOptionPane.showMessageDialog(null,"The following step is a variable declaration","Explanation",JOptionPane.PLAIN_MESSAGE);  }

	public void explanationAADisplay(){
		JOptionPane.showMessageDialog(null,"The following step is a array allocation","Explanation",JOptionPane.PLAIN_MESSAGE);  }

	public void explanationAACDisplay(){
		JOptionPane.showMessageDialog(null,"The following step is about that array can be accessed","Explanation",JOptionPane.PLAIN_MESSAGE);  }

}
