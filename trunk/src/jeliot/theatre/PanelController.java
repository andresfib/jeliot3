package jeliot.theatre;

import java.awt.*;

public class PanelController {

    PanelActor panel;
    Theatre theatre;
    AnimationEngine engine;
    Image bgImage;
    Image panelImage;

    int openDur = 1100;
    int closeDur = 900;

    public PanelController(Theatre theatre, ImageLoader iLoad) {
        this.theatre = theatre;
        this.engine = new AnimationEngine(theatre);
        this.bgImage = iLoad.getLogicalImage("Background");
        this.panelImage = iLoad.getLogicalImage("Panel");
        this.panel = new PanelActor(
                panelImage,
                iLoad.getLogicalImage("Panel-left"),
                iLoad.getLogicalImage("Panel-right"), 62);
    }

    public Thread slide(final boolean open, final Runnable next) {
        return new Thread() {
            public void run() {
                int dur;
                if (open) {
                    panel.setGap(0, 0);
                    dur = openDur;
                }
                else {
                    panel.setGap(1000, 1000);
                    dur = closeDur;
                }
                // animation
                panel.setSize(theatre.getWidth(), theatre.getHeight());
                theatre.addActor(panel);
                if (open) {
                    theatre.setBackground(bgImage);
                }
                theatre.capture();
                Animation anim = panel.slide(open);
                anim.setDuration(dur);
                engine.showAnimation(anim);
                theatre.removeActor(panel);
                if (!open) {
                    theatre.setBackground(panelImage);
                    theatre.cleanUp();
                }
                theatre.release();
                next.run();
            }
        };
    }
}
