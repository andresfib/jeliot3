/*
 * Created on Jul 1, 2004
 */
package jeliot.gui;

import java.awt.BorderLayout;
import java.awt.Image;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jeliot.mcode.Highlight;

/**
 * @author nmyller
 */
public class HistoryView extends JComponent {
	
	private Vector images = new Vector();
	
	private Vector highlights = new Vector();
	
	private JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 0, 0);

	private ImageCanvas ic = new ImageCanvas();
	
	private CodePane2 c;
		
	public HistoryView(final CodePane2 c) {
		this.c = c;
        setLayout(new BorderLayout());
        add("Center", new JScrollPane(ic));
        add("South", slider);
        
        slider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                int number = slider.getValue();
                if (number < images.size()) {
                	ic.setImage((Image) images.get(number));
                	if (highlights.get(number) != null) {
                		c.highlightStatement((Highlight) highlights.get(number));
                	}
                	ic.repaint();
                }
            }
        });
	}
	
	public void initialize() {
		images.removeAllElements();
		highlights.removeAllElements();
		ic.setImage(null);
	}
	
	public void addImage(Image i, Highlight h) {
		images.add(i);
		highlights.add(h);
		slider.setMaximum(images.size());
	}
}
