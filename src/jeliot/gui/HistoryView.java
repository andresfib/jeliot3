/*
 * Created on Jul 1, 2004
 */
package jeliot.gui;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jeliot.mcode.Highlight;

/**
 * @author nmyller
 */
public class HistoryView extends JComponent implements ActionListener {

	/**
	 * Comment for <code>HISTORY_SIZE</code>
	 */
	private static final int HISTORY_SIZE = 15;
	
	/**
	 * Comment for <code>LIMIT_HISTORY_SIZE</code>
	 */
	private static final boolean LIMIT_HISTORY_SIZE = true;
	
	
	/**
	 * Comment for <code>images</code>
	 */
	private Vector images = new Vector();
	
	/**
	 * Comment for <code>highlights</code>
	 */
	private Vector highlights = new Vector();
	
	/**
	 * Comment for <code>buttonL</code>
	 */
	private final JButton buttonL = new JButton("<");
	
	/**
	 * Comment for <code>buttonR</code>
	 */
	private final JButton buttonR = new JButton(">");
	
	/**
	 * Comment for <code>slider</code>
	 */
	private JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 0, 0);

	/**
	 * Comment for <code>ic</code>
	 */
	private ImageCanvas ic = new ImageCanvas();
	
	/**
	 * Comment for <code>codePane</code>
	 */
	private CodePane2 codePane;
	
	/**
	 * Comment for <code>bottomComponent</code>
	 */
	private JPanel bottomComponent = new JPanel(new BorderLayout());
	
	/**
	 * @param c
	 */
	public HistoryView(final CodePane2 c) {
		this.codePane = c;
		
        setLayout(new BorderLayout());

        slider.setMajorTickSpacing(5);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(false);
        slider.setSnapToTicks(true);
        
        bottomComponent.add("Center", slider);
        bottomComponent.add("West", buttonL);
        bottomComponent.add("East", buttonR);
        
        add("Center", new JScrollPane(ic));
        add("South", bottomComponent);
        
        buttonL.setEnabled(false);
        buttonR.setEnabled(false);
        buttonL.addActionListener(this);
        buttonR.addActionListener(this);
        buttonR.setMnemonic(KeyEvent.VK_GREATER);
        buttonL.setMnemonic(KeyEvent.VK_LESS);
        
        slider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                int number = slider.getValue();
                if (number == slider.getMaximum()) {
                	buttonR.setEnabled(false);
                } else {
                	buttonR.setEnabled(true);
                }
                if (number == slider.getMinimum()) {
                	buttonL.setEnabled(false);
                } else {
                	buttonL.setEnabled(true);
                }
                if (number < images.size()) {
                	ic.setImage((Image) images.get(number));
                	if (highlights.get(number) != null) {
                		c.highlightStatement((Highlight) highlights.get(number));
                	}
                	ic.repaint();
                    validate();
                }
            }
        });
	}
	
	/**
	 * 
	 */
	public void initialize() {
		images.removeAllElements();
		highlights.removeAllElements();
		ic.setImage(null);
	}
	
	/**
	 * @param i
	 * @param h
	 */
	public void addImage(Image i, Highlight h) {
		images.add(i);
		highlights.add(h);
		if (LIMIT_HISTORY_SIZE && images.size() > HISTORY_SIZE) {
			images.remove(0);
			highlights.remove(0);
		}
		int size = images.size();
		slider.setMaximum(size - 1);
		slider.setValue(size - 1);
		int value = size - 1;
    	ic.setImage((Image) images.get(value));
    	if (highlights.get(value) != null) {
    		codePane.highlightStatement((Highlight) highlights.get(value));
    	}
		repaint();
        validate();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource().equals(buttonL)) {
			slider.setValue(slider.getValue() - 1);
		} else if (arg0.getSource().equals(buttonR)) {
			slider.setValue(slider.getValue() + 1);			
		}
	}
}
