package jeliot.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JComponent;


/**
 * The LineNumbers component is used to show the line numbers in the
 * scroll panes left side in the code view and code editor.
 * 
 * @author Niko Myller
 * @see jeliot.gui.CodePane
 * @see jelio.gui.CodeEditor
 */
public class LineNumbers extends JComponent {

    /**
	 * The width of the component.
	 */
	private int size = 35;
    
    /**
	 * The font for this component.
	 */
	private Font font;
    
    /**
	 * The ascent of the font.
	 */
	private int ascent;
    
    /**
	 * The increment between two lines.
	 */
	private int increment;
    
    /**
	 * insets in the component.
	 */
	private Insets insets;

    /**
     * Sets the font and the insets and the determines the size
     * increment and ascent from the font's font metrics.
	 * @param font the font to be used in the component
	 * @param insets the insets for the layout.
	 */
	public LineNumbers(Font font, Insets insets) {
        this.font = font;
        this.insets = insets;
        FontMetrics fm = getFontMetrics(font);
        size = fm.stringWidth("000") + 6;
        increment = fm.getHeight();
        ascent = fm.getAscent();
    }

    /**
     * Sets the preferred height of the component.
	 * @param ph
	 */
	public void setPreferredHeight(int ph) {
        setPreferredSize(new Dimension(size, ph));
        revalidate();
    }


    /**
     * sets the height by the given number of lines that should be shown.
	 * @param lines
	 */
	public void setHeightByLines(int lines) {
        int height = insets.top + ascent + (lines * increment) + insets.bottom;
        //System.out.println("CodePane: " + height);
        setPreferredSize(new Dimension(size, height));
        revalidate();
    }

    /*
     * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
        Rectangle drawHere = g.getClipBounds();

        g.setColor(new Color(204, 204, 204));
        g.fillRect(drawHere.x, drawHere.y, drawHere.width, drawHere.height);

        // Do the ruler labels in the font that's black.
        g.setFont(font);
        g.setColor(Color.black);

        // Some vars we need.
        int end = 0;
        int start = 0;

        start = (drawHere.y / increment) * increment;

        end = (((drawHere.y + drawHere.height) / increment) + 1)
              * increment;

        int lineNumber = (int) Math.floor(drawHere.y / increment) + 1;

        start += insets.top + ascent;
        end += insets.top + ascent;

        //labels
        for (int i = start; i < end; i += increment) {
            g.drawString(Integer.toString(lineNumber), 3, i);
            lineNumber++;
        }
    }
}