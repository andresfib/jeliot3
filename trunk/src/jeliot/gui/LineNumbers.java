package jeliot.gui;

import java.awt.*;
import javax.swing.*;

public class LineNumbers extends JComponent {

    private int size = 35;
    private Font font;
    private int ascent;
    private int descent;
    private int increment;
    private Insets insets;

    public LineNumbers(Font font, Insets insets) {
        this.font = font;
        this.insets = insets;
        FontMetrics fm = getFontMetrics(font);
        size = fm.stringWidth("000") + 6;
        ascent = fm.getAscent();
        descent = fm.getDescent();
        increment = ascent + descent;
    }

    public void setPreferredHeight(int ph) {
        setPreferredSize(new Dimension(size, ph));
        revalidate();
        repaint();
    }

    public void paintComponent(Graphics g) {
        Rectangle drawHere = g.getClipBounds();

        //drawHere.height += 50;

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
