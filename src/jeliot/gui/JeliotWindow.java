package jeliot.gui;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import jeliot.theatre.*;
import jeliot.parser.*;
import jeliot.*;
import jeliot.ecode.*;

import koala.dynamicjava.interpreter.*;

/**
 * This is the main window of the Jeliot 2000.
 *
 * @author Pekka Uronen
 */
public class JeliotWindow {

    boolean errorOccured = false;

    /** The frame in which all the action goes on. */
    private JFrame frame;

    /** The theatre in which the programs are animated. */
    private Theatre theatre;

    /** The animation engine that that will animate the code. */
    private AnimationEngine engine;

    /** The main program. */
    private Jeliot jeliot;

    /** The code pane where the code is shown during the animation. */
    private CodePane codePane;

    /** The code editor in which the users can write their code. */
    private CodeEditor editor = new CodeEditor();

    /** The pane that splits the window. */
    private JSplitPane codeNest;

    /** The step button.*/
    private JButton stepButton;

    /** The play button.*/
    private JButton playButton;

    /** The pause button.*/
    private JButton pauseButton;

    /** The rewind button.*/
    private JButton rewindButton;

    /** The edit button.*/
    private JButton editButton;

    /** The compile button.*/
    private JButton compileButton;

    /** In this text area will come the output of the user-made programs. */
    private JTextArea outputConsole;

    /** This ImageLoader will load all the images. */
    private ImageLoader iLoad;

    /** This variable will control the panels. */
    private PanelController panelController;

    /** This JEditorPane errorPane will show the error messages for the users. */
    private JEditorPane errorPane = new JEditorPane(); {
        errorPane.setEditable(false);
        errorPane.setContentType("text/html");
        errorPane.setBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLoweredBevelBorder(),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
    }

/*
    JScrollPane editorScrollPane = new JScrollPane(editorPane);
    editorScrollPane.setVerticalScrollBarPolicy(
        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    editorScrollPane.setPreferredSize(new Dimension(250, 145));
*/


    /** This JPanel errorViewer will help the showing of the error messages for the users. */
    private JPanel errorViewer = new JPanel() {
        private Image backImage;

        public void paintComponent(Graphics g) {
            Dimension d = getSize();
            int w = d.width;
            int h = d.height;
            if (backImage == null) {
                backImage = iLoad.getLogicalImage("Panel");
            }
            int biw = backImage.getWidth(this);
            int bih = backImage.getHeight(this);

            if (biw >= 1 || bih >= 1) {
                for (int x = 0; x < w; x += biw) {
                    for (int y = 0; y < h; y += bih) {
                        g.drawImage(backImage, x, y, this);
                    }
                }
            }
        }
    };
    {
        errorViewer.setBorder(
                BorderFactory.createEmptyBorder(12, 12, 5, 12));
        errorViewer.setLayout(new BorderLayout());
        errorViewer.add("Center", errorPane);
        JPanel bp = new JPanel();
        bp.setOpaque(false);

        JButton ok = new JButton("OK");
        ok.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    changeTheatrePane(theatre);
                    editButton.setEnabled(true);
                }
           }
        );
        bp.add(ok);
        errorViewer.add("South", bp);
    }

    /**
     * Action listeners for the step- button.
     */
    private ActionListener stepAction =
        new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stepAnimation();
            }
        };

    /**
     * Action listeners for the play- button.
     */
    private ActionListener playAction =
        new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playAnimation();
            }
        };

    /**
     * Action listeners for the pause- button.
     */
    private ActionListener pauseAction =
        new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pauseAnimation();
            }
        };

    /**
     * Action listeners for the rewind- button.
     */
    private ActionListener rewindAction =
        new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rewindAnimation();
            }
        };

    /**
     * JFrame treeFrame for DEBUGGING.
     */
    private JFrame treeFrame = new JFrame("Program"); {
        treeFrame.setSize(300, 400);
    }

    /**
     * Helping to enable and disable the components.
     */
    private Vector editWidgets = new Vector();

    /**
     * Helping to enable and disable the components.
     */
    private Vector animWidgets = new Vector();




    /**
     * Assigns the values of the parameters in the object values.
     * Constructs the panelController with theatre and iload.
     *
     * @param   jeliot  The main program.
     * @param   codePane    The pane where all the code is shown while animated.
     * @param   theatre The theatre where all the code is animated.
     * @param   engine  The engine that animates the code.
     * @param   iLoad       The imageloader that loads all the images.
     */
    public JeliotWindow(
            Jeliot jeliot,
            CodePane codePane,
            Theatre theatre,
            AnimationEngine engine,
            ImageLoader iLoad ) {

        this.jeliot = jeliot;
        this.codePane = codePane;
        this.theatre = theatre;
        this.engine = engine;
        this.iLoad = iLoad;

        this.panelController = new PanelController(theatre, iLoad);
    }


    /**
     * Initializes the JFrame frame.
     * Sets up all the basic things for the window. (Panels, Panes, Menubars)
     * Things for debugging.
     */
    public void setUp() {

        frame = new JFrame("Jeliot 3 - Beta Version");
        frame.setIconImage(iLoad.getLogicalImage("Jeliot-icon"));

        frame.setJMenuBar(makeMenuBar());
        editor.setMasterFrame(frame);

        JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            codePane, theatre);
        pane.setOneTouchExpandable(true);
        codeNest = pane;

        Dimension minimumSize = new Dimension(0, 0);
        codePane.setMinimumSize(minimumSize);
        editor.setMinimumSize(minimumSize);

        JPanel bottomPane = new JPanel(new BorderLayout()) ;
            final JComponent conPan = makeControlPanel();
            bottomPane.add("West", conPan);

            OutputConsole oc = new OutputConsole(conPan);
            this.outputConsole = oc;
            bottomPane.add("Center", oc.container);

        JPanel rootPane = new JPanel(new BorderLayout());

            rootPane.add("Center", pane);

            rootPane.add("South", bottomPane);

        frame.setContentPane(rootPane);
        frame.setSize(800, 600);
        frame.addWindowListener(
            new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            }
        );

        enterEditTrue();
        pane.setDividerLocation(300);

        //TheatrePopup popup = new TheatrePopup();
        //theatre.addMouseListener(popup);
        //theatre.addMouseMotionListener(popup);

        frame.show();
    }

    /**
     * Makes and returns the menubar for the main frame.
     * Things for debugging.
     *
     * @return  The menubar for the main frame.
     */
    private JMenuBar makeMenuBar() {

        JMenuBar menuBar = new JMenuBar();

        JMenu menu, submenu;
        JMenuItem menuItem;
        JCheckBoxMenuItem cbMenuItem;
        JRadioButtonMenuItem rbMenuItem;

        //a group of JMenuItems
        JMenu programMenu = editor.makeProgramMenu();
        menuBar.add(programMenu);
        editWidgets.addElement(programMenu);

        JMenu editMenu = editor.makeEditMenu();
        menuBar.add(editMenu);
        editWidgets.addElement(editMenu);

        /**
          *Just for debugging and testing
          */
//         JMenu debugMenu = makeDebugMenu();
//         menuBar.add(debugMenu);

        return menuBar;
    }

    /**
     * Makes the JMenu Debug for the JMenuBar.
     * Things for debugging
     *
     * @return  The debugging menu.
     */
//     private JMenu makeDebugMenu() {
//         JMenu menu = new JMenu("Debug");
//         menu.setMnemonic(KeyEvent.VK_D);
//         JMenuItem menuItem;

//         menuItem = new JMenuItem("View Tree");
//         menuItem.setMnemonic(KeyEvent.VK_T);
//         menuItem.addActionListener(
//             new ActionListener() {
//                 public void actionPerformed(ActionEvent e) {
//                     showProgramTree();
//                 }
//             }
//         );
//         menu.add(menuItem);
//         return menu;
//     }

    /**
     * Makes the control buttons for the control panel.
     *
     * @param   label   The label for the button.
     * @param   iconName    The icon name for the icon on the button.
     * @return  The control button for control panel.
     */
    private JButton makeControlButton(String label, String iconName) {

        ImageIcon icon = new ImageIcon("images/"+iconName);
        JButton b = new JButton(label, icon);
        b.setVerticalTextPosition(AbstractButton.BOTTOM);
        b.setHorizontalTextPosition(AbstractButton.CENTER);
    //  b.setBorder(BorderFactory.createEtchedBorder());
        b.setMargin(new Insets(0,0,0,0));
        return b;
    }

    /**
     * Constructs the control panel.
     * Uses makeControlButton(String, String)
     *
     * @return  The constructed control panel.
     * @see #makeControlButton(String, String)
     */
    private JPanel makeControlPanel() {

        editButton = makeControlButton("Edit", "editicon.gif");
        compileButton = makeControlButton("Compile", "compileicon.gif");

        editButton.setMargin(new Insets(0, 2, 0, 2));
        compileButton.setMargin(new Insets(0, 2, 0, 2));

        editButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    enterEdit();
                }
            }
        );

        compileButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    tryToEnterAnimate();
                }
            }
        );

        editWidgets.addElement(compileButton);
        animWidgets.addElement(editButton);


        JPanel statePane = new JPanel();
        statePane.setLayout(new GridLayout(1, 2));
        statePane.add(editButton);
        statePane.add(compileButton);

        // create animation control buttons
        stepButton = makeControlButton("Step", "stepicon.gif");
        playButton = makeControlButton("Play", "playicon.gif");
        pauseButton = makeControlButton("Pause", "pauseicon.gif");
        rewindButton = makeControlButton("Rewind", "rewindicon.gif");

        animWidgets.addElement(stepButton);
        animWidgets.addElement(playButton);
        animWidgets.addElement(pauseButton);
        animWidgets.addElement(rewindButton);

        stepButton.addActionListener(stepAction);
        playButton.addActionListener(playAction);
        pauseButton.addActionListener(pauseAction);
        rewindButton.addActionListener(rewindAction);

        // create animation speed control slider
        final JSlider speedSlider = new JSlider(JSlider.HORIZONTAL,
                                                1, 60, 15);
        speedSlider.setMajorTickSpacing(15);
        speedSlider.setMinorTickSpacing(5);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(false);

        speedSlider.addChangeListener(
            new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    int volume = speedSlider.getValue();
                    engine.setVolume( (double)volume*100.0 );
                }
            }
        );
        animWidgets.addElement(speedSlider);

        JPanel bp = new JPanel();
        bp.setLayout(new GridLayout(1, 4));
        bp.add(stepButton);
        bp.add(playButton);
        bp.add(pauseButton);
        bp.add(rewindButton);

        JPanel p = new JPanel();
        GridBagLayout pl = new GridBagLayout();
        p.setLayout(pl);


        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 4, 8);
        pl.setConstraints(statePane, c);
        p.add(statePane);

        JLabel jicon = new JLabel(new ImageIcon("images/jeliot.gif"));
        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 2;
        c.insets = new Insets(0, 0, 0, 0);
        pl.setConstraints(jicon, c);
        p.add(jicon);

        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 0, 4, 0);
        pl.setConstraints(bp, c);
        p.add(bp);

        c.gridy = 1;
        c.gridwidth = 1;
        c.insets = new Insets(0, 0, 0, 0);
        c.anchor = GridBagConstraints.WEST;
        JLabel label = new JLabel("Animation");
        pl.setConstraints(label, c);
        p.add(label);

        c.gridy = 2;
        label = new JLabel("speed");
        pl.setConstraints(label, c);
        p.add(label);

        c.gridx = 2;
        c.gridy = 1;
        c.gridheight = 2;
        pl.setConstraints(speedSlider, c);
        p.add(speedSlider);

        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 2, 2, 10),
                BorderFactory.createEtchedBorder()));

        return p;
    }


    /**
     * Enables or disables the components depending on the second parameter.
     *
     * @param   enum    The collection of components that are set enabled or disabled as the boolean enable is set.
     * @param   enable  Sets wheter the components are enabled or disabled.
     */
    private void enableWidgets(Enumeration enum, boolean enable) {

        while (enum.hasMoreElements()) {
            Component comp = (Component)enum.nextElement();
            comp.setEnabled(enable);
        }
    }

    /**
     * Changes the code pane in the codeNest.
     * Sets inside the codeNest the new code pane.
     *
     * @param   comp    The component that is changes in the code pane.
     */
    private void changeCodePane(JComponent comp) {

        int loc = codeNest.getDividerLocation();
        codeNest.setLeftComponent(comp);
        codeNest.setDividerLocation(loc);
    }


    /**
     * Changes the theatre pane in the codeNest.
     * Sets inside the codeNest the new theatre pane.
     *
     * @param   comp    The component that is changes in the theatre pane.
     */
    private void changeTheatrePane(JComponent comp) {

        int loc = codeNest.getDividerLocation();
        codeNest.setRightComponent(comp);
        codeNest.setDividerLocation(loc);
    }

    /**
     * This method is called when user clicks the "Edit" button.
     */
    void enterEdit() {
        panelController.slide(false,
            new Runnable() {
                public void run() {
                    SwingUtilities.invokeLater(
                        new Runnable() {
                            public void run() {
                                enterEditTrue();
                            }
                        }
                    );
                }
            }
        ).start();
    }


    /**
     * Makes the user interface changes when user clicks the "Edit" button.
     */
    public void enterEditTrue() {
        changeCodePane(editor);
        enableWidgets(editWidgets.elements(), true);
        enableWidgets(animWidgets.elements(), false);
    }


    /**
     * Returns the program code from the CodeEditor -object.
     *
     * @return  The program code from the CodeEditor -object.
     */
    public String getProgram() {
        return editor.getProgram();
    }

    /**
     * Shows the program tree for debugging.
     */
//     void showProgramTree() {
//         treeFrame.setContentPane(jeliot.getTree().createPanel());
//         treeFrame.setVisible(true);
//     }

    /**f
     * Called when the user pushes the "Compile" button.
     * Gets the code from the CodeEditor -object.
     * Sends it to "compilation".
     */
    void tryToEnterAnimate() {
        try {
            try {
                String programCode = editor.getProgram();

                String methodCall = null;
                methodCall = findMainMethodCall(programCode);

                if (methodCall != null) {

                    //Reader r = new BufferedReader(new StringReader(programCode));
                    //jeliot.createLauncher(r);
                    //Reader s = new BufferedReader(new StringReader(methodCall));

                    jeliot.compile(programCode, methodCall);

                    changeTheatrePane(theatre);

                    panelController.slide(true,
                        new Runnable() {
                            public void run() {
                                SwingUtilities.invokeLater(
                                    new Runnable() {
                                        public void run() {
                                            enterAnimate();
                                        }
                                    }
                                );
                            }
                        }
                    ).start();
                } else {
                    showErrorMessage("<H2>No main method call found</H2>"+
                    "<P>No main method call was found from any class"+
                    "and thus the program cannot be started. Add a main method.</P>");
                }
            }

            catch (FeatureNotImplementedException e) {
                showErrorMessage(e);
                return;
            }
/*            catch (InterpreterException e) {
                showErrorMessage(e);
                return;
            }
            catch (SemanticException e) {
                showErrorMessage(e);
                return;
            }
            catch (SyntaxErrorException e) {
                showErrorMessage(e);
                return;
            }
*/
        }
        catch (Exception e) {
            editButton.doClick();
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    public String replaceChar(String from, char c, String with) {
        int index = from.indexOf(c);
        while(index != -1) {
            from = from.substring(0,index) +
            with +
            from.substring(index+1,from.length());
            index = from.indexOf(c);
        }
        return from;
    }


    /**
     * Tries to find the main method call from one of the classes.
     */
    public String findMainMethodCall(String programCode) {
        String commentsRemoved = removeComments(programCode);
        replaceChar(commentsRemoved, '\n', " ");
        replaceChar(commentsRemoved, '\t', " ");

        String mainMethod="static void main()";
        String classString = " class ";

        int methodIndex = commentsRemoved.indexOf(mainMethod);
        System.out.println(methodIndex);
        if (methodIndex > -1) {
            String partProgramCode = commentsRemoved.substring(0,methodIndex);
            int classIndex = partProgramCode.lastIndexOf(classString);
            System.out.println(classIndex);
            if (classIndex > -1) {
                partProgramCode = partProgramCode.substring(classIndex + classString.length()).trim();
                int classNameIndex = partProgramCode.indexOf(" ");
                System.out.println(classNameIndex);
                if (classNameIndex > -1) {
                    String mainMethodCall = partProgramCode.substring(0, classNameIndex) + ".main();";
                    System.out.println(mainMethodCall);
                    return mainMethodCall;
                }
            }
        }
        return null;
    }

    public String removeComments(String programCode) {

        String lineComment = "//";
        String beginningComment = "/*";
        String endingComment = "*/";

        int index = programCode.indexOf(lineComment);

        while (index > -1) {
            int endIndex = programCode.indexOf('\n', index);
            programCode = programCode.substring(0, index) +
            programCode.substring(endIndex, programCode.length());
            index = programCode.indexOf(lineComment);
        }

        index = programCode.indexOf(beginningComment);

        while (index > -1) {
            int endIndex = programCode.indexOf(endingComment, index);
            programCode = programCode.substring(0, index) +
            programCode.substring(endIndex, programCode.length());
            index = programCode.indexOf(beginningComment);
        }

        return programCode;

    }

    public void showErrorMessage(String e) {
        errorPane.setText(e);
        changeTheatrePane(errorViewer);
    }


    /**
     * Show the error message of the exception in the theatre pane.
     *
     * @param   e   The exception that is wanted to show.
     */
    public void showErrorMessage(Exception e) {
        showErrorMessage(e.toString());
    }


    /**
     * Shows the error message of the syntax error exception in the theatre pane.
     *
     * @param   e   The exception that is wanted to show.
     */
/*    public void showErrorMessage(SyntaxErrorException e) {
        errorPane.setText(e.toString());
        changeTheatrePane(errorViewer);
        editor.highlight(e.getHighlight());
    }
*/

    public void showErrorMessage(InterpreterError e) {

        pauseButton.setEnabled(false);
        errorOccured = true;

        showErrorMessage(e.getMessage());

        Component c = codeNest.getLeftComponent();

        if (e.getHighlight() != null) {

            if (c instanceof CodeEditor) {
                ((CodeEditor)c).highlight(e.getHighlight());
            }

            if (c instanceof CodePane) {
                ((CodePane)c).highlightStatement(e.getHighlight());
            }
        }
    }

    /**
     * Changes the user interface when the "Compile" button is pressed.
     * Rewinds the animation.
     */
    public void enterAnimate() {
        changeCodePane(codePane);
        enableWidgets(editWidgets.elements(), false);
        enableWidgets(animWidgets.elements(), true);
        rewindAnimation();
    }


    /**
     * Changes the user interface when the "Step" button is pressed.
     * Calls jeliot.step() method.
     *
     * @see jeliot.Jeliot#step()
     */
    void stepAnimation() {
        stepButton.setEnabled(false);
        playButton.setEnabled(false);
        pauseButton.setEnabled(true);
        rewindButton.setEnabled(false);
        editButton.setEnabled(false);

        jeliot.step();
    }

    /**
     * Changes the user interface when the "Play" button is pressed.
     * Calls jeliot.play() method.
     *
     * @see jeliot.Jeliot#play()
     */
    void playAnimation() {
        stepButton.setEnabled(false);
        playButton.setEnabled(false);
        pauseButton.setEnabled(true);
        rewindButton.setEnabled(false);
        editButton.setEnabled(false);

        jeliot.play();
    }


    /**
     * Changes the user interface when the "Pause" button is pressed.
     * Calls jeliot.pause() method.
     *
     * @see jeliot.Jeliot#pause()
     */
    public void pauseAnimation() {
        stepButton.setEnabled(true);
        playButton.setEnabled(true);
        pauseButton.setEnabled(false);
        rewindButton.setEnabled(true);
        editButton.setEnabled(true);

        jeliot.pause();
    }


    /**
     * Changes the user interface when the "Resume" button is pressed.
     */
    public void resumeAnimation() {
        stepButton.setEnabled(false);
        playButton.setEnabled(false);
        pauseButton.setEnabled(true);
        rewindButton.setEnabled(false);
        editButton.setEnabled(false);
    }

    /**
     * Changes the user interface when the animation is freezed.
     */
    public void freezeAnimation() {
        stepButton.setEnabled(false);
        playButton.setEnabled(false);
        pauseButton.setEnabled(false);
        rewindButton.setEnabled(false);
        editButton.setEnabled(true);

    }

    /**
     * Changes the user interface when the "Rewind" button is pressed.
     * Calls jeliot.rewind() method.
     * Calls theatre.repaint() method
     *
     * @see jeliot.Jeliot#rewind()
     * @see jeliot.theatre.Theatre#repaint()
     */
    void rewindAnimation() {

        errorOccured = false;

        jeliot.recompile();

        try {
            Thread.sleep(25);
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }

        stepButton.setEnabled(true);
        playButton.setEnabled(true);
        pauseButton.setEnabled(false);
        rewindButton.setEnabled(false);

        jeliot.rewind();
        theatre.repaint();
    }

    /**
     * Changes the user interface when the animation is finished.
     */
    public void animationFinished() {
        if (!errorOccured) {
            stepButton.setEnabled(false);
            playButton.setEnabled(false);
            pauseButton.setEnabled(false);
            rewindButton.setEnabled(true);
            editButton.setEnabled(true);
        } else {
            editButton.setEnabled(false);
            stepButton.setEnabled(false);
            playButton.setEnabled(false);
            pauseButton.setEnabled(false);
            rewindButton.setEnabled(false);

        }
    }

    /**
     * Writes the outputted string to the output console.
     *
     * @param   str String for output.
     */
    public void output(String str) {
        outputConsole.append(str);
    }

}
