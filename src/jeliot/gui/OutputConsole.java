package jeliot.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * OutputConsole is a text area on which the output of a user's
 * program is printed.
 *
 * @author Pekka Uronen
 *
 * created         3.10.1999
 */
public class OutputConsole extends JTextArea {

    /** A scroll pane that contains the output console. */
    public final JScrollPane container = new JScrollPane(this) {

            // If these methods didn't exist, the preferred size of
            // the console would grow as it is filled with text.
            // This would result to an ugly layout when the window is
            // resized. Hence this little hack.
            public Dimension getMaximumSize() {
                Dimension sms = super.getMaximumSize();
                return new Dimension(
                        sms.width,
                        model == null ?
                                sms.height :
                                model.getMaximumSize().height
                );
            }

            public Dimension getPreferredSize() {
                Dimension sps = super.getPreferredSize();
                return new Dimension(
                        sps.width,
                        model == null ?
                                sps.height :
                                model.getPreferredSize().height
                );
            }
        };

    /** A component that is queried for the preferred and maximum height of the console. */
    private Component model;

    /** The console's popup menu has one choice for emptying the console. */
    private JPopupMenu menu = new JPopupMenu(); {
        JMenuItem menuItem;
        menuItem = new JMenuItem("Clear");
        menu.add(menuItem);
        menuItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setText("");
                }
            }
        );
    }

    /** Creates a new output console.
      *
      *@param   model   The model is a component that is queried to set
      *         console's preferred and maximum height. May be null, in
      *         which case it has no effect (no error to be null).
      */
    public OutputConsole(Component model) {

        this.model = model;

        setFont(new Font("Courier", Font.BOLD, 14));
        setEditable(false);

        // create titled border
        TitledBorder title = BorderFactory.createTitledBorder(
                BorderFactory.createLoweredBevelBorder(), "Output");
        title.setTitlePosition(TitledBorder.ABOVE_TOP);
        container.setBorder(title);

        addMouseListener(
            new MouseAdapter() {
                public void mousePressed(MouseEvent evt) {
                    maybeShowPopup(evt);
                }
            }
        );
    }

    /**
      * Checks if a mouse event should pop up the popup menu. A bit of
      * a hack, because InputEvent.isPopupTrigger() doesn't seem to
      * work on Windows95/jdk1.1.7a/swing1.1.1.
      *
      * @param  evt The mouse event that is supposed to be a popup menu trigger.
      */
    private boolean isPopupTrigger(MouseEvent evt) {
        return evt.isPopupTrigger() ||
                (evt.getModifiers() | InputEvent.BUTTON2_MASK) != 0;
    }

    /** Checks if a mouse click is a popup menu trigger and if
      * it is, shows the popup menu.
      *
      * @param  evt The mouse event that is supposed to be a popup menu trigger.
      */
    private void maybeShowPopup(MouseEvent evt) {
        //System.err.println(evt);
        if (!isPopupTrigger(evt)) {
            return;
        }
        menu.show(this, evt.getX(), evt.getY());
    }

}