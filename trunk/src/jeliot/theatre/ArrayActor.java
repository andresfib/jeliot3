package jeliot.theatre;

import java.awt.*;
import java.util.*;
import jeliot.lang.*;
import java.lang.reflect.*;

/**
  * @author Pekka Uronen
  * created         10.10.1999
  * @modified Niko Myller
  * modified        10.5.2003
  */
public class ArrayActor extends InstanceActor {

    private Object variableActors;

    private Color valueColor;

    /** The x-coordinate of the vertical line separating indices from
      * values. */
    private int vlinex;

    /** The width of a cell reserved for a single value actor. */
    private int valuew;

    /** The height of a single value actor. */
    private int valueh;

    /** The width of an index label. */
    private int indexw;

    private int[] dimensions;

    public ArrayActor(Object valueActors, int[] dimensions) {

        this.dimensions = dimensions;
        this.variableActors = Array.newInstance(
                                (new VariableInArrayActor()).getClass(),
                                dimensions);

        int n = dimensions.length;

        int[] index = new int[n];

        for (int i = 0; i < n; i++) {
            index[i] = 0;
        }

        do {

            Object tempArray = valueActors;
            Object tempArray2 = variableActors;
            String indexString = "";
            for (int i = 0; i < n - 1; i++) {
                indexString += "[" + Integer.toString(i) + "]";
                tempArray = Array.get(tempArray, index[i]);
                tempArray2 = Array.get(tempArray2, index[i]);
            }

            for (int i = 0; i < dimensions[n-1]; i++) {
                VariableInArrayActor viaa = new
                                 VariableInArrayActor(this,
                                 indexString + "[" + Integer.toString(i) + "]");



                ValueActor va = (ValueActor) Array.get(tempArray, i);
                viaa.setValue(va);
                viaa.setParent(this);
                Array.set(tempArray2, i, viaa);
            }

        } while (ArrayUtilities.nextIndex(index, dimensions));

    }

    public void setValueColor(Color valueColor) {

        int n = dimensions.length;

        int[] index = new int[n];

        for (int i = 0; i < n; i++) {
            index[i] = 0;
        }

        do {

            for (int i = 0; i < dimensions[n-1]; i++) {
                index[n-1] = i;
                VariableInArrayActor viaa =
                   (VariableInArrayActor) ArrayUtilities.getObjectAt(
                                                 variableActors, index);

                viaa.setValueColor(valueColor);
            }

        } while (ArrayUtilities.nextIndex(index, dimensions));

    }

    public VariableActor getVariableActor(int[] index) {
        return (VariableActor) ArrayUtilities.getObjectAt(variableActors,
                                                          index);
    }

    public void calculateSize(int valuew, int valueh) {

        FontMetrics fm = getFontMetrics();
        this.valuew = valuew;
        this.valueh = valueh;
        this.indexw = fm.stringWidth("[00]");

        if (dimensions.length == 1) {

            if (dimensions[0] == 0) {
                int w = indexw + Math.max(fm.stringWidth("EMPTY"),
                                     fm.stringWidth("ARRAY"));
                int h = 10 + 2 * (fm.getHeight());
                setSize(w, h);

            } else {

                int n = dimensions[0];
                int w = 6 + valuew + indexw;
                int h = 3 + (valueh + 1) * n;
                setSize(w, h);

                int x = 2;
                int y = 2;
                for (int i = 0; i < n; ++i) {
                    VariableInArrayActor viaa =
                        (VariableInArrayActor) Array.get(variableActors, i);
                    viaa.setSize(valuew, valueh);
                    viaa.setLocation(x, y);
                    viaa.calculateSize(indexw, valuew, valueh);
                    y += 1 + valueh;
                }
            }
        } else if (dimensions.length == 2) {
            //Two dimensional array
            //Do this!
            //Not done yet.
        } else {
            //n dimensional arrays are not implemented (n > 2)
            //Needs to be catched earlier.
        }
    }

    public void paintActor(Graphics g) {

        int w = this.width;
        int h = this.height;
        int bw = 2;
        FontMetrics fm = getFontMetrics();
        int fonth = fm.getHeight();
        int word1w = fm.stringWidth("EMPTY");
        int word2w = fm.stringWidth("ARRAY");

        if (dimensions.length == 1) {

            int n = dimensions[0];

            if (n == 0) {

                int word1x = (w - word1w) / 2;
                int word2x = (w - word2w) / 2;

                int word1y = h/2 - fonth/2;
                int word2y = h/2 + fonth/2;

                // fill the area
                g.setColor(lightColor);
                g.fillRect(0, 0, w-1, h-1);

                // draw border
                g.setColor(borderColor);
                g.drawRect(0, 0, w-1, h-1);
                g.setColor(darkColor);
                g.drawRect(1, 1, w-3, h-3);

                g.setColor(fgcolor);
                g.setFont(font);
                g.drawString("EMPTY", word1x, word1y);
                g.drawString("ARRAY", word2x, word2y);

            } else {

                // draw cells
                for (int i = 0; i < n; ++i) {

                    VariableInArrayActor a =
                         (VariableInArrayActor) Array.get(variableActors, i);

                    int x = a.getX();
                    int y = a.getY();
                    g.translate(x, y);
                    a.paintActor(g);
                    g.translate(-x, -y);
                }

                // draw border
                g.setColor(borderColor);
                g.drawRect(0, 0, w-1, h-1);
                g.setColor(darkColor);
                g.drawRect(1, 1, w-3, h-3);

                // draw vertical line
                int vlinex = 2 + indexw;
                g.drawLine(vlinex, bw, vlinex, h-2);
                g.drawLine(vlinex+1, bw, vlinex+1, h-2);

                // draw horizontal lines
                int x1 = bw, x2 = w - 2 * bw;
                int yc = bw - 1;
                for (int i = 1; i < n; ++i) {
                    yc += 1 + valueh;
                    g.drawLine(x1, yc, x2, yc);
                }
            }

        } else if (dimensions.length == 2) {
            //Two dimensional array
            //Do this!
            //Not done yet.
        } else {
            //n dimensional arrays are not implemented (n > 2)
            //Needs to be catched earlier.
        }

    }

}