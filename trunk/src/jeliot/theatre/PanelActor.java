package jeliot.theatre;

import java.awt.*;
import java.util.*;

/**
  * @author Pekka Uronen
  *
  * created         23.9.1999
  */
public class PanelActor extends Actor {

    private Image panelImage;
    private Image leftImage;
    private Image rightImage;

    private int displace;
    private int lgap, rgap;
    private int gapplace;
    private int biw, lbiw, rbiw;

    public PanelActor(
            Image panelImage,
            Image leftImage,
            Image rightImage,
            int gapplace        ) {
                
        this.panelImage = panelImage;
        this.leftImage = leftImage;
        this.rightImage = rightImage;
        this.gapplace = gapplace;
        this.biw = panelImage.getWidth(dummy);
        this.lbiw = leftImage.getWidth(dummy);
        this.rbiw = rightImage.getWidth(dummy);
    }

    public void setSize(int w, int h) {
        super.setSize(w, h);
    //    displace = (w/2)%biw - gapplace;
    }

    public void setGap(int lgap, int rgap) {
        this.lgap = lgap;
        this.rgap = rgap;
    }

    public void paintActor(Graphics g) {
        if (lgap == 0 && rgap == 0) {
            paintBackground(g, panelImage, 0, 0, width, height);
        }
        else {
            int leftn = 2;
            paintBackground(g, panelImage,
                    -lgap, 0,
                    leftn * biw, height);
            
            paintBackground(g, leftImage,
                    leftn* biw - lgap, 0,
                    lbiw, height);

            
            int dp = (leftn+1) * biw + rgap;
            paintBackground(g, rightImage,
                    dp - rbiw, 0,
                    rbiw, height);

            paintBackground(g, panelImage,
                    dp , 0,
                    width - dp, height);
        }
    }

     public Animation slide(final boolean open) {

        int leftn = 2;
        final int left = Math.max(0, leftn*biw + lbiw);
        final int right = Math.max(0, width - (leftn*biw) + rbiw);

        return new Animation() {        
            double l = 0;
            double lstep, rstep;
            double dlgap, drgap;
            
            public void animate(double pulse) {
                if (l == 0) {
                    long dur = getDuration();
                    lstep = (double)left / dur;
                    rstep = (double)right / dur;
                }
                lgap = open ? (int)dlgap : left - (int)dlgap;
                rgap = open ? (int)drgap : right - (int)drgap;
                
                dlgap += pulse * lstep;
                drgap += pulse * rstep;
                l = 1;
                
                if (drgap > right || dlgap > left) {
                    finish();
                }
                repaint();
            }     
        };
    }

}                   
