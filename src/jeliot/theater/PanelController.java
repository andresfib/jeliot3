package jeliot.theater;

import java.awt.Image;
import java.util.Locale;
import java.util.ResourceBundle;

/**
  * @author Pekka Uronen
  * @author Niko Myller
  */
public class PanelController {

   /**
     * The resource bundle
     */
    static private ResourceBundle bundle = ResourceBundle.getBundle(
                                      "jeliot.theater.resources.properties",
                                      Locale.getDefault());

    private PanelActor panel;
    private Theater theatre;
    private AnimationEngine engine;
    private Image bgImage;
    private Image panelImage;

    private int openDur = Integer.parseInt(bundle.getString("curtain.open_duration"));
    private int closeDur = Integer.parseInt(bundle.getString("curtain.close_duration"));

    public PanelController(Theater theatre, ImageLoader iLoad) {
        this.theatre = theatre;
        this.engine = new AnimationEngine(theatre);
        this.bgImage = iLoad.getImage(bundle.getString("image.background"));
        this.panelImage = iLoad.getImage(bundle.getString("image.panel"));
        this.panel = new PanelActor(
                panelImage,
                iLoad.getImage(bundle.getString("image.panel.left")),
                iLoad.getImage(bundle.getString("image.panel.right")), 62);
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
