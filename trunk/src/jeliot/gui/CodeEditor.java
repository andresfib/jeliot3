/**
 * The package that contains Jeliot 2000's GUI
 */
package jeliot.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.net.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import jeliot.parser.*;
import jeliot.theatre.*;
import jeliot.gui.*;

/**
  * The simple code editor for the users to make the code for the animation theater.
  *
  * @author Pekka Uronen
  *
  * created         10.8.1999
  */
public class CodeEditor extends JComponent {


    /**
     * The String for the basic code template that is shown to the user on the start of the Jeliot 2000.
     */
    private String template =
        "\n" +
        "public class MyClass {\n" +
        "    public static void main() {\n\n" +
        "        // Your algorithm goes here.\n" +
        "    }\n" +
        "}";

    /**
     * Initialization of the text area for the user code.
     */
    JTextArea area = new JTextArea();
    {
        area.setMargin(new Insets(10, 10, 10, 10));
        area.setFont(new Font("Courier", Font.PLAIN, 12));
        area.setTabSize(4);
        clearProgram();
    }


    /**
     * The file chooser in which the users can load and save the program codes.
     */
    private JFileChooser fileChooser;


    /**
     * The master frame.
     */
    private JFrame masterFrame;


    /**
     * ActionListener that handles the saving of the program code from the code area.
     */
    private ActionListener saver = new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            saveProgram();
        }
    };


    /**
     * ActionListener that handles the loading of the program code to the code area.
     */
    private ActionListener loader = new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            loadProgram();
        }
    };


    /**
     * ActionListener that handels the clearing of the code area.
     */
    private ActionListener clearer = new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            clearProgram();
        }
    };


    private ActionListener exit = new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    };


    /**
     * ActionListener that handels the clearing of the code area.
     */
    private ActionListener cutter = new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            area.cut();
        }
    };

    /**
     * ActionListener that handels the copying of the code area.
     */
    private ActionListener copyist = new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            area.copy();
        }
    };

    /**
     * ActionListener that handels the pasting of the code area.
     */
    private ActionListener pasteur = new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            area.paste();
        }
    };

    /**
     * ActionListener that handels the selection of the whole code area.
     */
    private ActionListener allSelector = new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            area.selectAll();
        }
    };


    /**
     * Supposed to set the given frame as a masterframe
     * Now it just sets the masterFrame as masterFrame
     *
     * @param   frame   The Frame that should be set as new masterFrame
     */
    public void setMasterFrame(JFrame frame) {
        this.masterFrame = masterFrame;
    }


    /**
     * Sets the layout and adds the JScrollPane with JTextArea area and JToolbar in it.
     * Initializes the FileChooser.
     */
    public CodeEditor() {
        initFileChooser();
        setLayout(new BorderLayout());
        add("Center", new JScrollPane(area));
        add("North", makeToolBar());
    }

    /**
     * Sets up the file chooser with the user's working directory as default directory.
     */
    private void initFileChooser() {
        // set up the file chooser with user's working
        // directory as default directory
        Properties prop = System.getProperties();
        String wdname = prop.getProperty("user.dir");
        File wd = new File(wdname);
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(wd);
    }


    /**
     * Makes the JButton from the parameters given.
     *
     * @param   label   The label of the button.
     * @param   iconName The name of the image for the button.
     * @param   listener The actionlistener for that button.
     * @return  The constructed button from the given parameters.
     */
    private JButton makeToolButton(String label, String iconName,
            ActionListener listener) {

        ImageIcon icon = new ImageIcon("images/"+iconName);
        JButton b = new JButton(label, icon);
        b.setVerticalTextPosition(AbstractButton.BOTTOM);
        b.setHorizontalTextPosition(AbstractButton.CENTER);
        b.setMargin(new Insets(0,0,0,0));
        b.addActionListener(listener);
        return b;
    }


    /**
     * The method makes the Buttons for the toolbar of the codearea.
     * Then it adds the button to the JToolBar and returns it.
     * Uses makeToolButton(String, String, ActionListener) -method.
     *
     * @return  The finished toolbar for the code editor.
     * @see #makeToolButton(String, String, ActionListener)
     */
    private JToolBar makeToolBar() {
        JButton loadButton = makeToolButton("Open", "openicon.gif", loader);
        loadButton.setMnemonic(KeyEvent.VK_O);
        JButton saveButton = makeToolButton("Save", "saveicon.gif", saver);
        saveButton.setMnemonic(KeyEvent.VK_S);
        JButton clearButton = makeToolButton("New", "newicon.gif", clearer);
        clearButton.setMnemonic(KeyEvent.VK_N);

        JButton cutButton = makeToolButton("Cut", "cuticon.gif", cutter);
        cutButton.setMnemonic(KeyEvent.VK_X);
        JButton copyButton = makeToolButton("Copy", "copyicon.gif", copyist);
        copyButton.setMnemonic(KeyEvent.VK_C);
        JButton pasteButton = makeToolButton("Paste", "pasteicon.gif", pasteur);
        pasteButton.setMnemonic(KeyEvent.VK_P);

        JToolBar p = new JToolBar();
        p.add(clearButton);
        p.add(loadButton);
        p.add(saveButton);
        p.addSeparator();
        p.add(cutButton);
        p.add(copyButton);
        p.add(pasteButton);
        return p;
    }

    /**
     * Constructs the Program menu.
     *
     * @return  The Program menu (JMenu) for the Jeliot 2000
     */
    JMenu makeProgramMenu() {
        JMenu menu = new JMenu("Program");
        menu.setMnemonic(KeyEvent.VK_P);
        JMenuItem menuItem;

        menuItem = new JMenuItem("New");
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(clearer);
        menu.add(menuItem);

        menuItem = new JMenuItem("Open", KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(loader);
        menu.add(menuItem);

        menuItem = new JMenuItem("Save");
        menuItem.setMnemonic(KeyEvent.VK_S);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(saver);
        menu.add(menuItem);

        menuItem = new JMenuItem("Exit");
        menuItem.setMnemonic(KeyEvent.VK_Q);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(exit);
        menu.add(menuItem);

        return menu;
    }


    /**
     * Constructs the Edit menu.
     *
     * @return  The Edit menu (JMenu) for the Jeliot 2000
     */
    JMenu makeEditMenu() {
        JMenu menu = new JMenu("Edit");
        menu.setMnemonic(KeyEvent.VK_E);
        JMenuItem menuItem;

        menuItem = new JMenuItem("Cut");
        menuItem.setMnemonic(KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(cutter);
        menu.add(menuItem);

        menuItem = new JMenuItem("Copy");
        menuItem.setMnemonic(KeyEvent.VK_C);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(copyist);
        menu.add(menuItem);

        menuItem = new JMenuItem("Paste");
        menuItem.setMnemonic(KeyEvent.VK_P);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(pasteur);
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem("Select All");
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        menuItem.addActionListener(allSelector);
        menu.add(menuItem);

        return menu;
    }

    /**
     * Loads the program from a file to the JTextArea area.
     * Uses readProgram(File file) method to read the file.
     * Uses setProgram(String str) method to set the content of the file
     * into the JTextArea area.
     *
     * @see #readProgram(File)
     * @see #setProgram(String)
     */
    void loadProgram() {
        fileChooser.rescanCurrentDirectory();
        int returnVal = fileChooser.showOpenDialog(masterFrame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
           File file = fileChooser.getSelectedFile();
           String program = readProgram(file);
           setProgram(program);
        }
    }

    /**
     * Saves the program from the JTextArea area to the file.
     * Uses writeProgram(File file) method to write the code into a file.
     *
     * @see #writeProgram(File)
     */
    void saveProgram() {
        fileChooser.rescanCurrentDirectory();
        int returnVal = fileChooser.showSaveDialog(masterFrame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            writeProgram(file);
        }
    }

    /**
     * Saves the content of the JTextArea area to a given file.
     *
     * @param   file    The file where the content of the JTextArea is saved.
     */
    void writeProgram(File file) {
        try {
            FileWriter w = new FileWriter(file);
            w.write(area.getText());
            w.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the content of the given file and returns the content of the file as String.
     *
     * @param   file    The file from which the content is read and returned for the use of loadProgram() method.
     * @return  The content of the file that was given as parameter.
     */
    String readProgram(File file) {
        try {
            Reader fr = new FileReader(file);
            BufferedReader r = new BufferedReader(fr);

            StringBuffer buff = new StringBuffer();
            String line;
            while ( (line = r.readLine()) != null ) {
                buff.append(line);
                buff.append("\n");
            }
            r.close();
            return buff.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Sets in JTextAre area the default text as given in template.
     */
    void clearProgram() {
        area.setText(template);
    }


    /**
     * The given String program object will be set as the text inside the JTextArea area.
     *
     * @param   program The string that will be set in JTextArea area as the program code.
     */
    void setProgram(String program) {
        area.setText(program);
    }


    /**
     * Method returns the program code inside the JTextArea as String -object
     * Tabulators are changed to spaces for uniform handling of spaces.
     *
     * @return  The program code inside the JTextArea area.
     */
    public String getProgram() {
        String programCode = area.getText() + "\n";
        programCode = replace(programCode, "\t", "    ");
        return programCode;
    }


    public String replace(String from, String c, String with) {
        int index = from.indexOf(c);
        while(index != -1) {
            from = from.substring(0, index) +
            with +
            from.substring(index + 1, from.length());
            index = from.indexOf(c);
        }
        return from;
    }


    /**
     * Method highlights the specified Statement area by selecting it.
     *
     * @param   left    The beginning of the selection.
     * @param   right   The end of the selection.
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
                area.select(left, right);
            }
        };
        SwingUtilities.invokeLater(updateAComponent);
    }


    /**
     * Method highlights the specified code area by selecting it.
     *
     * @param   left    The beginning of the selection.
     * @param   right   The end of the selection.
     */
    public void highlight(Highlight h) {
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

        area.requestFocus();
        area.select(left, right);
    }

}
