package jeliot.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.net.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

//import jeliot.parser.*;
import jeliot.theatre.*;
import jeliot.gui.*;

/** This is the component that shows and highlights the program while
  * Jeliot is animating.
  *
  * @author Pekka Uronen
  *
  * created         10.8.1999
  */
public class CodePane extends JComponent {


    /**
     * The text area where the program code is shown and highlighted.
     */
    JTextArea area = new JTextArea();
    {
        area.setMargin(new Insets(10, 10, 10, 10));
        area.setFont(new Font("Courier", Font.PLAIN, 12));
        area.setTabSize(4);
        area.setBackground(new Color(0xFFF8F0));
        area.setSelectionColor(new Color(0x990000));
        area.setSelectedTextColor(Color.white);
        area.setEditable(false);

    }


    /**
     * Constructs the CodePane -objects.
     * Sets the layout.
     * Adds the JScrollPane with JTextArea in the layout.
     */
    public CodePane() {
        setLayout(new BorderLayout());
        add("Center", new JScrollPane(area));
    }

    /**
     * Sets the given program code String text into the JTextArea area.
     *
     * @param text The program code to be set in the JTextArea area.
     */
    public void installProgram(String text) {
        area.setText(text);
    }

    /**
     * Method highlights the specified Statement area by selecting it.
     *
     * @param left The beginning of the selection.
     * @param right The end of the selection.
     */
    public void highlightStatement(Highlight h) {

        int l = 0, r = 0;

        try {
            if (h.getBeginLine() > 0) {
                l = area.getLineStartOffset(h.getBeginLine() - 1);
            }
            l += h.getBeginColumn();

            if (h.getEndLine() > 0) {
                r = area.getLineStartOffset(h.getEndLine() - 1);
            }
            r += h.getEndColumn();
        } catch (Exception e) { }

        final int left = l-1;
        final int right = r;

        Runnable updateAComponent = new Runnable() {
            public void run() {
                area.requestFocus();
                area.setCaretPosition(left+1);
                area.select(left, right);
            }
        };
        SwingUtilities.invokeLater(updateAComponent);
    }


}
