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
  * @author Pekka Uronen
  *
  * created         25.8.1999
  * revised         4.10.1999
  */
public class InputComponent extends JPanel implements ActionListener {

    private InputValidator validator;
    private JTextField field;
    private JLabel label;
    
    private Actor bgactor;
    
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
	
    public void paintComponent(Graphics g) {
        if (bgactor == null) {
            super.paintComponent(g);
        }
        else {
            bgactor.paintActor(g);
            bgactor.paintShadow(g);
        }
    }
    
    public void setBgactor(Actor actor) {
        this.bgactor = actor;
        Font font = actor.getFont();
        label.setFont(font);
        field.setFont(font);
    }

    public void popup() {
       field.requestFocus();
       bgactor.setSize(getSize());
    }

    public void actionPerformed(ActionEvent evt) {
        String text = field.getText();
        validator.validate(text);
    }
	
}
