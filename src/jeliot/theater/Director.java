package jeliot.theater;

import java.awt.Point;
import java.text.MessageFormat;
import java.util.ListIterator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Stack;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import jeliot.Jeliot;
import jeliot.gui.CodePane2;
import jeliot.lang.ArrayInstance;
import jeliot.lang.MethodFrame;
import jeliot.lang.ObjectFrame;
import jeliot.lang.Reference;
import jeliot.lang.Value;
import jeliot.lang.Variable;
import jeliot.lang.VariableInArray;
import jeliot.mcode.Highlight;
import jeliot.mcode.InterpreterError;
import jeliot.mcode.MCodeInterpreter;
import jeliot.mcode.MCodeUtilities;
import jeliot.tracker.Tracker;

/**
 * Directs the program animation. Contains the commands to visualize
 * all the expressions and statements in the Theater.
 *
 * @author Pekka Uronen
 * @author Niko Myller
 * 
 * @see jeliot.mcode.TheaterMCodeIntepreter
 */
public class Director {

    /**
     * The resource bundle for theater package.
     */
    static private ResourceBundle bundle =
            ResourceBundle.getBundle("jeliot.theater.resources.messages",
                    Locale.getDefault());

    //DOC: Document!

    /**
     * True, if the director should stop after executing one statement.
     */
    private boolean stepByStep;

    /**
     * Theatre to show the animation in.
     */
    private Theater theatre;

    /**
     * Pane showing the code. For highlighting.
     */
    private CodePane2 codePane;

    /**
     * Master Jeliot.
     */
    private Jeliot jeliot;

    /**
     * Factory that produces the actors.
     */
    private ActorFactory factory;

    /**
     *
     */
    private TheaterManager manager;

    /**
     *
     */
    private AnimationEngine engine;

    /**
     *
     */
    private MethodFrame currentMethodFrame;

    /**
     *
     */
    private Scratch currentScratch;

    /**
     *
     */
    private ConstantBox cbox;

    /**
     *
     */
    private ThreadController controller;

    /**
     *
     */
    private Stack scratchStack = new Stack();

    /**
     *
     */
    private Stack frameStack = new Stack();

    /**
     *
     */
    private boolean errorOccured = false;

    /**
     *
     */
    private MCodeInterpreter mCodeInterpreter;

    /**
     *
     */
    private LinesAndText lat;

    /**
     *
     */
    private int runUntilLine = -1;

    /**
     * 
     */
    private Highlight hPrev;

    /**
     * @param theatre
     * @param codePane
     * @param jeliot
     * @param engine
     */
    public Director(Theater theatre,
                    CodePane2 codePane,
                    Jeliot jeliot,
                    AnimationEngine engine) {

        this.theatre = theatre;
        this.codePane = codePane;
        this.jeliot = jeliot;
        this.engine = engine;

        this.manager = theatre.getManager();
    }

    /**
     * @param controller
     */
    public void setController(ThreadController controller) {
        this.controller = controller;
    }

    /**
     * @return
     */
    public Scratch getCurrentScratch() {
        return currentScratch;
    }

    /**
     * @param factory
     */
    public void setActorFactory(ActorFactory factory) {
        this.factory = factory;
    }

    /**
     * @return
     */
    public MethodFrame getCurrentMethodFrame() {
        return currentMethodFrame;
    }

    /**
     * @throws Exception
     */
    public void direct() throws Exception {
        errorOccured = false;
        highlight(new Highlight(0, 0, 0, 0));
        cbox = factory.produceConstantBox();
        theatre.addPassive(cbox);
        manager.setConstantBox(cbox);

        LinesAndText lat = factory.produceLinesAndText();
        manager.setLinesAndText(lat);
        theatre.addPassive(lat);

        //Excecution of the program code takes place here.
        mCodeInterpreter.execute();
        if (!errorOccured) {
            highlight(new Highlight(0, 0, 0, 0));
        }
        theatre.flush();
        Tracker.writeToFile("AnimationEnded", System.currentTimeMillis());
    }

    /**
     * @param step
     */
    public void setStep(boolean step) {
        this.stepByStep = step;
    }

    /**
     * @param line
     */
    public void runUntil(int line) {
    	if (line > 0) {
            runUntilLine = line;
    		theatre.setRunUntilEnabled(true);
    	} else {
    		theatre.flush();
            runUntilLine = 0;
    		theatre.setRunUntilEnabled(false);
    	}
    }

    /**
     * @param h
     */
    public void highlight(Highlight h) {
        //requestHistoryImage();
        this.hPrev = h;
        if (!mCodeInterpreter.starting()) {

            if (stepByStep) {
                jeliot.directorPaused();
                controller.checkPoint();
            }

            if (h != null) {
                /*
                if (runUntilLine >= h.getBeginLine() &&
                      runUntilLine <= h.getEndLine()) {
                */
                if (runUntilLine == h.getBeginLine()) {
                    runUntilLine = -1;
                    theatre.setRunUntilEnabled(false);
                    jeliot.runUntilDone();
                    theatre.repaint();
                }

                codePane.highlightStatement(h);
            }
        }
    }

    public void requestHistoryImage() {
        if (!jeliot.isExperiment()) {
            jeliot.addImageToHistory(theatre.requestImage(), hPrev);
        }
    }

    /**
     * 
     */
    public void openScratch() {
        if (currentScratch != null) {
            scratchStack.push(currentScratch);
        }

        currentScratch = new Scratch();
        manager.addScratch(currentScratch);
        //return currentScratch;
    }

    /**
     * 
     */
    public void closeScratch() {
        if (currentScratch != null) {
            currentScratch.removeCrap();
            manager.removeScratch(currentScratch);
            theatre.flush();
            if (!scratchStack.empty()) {
                currentScratch = (Scratch) scratchStack.pop();
            }
        }
    }

    /**
     * 
     */
    public void closeExpression() {
    }

    public void capture() {
        requestHistoryImage();
        theatre.capture();
    }

    public void updateCapture() {
        theatre.updateCapture();
        requestHistoryImage();
    }

    public void release() {
        theatre.release();
        requestHistoryImage();
    }

    /**
     * This method animates the first half of a binary expression.
     * For example in expression  a + b  this will animate as
     * (supposing that the value of a is 1):   1 + ...
     *
     * @returns The expression actor which the expression is put on.
     */
    public ExpressionActor beginBinaryExpression(Value operand,
                                                 int operator,
                                                 long expressionReference,
                                                 Highlight h) {

        highlight(h);

        // Prepare the actors
        ValueActor operandAct = operand.getActor();
        OperatorActor operatorAct = factory.produceBinOpActor(operator);
        OperatorActor dotsAct = factory.produceEllipsis();

        // Create the expression actor for 5 elements and reserve
        // places for the three first actors.
        ExpressionActor expr =
                currentScratch.getExpression(5, expressionReference);
        Point operandLoc = expr.reserve(operandAct);
        Point operatorLoc = expr.reserve(operatorAct);
        Point dotsLoc = expr.reserve(dotsAct);

        // Prepare the theatre for animation.
        capture();        

        // Move the first operand to its place.
        engine.showAnimation(operandAct.fly(operandLoc));
        expr.bind(operandAct);

        updateCapture();

        // Make the operator appear.
        engine.showAnimation(operatorAct.appear(operatorLoc));
        expr.bind(operatorAct);

        updateCapture();

        // Make the ellipsis appear.
        engine.showAnimation(dotsAct.appear(dotsLoc));
        expr.bind(dotsAct);

        // Re-activate the theatre after animation.
        release();

        return expr;
    }

    /**
     * @param operand
     * @param expr
     * @param h
     */
    //Added for Jeliot 3
    public void rightBinaryExpression(Value operand,
                                      ExpressionActor expr,
                                      Highlight h) {

        highlight(h);

        // If there is a second operand, remove the ellipsis and
        // replace them with the second operand.
        if (operand != null) {
            // Get the operand's actor.
            ValueActor operandAct = operand.getActor();

            // Remove the ellipsis and reserve its place for the second
            // operand.
            expr.cut();
            Point operandLoc = expr.reserve(operandAct);

            // Prepare the theatre for animation.
            capture();

            // Move the operand to its place.
            engine.showAnimation(operandAct.fly(operandLoc));
            expr.bind(operandAct);

            // De-activate the theatre.
            release();
        }
    }

    /**
     * Animates the second part of a binary expression.
     *
     * @param result
     * @param operator
     * @param expr
     * @param h
     * @return
     */
    public Value finishBinaryExpression(Value result,
                                        int operator,
                                        ExpressionActor expr,
                                        Highlight h) {

        highlight(h);

        // Prepare the actors.
        ValueActor resultAct = factory.produceValueActor(result);
        OperatorActor operatorAct = factory.produceBinOpResActor(operator);

        // Prepare the theatre for animation.
        capture();

        // Reserve places for the equals sign and the result.
        Point operatorLoc = expr.reserve(operatorAct);
        Point resultLoc = expr.reserve(resultAct);

        // Make the equals sign (operator) appear.
        engine.showAnimation(operatorAct.appear(operatorLoc));
        expr.bind(operatorAct);

        updateCapture();

        // Make the result appear.
        engine.showAnimation(resultAct.appear(resultLoc));
        expr.bind(resultAct);

        updateCapture();

        // Make the expression dark.
        expr.setLight(Actor.SHADED);

        // Create and set a new actor for the result.
        ValueActor clone = (ValueActor) resultAct.clone();
        clone.setLight(Actor.NORMAL);
        clone.setLocation(resultAct.getRootLocation());
        currentScratch.registerCrap(clone);
        theatre.addPassive(clone);
        result.setActor(clone);

        // De-activate the theatre.
        release();

        Value val = (Value) result.clone();
        ValueActor rAct = factory.produceValueActor(val);
        rAct.setLight(Actor.NORMAL);
        rAct.setLocation(resultAct.getRootLocation());
        val.setActor(rAct);

        return val;
    }

    /* Shows an animation of the invocation of a static foreign
     * method.
     */
    /*  public Return animateSFMInvocation(ForeignMethodPointer method,
                                           Value[] args) {
            // Get animator for the invocation.
            Animator animator = method.getAnimator(args);
    
            // Get actors for the arguments.
            int n = args.length;
            ValueActor[] actors = new ValueActor[n];
            for (int i = 0; i < n; ++i) {
                actors[i] = args[i].getActor();
            }
            // Animate the invocation
            animator.setArguments(args);
            animator.setArgumentActors(actors);
            animator.animate(this);
    
            return new Return(animator.getReturnValue());
        }
    */

    /**
     * @param methodCall
     * @param args
     * @param h
     * @param thisValue
     * @return
     */
    public Value[] animateOMInvocation(String methodCall,
                                       Value[] args,
                                       Highlight h,
                                       Value thisValue) {
        highlight(h);

        // Remember the scratch of current expression.
        // scratchStack.push(currentScratch);

        ValueActor valAct = thisValue.getActor();
        if (valAct == null) {
            valAct = factory.produceValueActor(thisValue);
            thisValue.setActor(valAct);
            if (valAct instanceof jeliot.theater.ReferenceActor) {
                valAct.setLocation(((jeliot.theater.ReferenceActor) valAct)
                        .getInstanceActor()
                        .getRootLocation());
            }
        }

        // Create the actor for the invocation.
        int n = 0;
        if (args != null) {
            n = args.length;
        }

        OMIActor actor = factory.produceOMIActor(methodCall, n);
        ExpressionActor expr = currentScratch.getExpression(1, -1);
        currentScratch.registerCrap(actor);

        Point invoLoc = expr.getRootLocation();
        actor.setLocation(invoLoc);

        Animation thisFly = valAct.fly(actor.reserveThisActor(valAct));

        // Calculate the size of the invocation actor, taking into account
        // the this actor.
        actor.calculateSize();

        // Create actors and reserve places for all argument values,
        // and create animations to bring them in their right places.
        ValueActor[] argact = new ValueActor[n];
        Animation[] fly = new Animation[n];
        for (int i = 0; i < n; ++i) {
            argact[i] = args[i].getActor();
            args[i].setActor(argact[i]);
            fly[i] = argact[i].fly(actor.reserve(argact[i]));
        }

        // Calculate the size of the invocation actor, taking into account
        // the argument actors.
        actor.calculateSize();

        // Show the animation.
        capture();

        // Introduce the invocation and the this Value fly.
        engine.showAnimation(new Animation[]{actor.appear(invoLoc), thisFly});
        theatre.passivate(actor);

        //bind this value
        actor.bindThisActor();

        updateCapture();

        // Bring in arguments.
        engine.showAnimation(fly);

        // Bind argument actors to the invocation actor.
        for (int i = 0; i < n; ++i) {
            actor.bind(argact[i]);
        }

        // De-activate the theatre.
        release();

        return args;
    }

    /**
     * @param methodCall
     * @param args
     * @param h
     * @return
     */
    public Value[] animateOMInvocation(String methodCall,
                                       Value[] args,
                                       Highlight h) {
        return animateSMInvocation(methodCall, args, h);
    }

    /**
     * Animates the invocation of a domestic (user-defined) method.
     *
     * @param methodName
     * @param args
     * @param h
     * @return
     */
    public Value[] animateSMInvocation(String methodName,
                                       Value[] args, Highlight h) {

        highlight(h);

        // Remember the scratch of current expression.
        // scratchStack.push(currentScratch);

        // Create the actor for the invocation.
        int n = 0;
        if (args != null) {
            n = args.length;
        }
        SMIActor actor = factory.produceSMIActor(methodName, n);
        ExpressionActor expr = currentScratch.getExpression(1, -1);
        currentScratch.registerCrap(actor);

        Point invoLoc = expr.getRootLocation();
        actor.setLocation(invoLoc);

        // Create actors and reserve places for all argument values,
        // and create animations to bring them in their right places.
        ValueActor[] argact = new ValueActor[n];
        Animation[] fly = new Animation[n];
        for (int i = 0; i < n; ++i) {
            argact[i] = args[i].getActor();
            args[i].setActor(argact[i]);
            fly[i] = argact[i].fly(actor.reserve(argact[i]));
        }

        // Calculate the size of the invocation actor, taking into account
        // the argument actors.
        actor.calculateSize();

        // Show the animation.
        capture();

        // Introduce the invocation.
        engine.showAnimation(actor.appear(invoLoc));
        theatre.passivate(actor);

        // Bring in arguments.
        engine.showAnimation(fly);

        // Bind argument actors to the invocation actor.
        for (int i = 0; i < n; ++i) {
            actor.bind(argact[i]);
        }

        // De-activate the theatre.
        release();

        return args;
    }

    /**
     * Called when the program enters a method.
     * Sets up a frame for the method.
     *
     * @param methodName
     * @param args
     * @param formalParameters
     * @param formalParameterTypes
     * @param h
     * @param thisValue
     */
    public void setUpMethod(String methodName,
                            Value[] args,
                            String[] formalParameters,
                            String[] formalParameterTypes,
                            Highlight h,
                            Value thisValue) {

        // highlight the method header.
        highlight(h);

        // create new method frame
        MethodFrame frame = new MethodFrame(methodName);

        // create a stage for the method
        MethodStage stage = factory.produceMethodStage(frame);
        frame.setMethodStage(stage);
        currentMethodFrame = frame;
        frameStack.push(frame);

        Variable thisVariable = null;
        VariableActor thisVariableActor = null;
        ValueActor thisValueActor = null;

        int n = 0;
        Variable[] vars = null;
        VariableActor[] varact = null;
        Animation[] anim = null;
        ValueActor[] valact = null;

        thisVariable =
                frame.declareVariable(new Variable("this", thisValue.getType()));
        thisVariableActor = factory.produceVariableActor(thisVariable);
        thisVariable.setActor(thisVariableActor);
        stage.reserve(thisVariableActor);
        stage.bind();

        if (args != null && args.length > 0) {
            n = args.length;
            vars = new Variable[n];
            varact = new VariableActor[n];
            anim = new Animation[n];
            valact = new ValueActor[n];

            for (int i = 0; i < args.length; ++i) {
                vars[i] =
                        frame.declareVariable(new Variable(formalParameters[i],
                                formalParameterTypes[i]));
                varact[i] = factory.produceVariableActor(vars[i]);
                vars[i].setActor(varact[i]);
                stage.reserve(varact[i]);
                //stage.extend();
                stage.bind();
            }

            Animation a = stage.extend();
            if (a != null) {
                engine.showAnimation(a);
            }
        }

        capture();

        Point sLoc = manager.reserve(stage);
        engine.showAnimation(stage.appear(sLoc));
        manager.bind(stage);

        updateCapture();

        thisVariable.assign(thisValue);
        Value thisCasted = thisVariable.getValue();
        ValueActor thisCastAct = factory.produceValueActor(thisCasted);
        thisValueActor = thisValue.getActor();

        if (thisValueActor == null) {
            thisValueActor = factory.produceValueActor(thisValue);
            if (thisValueActor instanceof ReferenceActor) {
                InstanceActor ai =
                        ((ReferenceActor) thisValueActor).getInstanceActor();
                thisValueActor.setLocation(ai.getRootLocation());
            } else {
                introduceLiteral(thisValue);
                thisValueActor = (ValueActor) thisValue.getActor();
            }
        }

        thisValue.setActor(thisValueActor);

        Animation thisAnim =
                thisValueActor.fly(thisVariableActor.reserve(thisCastAct));

        engine.showAnimation(thisAnim);

        thisVariableActor.bind();
        theatre.removeActor(thisValueActor);

        updateCapture();

        if (args != null && args.length > 0) {

            for (int i = 0; i < n; ++i) {
                vars[i].assign(args[i]);
                Value casted = vars[i].getValue();
                ValueActor castact = factory.produceValueActor(casted);
                valact[i] = args[i].getActor();
                anim[i] = valact[i].fly(varact[i].reserve(castact));
            }

            engine.showAnimation(anim);

            for (int i = 0; i < n; ++i) {
                varact[i].bind();
                theatre.removeActor(valact[i]);
            }
        }

        updateCapture();

        if (currentScratch != null) {
            Scratch scratch = currentScratch;
            //scratchStack.push(scratch);
            scratch.memorizeLocation();

            scratch.removeCrap();
            if (mCodeInterpreter.emptyScratch()) {
                scratch.clean();
            }
            manager.removeScratch(scratch);
            Point p = new Point(scratch.getX(), -scratch.getHeight());
            updateCapture();
            engine.showAnimation(scratch.fly(p));
            theatre.removePassive(scratch);
        }
        openScratch();
        release();
    }

    /**
     * @param methodName
     * @param h
     */
    public void setUpMethod(String methodName, Highlight h) {
        setUpMethod(methodName, null, null, null, h);
    }

    /**
     * Called when the program enters a new user-defined method.
     * Sets up a frame for the method.
     *
     * @param methodName
     * @param args
     * @param formalParameters
     * @param formalParameterTypes
     * @param h
     */
    public void setUpMethod(String methodName,
                            Value[] args,
                            String[] formalParameters,
                            String[] formalParameterTypes,
                            Highlight h) {

        // highlight the method header.
        highlight(h);

        // create new method frame
        MethodFrame frame = new MethodFrame(methodName);

        // create a stage for the method
        MethodStage stage = factory.produceMethodStage(frame);
        frame.setMethodStage(stage);
        currentMethodFrame = frame;
        frameStack.push(frame);

        int n = 0;
        Variable[] vars = null;
        VariableActor[] varact = null;
        Animation[] anim = null;
        ValueActor[] valact = null;

        if (args != null && args.length > 0) {
            n = args.length;
            vars = new Variable[n];
            varact = new VariableActor[n];
            anim = new Animation[n];
            valact = new ValueActor[n];

            for (int i = 0; i < args.length; ++i) {
                vars[i] =
                        frame.declareVariable(new Variable(formalParameters[i],
                                formalParameterTypes[i]));
                varact[i] = factory.produceVariableActor(vars[i]);
                vars[i].setActor(varact[i]);
                stage.reserve(varact[i]);
                //stage.extend();
                stage.bind();
            }

            Animation a = stage.extend();
            if (a != null) {
                engine.showAnimation(a);
            }
        }

        capture();
        Point sLoc = manager.reserve(stage);
        engine.showAnimation(stage.appear(sLoc));
        manager.bind(stage);

        updateCapture();

        if (args != null && args.length > 0) {

            for (int i = 0; i < n; ++i) {
                vars[i].assign(args[i]);
                Value casted = vars[i].getValue();
                ValueActor castact = factory.produceValueActor(casted);
                valact[i] = args[i].getActor();
                anim[i] = valact[i].fly(varact[i].reserve(castact));
            }

            engine.showAnimation(anim);

            for (int i = 0; i < n; ++i) {
                varact[i].bind();
                theatre.removeActor(valact[i]);
            }
        }

        updateCapture();

        if (currentScratch != null) {
            Scratch scratch = currentScratch;
            //scratchStack.push(scratch);
            scratch.memorizeLocation();

            scratch.removeCrap();
            if (mCodeInterpreter.emptyScratch()) {
                scratch.clean();
            }
            manager.removeScratch(scratch);
            Point p = new Point(scratch.getX(), -scratch.getHeight());
            updateCapture();
            engine.showAnimation(scratch.fly(p));
            theatre.removePassive(scratch);
        }
        openScratch();
        release();
    }

    /**
     * @param returnAct
     * @param expressionCounter
     * @return
     */
    public ValueActor finishMethod(Actor returnAct, long expressionCounter) {

        // Get the stage and remove it.
        MethodStage stage = ((MethodFrame) frameStack.pop()).getMethodStage();
        manager.removeMethodStage(stage);
        Animation stageDisappear = stage.disappear();

        if (returnAct != null) {
            currentScratch.removeCrap();
            returnAct.setShadow(4);
            engine.showAnimation(new Animation[]{
                stageDisappear,
                returnAct.fly(returnAct.getRootLocation())});
        } else {
            engine.showAnimation(stageDisappear);
        }

        if (!frameStack.empty()) {
            currentMethodFrame = (MethodFrame) frameStack.peek();
        }

        // Remove the current scratch -- the scratch used by the
        // invoked method -- and replace it with the old scratch.
        //manager.removeScratch(currentScratch);
        closeScratch();

        capture();

        ExpressionActor expr = null;
        if (returnAct != null) {
            expr = currentScratch.findActor(-1);
            if (expr != null) {
                expr.setId(expressionCounter);
            } else {
                expr = currentScratch.getExpression(1, expressionCounter);
            }
        }

        // Get the old location of the scratch
        Point scratchLoc = currentScratch.recallLocation();
        Animation flyScratch = currentScratch.fly(scratchLoc);

        // Create animation to move the old scratch back to its place.
        // If the method returned a value, create another animation to
        // move the return value to the scratch.
        Animation[] anim;
        if (returnAct == null) {

            anim = new Animation[]{flyScratch};
            engine.showAnimation(anim);

        } else {

            theatre.addPassive(currentScratch);
            Point returnLoc = expr.reserve(returnAct);
            returnLoc.translate(scratchLoc.x - currentScratch.getX(),
                    scratchLoc.y - currentScratch.getY());

            Animation flyReturn = returnAct.fly(returnLoc);
            anim = new Animation[]{flyScratch, flyReturn};

            engine.showAnimation(anim);
            expr.bind(returnAct);

        }

        theatre.removeActor(currentScratch);
        manager.addScratch(currentScratch);

        release();

        if (returnAct != null) {
            return (ValueActor) ((BubbleActor) returnAct).getActor();
        } else {
            return null;
        }
    }

    /**
     * Animates a return statement
     *
     * @param returnValue
     * @param casted
     * @param h
     * @return
     */
    public Actor animateReturn(Value returnValue, Value casted, Highlight h) {

        highlight(h);

        ValueActor castAct = factory.produceValueActor(casted);
        casted.setActor(castAct);
        ValueActor valueAct = returnValue.getActor();

        MethodStage stage = currentMethodFrame.getMethodStage();
        BubbleActor bubble = factory.produceBubble(stage);

        //The return value goes inside the Method stage in the last
        //place for variables.

        bubble.reserve(castAct);

        Point bubbleLoc = stage.reserve(bubble);
        Animation a = stage.extend();
        if (a != null) {
            engine.showAnimation(a);
        }
        bubble.setLocation(bubbleLoc);

        /*      Point bubbleLoc = new Point(
                        stage.getX() + stage.getWidth() / 2,
                        stage.getY() + stage.getHeight() + 25);
                bubble.setLocation(bubbleLoc);
        */
        Point valueLoc = bubble.reserve(castAct);

        bubble.removeTip();
        capture();
        engine.showAnimation(bubble.appear(bubbleLoc));
        engine.showAnimation(valueAct.fly(valueLoc));
        bubble.bind();
        //stage.bind();
        theatre.removePassive(valueAct);
        release();

        return bubble;
    }

    /**
     * @param fromValue
     * @param toValue
     * @param h
     */
    public void animateCastExpression(Value fromValue, Value toValue, Highlight h) {
        highlight(h);
        animateCastExpression(fromValue, toValue);
    }

    /**
     * @param fromValue
     * @param toValue
     */
    public void animateCastExpression(Value fromValue, Value toValue) {

        ValueActor fromActor = fromValue.getActor();
        ValueActor toActor = factory.produceValueActor(toValue);
        CastActor castActor = new CastActor(fromActor, toActor);
        toValue.setActor(toActor);

        Point loc = fromActor.getRootLocation();
        toActor.setLocation(loc);
        castActor.setLocation(loc);

        ActorContainer parent = fromActor.getParent();
        if (parent != null) {
            parent.removeActor(fromActor);
        }
        theatre.addActor(castActor);
        engine.showAnimation(castActor.cast());
        theatre.removeActor(castActor);
        theatre.addPassive(toActor);
        currentScratch.registerCrap(toActor);
    }

    /**
     * @param operator
     * @param first
     * @param second
     * @param result
     * @param expressionCounter
     * @param h
     * @return
     */
    public Value animateBinaryExpression(int operator,
                                         Value first,
                                         Value second,
                                         Value result,
                                         long expressionCounter,
                                         Highlight h) {

        highlight(h);

        // prepare the actors
        Actor firstAct = first.getActor();

        Actor secondAct =
                (second == null)
                ? (Actor) factory.produceEllipsis()
                : second.getActor();

        Actor resultAct = factory.produceValueActor(result);

        OperatorActor operatorAct = factory.produceBinOpActor(operator);

        OperatorActor eqAct = factory.produceBinOpResActor(operator);

        ExpressionActor expr =
                currentScratch.getExpression(5, expressionCounter);
        Point firstLoc = expr.reserve(firstAct);
        Point operatorLoc = expr.reserve(operatorAct);
        Point secondLoc = expr.reserve(secondAct);
        Point eqLoc = expr.reserve(eqAct);
        Point resultLoc = expr.reserve(resultAct);

        // Prepare the theatre
        capture();

        // Move the operands to positions.
        engine.showAnimation(new Animation[]{
            firstAct.fly(firstLoc),
            secondAct.fly(secondLoc)});

        updateCapture();

        expr.bind(firstAct);
        expr.bind(secondAct);

        updateCapture();

        engine.showAnimation(operatorAct.appear(operatorLoc));
        expr.bind(operatorAct);

        updateCapture();

        engine.showAnimation(eqAct.appear(eqLoc));
        expr.bind(eqAct);

        updateCapture();

        engine.showAnimation(resultAct.appear(resultLoc));
        expr.bind(resultAct);

        updateCapture();

        // Darken the expression.
        expr.setLight(Actor.SHADED);

        // Create and set a new actor for the result.
        ValueActor clone = (ValueActor) resultAct.clone();
        result.setActor(clone);
        clone.setLight(Actor.NORMAL);
        clone.setLocation(resultAct.getRootLocation());
        currentScratch.registerCrap(clone);
        theatre.addPassive(clone);

        release();

        Value val = (Value) result.clone();
        ValueActor rAct = factory.produceValueActor(val);
        rAct.setLight(Actor.NORMAL);
        rAct.setLocation(resultAct.getRootLocation());
        val.setActor(rAct);

        return val;
    }

    /**
     * @param name
     * @param type
     * @param h
     * @return
     */
    public Variable declareVariable(String name, String type, Highlight h) {

        highlight(h);

        // Create a new variable and its actor.
        Variable v =
                currentMethodFrame.declareVariable(new Variable(name, type));
        VariableActor actor = factory.produceVariableActor(v);
        v.setActor(actor);

        MethodStage stage = currentMethodFrame.getMethodStage();

        Point loc = stage.reserve(actor);
        Animation a = stage.extend();
        if (a != null) {
            engine.showAnimation(a);
        }

        capture();

        engine.showAnimation(actor.appear(loc));
        stage.bind();

        release();

        return v;
    }

    /**
     * @param of
     * @param name
     * @param type
     * @param h
     * @return
     */
    public Variable declareObjectVariable(ObjectFrame of,
                                          String name,
                                          String type,
                                          Highlight h) {

        highlight(h);

        // Create a new variable and its actor.
        Variable v = of.declareVariable(new Variable(name, type));
        VariableActor actor = factory.produceObjectVariableActor(v);
        v.setActor(actor);

        ObjectStage stage = of.getObjectStage();

        Point loc = stage.reserve(actor);
        capture();

        engine.showAnimation(actor.appear(loc));
        stage.bind();

        release();

        return v;
    }

    /**
     * @param literal
     */
    public void introduceLiteral(Value literal) {
        ValueActor valact = factory.produceValueActor(literal);
        valact.setLocation(cbox.getRootLocation());
        literal.setActor(valact);
    }

    /**
     * @param ref
     */
    public void introduceReference(Reference ref) {
        ReferenceActor refAct = factory.produceReferenceActor(ref);
        refAct.setLocation(refAct.getInstanceActor().getRootLocation());
        ref.setActor(refAct);
    }

    /*
        public void introduceInput(Value input) {
            ValueActor valact = factory.produceValueActor(input);
            valact.setLocation(cbox.getRootLocation());
            input.setActor(valact);
        }
    */


    /**
     * @param var
     * @return
     */
    public ValueActor initiateVariableAccess(Variable var) {

//Problem with instances.
        if (!MCodeUtilities.isPrimitive(var.getType())) {
            return var.getValue().getActor();
        }

//      Value value = var.getValue();
//      Value clone = (Value)value.clone();
//      ValueActor act = factory.produceValueActor(clone);
//      clone.setActor(act);

        ValueActor va = var.getActor().getValue();
        ValueActor act = factory.produceValueActor(va);
        Point loc = va.getRootLocation();

        capture();
        Animation appear = act.appear(loc);
        appear.setDuration(200);
        engine.showAnimation(appear);
        release();

        currentScratch.registerCrap(act);
        return act;
    }

    /**
     * @param variable
     * @param value
     * @param casted
     * @param returnValue
     * @param h
     */
    public void animateAssignment(Variable variable,
                                  Value value,
                                  Value casted,
                                  Value returnValue,
                                  Highlight h) {

        highlight(h);

        String type = variable.getType();
        VariableActor variableAct = variable.getActor();
        ValueActor valueAct = value.getActor();

        if (MCodeUtilities.isPrimitive(type)) {

            // Get/create actors.
            ValueActor castAct = factory.produceValueActor(casted);
            casted.setActor(castAct);

            Point valueLoc = variableAct.reserve(castAct);

            capture();
            engine.showAnimation(valueAct.fly(valueLoc));
            variableAct.bind();
            theatre.removePassive(valueAct);
            release();

            if (returnValue != null) {
                ValueActor returnAct = factory.produceValueActor(returnValue);

                returnAct.setLocation(castAct.getRootLocation());
                returnValue.setActor(returnAct);
            }
        } else {
            // Get/create actors.
            ReferenceActor refAct = (ReferenceActor) value.getActor();
            //refAct.calculateBends();
            ReferenceActor ra = factory.produceReferenceActor(refAct);
            casted.setActor(ra);

            if (variableAct instanceof ReferenceVariableActor) {
                ReferenceVariableActor rva = (ReferenceVariableActor) variableAct;

                //refAct.setBackground(rva.getBackground());

                //rva.setReference(refAct);
                //instAct.addReference(refAct);
                Point valueLoc = rva.reserve(ra);

                capture();
                engine.showAnimation(refAct.fly(valueLoc));
                rva.bind();
                theatre.removePassive(refAct);
                release();
            } else if (variableAct instanceof ReferenceVariableInArrayActor) {
                ReferenceVariableInArrayActor rva = (ReferenceVariableInArrayActor) variableAct;

                //refAct.setBackground(rva.getBackground());

                //rva.setReference(refAct);
                //instAct.addReference(refAct);
                Point valueLoc = rva.reserve(ra);

                capture();
                engine.showAnimation(refAct.fly(valueLoc));
                rva.bind();
                theatre.removePassive(refAct);
                release();
            }
            if (returnValue != null) {
                ValueActor returnAct =
                        factory.produceReferenceActor((Reference) returnValue);
                returnAct.setLocation(ra.getRootLocation());
                returnValue.setActor(returnAct);
            }

            /*
                        try {
                            Thread.sleep(200);
                        }
                        catch (InterruptedException e) { }
            */
        }

    }

    /**
     * @param operator
     * @param var
     * @param result
     * @param h
     */
    public void animatePreIncDec(int operator,
                                 Variable var,
                                 Value result,
                                 Highlight h) {

        highlight(h);

        VariableActor varAct = var.getActor();
        //Value value = var.getValue();
        ValueActor resAct = factory.produceValueActor(result);
        ValueActor valAct = var.getActor().getValue();
        //ValueActor valact = factory.produceValueActor(value);
        Actor opAct = factory.produceUnaOpActor(operator);

        Point resLoc = varAct.reserve(valAct);
        Point opLoc = varAct.getRootLocation();

        if (varAct instanceof VariableInArrayActor) {
            opLoc.translate(-15, 8);
        } else {
            opLoc.translate(varAct.getWidth() + 2, 8);
        }

        capture();

        engine.showAnimation(opAct.appear(opLoc));
        engine.showAnimation(resAct.appear(resLoc));
        varAct.bind();

        //value.setActor(valact);
        result.setActor(resAct);

        //Jeliot 3
        Value val = (Value) result.clone();
        ValueActor rAct = factory.produceValueActor(val);
        rAct.setLight(Actor.NORMAL);
        rAct.setLocation(resAct.getRootLocation());
        val.setActor(rAct);
        //var.assign(val);
        var.getActor().setValue(rAct);

        theatre.removeActor(resAct);
        theatre.removeActor(opAct);
        currentScratch.registerCrap(rAct);

        release();
    }

    /**
     * @param operator
     * @param var
     * @param resVal
     * @param h
     */
    public void animatePostIncDec(int operator,
                                  Variable var,
                                  Value resVal,
                                  Highlight h) {

        highlight(h);

        VariableActor varAct = var.getActor();
        //Value value = var.getValue();
        ValueActor valAct = var.getActor().getValue();
        //ValueActor valact = factory.produceValueActor(value);
        ValueActor resAct =
                (resVal == null) ? null : factory.produceValueActor(resVal);

        Actor opAct = factory.produceUnaOpActor(operator);

        Point resLoc = varAct.reserve(valAct);
        Point opLoc = varAct.getRootLocation();

        if (varAct instanceof VariableInArrayActor) {
            opLoc.translate(-15, 8);
        } else {
            opLoc.translate(varAct.getWidth() + 2, 8);
        }

        capture();

        if (resAct != null) {
            Point movLoc = new Point(opLoc);
            movLoc.translate(6, -resAct.getHeight() - 6);
            engine.showAnimation(resAct.appear(resLoc));
            engine.showAnimation(resAct.fly(movLoc));
        }

        engine.showAnimation(opAct.appear(opLoc));
        engine.showAnimation(valAct.appear(resLoc));
        varAct.bind();

        //value.setActor(valact);
        if (resVal != null) {
            resVal.setActor(resAct);
            //var.assign(resval); //jeliot 3
            var.getActor().setValue(resAct);
            currentScratch.registerCrap(resAct);
        }

        theatre.removeActor(opAct);

        release();
    }

    /**
     * @param operator
     * @param arg
     * @param expressionCounter
     * @param h
     * @return
     */
    public ExpressionActor beginUnaryExpression(int operator,
                                                Value arg,
                                                long expressionCounter,
                                                Highlight h) {
        highlight(h);

        ValueActor argAct = arg.getActor();
        OperatorActor opAct = factory.produceUnaOpActor(operator);

        capture();

        ExpressionActor exp =
                currentScratch.getExpression(4, expressionCounter);
        Point oLoc = exp.reserve(opAct);
        Point aLoc = exp.reserve(argAct);

        engine.showAnimation(argAct.fly(aLoc));
        exp.bind(argAct);
        updateCapture();

        engine.showAnimation(opAct.appear(oLoc));
        exp.bind(opAct);
        updateCapture();

        release();

        return exp;
    }

    /**
     * @param operator
     * @param exp
     * @param result
     * @param expressionCounter
     * @param h
     * @return
     */
    public Value finishUnaryExpression(int operator,
                                       ExpressionActor exp,
                                       Value result,
                                       long expressionCounter,
                                       Highlight h) {

        highlight(h);

        ValueActor resAct = factory.produceValueActor(result);
        OperatorActor eqAct = factory.produceUnaOpResActor(operator);

        Point eLoc = exp.reserve(eqAct);
        Point rLoc = exp.reserve(resAct);

        capture();

        engine.showAnimation(eqAct.appear(eLoc));
        exp.bind(eqAct);

        updateCapture();

        engine.showAnimation(resAct.appear(rLoc));
        exp.bind(resAct);

        updateCapture();

        exp.setLight(Actor.SHADED);

        release();

        ValueActor clone = (ValueActor) resAct.clone();
        clone.setLight(Actor.NORMAL);
        clone.setLocation(resAct.getRootLocation());
        theatre.addActor(clone);
        currentScratch.registerCrap(clone);
        result.setActor(clone);

        Value val = (Value) result.clone();
        ValueActor rAct = factory.produceValueActor(val);
        rAct.setLight(Actor.NORMAL);
        rAct.setLocation(resAct.getRootLocation());
        val.setActor(rAct);

        return val;
    }

    /**
     * @param operator
     * @param arg
     * @param result
     * @param expressionCounter
     * @param h
     * @return
     */
    public Value animateUnaryExpression(int operator,
                                        Value arg,
                                        Value result,
                                        long expressionCounter,
                                        Highlight h) {

        highlight(h);

        ValueActor argAct = arg.getActor();
        ValueActor resAct = factory.produceValueActor(result);

        OperatorActor opAct = factory.produceUnaOpActor(operator);
        OperatorActor eqAct = factory.produceUnaOpResActor(operator);

        capture();

        ExpressionActor exp =
                currentScratch.getExpression(4, expressionCounter);
        Point oLoc = exp.reserve(opAct);
        Point aLoc = exp.reserve(argAct);
        Point eLoc = exp.reserve(eqAct);
        Point rLoc = exp.reserve(resAct);

        engine.showAnimation(argAct.fly(aLoc));
        exp.bind(argAct);
        updateCapture();

        engine.showAnimation(opAct.appear(oLoc));
        exp.bind(opAct);
        updateCapture();

        engine.showAnimation(eqAct.appear(eLoc));
        exp.bind(eqAct);
        updateCapture();
        engine.showAnimation(resAct.appear(rLoc));
        exp.bind(resAct);
        exp.setLight(Actor.SHADED);

        release();

        ValueActor clone = (ValueActor) resAct.clone();
        clone.setLight(Actor.NORMAL);
        clone.setLocation(resAct.getRootLocation());
        theatre.addActor(clone);
        currentScratch.registerCrap(clone);
        result.setActor(clone);

        Value val = (Value) result.clone();
        val.setActor((ValueActor) clone.clone());

        return val;
    }

    /**
     * @param message
     */
    private void showMessage(String[] message, Highlight h) {

        if (jeliot.showMessagesInDialogs()) {

            String msg = "";
            int n = message.length;
            for (int i = 0; i < n; i++) {
                msg += message[i] + "\n";
            }

            JOptionPane.showMessageDialog(null,
                    msg,
                    bundle.getString("dialog.message.title"),
                    JOptionPane.PLAIN_MESSAGE);
        } else {
            MessageActor actor = factory.produceMessageActor(message);
            showMessage(actor, h);
        }
    }

    /**
     * @param message
     */
    private void showMessage(String message, Highlight h) {
        String[] ms = {message};
        showMessage(ms, h);
    }

    /*  Not Valid Code Any More
        private void showMessage(String message, Value val) {
    
            if (jeliot.showMessagesInDialogs()) {
                JOptionPane.showMessageDialog(null, message, "Message",
                                              JOptionPane.PLAIN_MESSAGE);
            } else {
                String[] ms = {message};
                MessageActor actor = factory.produceMessageActor(ms);
                ValueActor valact = val.getActor();
                showMessage(actor, valact);
            }
        }
    
        private void showMessage(MessageActor message, Actor anchor) {
    
            Point aloc = anchor.getRootLocation();
            Dimension asize = anchor.getSize();
            aloc.translate(asize.width/2, 0);
            Dimension msize = message.getSize();
            Dimension tsize = theatre.getSize();
    
            if (aloc.x - msize.width/2 < 10) {
                aloc.x = 10;
            } else if (aloc.x + msize.width/2 > tsize.width-10) {
                aloc.x = tsize.width-10-msize.width;
            } else {
                aloc.x -= msize.width/2;
            }
    
            aloc.y += asize.height + 10;
            showMessage(message, new Point(aloc.x, aloc.y));
            currentScratch.registerCrap(anchor);
        }
    */



    /**
     * @param message
     */
    private void showMessage(MessageActor message, Highlight h) {
//Dimension msize = message.getSize();
//Dimension tsize = theatre.getSize();
//int x = (tsize.width-msize.width)/2;
//int y = (tsize.height-msize.height)/2;
        ExpressionActor ea = currentScratch.getExpression(1, -3);
        Point loc = ea.getRootLocation();
        ea.reserve(message);
        ea.bind(message);
        showMessage(message, loc, h);
        ea.removeActor(message);
    }

    /**
     * @param message
     * @param p
     */
    private void showMessage(MessageActor message, Point p, Highlight h) {
        capture();
        engine.showAnimation(message.appear(p));
        release();
        highlight(h);
        //messagePause = true;
        theatre.removeActor(message);
    }

    /* All the message formats are here */

    /**
     *
     */
    private MessageFormat enterLoop =
            new MessageFormat(bundle.getString("message.enter_loop"));
    /**
     *
     */
    private MessageFormat continueLoop =
            new MessageFormat(bundle.getString("message.continue_loop"));
    /**
     *
     */
    private MessageFormat exitLoop =
            new MessageFormat(bundle.getString("message.exit_loop"));
    /**
     *
     */
    private MessageFormat breakLoop =
            new MessageFormat(bundle.getString("message.break_loop"));
    /**
     *
     */
    private MessageFormat skipLoop =
            new MessageFormat(bundle.getString("message.skip_loop"));
    /**
     *
     */
    private MessageFormat arrayCreation =
            new MessageFormat(bundle.getString("message.array_creation"));
    /**
     *
     */
    private MessageFormat arrayCreationDimensions =
            new MessageFormat(bundle.getString("message.array_creation.dimensions"));

    //message.open_scope = Opening new scope for variables.
    //message.close_scope = Closing a scope and erasing the scope variables.
    //message.break_switch = Exiting the switch statement because of break.
    //message.if_then = Choosing then-branch.
    //message.if_else = Choosing else-branch.
    //message.skip_if = Continuing without branching.
    //message.enter_switch = Entering a switch statement.
    //message.exit_switch = Exiting a switch statement.
    //message.select_switch = This case selected.
    //message.default_switch = Default case selected.
            
    /**
     * 
     */
    public void openScope() {
        //highlight(null);
        //showMessage(bundle.getString("message.open_scope"));
        getCurrentMethodFrame().openScope();
    }

    /**
     * 
     */
    public void closeScope() {
        //highlight(null);
        //showMessage(bundle.getString("message.close_scope"));
        getCurrentMethodFrame().closeScope();
    }

    /**
     * @param statementName
     * @param h
     */
    public void enterLoop(String statementName, Highlight h) {
        highlight(h);
        showMessage(enterLoop.format(new String[]{statementName}), h);
    }

    /**
     * @param statementName
     * @param check
     * @param h
     */
    public void enterLoop(String statementName, Value check, Highlight h) {
        highlight(h);
        showMessage(enterLoop.format(new String[]{statementName}), h);
        //, check);
    }

    /**
     * @param statementName
     * @param check
     * @param h
     */
    public void continueLoop(String statementName, Value check, Highlight h) {
        highlight(h);
        showMessage(continueLoop.format(new String[]{statementName}), h);
        //, check);
    }

    /**
     * @param statementName
     * @param check
     */
    public void exitLoop(String statementName, Value check) {
        highlight(null);
        showMessage(exitLoop.format(new String[]{statementName}), null);
        //, check);
    }

    /**
     * @param statementName
     * @param h
     */
    public void breakLoop(String statementName, Highlight h) {
        highlight(h);
        showMessage(breakLoop.format(new String[]{statementName}), h);
    }

    /**
     * @param h
     */
    public void breakSwitch(Highlight h) {
        highlight(h);
        showMessage(bundle.getString("message.break_switch"), h);
    }

    /**
     * @param statementName
     * @param check
     */
    public void skipLoop(String statementName, Value check) {
        highlight(null);
        showMessage(skipLoop.format(new String[]{statementName}), null);
        //, check);
    }

    /**
     * @param statementName
     * @param h
     */
    public void continueLoop(String statementName, Highlight h) {
        highlight(h);
        showMessage(continueLoop.format(new String[]{statementName}), h);
    }

    /**
     * @param check
     * @param h
     */
    public void branchThen(Value check, Highlight h) {
        highlight(h);
        showMessage(bundle.getString("message.if_then"), h); //, check, h);
    }

    /**
     * @param check
     * @param h
     */
    public void branchElse(Value check, Highlight h) {
        highlight(h);
        showMessage(bundle.getString("message.if_else"), h); //, check, h);
    }

    /**
     * @param check
     * @param h
     */
    public void skipIf(Value check, Highlight h) {
        highlight(h);
        showMessage(bundle.getString("message.skip_if"), h); //, check, h);
    }

    /**
     * @param h
     */
    public void openSwitch(Highlight h) {
        highlight(h);
        showMessage(bundle.getString("message.enter_switch"), h); //, check, h);
    }

    /**
     * @param h
     */
    public void closeSwitch(Highlight h) {
        highlight(h);
        showMessage(bundle.getString("message.exit_switch"), h); //, check, h);
    }

    /**
     * @param h
     */
    public void switchSelected(Highlight h) {
        highlight(h);
        showMessage(bundle.getString("message.select_switch"), h); //, check, h);
    }

    /**
     * @param h
     */
    public void switchDefault(Highlight h) {
        highlight(h);
        showMessage(bundle.getString("message.default_switch"), h); //, check, h);
    }

    public void openArrayInitializer(Highlight h) {
        highlight(h);
        showMessage(bundle.getString("message.open_array_initializer"), h);
    }

    public void closeArrayInitializer(Highlight h) {
        highlight(h);
        showMessage(bundle.getString("message.close_array_initializer"), h);
    }

    /**
     * @param dims
     * @param h
     */
    public void arrayCreation(int[] dims, Highlight h) {

        String dimensions = "";
        int n = dims.length;
        for (int i = 0; i < n; i++) {
            if (i == n - 1) {
                dimensions += dims[i];
            } else {
                dimensions += dims[i] + ", ";
            }
        }
        String[] dimensionNumber = new String[1];
        dimensionNumber[0] = String.valueOf(dims.length);

        highlight(h);
        String[] message = new String[2];
        message[0] = arrayCreation.format(dimensionNumber);
        message[1] =
                arrayCreationDimensions.format(new String[]{dimensions});
        showMessage(message, h);
    }

    /**
     * @param val
     * @param h
     */
    public void output(Value val, Highlight h) {

        highlight(h);

        ValueActor actor = val.getActor();
        if (actor == null) {
            actor = factory.produceValueActor(val);
        }

        AnimatingActor hand = factory.produceHand();

        Point dest = manager.getOutputPoint();
        Point handp = new Point(dest.x, dest.y - hand.getHeight());
        Point vdp =
                new Point(dest.x + hand.getWidth() / 3,
                        dest.y - hand.getHeight() * 2 / 3);

        Animation fist = hand.changeImage(factory.produceImage("image.hand2"));
        fist.setDuration(800);

        capture();

        hand.setLocation(dest);
        engine.showAnimation(new Animation[]{actor.fly(vdp), hand.fly(handp, 0), fist});

        hand.setImage(factory.produceImage("image.hand3"));
        theatre.removeActor(actor);
        engine.showAnimation(hand.fly(dest, 0));

        theatre.removeActor(hand);
        release();

        this.output(val.getValue()); //+ "\n");
    }

    /**
     * @param e
     */
    public void showErrorMessage(InterpreterError e) {
        errorOccured = true;
        jeliot.showErrorMessage(e);
    }

    /**
     * @param str
     */
    public void output(String str) {
        str = MCodeUtilities.replace(str, "\\n", "\n");
        jeliot.output(str);
    }

    /**
     * @return
     */
    public AnimationEngine getEngine() {
        return engine;
    }

    /**
     * @return
     */
    public TheaterManager getManager() {
        return manager;
    }

    /**
     * @return
     */
    public ActorFactory getFactory() {
        return factory;
    }

    /**
     * @return
     */
    public Theater getTheatre() {
        return theatre;
    }

    /**
     * @return
     */
    public static Animator readInt(String prompt) {
        if (prompt == null) {
            prompt = bundle.getString("input.int");
        }
        return new InputAnimator(prompt,
                new InputValidator() {
                    public void validate(String s) {
                        try {
                            Integer i = new Integer(s);
                            accept(new Value(i.toString(), int.class.getName()));
                        } catch (NumberFormatException e) {
                        }
                    }
                });
    }

    /**
     * @return
     */
    public static Animator readDouble(String prompt) {
        if (prompt == null)
            prompt = bundle.getString("input.double");
        return new InputAnimator(prompt,
                new InputValidator() {
                    public void validate(String s) {
                        try {
                            Double d = new Double(s);
                            accept(new Value(d.toString(), double.class.getName()));
                        } catch (NumberFormatException e) {
                        }
                    }
                });
    }

    /**
     * @return
     */
    public static Animator readString(String prompt) {
        if (prompt == null)
            prompt = bundle.getString("input.string");
        return new InputAnimator(prompt,
                new InputValidator() {
                    public void validate(String s) {
                        try {
                            accept(new Value(s, "".getClass().getName()));
                        } catch (NumberFormatException e) {
                        }
                    }
                });
    }

    /**
     * @return
     */
    public static Animator readChar(String prompt) {
        if (prompt == null)
            prompt = bundle.getString("input.char");
        return new InputAnimator(prompt,
                new InputValidator() {
                    public void validate(String s) {
                        if (s.length() == 1) {
                            accept(new Value(s, char.class.getName()));
                        }
                    }
                });
    }

    /**
     * @author Pekka Uronen
     * @author Niko Myller
     */
    private static class InputAnimator extends Animator {

        private String prompt;
        private InputValidator validator;

        private InputAnimator(String prompt, InputValidator valid) {

            this.prompt = prompt;
            this.validator = valid;
        }

        public void animate(Director director) {
            Value val = director.getInput(prompt, validator);
            setReturnValue(val);
        }
    }

    /**
     * Shows an animation of the invocation of a static foreign
     * method for handling input.
     *
     * @param type
     * @param prompt
     * @param h
     * @return
     */
    public Value animateInputHandling(String type, String prompt, Highlight h) {

        Animator animator = null;

        if (MCodeUtilities.resolveType(type) == MCodeUtilities.DOUBLE) {
            animator = readDouble(prompt);
        } else if (MCodeUtilities.resolveType(type) == MCodeUtilities.INT) {
            animator = readInt(prompt);
        } else if (MCodeUtilities.resolveType(type) == MCodeUtilities.STRING) {
            animator = readString(prompt);
        } else if (MCodeUtilities.resolveType(type) == MCodeUtilities.CHAR) {
            animator = readChar(prompt);
        } else if (MCodeUtilities.resolveType(type) == MCodeUtilities.FLOAT) {
            animator = readFloat(prompt);
        } else if (MCodeUtilities.resolveType(type) == MCodeUtilities.BYTE) {
            animator = readByte(prompt);
        } else if (MCodeUtilities.resolveType(type) == MCodeUtilities.LONG) {
            animator = readLong(prompt);
        } else if (MCodeUtilities.resolveType(type) == MCodeUtilities.SHORT) {
            animator = readShort(prompt);
        } else if (MCodeUtilities.resolveType(type) == MCodeUtilities.BOOLEAN) {
            animator = readBoolean(prompt);
        }

        if (animator != null) {
            highlight(h);
            animator.animate(this);
            return animator.getReturnValue();
        } else {
            //TODO: Here should be an exception
            return null;
        }
    }

    /**
     * @param prompt
     * @return
     */
    private Animator readByte(String prompt) {
        if (prompt == null)
            prompt = bundle.getString("input.byte");
        return new InputAnimator(prompt,
                new InputValidator() {
                    public void validate(String s) {
                        try {
                            Byte d = new Byte(s);
                            accept(new Value(d.toString(), byte.class.getName()));
                        } catch (NumberFormatException e) {
                        }
                    }
                });
    }

    /**
     * @param prompt
     * @return
     */
    private Animator readShort(String prompt) {
        if (prompt == null)
            prompt = bundle.getString("input.short");
        return new InputAnimator(prompt,
                new InputValidator() {
                    public void validate(String s) {
                        try {
                            Short d = new Short(s);
                            accept(new Value(d.toString(), short.class.getName()));
                        } catch (NumberFormatException e) {
                        }
                    }
                });
    }


    /**
     * @param prompt
     * @return
     */
    private Animator readBoolean(String prompt) {
        if (prompt == null)
            prompt = bundle.getString("input.boolean");
        return new InputAnimator(prompt,
                new InputValidator() {
                    public void validate(String s) {
                        try {
                            Boolean d = new Boolean(s);
                            accept(new Value(d.toString(), boolean.class.getName()));
                        } catch (NumberFormatException e) {
                        }
                    }
                });
    }


    /**
     * @param prompt
     * @return
     */
    private Animator readFloat(String prompt) {
        if (prompt == null)
            prompt = bundle.getString("input.float");
        return new InputAnimator(prompt,
                new InputValidator() {
                    public void validate(String s) {
                        try {
                            Float d = new Float(s);
                            accept(new Value(d.toString(), float.class.getName()));
                        } catch (NumberFormatException e) {
                        }
                    }
                });
    }

    /**
     * @param prompt
     * @return
     */
    private Animator readLong(String prompt) {
        if (prompt == null)
            prompt = bundle.getString("input.long");
        return new InputAnimator(prompt,
                new InputValidator() {
                    public void validate(String s) {
                        try {
                            Long d = new Long(s);
                            accept(new Value(d.toString(), long.class.getName()));
                        } catch (NumberFormatException e) {
                        }
                    }
                });
    }

    /**
     * @param prompt
     * @param validator
     * @return
     */
    public Value getInput(String prompt, InputValidator validator) {

        validator.setController(controller);
        final InputComponent ic = new InputComponent(prompt, validator);

        //TODO: -2 should be changed to a constant of its own
        final ExpressionActor ea = currentScratch.getExpression(1, -2);

        Actor bga = factory.produceMessageActor(null);
        final Point p = ea.reserve(bga);
        ic.setBgactor(bga);

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    theatre.add(ic);
                    ic.setSize(ic.getPreferredSize());
                    ic.setLocation(p);
                    ic.revalidate();
                    theatre.showComponents(true);
                    release();
                    ic.popup();
                    theatre.flush();
                }
            });
        } catch (java.lang.reflect.InvocationTargetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        do {
            controller.pause();

            controller.checkPoint(new Controlled() {

                public void suspend() {
                    jeliot.directorFreezed();
                }

                public void resume() {
                    jeliot.directorResumed();
                }
            });
        } while (!validator.isOk());

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    theatre.remove(ic);
                    theatre.showComponents(false);
                    theatre.flush();
                }
            });
        } catch (java.lang.reflect.InvocationTargetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Value val = validator.getValue();
        ValueActor act = factory.produceValueActor(val);
        act.setLocation(p);
        val.setActor(act);
        currentScratch.registerCrap(act);
        //theatre.addActor(act);
        ea.cut();

        capture();
        engine.showAnimation(act.appear(ea.reserve(act)));
        release();

        ea.bind(act);

        return val;
    }

    /**
     * @param array
     * @param ref
     * @param lenVal
     * @param expressionCounter
     * @param h
     */
    public void showArrayCreation(ArrayInstance array,
                                  jeliot.lang.Reference ref,
                                  Value[] lenVal,
                                  long expressionCounter,
                                  Highlight h) {

        highlight(h);

        //Array creation here
        //Use SMIActor as base.
        int n = 0;
        if (lenVal != null) {
            n = lenVal.length;
        }

        ACActor actor =
                factory.produceACActor("new " + array.getComponentType(), n);
        ExpressionActor ea = currentScratch.getExpression(1, -1);
        currentScratch.registerCrap(actor);

        //Point invoLoc = ea.getRootLocation();
        Point invoLoc = ea.reserve(actor);
        actor.setLocation(invoLoc);

        // Create actors and reserve places for all argument values,
        // and create animations to bring them in their right places.
        ValueActor[] argact = new ValueActor[n];
        Animation[] fly = new Animation[n];
        for (int i = 0; i < n; ++i) {
            argact[i] = lenVal[i].getActor();
            lenVal[i].setActor(argact[i]);
            fly[i] = argact[i].fly(actor.reserve(argact[i]));
        }

        // Calculate the size of the invocation actor, taking into account
        // the argument actors.
        actor.calculateSize();

        // Show the animation.
        capture();

        // Introduce the invocation.
        engine.showAnimation(actor.appear(invoLoc));
        theatre.passivate(actor);

        // Bring in arguments.
        engine.showAnimation(fly);

        // Bind argument actors to the invocation actor.
        for (int i = 0; i < n; ++i) {
            actor.bind(argact[i]);
        }

        highlight(h);

        ArrayActor arrayAct = factory.produceArrayActor(array);
        array.setArrayActor(arrayAct);

        Point loc = manager.reserve(arrayAct);
        capture();
        engine.showAnimation(arrayAct.appear(loc));
        release();
        manager.bind(arrayAct);

        ReferenceActor refAct = factory.produceReferenceActor(ref);
        ref.setActor(refAct);
        refAct.setLocation(arrayAct.getRootLocation());

        //Remove passive Actor "actor" from Theatre.
        currentScratch.removeCrap(actor);
        //Remove ea from scratch!.
        currentScratch.removeActor(ea);

        capture();

        ExpressionActor expr =
                currentScratch.getExpression(1, expressionCounter);
        Point firstLoc = expr.reserve(refAct);
        engine.showAnimation(refAct.fly(firstLoc));
        expr.bind(refAct);

        release();
    }

    /**
     * @param var
     * @param indexVal
     * @param returnVal
     * @param h
     */
    public void showArrayAccess(VariableInArray var,
                                Value[] indexVal,
                                Value returnVal,
                                Highlight h) {

        highlight(h);

        Value value = var.getValue();

        final VariableInArrayActor varAct =
                (VariableInArrayActor) var.getActor();

        ValueActor returnAct = factory.produceValueActor(returnVal);
        returnVal.setActor(returnAct);
        Point loc = value.getActor().getRootLocation();
        returnAct.setLocation(loc);

        int n = indexVal.length;

        Animation[] appear = new Animation[n];
        Animation[] index = new Animation[n];
        IndexActor[] indexAct = new IndexActor[n];
        ValueActor[] indexValAct = new ValueActor[n];

        for (int i = 0; i < n; i++) {
            indexValAct[i] = indexVal[i].getActor();
            indexAct[i] = new IndexActor(indexValAct[i]);
            appear[i] = indexValAct[i].appear(indexValAct[i].getRootLocation());
            appear[i].setDuration(600);
            index[i] = indexAct[i].index(varAct);
        }

        capture();
        engine.showAnimation(appear);
        engine.showAnimation(index);
        varAct.setLight(Actor.HIGHLIGHT);
        //engine.showAnimation(returnAct.appear(loc));
        release();

        for (int i = 0; i < n; i++) {
            currentScratch.registerCrap(indexValAct[i]);
            currentScratch.registerCrap(indexAct[i]);
        }
        //currentScratch.registerCrap(returnAct);

        currentScratch.registerCrapRemover(new Runnable() {
            public void run() {
                varAct.setLight(Actor.NORMAL);
            }
        });
    }

    /**
     * @param variable
     * @param value
     * @param casted
     * @param literal
     * @param h
     */
    public void initializeArrayVariable(VariableInArray variable, Value value, Value casted, boolean literal, Highlight h) {

        highlight(h);

        final VariableInArrayActor variableAct = (VariableInArrayActor) variable.getActor();

        String type = variable.getType();
        ValueActor valueAct = value.getActor();

        if (MCodeUtilities.isPrimitive(type)) {
            // Get/create actors.
            ValueActor castAct = factory.produceValueActor(casted);
            casted.setActor(castAct);

            Point valueLoc = variableAct.reserve(castAct);

            capture();
            variableAct.setLight(Actor.HIGHLIGHT);

            if (!literal) {
                engine.showAnimation(valueAct.fly(valueLoc));
            } else {
                Animation appear = valueAct.appear(valueLoc);
                appear.setDuration(100);
                engine.showAnimation(appear);
            }

            variableAct.bind();
            theatre.removePassive(valueAct);
            release();

        } else {

            // Get/create actors.
            ReferenceActor refAct = (ReferenceActor) value.getActor();
            //refAct.calculateBends();
            ReferenceActor ra = factory.produceReferenceActor(refAct);
            casted.setActor(ra);

            if (variableAct instanceof ReferenceVariableInArrayActor) {
                ReferenceVariableInArrayActor rva = (ReferenceVariableInArrayActor) variableAct;

                //refAct.setBackground(rva.getBackground());

                //rva.setReference(refAct);
                //instAct.addReference(refAct);
                Point valueLoc = rva.reserve(ra);

                capture();

                variableAct.setLight(Actor.HIGHLIGHT);

                if (!literal) {
                    engine.showAnimation(refAct.fly(valueLoc));
                } else {
                    Animation appear = refAct.appear(valueLoc);
                    appear.setDuration(200);
                    engine.showAnimation(appear);
                }

                rva.bind();
                theatre.removePassive(refAct);
                variableAct.setLight(Actor.NORMAL);
                release();
            }
        }
    }


    /**
     * @param length
     * @param ai
     */
    public void introduceArrayLength(Value length, ArrayInstance ai) {
        ValueActor lengthAct = factory.produceValueActor(length);
        lengthAct.setLocation(ai.getActor().getRootLocation());
        length.setActor(lengthAct);
    }

    /**
     * @param of
     * @param h
     */
    public void showObjectCreation(ObjectFrame of, Highlight h) {

        highlight(h);

        ObjectStage os = factory.produceObjectStage(of);
        of.setObjectStage(os);

        Point loc = manager.reserve(os);
        capture();
        engine.showAnimation(os.appear(loc));
        release();
        manager.bind(os);

    }

    /**
     * @param actor
     */
    public void removeInstance(InstanceActor actor) {
        manager.removeInstance(actor);
    }


    /*
        public void showArrayVariableAccess(VariableInArray var,
                                            Value indexVal) {
    
            //System.err.println("Wopee! " + var + " " + indexVal);
    
            final VariableInArrayActor varAct =
                    (VariableInArrayActor)var.getActor();
    
            ValueActor indexValAct = indexVal.getActor();
    
            IndexActor indexAct = new IndexActor(indexValAct);
            Animation appear = indexValAct.appear(
                    indexValAct.getRootLocation());
            appear.setDuration(600);
    
            capture();
            engine.showAnimation(appear);
            engine.showAnimation(indexAct.index(varAct));
            varAct.setLight(Actor.HIGHLIGHT);
            release();
    
            currentScratch.registerCrap(indexValAct);
            currentScratch.registerCrap(indexAct);
            currentScratch.registerCrapRemover(
                new Runnable() {
                    public void run() {
                        varAct.setLight(Actor.NORMAL);
                    }
                }
            );
    
        }
    */

    /**
     * @param of
     * @param h
     */
    public void showClassCreation(jeliot.lang.Class c) {

        highlight(null);

        ClassActor ca = factory.produceClassActor(c);
        c.setClassActor(ca);

        Point loc = manager.reserve(ca);
        capture();
        engine.showAnimation(ca.appear(loc));
        release();
        manager.bind(ca);

        ListIterator li = c.getVariables();

        while (li.hasNext()) {
            Variable v = (Variable) li.next();
            declareClassVariable(c, v, v.getValue());
        }
    }

    /**
     * @param c
     * @param var
     * @param val
     */
    public void declareClassVariable(jeliot.lang.Class c, Variable var, Value val) {

        highlight(var.getLocationInCode());

        ClassActor ca = c.getClassActor();
        VariableActor actor = factory.produceVariableActor(var);
        var.setActor(actor);

        Point loc2 = ca.reserve(actor);

        Animation a = ca.extend();
        if (a != null) {
            engine.showAnimation(a);
        }

        capture();

        engine.showAnimation(actor.appear(loc2));
        ca.bind();

        release();

        if (val != null && var.isFinal()) {
            introduceLiteral(val);
            
            Value casted = (Value) val.clone();
            casted.setActor(factory.produceValueActor(val));
            
            Value returned = (Value) val.clone();
            returned.setActor(factory.produceValueActor(val));
            
            animateAssignment(var,
                    val,
                    casted,
                    returned,
                    var.getLocationInCode());
            var.assign(casted);
        } 
    }
    
    //Jeliot 3
    /**
     * Used for setting the mcode interpreter
     */
    public void setInterpreter(MCodeInterpreter mCodeInterpreter) {
        this.mCodeInterpreter = mCodeInterpreter;
    }
}
