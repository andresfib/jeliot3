package jeliot.theatre;

import java.awt.*;
import java.util.*;

/**
  * @author Niko Myller
  */
public class LinesAndText extends Actor {

    /**
     * The resource bundle
     */
    static private ResourceBundle bundle = ResourceBundle.getBundle(
                                      "jeliot.theatre.resources.properties",
                                      Locale.getDefault());

    private Theatre theatre;
    private TheatreManager manager;

    private String constantArea = bundle.getString("string.constant_area");
    private String methodArea = bundle.getString("string.method_area");
    private String instanceArea = bundle.getString("string.instance_area");
    private String evaluationArea = bundle.getString("string.Evaluation_area");
    private int constantAreaWidth;
    private int methodAreaWidth;
    private int instanceAreaWidth;
    private int evaluationAreaWidth;

    public LinesAndText() {
        FontMetrics fm = dummy.getFontMetrics(font);
        constantAreaWidth = fm.stringWidth(constantArea);
        methodAreaWidth = fm.stringWidth(methodArea);
        instanceAreaWidth = fm.stringWidth(instanceArea);
        evaluationAreaWidth = fm.stringWidth(evaluationArea);
    }

    public LinesAndText(Theatre t, TheatreManager tm) {
        super();
        this.theatre = t;
        this.manager = tm;
    }

    public void setTheatre(Theatre t) {
        this.theatre = t;
    }

    public void setManager(TheatreManager tm) {
        this.manager = tm;
    }

    /** Draws the lines separating different areas
     *  and writes texts on them.
     */
    public void paintActor(Graphics g) {
        Dimension d = theatre.getSize();
        int w = d.width;
        int h = d.height;
        int methodX = manager.getScratchPositionX();
        int constantY = manager.getConstantPositionY();
        int instanceY = manager.getMinInstanceY();
        int instanceX = manager.getMinInstanceX();

        if (constantY < instanceY) {
            instanceY = constantY;
        }

        if (methodX < instanceX) {
            instanceX = methodX;
        }

        g.setColor(bgcolor);

        for (int i = instanceY-10; i > 0; i-=40) {
            g.drawLine(methodX-5, i, methodX-5, i-20);
            g.drawLine(methodX-6, i, methodX-6, i-20);
        }

        for (int i = instanceY+10; i < h; i+=40) {
            g.drawLine(instanceX-5, i, instanceX-5, i+20);
            g.drawLine(instanceX-6, i, instanceX-6, i+20);
        }

        for (int i = instanceX-10; i > 0; i-=40) {
            g.drawLine(i, constantY-10, i-20, constantY-10);
            g.drawLine(i, constantY-11, i-20, constantY-11);
        }

        for (int i = instanceX+10; i < w; i+=40) {
            g.drawLine(i, instanceY-10, i+20, instanceY-10);
            g.drawLine(i, instanceY-11, i+20, instanceY-11);
        }

        g.setColor(fgcolor);
        g.setFont(font);
        g.drawString(constantArea, (instanceX-constantAreaWidth)/2, constantY-11);
        g.drawString(methodArea, (methodX-methodAreaWidth)/2, 15);
        g.drawString(instanceArea, instanceX + (w-instanceX-instanceAreaWidth)/2, instanceY-11);
        g.drawString(evaluationArea, methodX + (w-methodX-evaluationAreaWidth)/2, 15);

    }
}