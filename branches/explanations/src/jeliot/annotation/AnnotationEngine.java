package jeliot.annotation;

import javax.swing.JOptionPane;

public class AnnotationEngine {
	private String name;
	/*private boolean isInConstructor()throws Exception{
		
		return true;
	}*/
	private void setConstructorCall(String methodCall){
		this.name = methodCall;
	}
	private String getConstructorCall(){
		return name;
	}
	public void explanationMCDisplay(String methodCall){
		setConstructorCall(methodCall);
		JOptionPane.showMessageDialog(null,"Constructor of the object of the class: " + getConstructorCall()+ "() is called","Explanation",JOptionPane.PLAIN_MESSAGE);  }

	/*public void explanationVDDisplay(){
		JOptionPane.showMessageDialog(null,"The following step is a variable declaration","Explanation",JOptionPane.PLAIN_MESSAGE);  }

	public void explanationAADisplay(){
		JOptionPane.showMessageDialog(null,"The following step is a array allocation","Explanation",JOptionPane.PLAIN_MESSAGE);  }

	public void explanationAACDisplay(){
		JOptionPane.showMessageDialog(null,"The following step is about that array can be accessed","Explanation",JOptionPane.PLAIN_MESSAGE);  }
	
	public void explanationRDisplay(){
		JOptionPane.showMessageDialog(null,"The following step indicates it will return a value","Explanation",JOptionPane.PLAIN_MESSAGE);  }

	public void explanationSADisplay(){
		JOptionPane.showMessageDialog(null,"The following step is about class allocation","Explanation",JOptionPane.PLAIN_MESSAGE);  }
	*/
}
