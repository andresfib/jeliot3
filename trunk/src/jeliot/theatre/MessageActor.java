package jeliot.theatre;

import java.awt.*;

/**
  * @author Pekka Uronen
  *
  * created         17.8.1999
  */
public class MessageActor extends Actor {

    private int borderw = 2;
    private int padding = 5;
    private int fheight;

    private String[] text;

    private Image backImage;

    public void setText(String[] text) {
        this.text = text;
    }

    public void setBackground(Image backImage) {
        this.backImage = backImage;
    }

    public void calculateSize() {
        FontMetrics fm = getFontMetrics();
        int w = 0;
        int h = fheight = fm.getHeight();
        int n = text.length;
        for (int i = 0; i < n; ++i) {
            int l = fm.stringWidth(text[i]);
            w = (l > w) ? l : w;
        }
        w += 2 * (borderw + padding);
        h += 2 * (borderw + padding);
        setSize(w, h);
    }

    public void paintActor(Graphics g) {

        int w = getWidth();
        int h = getHeight();

        // draw background
        if (backImage == null) {
            g.setColor(bgcolor);
            g.fillRect(borderw, borderw, w-borderw*2, h-borderw*2);
        }
        else {
            paintBackground(g, backImage,
                    borderw, borderw, w-borderw*2, h-borderw*2);
        }
        // draw text
        if (text != null) {
            g.setFont(getFont());
            g.setColor(fgcolor);
            int x = borderw + padding;
            int y = borderw + padding + fheight;
            int n = text.length;
            for (int i = 0; i < n; ++i) {
                g.drawString(text[i], x, y);
                y += fheight;
            }
        }

        // draw border
        g.setColor(darkColor);
        for (int i = 1; i < 2; ++i) {
            g.drawRect(i, i, w-i*2-1, h-i*2-1); 
        }
        g.setColor(fgcolor);
        g.drawRect(0, 0, w-1, h-1);

    }
}       
