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


    private LineNumbers nb;
    private Font font = new Font("Courier", Font.PLAIN, 12);
    private Insets insets = new Insets(5, 5, 5, 5);
    private JScrollPane jsp;

    /**
     * The text area where the program code is shown and highlighted.
     */
    JTextArea area = new JTextArea();
    {
        area.setMargin(insets);
        area.setFont(font);
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
        add("Center", makeScrollPane());
        validateScrollPane();
    }

    public JComponent makeScrollPane() {
        jsp = new JScrollPane(area);
        nb = new LineNumbers(font, insets);
        jsp.setRowHeaderView(nb);
        validateScrollPane();
        return jsp;
    }

    /**
     * Sets the given program code String text into the JTextArea area.
     *
     * @param text The program code to be set in the JTextArea area.
     */
    public void installProgram(String text) {
        area.setText(text);
        validateScrollPane();
    }

    public int calculateLines(String text) {
        int lines = 1;
        int index = text.indexOf("\n");
        while (index >= 0) {
            lines++;
            index++;
            index = text.indexOf("\n", index);
        }
        return lines;
    }

    public void validateScrollPane() {
        final int lines = calculateLines(area.getText());

        if (nb != null) {
            Runnable updateAComponent = new Runnable() {
                public void run() {
                    nb.setHeightByLines(lines);
                }
            };
            SwingUtilities.invokeLater(updateAComponent);
        }
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
