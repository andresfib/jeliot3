package jeliot.theater;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * InputComponent is shown when ever the executed program
 * requests input. The InputComponent is rendered as a message
 * label and a text field that collects the input. 
 * 
 * @author Pekka Uronen
 * @author Niko Myller
 * 
 * @see jeliot.theater.Director#animateInputHandling(String,Highlight)
 */
public class InputComponent extends JPanel implements ActionListener {

//  DOC: Document!

    /**
	 *
	 */
	private InputValidator validator;
    
    /**
	 *
	 */
	private JTextField field;
    
    /**
	 *
	 */
	private JLabel label;
    
    /**
	 *
	 */
	private Actor bgactor;
    
    /**
	 * @param prompt
	 * @param validator
	 */
	public InputComponent(String prompt, InputValidator validator) {
        this.validator = validator;
        
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
            field = new JTextField(8);
            add(field);
        
            label = new JLabel(prompt);
            add(label);
        
        field.addActionListener(this);
	}
	
    /* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
        if (bgactor == null) {
            super.paintComponent(g);
        } else {
            bgactor.paintActor(g);
            bgactor.paintShadow(g);
        }
    }
    
    /**
	 * @param actor
	 */
	public void setBgactor(Actor actor) {
        this.bgactor = actor;
        Font font = actor.getFont();
        label.setFont(font);
        field.setFont(font);
    }

    /**
	 * 
	 */
	public void popup() {
       field.requestFocus();
       bgactor.setSize(getSize());
    }

    /* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt) {
        String text = field.getText();
        validator.validate(text);
    }
	
}
