package jeliot.ecode;

import java.util.*;
import java.io.*;
import jeliot.lang.*;
import jeliot.theatre.*;

/*
* Created by Niko Myller 10.12.2002
*
**/

public class Interpreter {

    private Director director = null;
    private BufferedReader ecode = null;
    private PrintWriter input = null;

    private String programCode = "";

    private boolean running = true;
    private boolean start = true;
    private boolean firstLineRead = false;
    private boolean invokingMethod = false;

    //Keeps track of current return value
    private boolean returned = false;
    private Value returnValue = null;
    private Actor returnActor = null;
    private int returnExpressionCounter = 0;

    private Stack commands = new Stack();
    private Stack exprs = new Stack();
    private Hashtable values = new Hashtable();
    private Hashtable variables = new Hashtable();
    private Hashtable instances = new Hashtable();
    private Stack methodInvocation = new Stack();

    private Hashtable postIncsDecs = new Hashtable();

    private ClassInfo currentClass = null;

    private Hashtable classes = new Hashtable();

    private String line = null;

    /**
    * currentMethodInvocation keeps track of all the information that
    * is collected during the method invocation.
    * Cells:
    * 0: Method name
    * 1: Class/Object expression
    * 2: Parameter values
    * 3: Parameter types
    * 4: Parameter names
    * 5: Highlight info for invocation
    * 6: Highlight info for declaration
    * 7: Parameter expression references
    * 8: Object reference if method is constructor or object method
    */
    private Object[] currentMethodInvocation = null;

    private Stack objectCreation = new Stack();

    protected Interpreter() { }

    public Interpreter(BufferedReader r,
                       Director d,
                       String programCode,
                       PrintWriter pr) {
        this.ecode = r;
        this.director = d;
        this.programCode = programCode;
        this.input = pr;
    }

    public void initialize() {
        running = true;
        start = true;
        Actor returnActor = null;
        currentMethodInvocation = null;
        currentClass = null;
        classes = new Hashtable();
        commands = new Stack();
        exprs = new Stack();
        values = new Hashtable();
        variables = new Hashtable();
        methodInvocation = new Stack();
        Value returnValue = null;
        Actor ReturnActor= null;
        postIncsDecs = new Hashtable();
        instances = new Hashtable();
        classes = new Hashtable();
        objectCreation = new Stack();

        try {
            line = ecode.readLine();
            System.out.println(line);
        } catch (Exception e) {}

        //Change this to be something more meaningful!
        if (line == null) {
            line = "" + Code.ERROR + Code.DELIM +
                   "<H1>Runtime Exception</H1>" +
                   Code.DELIM + "0,0,0,0";
        }

        StringTokenizer tokenizer = new StringTokenizer(line, Code.DELIM);

        if (Integer.parseInt(tokenizer.nextToken()) == Code.ERROR) {
            String message = tokenizer.nextToken();
            Highlight h = ECodeUtilities.makeHighlight(tokenizer.nextToken());

            director.showErrorMessage(new InterpreterError(message, h));
            running = false;
        } else {
            firstLineRead = true;
        }

    }

    public boolean starting() {
        return start;
    }

    public boolean emptyScratch() {
        return exprs.empty();
    }

    public void execute() {

        director.openScratch();

        while (running) {

            if (!firstLineRead) {
                try {

                    line = ecode.readLine();
                    System.out.println(line);

                } catch (Exception e) {}

                //Change this to be something more meaningful!
                if (line == null) {
                    line = "" + Code.ERROR + Code.DELIM +
                           "<H1>Runtime Exception</H1>" +
                           Code.DELIM + "0,0,0,0";
                }
            } else {
                firstLineRead = false;
            }

            if (!line.equals("" + Code.END)) {

                StringTokenizer tokenizer = new StringTokenizer(line, Code.DELIM);

                if (tokenizer.hasMoreTokens()) {

                    int token = Integer.parseInt(tokenizer.nextToken());


                    if (exprs.empty()         &&
                        !invokingMethod       &&
                        token != Code.WHI     &&
                        token != Code.FOR     &&
                        token != Code.DO      &&
                        token != Code.IFT     &&
                        token != Code.IFTE    &&
                        token != Code.SWIBF   &&
                        token != Code.SWITCHB &&
                        token != Code.SWITCH  &&
                        token != Code.VD      &&
                        token != Code.OUTPUT  &&
                        token != Code.INPUT   &&
                        token != Code.INPUTTED) {
                            director.closeScratch();
                            director.openScratch();
                    }

                    //checkInstancesForRemoval();

                    switch (token) {

                        //Gives a reference to the left hand side of the expression
                        case Code.LEFT: {

                            int token1 = Integer.parseInt(tokenizer.nextToken());
                            commands.push("" + Code.LEFT + Code.DELIM + token1);
                            break;
                        }

                        //Gives a reference to the right hand side of the expression
                        case Code.RIGHT: {

                            int token1 = Integer.parseInt(tokenizer.nextToken());
                            commands.push("" + Code.RIGHT + Code.DELIM + token1);
                            break;
                        }

                        //Begins an expression
                        case Code.BEGIN: {

                            //first token
                            int expressionType = Integer.parseInt(tokenizer.nextToken());
                            int expressionReference = Integer.parseInt(tokenizer.nextToken());
                            String location = tokenizer.nextToken();
                            exprs.push(expressionType + Code.DELIM + expressionReference + Code.DELIM + location);
                            break;
                        }

                        //Indicates where the value is assigned
                        case Code.TO: {

                            int expressionReference = Integer.parseInt(tokenizer.nextToken());
                            commands.push("" + Code.TO + Code.DELIM + expressionReference);
                            break;
                        }

                        //Assignment
                        case Code.A: {

                            int expressionCounter = Integer.parseInt(tokenizer.nextToken());
                            int fromExpression = Integer.parseInt(tokenizer.nextToken());
                            int toExpression =  Integer.parseInt(tokenizer.nextToken());
                            String value = null;
                            if (tokenizer.countTokens() >= 2) {
                                value = tokenizer.nextToken();
                            } else {
                                value = "";
                            }
                            String type = tokenizer.nextToken();
                            Highlight h = ECodeUtilities.makeHighlight(
                                                         tokenizer.nextToken());

                            Variable toVariable = (Variable) variables.remove(new Integer(toExpression));

                            //just to get rid of extra references
                            variables.remove(new Integer(fromExpression));

                            Value fromValue = (Value) values.remove(new Integer(fromExpression));
                            Value casted = null;
                            Value expressionValue = null;
                            if (ECodeUtilities.isPrimitive(type) || type.equals("null")) {
                                casted = new Value(value, type);
                                expressionValue = new Value(value, type);

                                if (!casted.getType().equals(fromValue.getType()) &&
                                    ECodeUtilities.resolveType(casted.getType()) !=
                                    ECodeUtilities.resolveType(fromValue.getType())) {
                                    director.animateCastExpression(fromValue, casted);
                                    fromValue.setActor(casted.getActor());
                                }

                            } else {
                                Instance inst =
                                 (Instance) instances.get(
                                        ECodeUtilities.getHashCode(value));
                                if (inst != null) {
                                    casted = new Reference(inst);
                                    ((Reference)casted).makeReference();
                                    expressionValue = new Reference(inst);
                                } else {
                                    casted = new Reference();
                                    expressionValue = new Reference();
                                }
                            }


                            director.animateAssignment(toVariable, fromValue, casted, expressionValue, h);
                            toVariable.assign(casted);

                            values.put(new Integer(expressionCounter), expressionValue);

                            Object[] postIncDec = (Object[]) postIncsDecs.remove(new Integer(fromExpression));

                            if (postIncDec != null) {
                                doPostIncDec(postIncDec);
                            }

                            postIncDec = (Object[]) postIncsDecs.remove(new Integer(toExpression));

                            if (postIncDec != null) {
                                doPostIncDec(postIncDec);
                            }

                            exprs.pop();

                            director.closeScratch();
                            director.openScratch();

                            break;
                        }


                        // Unary Expressions
                        case Code.COMP:     // Complement
                        case Code.PLUS:     // Plus operator
                        case Code.MINUS:    // Minus operator
                        case Code.NO: {     // Boolean Not

                            int expressionCounter = Integer.parseInt(tokenizer.nextToken());
                            int unaryExpressionReference = Integer.parseInt(tokenizer.nextToken());
                            String value = tokenizer.nextToken();
                            String type = tokenizer.nextToken();

                            Highlight h = ECodeUtilities.makeHighlight(
                                                         tokenizer.nextToken());
                            Value result = new Value(value, type);
                            Value val = (Value) values.remove(new Integer(unaryExpressionReference));

                            int unaryOperator = ECodeUtilities.resolveUnOperator(token);

                            ExpressionActor expr = director.getCurrentScratch().findActor(expressionCounter);

                            if (expr == null) {
                                expr = director.beginUnaryExpression(unaryOperator,
                                                                     val,
                                                                     expressionCounter,
                                                                     h);
                            }

                            Object[] postIncDec = (Object[]) postIncsDecs.remove(new Integer(unaryExpressionReference));

                            if (postIncDec != null) {
                                doPostIncDec(postIncDec);
                            }

                            Value expressionValue =
                                  director.finishUnaryExpression(unaryOperator,
                                                                 expr,
                                                                 result,
                                                                 expressionCounter,
                                                                 h);

                            //NOT NEEDED ANY MORE!
                            //This is not needed after the changes.
                            //Value expressionValue =
                            //        director.animateUnaryExpression(operator,
                            //                                        val,
                            //                                        result,
                            //                                        expressionCounter,
                            //                                        h);

//                          values.put(new Integer(expressionCounter), expressionValue);

                            exprs.pop();

                            handleExpression(expressionValue, expressionCounter);

/*
                            //command that wait for this expression (left, right)
                            int command = -1;
                            int oper = -1;
                            int size = commands.size();

                            //We find the command
                            for (int i = size - 1; i >= 0; i--) {
                                StringTokenizer commandTokenizer = new StringTokenizer(
                                                (String) commands.elementAt(i),
                                                Code.DELIM);
                                int comm = Integer.parseInt(commandTokenizer.nextToken());
                                int cid = Integer.parseInt(commandTokenizer.nextToken());
                                if (expressionCounter == cid) {
                                    command = comm;
                                    commands.removeElementAt(i);
                                    break;
                                }
                            }
*/
                            /**
                            * Look from the expression stack
                            * what expression should be shown next
                            */
/*
                            int expressionReference = 0;
                            Highlight highlight = null;

                            if (!exprs.empty()) {

                                StringTokenizer expressionTokenizer = new StringTokenizer(
                                                                      (String) exprs.peek(),
                                                                      Code.DELIM);

                                oper = Integer.parseInt(expressionTokenizer.nextToken());

                                expressionReference = Integer.parseInt(
                                                              expressionTokenizer.nextToken());

                                //Make the location information for the location token
                                highlight = ECodeUtilities.makeHighlight(
                                expressionTokenizer.nextToken());
                            }

                            //Do different things depending on in what expression
                            //the literal is used.

                            //If operator is assignment we just store the value
                            if (oper == Code.A) {

                                values.put(new Integer(expressionCounter), expressionValue);

                            //If oper is other binary operator we will show it
                            //on the screen with operator
                            } else if (ECodeUtilities.isBinary(oper)) {

                                int operator = ECodeUtilities.resolveBinOperator(oper);

                                if (command == Code.LEFT) {

                                    director.beginBinaryExpression(expressionValue, operator,
                                                expressionReference, highlight);

                                } else if (command == Code.RIGHT) {

                                    ExpressionActor ea = (ExpressionActor)
                                        director.getCurrentScratch().findActor(expressionReference);
                                    if (ea != null) {
                                        director.rightBinaryExpression(expressionValue, ea, highlight);
                                    } else {
                                        values.put(new Integer(expressionCounter), expressionValue);
                                    }

                                } else {
                                        values.put(new Integer(expressionCounter), expressionValue);
                                }

                            //If oper is a unary operator we will show it
                            //on the screen with operator
                            } else if (ECodeUtilities.isUnary(oper)) {

                                values.put(new Integer(expressionCounter), expressionValue);

                                int operator = ECodeUtilities.resolveUnOperator(oper);

                                if (command == Code.RIGHT) {
                                  director.beginUnaryExpression(operator, expressionValue,
                                                      expressionReference, highlight);
                                }

                            //If it is something else we will store it for later use.
                            } else {

                                values.put(new Integer(expressionCounter), expressionValue);

                            }
*/
                            break;

                        }


                        // Unary Expression
                        case Code.PIE:      // PostIncrement
                        case Code.PDE: {    // PostDecrement

                            int expressionCounter = Integer.parseInt(tokenizer.nextToken());
                            int expressionReference = Integer.parseInt(tokenizer.nextToken());

                            String value = tokenizer.nextToken();
                            String type = tokenizer.nextToken();

                            Highlight h = ECodeUtilities.makeHighlight(tokenizer.nextToken());

                            Value result = new Value(value, type);

                            exprs.pop();

                            if (exprs.empty()) {

                                Variable var = (Variable) variables.remove(new Integer(expressionReference));

                                int operator = ECodeUtilities.resolveUnOperator(token);
                                director.animatePreIncDec(operator, var, result, h);

                            } else {

                                Object[] postIncDec = { new Integer(ECodeUtilities.resolveUnOperator(token)),
                                                        new Integer(expressionReference),
                                                        result,
                                                        h };
                                postIncsDecs.put(new Integer(expressionCounter), postIncDec);

                            }

                            break;

                        }


                        // Unary Expression
                        case Code.PRIE:      // PreIncrement
                        case Code.PRDE: {    // PreDecrement

                            int expressionCounter = Integer.parseInt(tokenizer.nextToken());
                            int expressionReference = Integer.parseInt(tokenizer.nextToken());
                            String value = tokenizer.nextToken();
                            String type = tokenizer.nextToken();

                            Highlight h = ECodeUtilities.makeHighlight(
                                                         tokenizer.nextToken());

                            Value result = new Value(value, type);
                            Variable var = (Variable) variables.remove(new Integer(expressionReference));

                            int operator = ECodeUtilities.resolveUnOperator(token);
                            director.animatePreIncDec(operator, var, result, h);
                            values.put(new Integer(expressionCounter), result);

                            exprs.pop();

                            Object[] postIncDec = (Object[]) postIncsDecs.remove(new Integer(expressionReference));

                            if (postIncDec != null) {
                                doPostIncDec(postIncDec);
                            }

                            break;
                        }


                        // Binary Expressions
                        case Code.BITOR:    // Bitwise Or
                        case Code.BITXOR:   // Bitwise Xor
                        case Code.BITAND:   // Bitwise And

                        case Code.LSHIFT:   // Bitwise Left Shift
                        case Code.RSHIFT:   // Bitwise Right Shift
                        case Code.URSHIFT:  // Unsigned Right Shift

                        case Code.XOR:      // Xor Expression
                        case Code.AND:      // And Expression
                        case Code.OR:       // Or Expression

                        case Code.EE:       // Equal Expression
                        case Code.NE:       // Not Equal Expression
                        case Code.LE:       // Less Expression
                        case Code.LQE:      // Less or Equal Expression
                        case Code.GT:       // Greater Than
                        case Code.GQT:      // Greater or Equal Expression

                        case Code.ME:       // Multiplication Expression
                        case Code.RE:       // Remainder (mod) Expression
                        case Code.DE:       // Division Expression
                        case Code.SE:       // Substract Expression
                        case Code.AE: {     // Add Expression

                            int expressionCounter = Integer.parseInt(tokenizer.nextToken());
                            int leftExpressionReference = Integer.parseInt(tokenizer.nextToken());
                            int rightExpressionReference = Integer.parseInt(tokenizer.nextToken());
                            String value = null;
                            if (tokenizer.countTokens() >= 3) {
                                value = tokenizer.nextToken();
                            } else {
                                value = "";
                            }
                            String type = tokenizer.nextToken();

                            Highlight h = ECodeUtilities.makeHighlight(
                                                         tokenizer.nextToken());
                            Value result = new Value(value, type);

                            ExpressionActor expr = director.getCurrentScratch().findActor(expressionCounter);

                            Value expressionValue = null;

                            /*
                            * The expression is created because
                            * its left side consists of literal or variable.
                            */
                            if (expr != null) {

                                /*
                                * It is possible that the right hand side is not
                                * yet set thus we need to check that to be sure.
                                */

                                Value right = (Value) values.remove(new Integer(rightExpressionReference));

                                if (right != null) {
                                    director.rightBinaryExpression(right, expr, h);
                                }

                                expressionValue = director.finishBinaryExpression(result,
                                            // token is declared and assigned in the line 91.
                                            ECodeUtilities.resolveBinOperator(token),
                                            expr, h);

                                exprs.pop();

//                              values.put(new Integer(expressionCounter), expressionValue);

                                Object[] postIncDec = (Object[]) postIncsDecs.remove(new Integer(rightExpressionReference));

                                if (postIncDec != null) {
                                    doPostIncDec(postIncDec);
                                }

                            /*
                            * The expression is not created before because
                            * its left side consists of expression.
                            */
                            } else {

                                Value left = (Value) values.remove(new Integer(leftExpressionReference));
                                Value right = (Value) values.remove(new Integer(rightExpressionReference));

                                expr = director.beginBinaryExpression(left,
                                                ECodeUtilities.resolveBinOperator(token),
                                                expressionCounter,
                                                h);

                                Object[] postIncDec = (Object[]) postIncsDecs.remove(new Integer(leftExpressionReference));

                                if (postIncDec != null) {
                                    doPostIncDec(postIncDec);
                                }

                                director.rightBinaryExpression(right, expr, h);

                                postIncDec = (Object[]) postIncsDecs.remove(new Integer(rightExpressionReference));

                                if (postIncDec != null) {
                                    doPostIncDec(postIncDec);
                                }

                                expressionValue = director.finishBinaryExpression(result,
                                            // token is declared and assigned in the line 91.
                                            ECodeUtilities.resolveBinOperator(token),
                                            expr, h);

/*                              Value expressionValue = director.animateBinaryExpression(
                                                        ECodeUtilities.resolveBinOperator(token),
                                                        left,
                                                        right,
                                                        result,
                                                        expressionCounter,
                                                        h);
*/
                                exprs.pop();

//                              values.put(new Integer(expressionCounter), expressionValue);

                            }

                            handleExpression(expressionValue, expressionCounter);

/*
                            int command = -1;
                            int oper = -1;
                            int size = commands.size();

                            //We find the command
                            for (int i = size - 1; i >= 0; i--) {
                                StringTokenizer commandTokenizer = new StringTokenizer(
                                                (String) commands.elementAt(i),
                                                Code.DELIM);
                                int comm = Integer.parseInt(commandTokenizer.nextToken());
                                int cid = Integer.parseInt(commandTokenizer.nextToken());
                                if (expressionCounter == cid) {
                                    command = comm;
                                    commands.removeElementAt(i);
                                    break;
                                }
                            }
*/
                            /**
                            * Look from the expression stack
                            * what expression should be shown next
                            */
/*
                            int expressionReference = 0;
                            Highlight highlight = null;

                            if (!exprs.empty()) {

                                StringTokenizer expressionTokenizer = new StringTokenizer(
                                                                     (String) exprs.peek(),
                                                                      Code.DELIM);

                                oper = Integer.parseInt(expressionTokenizer.nextToken());

                                expressionReference = Integer.parseInt(
                                                        expressionTokenizer.nextToken());

                                //Make the location information for the location token
                                highlight = ECodeUtilities.makeHighlight(
                                                        expressionTokenizer.nextToken());
                            }

                            //Do different things depending on in what expression
                            //the literal is used.

                            //If operator is assignment we just store the value
                            if (oper == Code.A){
                                values.put(new Integer(expressionCounter), expressionValue);

                            //If oper is other binary operator we will show it
                            //on the screen with operator
                            } else if (ECodeUtilities.isBinary(oper)) {

                                int operator = ECodeUtilities.resolveBinOperator(oper);

                                if (command == Code.LEFT) {

                                    director.beginBinaryExpression(expressionValue, operator,
                                                expressionReference, highlight);

                                } else if (command == Code.RIGHT) {

                                    ExpressionActor ea = (ExpressionActor)
                                        director.getCurrentScratch().findActor(expressionReference);
                                    if (ea != null) {
                                        director.rightBinaryExpression(expressionValue, ea, highlight);
                                    } else {
                                        values.put(new Integer(expressionCounter), expressionValue);
                                    }

                                } else {
                                    values.put(new Integer(expressionCounter), expressionValue);
                                }

                            //If oper is a unary operator we will show it
                            //on the screen with operator
                            } else if (ECodeUtilities.isUnary(oper)) {

                                values.put(new Integer(expressionCounter), expressionValue);

                                int operator = ECodeUtilities.resolveUnOperator(oper);

                                if (command == Code.RIGHT) {
                                    director.beginUnaryExpression(operator, expressionValue,
                                                                  expressionReference, highlight);
                                }

                            //If it is something else we will store it for later use.
                            } else {

                                values.put(new Integer(expressionCounter), expressionValue);

                            }
*/
                            break;
                        }

                        //Variable Declaration
                        case Code.VD: {

                            String variableName = tokenizer.nextToken();
                            int initializerExpression = Integer.parseInt(tokenizer.nextToken());
                            String value = null;
                            if (tokenizer.countTokens() >= 4) {
                                value = tokenizer.nextToken();
                            } else {
                                value = "";
                            }
                            String type = tokenizer.nextToken();
                            String modifier = tokenizer.nextToken();

                            //Make the location information for the location token
                            Highlight highlight = ECodeUtilities.makeHighlight(
                                                tokenizer.nextToken());

                            Variable var = director.declareVariable(variableName, type, highlight);

                            Value casted = null;

                            if (ECodeUtilities.isPrimitive(type)) {
                                casted = new Value(value, type);
                            } else {
                                if (value.equals("null")) {
                                    casted = new Reference();
                                } else {
                                    Instance inst = (Instance) instances.get(
                                                    ECodeUtilities.getHashCode(value));

                                    if (inst != null) {
                                        casted = new Reference(inst);
                                    } else {
                                        casted = new Reference();
                                    }
                                }
                                casted.setActor(var.getActor().getValue());
                            }

                            if (initializerExpression > 0) {

                                Value val = (Value) values.remove(new Integer(initializerExpression));
                                director.animateAssignment(var, val, casted, null, highlight);

                                Object[] postIncDec =
                                    (Object[]) postIncsDecs.remove(new Integer(initializerExpression));

                                if (postIncDec != null) {
                                    doPostIncDec(postIncDec);
                                }
                            }

                            director.closeScratch();
                            director.openScratch();

                            break;
                        }

                        //Qualified Name (variable)
                        case Code.QN: {

                            int expressionCounter = Integer.parseInt(tokenizer.nextToken());
                            String variableName = tokenizer.nextToken();
                            String value = null;
                            if (tokenizer.countTokens() >= 2) {
                                value = tokenizer.nextToken();
                            } else {
                                value = "";
                            }
                            String type = tokenizer.nextToken();

                            Variable var = director.getCurrentMethodFrame().getVariable(variableName);

                            //command that waits for this expression
                            int command = -1;
                            int oper = -1;
                            int size = commands.size();

                            //We find the command
                            for (int i = size - 1; i >= 0; i--) {
                                StringTokenizer commandTokenizer = new StringTokenizer(
                                                (String) commands.elementAt(i),
                                                Code.DELIM);
                                int comm = Integer.parseInt(commandTokenizer.nextToken());
                                int cid = Integer.parseInt(commandTokenizer.nextToken());

                                if (expressionCounter == cid) {
                                    command = comm;
                                    commands.removeElementAt(i);
                                    break;
                                }
                            }

                            /**
                            * Look from the expression stack
                            * what expression should be shown next
                            */

                            int expressionReference = 0;
                            Highlight highlight = null;
                            if (!exprs.empty()) {
                                StringTokenizer expressionTokenizer = new StringTokenizer(
                                                                     (String) exprs.peek(),
                                                                      Code.DELIM);

                                oper = Integer.parseInt(expressionTokenizer.nextToken());

                                expressionReference = Integer.parseInt(
                                                        expressionTokenizer.nextToken());

                                //Make the location information for the location token
                                highlight = ECodeUtilities.makeHighlight(
                                expressionTokenizer.nextToken());
                            }

                            Value val = null;
                            if (ECodeUtilities.isPrimitive(type)) {
                                val = new Value(value, type);
                                ValueActor va = var.getActor().getValue();
                                val.setActor(va);
                            } else {
                                if (value.equals("null")) {
                                    val = new Reference();
                                } else {
                                    Instance inst = (Instance) instances.get(
                                                ECodeUtilities.getHashCode(value));
                                    if (inst != null) {
                                        val = new Reference(inst);
                                    } else {
                                        val = new Reference();
                                    }
                                }
                                val.setActor(var.getActor().getValue());
                            }

                            /**
                            * Do different kind of things depending on
                            * in what expression the variable is used.
                            */

                            //If operator is assignment we just store the value
                            if (oper == Code.A) {

                                if (command == Code.TO) {

                                    variables.put(new Integer(expressionCounter), var);

                                } else {

                                    values.put(new Integer(expressionCounter), val);
                                }

                            //If oper is other binary operator we will show it
                            //on the screen with operator
                            } else if (ECodeUtilities.isBinary(oper)) {

                                int operator = ECodeUtilities.resolveBinOperator(oper);

                                if (command == Code.LEFT) {

                                    director.beginBinaryExpression(val, operator,
                                                expressionReference, highlight);

                                } else if (command == Code.RIGHT) {

                                    ExpressionActor ea = (ExpressionActor)
                                        director.getCurrentScratch().findActor(expressionReference);
                                    if (ea != null) {

                                        director.rightBinaryExpression(val, ea, highlight);

                                    } else {
                                        values.put(new Integer(expressionCounter), val);
                                    }
                                } else {
                                    values.put(new Integer(expressionCounter), val);
                                }

                            //If oper is a unary operator we will show it
                            //on the screen with operator
                            } else if (ECodeUtilities.isUnary(oper)) {

                                if (oper == Code.PRIE ||
                                    oper == Code.PRDE) {

                                        variables.put(new Integer(expressionCounter), var);

                                } else if (oper == Code.PIE  ||
                                           oper == Code.PDE) {

                                        variables.put(new Integer(expressionCounter), var);
                                        values.put(new Integer(expressionReference), val);


                                } else {

                                    values.put(new Integer(expressionCounter), val);
                                    int operator = ECodeUtilities.resolveUnOperator(oper);
                                    if (command == Code.RIGHT) {
                                        director.beginUnaryExpression(operator, val,
                                                        expressionReference, highlight);
                                    }
                                }

                            //If it is something else we will store it for later use.
                            } else {

                                values.put(new Integer(expressionCounter), val);
                                variables.put(new Integer(expressionCounter), var);

                            }

                            break;
                        }

                        //Literal
                        case Code.L: {

                            //Second token is the expression counter
                            int expressionCounter = Integer.parseInt(tokenizer.nextToken());

                            String value = null;
                            if (tokenizer.countTokens() >= 3) {
                                //Third token is the value of the literal
                                value = tokenizer.nextToken();
                            } else {
                                /**
                                 * There is no third token
                                 * because the literal is an empty string
                                 */
                                value = "";
                            }

                            //Fourth token is the type of the literal
                            String type = tokenizer.nextToken();

                            //Fifth token is the highlight information.
                            //Not used because the whole expression is highlighted.
                            //Highlight highlight =
                            //ECodeUtilities.makeHighlight(tokenizer.nextToken());

                            Value lit = new Value(value, type);
                            director.introduceLiteral(lit);

                            handleExpression(lit, expressionCounter);

/*
                            //command that wait for this expression (left, right)
                            int command = -1;
                            int oper = -1;
                            int size = commands.size();

                            //We find the command
                            for (int i = size - 1; i >= 0; i--) {
                                StringTokenizer commandTokenizer = new StringTokenizer(
                                                (String) commands.elementAt(i),
                                                Code.DELIM);
                                int comm = Integer.parseInt(commandTokenizer.nextToken());
                                int cid = Integer.parseInt(commandTokenizer.nextToken());
                                if (expressionCounter == cid) {
                                    command = comm;
                                    commands.removeElementAt(i);
                                    break;
                                }
                            }
*/
                            /**
                            * Look from the expression stack
                            * what expression should be shown next
                            */
/*
                            int expressionReference = 0;
                            Highlight highlight = null;

                            if (!exprs.empty()) {
                                StringTokenizer expressionTokenizer = new StringTokenizer(
                                                                     (String) exprs.peek(),
                                                                      Code.DELIM);

                                oper = Integer.parseInt(expressionTokenizer.nextToken());

                                expressionReference = Integer.parseInt(
                                                        expressionTokenizer.nextToken());

                                //Make the location information for the location token
                                highlight = ECodeUtilities.makeHighlight(
                                                           expressionTokenizer.nextToken());
                            }

                            //Value of the literal
                            Value lit = new Value(value, type);
                            director.introduceLiteral(lit);

                            //Do different things depending on in what expression
                            //the literal is used.

                            //If operator is assignment we just store the value
                            if (oper == Code.A){
                                values.put(new Integer(expressionCounter), lit);

                            //If oper is other binary operator we will show it
                            //on the screen with operator
                            } else if (ECodeUtilities.isBinary(oper)) {

                                int operator = ECodeUtilities.resolveBinOperator(oper);

                                if (command == Code.LEFT) {

                                    director.beginBinaryExpression(lit, operator,
                                                expressionReference, highlight);

                                } else if (command == Code.RIGHT) {

                                    ExpressionActor ea = (ExpressionActor)
                                        director.getCurrentScratch().findActor(expressionReference);
                                    if (ea != null) {
                                        director.rightBinaryExpression(lit, ea, highlight);
                                    } else {
                                        values.put(new Integer(expressionCounter), lit);
                                    }

                                } else {
                                    values.put(new Integer(expressionCounter), lit);
                                }

                            //If oper is a unary operator we will show it
                            //on the screen with operator
                            } else if (ECodeUtilities.isUnary(oper)) {

                                int operator = ECodeUtilities.resolveUnOperator(oper);
                                values.put(new Integer(expressionCounter), lit);
                                if (command == Code.RIGHT) {
                                    director.beginUnaryExpression(operator, lit,
                                                        expressionReference, highlight);
                                }

                            //If it is something else we will store it for later use.
                            } else {
                                values.put(new Integer(expressionCounter), lit);
                            }
*/
                            break;
                        }


                        //Simple Allocation (Object Allocation)
                        case Code.SA: {

                            //simpleAllocationCounter
                            int expressionCounter =
                                Integer.parseInt(tokenizer.nextToken());

                            String declaringClass = tokenizer.nextToken();
                            String constructorName = tokenizer.nextToken();

                            int parameterCount = Integer.parseInt(tokenizer.nextToken());
                            Highlight highlight = ECodeUtilities.makeHighlight(
                                                          tokenizer.nextToken());

                            //Create here Object Stage with initial variables and values
                            ClassInfo ci = (ClassInfo) classes.get(declaringClass);

                            //If ci is not null it means that we are dealing with
                            //user defined class and can find the class information
                            //extracted during the compilation with DynamicJava.
                            //If ci is null there is no user defined class and we
                            //need to use the Class.for(String name) method to
                            //find out as much as possible from the class.
                            if (ci == null) {
                                Class declaredClass = null;
                                try {
                                    declaredClass = Class.forName(declaringClass);
                                } catch (Exception e) {
                                    String message = "<H1>Runtime Error</H1> <P>The class that was supposed to be initiated could not be found.</P>";
                                    director.showErrorMessage(new InterpreterError(message, null));
                                }
                                ci = new ClassInfo(declaredClass);
                                classes.put(ci.getName(), ci);
                            }

                            //This works for not primitive classes.
                            //There needs to be a check whether invoked
                            //class is primitive or not.
                            ObjectFrame of = createNewInstance(ci, highlight);
                            Reference ref = new Reference(of);

                            invokingMethod = true;

                            if (currentMethodInvocation != null) {
                                   methodInvocation.push(currentMethodInvocation);
                            }
                            currentMethodInvocation = new Object[9];

                            int n = currentMethodInvocation.length;
                            for (int i = 0; i < n; i++) {
                                currentMethodInvocation[i] = null;
                            }

                            currentMethodInvocation[0] = constructorName;
                            currentMethodInvocation[1] = "";

                            Value[] parameterValues = new Value[parameterCount];
                            String[] parameterTypes = new String[parameterCount];
                            String[] parameterNames = new String[parameterCount];
                            Integer[] parameterExpressionReferences = new Integer[parameterCount];

                            for (int i = 0; i < parameterCount; i++) {
                                parameterValues[i] = null;
                                parameterTypes[i] = null;
                                parameterNames[i] = null;
                                parameterExpressionReferences[i] = null;
                            }

                            currentMethodInvocation[2] = parameterValues;
                            currentMethodInvocation[3] = parameterTypes;
                            currentMethodInvocation[4] = parameterNames;
                            currentMethodInvocation[5] = highlight;
                            currentMethodInvocation[7] = parameterExpressionReferences;
                            currentMethodInvocation[8] = ref;

                            objectCreation.push(new Reference(of));

                            break;
                        }

                        // Simple class allocation close
                        case Code.SAC: {

                            //simpleAllocationCounter
                            int expressionCounter =
                                Integer.parseInt(tokenizer.nextToken());

                            String hashCode = tokenizer.nextToken();

                            Highlight h = ECodeUtilities.makeHighlight(
                                                 tokenizer.nextToken());

                            director.finishMethod(null, 0);

                            //Handle the object reference return

                            //This should handle the possible object
                            //assignment etc.
                            if (!objectCreation.empty()) {
                                //First reference to the created object is taken
                                Reference ref = (Reference) objectCreation.pop();
                                Instance inst = ref.getInstance();
                                inst.setHashCode(hashCode);
                                //The instance is putted in the hashtable that
                                //keeps the instances
                                instances.put(hashCode, inst);
                                //Then we handle the possible expressions
                                //concerning this reference.
                                director.introduceReference(ref);
                                handleExpression(ref, expressionCounter);
                            }

                            break;
                        }

                        // Object field access
                        case Code.OFA: {

                            int expressionCounter = Integer.parseInt(tokenizer.nextToken());
                            int objectCounter = Integer.parseInt(tokenizer.nextToken());
                            String variableName = tokenizer.nextToken();
                            String value = "";
                            if (tokenizer.countTokens() >= 2) {
                                value = tokenizer.nextToken();
                            }

                            String type = tokenizer.nextToken();

                            Reference objVal = (Reference) values.remove(new Integer(objectCounter));
                            if (objVal == null && !objectCreation.empty()) {
                                objVal = (Reference) objectCreation.peek();
                            }

                            ObjectFrame obj = (ObjectFrame) objVal.getInstance();

                            if (obj == null && !objectCreation.empty()) {
                                objVal = (Reference) objectCreation.peek();
                                obj = (ObjectFrame) objVal.getInstance();
                            }

                            Variable var = obj.getVariable(variableName);

                            //command that waits for this expression
                            int command = -1;
                            int oper = -1;
                            int size = commands.size();

                            //We find the command
                            for (int i = size - 1; i >= 0; i--) {
                                StringTokenizer commandTokenizer = new StringTokenizer(
                                                (String) commands.elementAt(i),
                                                Code.DELIM);
                                int comm = Integer.parseInt(commandTokenizer.nextToken());
                                int cid = Integer.parseInt(commandTokenizer.nextToken());

                                if (expressionCounter == cid) {
                                    command = comm;
                                    commands.removeElementAt(i);
                                    break;
                                }
                            }

                            /**
                            * Look from the expression stack
                            * what expression should be shown next
                            */
                            int expressionReference = 0;
                            Highlight highlight = null;
                            if (!exprs.empty()) {
                                StringTokenizer expressionTokenizer = new StringTokenizer(
                                                                     (String) exprs.peek(),
                                                                      Code.DELIM);

                                oper = Integer.parseInt(expressionTokenizer.nextToken());

                                expressionReference = Integer.parseInt(
                                                        expressionTokenizer.nextToken());

                                //Make the location information for the location token
                                highlight = ECodeUtilities.makeHighlight(
                                expressionTokenizer.nextToken());
                            }

                            Value val = null;
                            if (ECodeUtilities.isPrimitive(type)) {
                                val = new Value(value, type);
                                ValueActor va = var.getActor().getValue();
                                val.setActor(va);
                            } else {
                                if (value.equals("null")) {
                                    val = new Reference();
                                } else {
                                    Instance inst = (Instance) instances.get(
                                                ECodeUtilities.getHashCode(value));
                                    if (inst != null) {
                                        val = new Reference(inst);
                                    } else {
                                        val = new Reference();
                                    }
                                }
                                val.setActor(var.getActor().getValue());
                            }

                            /**
                            * Do different kind of things depending on
                            * in what expression the variable is used.
                            */

                            //If operator is assignment we just store the value
                            if (oper == Code.A) {

                                if (command == Code.TO) {

                                    variables.put(new Integer(expressionCounter), var);

                                } else {

                                    values.put(new Integer(expressionCounter), val);
                                }

                            //If oper is other binary operator we will show it
                            //on the screen with operator
                            } else if (ECodeUtilities.isBinary(oper)) {

                                int operator = ECodeUtilities.resolveBinOperator(oper);

                                if (command == Code.LEFT) {

                                    director.beginBinaryExpression(val, operator,
                                                expressionReference, highlight);

                                } else if (command == Code.RIGHT) {

                                    ExpressionActor ea = (ExpressionActor)
                                        director.getCurrentScratch().findActor(expressionReference);
                                    if (ea != null) {

                                        director.rightBinaryExpression(val, ea, highlight);

                                    } else {
                                        values.put(new Integer(expressionCounter), val);
                                    }
                                } else {
                                    values.put(new Integer(expressionCounter), val);
                                }

                            //If oper is a unary operator we will show it
                            //on the screen with operator
                            } else if (ECodeUtilities.isUnary(oper)) {

                                if (oper == Code.PRIE ||
                                    oper == Code.PRDE) {

                                        variables.put(new Integer(expressionCounter), var);

                                } else if (oper == Code.PIE  ||
                                           oper == Code.PDE) {

                                        variables.put(new Integer(expressionCounter), var);
                                        values.put(new Integer(expressionReference), val);


                                } else {

                                    values.put(new Integer(expressionCounter), val);
                                    int operator = ECodeUtilities.resolveUnOperator(oper);
                                    if (command == Code.RIGHT) {
                                        director.beginUnaryExpression(operator, val,
                                                        expressionReference, highlight);
                                    }
                                }

                            //If it is something else we will store it for later use.
                            } else {

                                values.put(new Integer(expressionCounter), val);
                                variables.put(new Integer(expressionCounter), var);

                            }

                            break;
                        }

                        // Object method call
                        case Code.OMC: {

                            String methodName = tokenizer.nextToken();
                            int parameterCount = Integer.parseInt(tokenizer.nextToken());
                            int objectCounter =  Integer.parseInt(tokenizer.nextToken());
                            Highlight highlight = ECodeUtilities.makeHighlight(tokenizer.nextToken());

                            Value val = (Value) values.remove(new Integer(objectCounter));
                            Variable var = (Variable) variables.remove(new Integer(objectCounter));

                            if (val == null && !objectCreation.empty()) {
                                val = (Reference) objectCreation.peek();
                            }

                            if (val instanceof Reference) {
                                ObjectFrame obj = (ObjectFrame) ((Reference)val).getInstance();

                                if (obj == null && !objectCreation.empty()) {
                                    val = (Reference) objectCreation.peek();
                                    obj = (ObjectFrame) ((Reference)val).getInstance();
                                }
                            }

                            invokingMethod = true;

                            if (currentMethodInvocation != null) {
                                   methodInvocation.push(currentMethodInvocation);
                            }
                            currentMethodInvocation = new Object[9];

                            int n = currentMethodInvocation.length;
                            for (int i = 0; i < n; i++) {
                                currentMethodInvocation[i] = null;
                            }

                            currentMethodInvocation[0] = methodName;

                            if (var != null) {
                                currentMethodInvocation[1] = var.getName();
                            } else {
                                if (val instanceof Reference) {
                                    currentMethodInvocation[1] = "new " +
                                      ((Reference) val).getInstance().getType();
                                } else {
                                    currentMethodInvocation[1] = val.getValue();
                                }
                            }

                            Value[] parameterValues = new Value[parameterCount];
                            String[] parameterTypes = new String[parameterCount];
                            String[] parameterNames = new String[parameterCount];
                            Integer[] parameterExpressionReferences = new Integer[parameterCount];

                            for (int i = 0; i < parameterCount; i++) {
                                parameterValues[i] = null;
                                parameterTypes[i] = null;
                                parameterNames[i] = null;
                                parameterExpressionReferences[i] = null;
                            }

                            currentMethodInvocation[2] = parameterValues;
                            currentMethodInvocation[3] = parameterTypes;
                            currentMethodInvocation[4] = parameterNames;
                            currentMethodInvocation[5] = highlight;
                            currentMethodInvocation[7] = parameterExpressionReferences;
                            currentMethodInvocation[8] = val;

                           break;
                        }

                        // Object method call close
                        case Code.OMCC: {

                            if (!returned) {

                                director.finishMethod(null, 0);

                            } else {

                                Value rv = null;

                                if (returnValue instanceof Reference) {
                                    rv = (Value) ((Reference)returnValue).clone();
                                } else {

                                    rv = (Value) returnValue.clone();
                                }

                                ValueActor va = director.finishMethod(
                                                    returnActor,
                                                    returnExpressionCounter);
                                rv.setActor(va);

                                handleExpression(rv, returnExpressionCounter);
                            }

                            returned = false;

                            break;
                        }

                        //Static Method Call
                        case Code.SMC: {

                            invokingMethod = true;

                            if (currentMethodInvocation != null) {
                                   methodInvocation.push(currentMethodInvocation);
                            }
                            currentMethodInvocation = new Object[8];

                            for (int i = 0; i < currentMethodInvocation.length; i++) {
                                currentMethodInvocation[i] = null;
                            }

                            currentMethodInvocation[0] = tokenizer.nextToken();
                            currentMethodInvocation[1] = tokenizer.nextToken();
                            int parameterCount = Integer.parseInt(tokenizer.nextToken());

                            Value[] parameterValues = new Value[parameterCount];
                            String[] parameterTypes = new String[parameterCount];
                            String[] parameterNames = new String[parameterCount];
                            Integer[] parameterExpressionReferences = new Integer[parameterCount];

                            for (int i = 0; i < parameterCount; i++) {
                                parameterValues[i] = null;
                                parameterTypes[i] = null;
                                parameterNames[i] = null;
                                parameterExpressionReferences[i] = null;
                            }

                            currentMethodInvocation[2] = parameterValues;
                            currentMethodInvocation[3] = parameterTypes;
                            currentMethodInvocation[4] = parameterNames;
                            currentMethodInvocation[5] =
                                           ECodeUtilities.makeHighlight(
                                                 tokenizer.nextToken());
                            currentMethodInvocation[7] = parameterExpressionReferences;

                            break;
                        }

                        //Parameter
                        case Code.P: {

                            int expressionReference = Integer.parseInt(tokenizer.nextToken());

                            Value[] parameterValues = (Value[]) currentMethodInvocation[2];
                            String[] parameterTypes = (String[]) currentMethodInvocation[3];
                            Integer[] parameterExpressionReferences = (Integer[]) currentMethodInvocation[7];

                            Value parameterValue = (Value) values.remove(new Integer(expressionReference));

                            //if (parameterValue == null) {
                            //  System.out.println("Mistake");
                            //}

                            int i = 0;
                            while (parameterValues[i] != null) {
                                i++;
                            }
                            parameterValues[i] = parameterValue;
                            parameterTypes[i] = tokenizer.nextToken();
                            parameterExpressionReferences[i] = new Integer(expressionReference);

                            exprs.pop();

                            break;
                        }

                        //Method declaration
                        case Code.MD: {

                            //Make the location information for the location token
                            currentMethodInvocation[6] =
                                ECodeUtilities.makeHighlight(tokenizer.nextToken());

                            //Object method call or constructor
                            if (currentMethodInvocation.length == 9) {

                                //Change this!
                                //This is not ready yet!
                                //if (current)
                                Value[] args = null;

                                if (((String) currentMethodInvocation[1]).equals("")) {
                                    args = director.animateOMInvocation(
                                            "new " + ((String) currentMethodInvocation[0]),
                                            (Value[]) currentMethodInvocation[2],
                                            (Highlight) currentMethodInvocation[5]);
                                } else {
                                    args = director.animateOMInvocation(
                                            "." + ((String) currentMethodInvocation[0]),
                                            (Value[]) currentMethodInvocation[2],
                                            (Highlight) currentMethodInvocation[5],
                                            (Value) currentMethodInvocation[8]);
                                }

                                String call;

                                if (((String) currentMethodInvocation[1]).equals("")) {
                                    call = ((String) currentMethodInvocation[0]);
                                } else {
                                    call = ((String) currentMethodInvocation[1]) +
                                           "." +((String) currentMethodInvocation[0]);
                                }

                                director.setUpMethod(
                                 call,
                                 args,
                                 (String[]) currentMethodInvocation[4],
                                 (String[]) currentMethodInvocation[3],
                                 (Highlight) currentMethodInvocation[6],
                                 (Value) currentMethodInvocation[8]);

                            //Static method invocation
                            } else {
                                Value[] args = null;
                                if (start) {
                                    args = director.animateSMInvocation(
                                            ((String) currentMethodInvocation[1]) + "." +
                                            ((String) currentMethodInvocation[0]),
                                            (Value[]) currentMethodInvocation[2],
                                            null);
                                    start = false;
                                } else {
                                    args = director.animateSMInvocation(
                                           ((String) currentMethodInvocation[1]) + "." +
                                           ((String) currentMethodInvocation[0]),
                                           (Value[]) currentMethodInvocation[2],
                                           (Highlight) currentMethodInvocation[5]);
                                }

                                director.setUpMethod(
                                        ((String) currentMethodInvocation[1]) + "." +
                                        ((String) currentMethodInvocation[0]),
                                        args,
                                        (String[]) currentMethodInvocation[4],
                                        (String[]) currentMethodInvocation[3],
                                        (Highlight) currentMethodInvocation[6]);
                            }

                            Integer[] parameterExpressionReferences = (Integer[]) currentMethodInvocation[7];

                            if (parameterExpressionReferences != null) {
                                int i = 0;
                                while (i < parameterExpressionReferences.length) {
                                    Object[] postIncDec = (Object[]) postIncsDecs.remove(((Integer) parameterExpressionReferences[i]));
                                    if (postIncDec != null) {
                                        doPostIncDec(postIncDec);
                                    }
                                    i++;
                                }
                            }

                            if (!methodInvocation.empty()) {
                                currentMethodInvocation = (Object[]) methodInvocation.pop();
                            } else {
                                currentMethodInvocation = null;
                            }

                            invokingMethod = false;

                            break;
                        }

                        //Parameters list
                        case Code.PARAMETERS: {

                            if (tokenizer.hasMoreTokens()) {
                                String parameters = tokenizer.nextToken();
                                String[] parameterNames = (String[]) currentMethodInvocation[4];
                                StringTokenizer names = new StringTokenizer(parameters, ",");
                                for (int i = 0; i < parameterNames.length; i++) {
                                    parameterNames[i] = names.nextToken();
                                }
                            }
                            break;
                        }

                        // Return Statement
                        case Code.R: {

                            int expressionCounter = Integer.parseInt(tokenizer.nextToken());
                            int expressionReference = Integer.parseInt(tokenizer.nextToken());
                            String value = null;
                            if (tokenizer.countTokens() >= 3) {
                                value = tokenizer.nextToken();
                            } else {
                                value = "";
                            }
                            String type = tokenizer.nextToken();
                            Highlight h = ECodeUtilities.makeHighlight(
                                                         tokenizer.nextToken());

                            if (type.equals(Void.TYPE.getName())) {

                                //director.finishMethod(null, expressionCounter);
                                returned = false;

                            } else {

                                Value ret = (Value) values.remove(new Integer(expressionReference));

                                Value casted = null;

                                if (ECodeUtilities.isPrimitive(type)) {
                                    casted = new Value(value, type);
                                } else {
                                    Instance inst = (Instance) instances.get(
                                                ECodeUtilities.getHashCode(value));
                                    if (inst != null) {
                                        casted = new Reference(inst);
                                    } else {
                                        casted = new Reference();
                                    }
                                }

                                returnActor = director.animateReturn(ret, casted, h);
                                returnValue = (Value) casted.clone();
                                returnExpressionCounter = expressionCounter;
                                returned = true;

                            }

                            exprs.pop();

                            break;
                        }

                        // Static method call closed
                        case Code.SMCC : {

                            if (!returned) {

                                director.finishMethod(null, 0);

                            } else {

                                Value rv = null;

                                if (returnValue instanceof Reference) {
                                    rv = (Value) ((Reference)returnValue).clone();
                                } else {

                                    rv = (Value) returnValue.clone();
                                }

                                ValueActor va = director.finishMethod(
                                                    returnActor,
                                                    returnExpressionCounter);
                                rv.setActor(va);

                                handleExpression(rv, returnExpressionCounter);
                            }

                            returned = false;

/*
                                //command that wait for this expression (left, right)
                                int command = -1;
                                int size = commands.size();
                                //We find the command
                                for (int i = size - 1; i >= 0; i--) {
                                    StringTokenizer commandTokenizer = new StringTokenizer(
                                                    (String) commands.elementAt(i),
                                                    Code.DELIM);
                                    command = Integer.parseInt(commandTokenizer.nextToken());
                                    int cid = Integer.parseInt(commandTokenizer.nextToken());
                                    if (returnExpressionCounter == cid) {
                                        commands.removeElementAt(i);
                                        break;
                                    }
                                }
*/
                                /**
                                * Look from the expression stack
                                * what expression should be shown next
                                */
/*
                                int expressionReference = 0;
                                int oper = -1;
                                Highlight highlight = null;

                                if (!exprs.empty()) {

                                    StringTokenizer expressionTokenizer = new StringTokenizer(
                                                                         (String) exprs.peek(),
                                                                         Code.DELIM);

                                    oper = Integer.parseInt(expressionTokenizer.nextToken());

                                    expressionReference = Integer.parseInt(
                                                            expressionTokenizer.nextToken());

                                    //Make the location information for the location token
                                    highlight = ECodeUtilities.makeHighlight(
                                                    expressionTokenizer.nextToken());
                                }

                                //Do different things to the return value
                                //depending on in what expression the return value is used.

                                //If operator is assignment we just store the value
                                if (oper == Code.A){

                                    values.put(new Integer(returnExpressionCounter), rv);

                                //If oper is other binary operator we will show it
                                //on the screen with operator
                                } else if (ECodeUtilities.isBinary(oper)) {

                                    int operator = ECodeUtilities.resolveBinOperator(oper);

                                    if (command == Code.LEFT) {

                                        director.beginBinaryExpression(rv, operator,
                                                    expressionReference, highlight);

                                    } else if (command == Code.RIGHT) {

                                        ExpressionActor ea = (ExpressionActor)
                                        director.getCurrentScratch().findActor(expressionReference);
                                        director.rightBinaryExpression(rv, ea, highlight);

                                    } else {
                                        values.put(new Integer(returnExpressionCounter), rv);
                                    }

                                //If oper is a unary operator we will show it
                                //on the screen with operator
                                } else if (ECodeUtilities.isUnary(oper)) {

                                    int operator = ECodeUtilities.resolveUnOperator(oper);

                                    values.put(new Integer(returnExpressionCounter), rv);

                                    if (command == Code.RIGHT) {
                                        director.beginUnaryExpression(operator, rv,
                                                        expressionReference, highlight);
                                    }

                                //If it is something else we will store it for later use.
                                } else {

                                    values.put(new Integer(returnExpressionCounter), rv);

                                }

                            }
                            returned = false;
*/
                            break;
                        }

                        //If Then Statement
                        case Code.IFT: {

                            int expressionReference =
                                Integer.parseInt(tokenizer.nextToken());
                            String value = tokenizer.nextToken();

                            Highlight h = null;
                            if (tokenizer.hasMoreElements()) {
                                h = ECodeUtilities.makeHighlight(tokenizer.nextToken());
                            }

                            Value result = (Value) values.remove(new Integer(expressionReference));

                            if (value.equals(Boolean.TRUE.toString())) {
                                director.branchThen(result, h);
                            } else {
                                director.skipIf(result, h);
                            }

                            director.closeScratch();
                            director.openScratch();

                            break;
                        }

                        //IF Then Else Statement
                        case Code.IFTE: {

                            int expressionReference = Integer.parseInt(tokenizer.nextToken());
                            String value = tokenizer.nextToken();

                            Highlight h = ECodeUtilities.makeHighlight(tokenizer.nextToken());

                            Value result = (Value) values.remove(new Integer(expressionReference));

                            if (value.equals(Boolean.TRUE.toString())) {
                                director.branchThen(result, h);
                            } else {
                                director.branchElse(result, h);
                            }

                            director.closeScratch();
                            director.openScratch();

                            break;
                        }

                        //While Statement
                        case Code.WHI: {

                            int expressionReference = Integer.parseInt(tokenizer.nextToken());
                            String value = tokenizer.nextToken();

                            int round = Integer.parseInt(tokenizer.nextToken());

                            Highlight h = ECodeUtilities.makeHighlight(tokenizer.nextToken());

                            Value result = (Value) values.remove(new Integer(expressionReference));

                            if (round == 0) {

                                if (value.equals(Boolean.TRUE.toString())) {
                                    director.enterLoop("while", result, h);
                                } else {
                                    director.skipLoop("while", result);
                                }

                            } else {

                                if (value.equals(Boolean.TRUE.toString())) {
                                    director.continueLoop("while", result, h);
                                } else {
                                    director.exitLoop("while", result);
                                }

                            }

                            director.closeScratch();
                            director.openScratch();

                            break;
                        }

                        //For Statement
                        case Code.FOR: {

                            int expressionReference = Integer.parseInt(tokenizer.nextToken());
                            String value = tokenizer.nextToken();
                            int round = Integer.parseInt(tokenizer.nextToken());
                            Highlight h = ECodeUtilities.makeHighlight(tokenizer.nextToken());

                            Value result = (Value) values.remove(new Integer(expressionReference));

                            if (round == 0) {

                                if (value.equals(Boolean.TRUE.toString())) {
                                    director.enterLoop("for", result, h);
                                } else {
                                    director.skipLoop("for", result);
                                }

                            } else {

                                if (value.equals(Boolean.TRUE.toString())) {
                                    director.continueLoop("for", result, h);
                                } else {
                                    director.exitLoop("for", result);
                                }

                            }

                            director.closeScratch();
                            director.openScratch();

                            break;
                        }

                        //Do-While Statement
                        case Code.DO: {

                            int expressionReference = Integer.parseInt(
                                                 tokenizer.nextToken());
                            String value = tokenizer.nextToken();
                            int round = Integer.parseInt(
                                                 tokenizer.nextToken());
                            Highlight h = ECodeUtilities.makeHighlight(
                                                 tokenizer.nextToken());

                            Value result = (Value) values.remove(
                                      new Integer(expressionReference));

                            if (round == 0) {

                                director.enterLoop("do - while", h);

                            } else {

                                if (value.equals(Boolean.TRUE.toString())) {
                                    director.continueLoop("do - while", result, h);
                                } else {
                                    director.exitLoop("do - while", result);
                                }

                            }

                            director.closeScratch();
                            director.openScratch();

                            break;
                        }

                        case Code.SWITCHB: {

                            Highlight h = ECodeUtilities.makeHighlight(
                                                 tokenizer.nextToken());

                            director.openSwitch(h);

                            director.closeScratch();
                            director.openScratch();

                            break;
                        }

                        case Code.SWIBF: {

                            int selectorReference = Integer.parseInt(
                                                 tokenizer.nextToken());
                            int switchBlockReference = Integer.parseInt(
                                                 tokenizer.nextToken());
                            Highlight h = ECodeUtilities.makeHighlight(
                                                 tokenizer.nextToken());

                            if (switchBlockReference != -1) {

                                Value selector = (Value) values.remove(new Integer(selectorReference));
                                Value switchBlock = (Value) values.remove(new Integer(switchBlockReference));
                                Value result = new Value("true", "boolean");

                                director.animateBinaryExpression(
                                    ECodeUtilities.resolveBinOperator(
                                                               Code.EE),
                                    selector,
                                    switchBlock,
                                    result,
                                    -3,
                                    h);
                                director.switchSelected(h);
                            } else {
                                director.switchDefault(h);
                            }

                            break;
                        }

                        case Code.SWITCH: {

                            Highlight h = ECodeUtilities.makeHighlight(
                                                 tokenizer.nextToken());

                            director.closeSwitch(h);

                            director.closeScratch();
                            director.openScratch();

                            break;
                        }

                        //Break Statement
                        case Code.BR: {

                            int statementName = Integer.parseInt(tokenizer.nextToken());
                            Highlight h = ECodeUtilities.makeHighlight(tokenizer.nextToken());
                            String stmt = "";

                            if (statementName == Code.WHI) {
                                stmt = "while";
                                director.breakLoop(stmt, h);
                            } else if (statementName == Code.FOR) {
                                stmt = "for";
                                director.breakLoop(stmt, h);
                            } else if (statementName == Code.DO) {
                                stmt = "do - while";
                                director.breakLoop(stmt, h);
                            } else if (statementName == Code.SWITCH) {
                                director.breakSwitch(h);
                            }

                            director.closeScratch();
                            director.openScratch();

                            break;
                        }

                        //Continue Statement
                        case Code.CONT: {

                            int statementName = Integer.parseInt(tokenizer.nextToken());
                            Highlight h = ECodeUtilities.makeHighlight(tokenizer.nextToken());
                            String stmt = "";

                            if (statementName == Code.WHI) {
                                stmt = "while";
                            } else if (statementName == Code.FOR) {
                                stmt = "for";
                            } else if (statementName == Code.DO) {
                                stmt = "do - while";
                            }

                            director.continueLoop(stmt, h);

                            director.closeScratch();
                            director.openScratch();

                            break;
                        }

                        //Opening and closing scopes
                        case Code.OUTPUT: {

                            int expressionReference = Integer.parseInt(
                                                         tokenizer.nextToken());

                            String value = tokenizer.nextToken();
                            String type = tokenizer.nextToken();

                            Highlight highlight = ECodeUtilities.makeHighlight(
                                                tokenizer.nextToken());

                            Value output = (Value) values.remove(
                                              new Integer(expressionReference));

                            if (output == null) {
                                output = new Value(value, type);
                            }

                            director.output(output, highlight);

                            break;
                        }

                        //Input needs to be read
                        case Code.INPUT: {

                            int expressionCounter = Integer.parseInt(
                                                         tokenizer.nextToken());

                            String type = tokenizer.nextToken();

                            Highlight h = ECodeUtilities.makeHighlight(
                                                tokenizer.nextToken());

                            Value in = director.animateInputHandling(type, h);

                            input.println(in.getValue());

                            values.put(new Integer(expressionCounter), in);

                            break;
                        }

                        //Inputted value is returned
                        case Code.INPUTTED: {

                            int expressionCounter = Integer.parseInt(
                                                         tokenizer.nextToken());

                            String value = tokenizer.nextToken();

                            String type = tokenizer.nextToken();

                            Highlight h = ECodeUtilities.makeHighlight(
                                                tokenizer.nextToken());

                            Value in = (Value) values.remove(
                                              new Integer(expressionCounter));
                            if (in == null) {
                                in = new Value(value, type);
                            }

                            handleExpression(in, expressionCounter);

/*
                            //command that wait for this expression (left, right)
                            int command = -1;
                            int oper = -1;
                            int size = commands.size();

                            //We find the command
                            for (int i = size - 1; i >= 0; i--) {
                                StringTokenizer commandTokenizer = new StringTokenizer(
                                                (String) commands.elementAt(i),
                                                Code.DELIM);
                                int comm = Integer.parseInt(commandTokenizer.nextToken());
                                int cid = Integer.parseInt(commandTokenizer.nextToken());
                                if (expressionCounter == cid) {
                                    command = comm;
                                    commands.removeElementAt(i);
                                    break;
                                }
                            }
*/
                            /**
                            * Look from the expression stack
                            * what expression should be shown next
                            */
/*
                            int expressionReference = 0;
                            Highlight highlight = null;

                            if (!exprs.empty()) {
                                StringTokenizer expressionTokenizer =
                                                    new StringTokenizer(
                                                          (String) exprs.peek(),
                                                          Code.DELIM);

                                oper = Integer.parseInt(
                                               expressionTokenizer.nextToken());

                                expressionReference = Integer.parseInt(
                                               expressionTokenizer.nextToken());

                                //Make the location information for the location token
                                highlight = ECodeUtilities.makeHighlight(
                                               expressionTokenizer.nextToken());
                            }

                            //Do different things depending on in what expression
                            //the literal is used.

                            //If operator is assignment we just store the value
                            if (oper == Code.A){
                                values.put(new Integer(expressionCounter), in);

                            //If oper is other binary operator we will show it
                            //on the screen with operator
                            } else if (ECodeUtilities.isBinary(oper)) {

                                int operator = ECodeUtilities.resolveBinOperator(oper);

                                if (command == Code.LEFT) {

                                    director.beginBinaryExpression(in, operator,
                                                expressionReference, highlight);

                                } else if (command == Code.RIGHT) {

                                    ExpressionActor ea = (ExpressionActor)
                                        director.getCurrentScratch().findActor(expressionReference);
                                    if (ea != null) {
                                        director.rightBinaryExpression(in, ea, highlight);
                                    } else {
                                        values.put(new Integer(expressionCounter), in);
                                    }

                                } else {
                                    values.put(new Integer(expressionCounter), in);
                                }

                            //If oper is a unary operator we will show it
                            //on the screen with operator
                            } else if (ECodeUtilities.isUnary(oper)) {

                                int operator = ECodeUtilities.resolveUnOperator(oper);

                                values.put(new Integer(expressionCounter), in);

                                if (command == Code.RIGHT) {
                                    director.beginUnaryExpression(operator, in,
                                                        expressionReference, highlight);
                                }

                            //If it is something else we will store it for later use.
                            } else {
                                values.put(new Integer(expressionCounter), in);
                            }
*/
                            break;
                        }

                        //Opening and closing scopes
                        case Code.SCOPE: {

                            int scope = Integer.parseInt(tokenizer.nextToken());

                            //Open the scope
                            if (scope == 1) {

                                director.openScope();
                                director.closeScratch();
                                director.openScratch();

                            //Close the scope
                            } else if (scope == 0) {

                                director.closeScope();
                                director.closeScratch();
                                director.openScratch();
                            }

                            break;
                        }

                        //Array Allocation
                        case Code.AA: {
                            int expressionReference =
                                        Integer.parseInt(tokenizer.nextToken());

                            String hashCode = tokenizer.nextToken();
                            String compType = tokenizer.nextToken();
                            int dims = Integer.parseInt(tokenizer.nextToken());

                            //References of the dimension values
                            String dimensionReferences = tokenizer.nextToken();
                            StringTokenizer st =
                                  new StringTokenizer(dimensionReferences, ",");

                            int [] dimensionReference = new int[dims];

                            for (int i = 0; st.hasMoreTokens(); i++) {
                                dimensionReference[i] =
                                    Integer.parseInt(st.nextToken());
                            }

                            //int values of the dimension sizes
                            String dimensionSizes = tokenizer.nextToken();
                            st = new StringTokenizer(dimensionSizes, ",");
                            int [] dimensionSize = new int[dims];

                            for (int i = 0; st.hasMoreTokens(); i++) {
                                dimensionSize[i] =
                                    Integer.parseInt(st.nextToken());
                            }

                            Highlight h = null;
                            if (tokenizer.hasMoreElements()) {
                                h = ECodeUtilities.makeHighlight(tokenizer.nextToken());
                            }

                            Value[] dimensionValues = new Value[dims];

                            for (int i = 0; i < dims; i++) {
                                dimensionValues[i] = (Value) values.remove(
                                            new Integer(dimensionReference[i]));

                            }

                            ArrayInstance ai = new ArrayInstance(hashCode,
                                                                 compType,
                                                                 dimensionSize);

                            Reference ref = new Reference(ai);

                            director.showArrayCreation(ai, ref, dimensionValues,
                                              expressionReference, h);

                            //director.arrayCreation(dimensionSize, h);

                            instances.put(hashCode, ai);

                            ref.makeReference();

                            values.put(new Integer(expressionReference), ref);

                            break;
                        }

                        //Array Access
                        case Code.AAC: {

                            int expressionCounter =
                                Integer.parseInt(tokenizer.nextToken());

                            int expressionReference =
                                Integer.parseInt(tokenizer.nextToken());

                            int dims =
                                Integer.parseInt(tokenizer.nextToken());

                            String cellNumberReferences = tokenizer.nextToken();
                            StringTokenizer st =
                                  new StringTokenizer(cellNumberReferences, ",");

                            int [] cellNumberReference = new int[dims];

                            for (int i = 0; st.hasMoreTokens(); i++) {
                                cellNumberReference[i] =
                                    Integer.parseInt(st.nextToken());
                            }

                            //int values of the dimension sizes
                            String cellNumbers = tokenizer.nextToken();
                            st = new StringTokenizer(cellNumbers, ",");
                            int [] cellNumber = new int[dims];

                            for (int i = 0; st.hasMoreTokens(); i++) {
                                cellNumber[i] =
                                    Integer.parseInt(st.nextToken());
                            }

                            String value = null;

                            if (tokenizer.countTokens() >= 3) {
                                value = tokenizer.nextToken();
                            } else {
                                value = "";
                            }

                            String type = tokenizer.nextToken();

                            Highlight h = null;
                            if (tokenizer.hasMoreElements()) {
                                h = ECodeUtilities.makeHighlight(tokenizer.nextToken());
                            }

                            //Finding the VariableInArray
                            values.remove(new Integer(expressionReference));
                            Variable variable = (Variable) variables.remove(new Integer(expressionReference));
                            Reference varRef = (Reference) variable.getValue();
                            ArrayInstance ainst = (ArrayInstance) varRef.getInstance();
                            VariableInArray var = ainst.getVariableAt(cellNumber);

                            //Getting the right Values that point to the cell in the array
                            Value[] cellNumberValues = new Value[dims];
                            for (int i = 0; i < dims; i++) {
                                cellNumberValues[i] = (Value) values.remove(
                                            new Integer(cellNumberReference[i]));

                            }

                            //Actual value in the array in pointed cell
                            Value val = null;
                            if (ECodeUtilities.isPrimitive(type)) {
                                val = new Value(value, type);
                            } else {
                                if (value.equals("null")) {
                                    val = new Reference();
                                } else {
                                    Instance inst = (Instance) instances.get(
                                                ECodeUtilities.getHashCode(value));
                                    if (inst != null) {
                                        val = new Reference(inst);
                                    } else {
                                        val = new Reference();
                                    }
                                }
                            }

                            director.showArrayAccess(var,
                                                     cellNumberValues,
                                                     val, h);

                            exprs.pop();

                            //command that waits for this expression
                            int command = -1;
                            int oper = -1;
                            int size = commands.size();

                            //We find the command
                            for (int i = size - 1; i >= 0; i--) {
                                StringTokenizer commandTokenizer = new StringTokenizer(
                                                (String) commands.elementAt(i),
                                                Code.DELIM);
                                int comm = Integer.parseInt(commandTokenizer.nextToken());
                                int cid = Integer.parseInt(commandTokenizer.nextToken());

                                if (expressionCounter == cid) {
                                    command = comm;
                                    commands.removeElementAt(i);
                                    break;
                                }
                            }

                            /**
                            * Look from the expression stack
                            * what expression should be shown next
                            */
                            expressionReference = 0;
                            Highlight highlight = null;
                            if (!exprs.empty()) {
                                StringTokenizer expressionTokenizer = new StringTokenizer(
                                                                     (String) exprs.peek(),
                                                                      Code.DELIM);

                                oper = Integer.parseInt(expressionTokenizer.nextToken());

                                expressionReference = Integer.parseInt(
                                                        expressionTokenizer.nextToken());

                                //Make the location information for the location token
                                highlight = ECodeUtilities.makeHighlight(
                                expressionTokenizer.nextToken());
                            }


                            /**
                            * Do different kind of things depending on
                            * in what expression the variable is used.
                            */

                            //If operator is assignment we just store the value
                            if (oper == Code.A) {

                                if (command == Code.TO) {

                                    variables.put(new Integer(expressionCounter), var);

                                } else {

                                    values.put(new Integer(expressionCounter), val);
                                }

                            //If oper is other binary operator we will show it
                            //on the screen with operator
                            } else if (ECodeUtilities.isBinary(oper)) {

                                int operator = ECodeUtilities.resolveBinOperator(oper);

                                if (command == Code.LEFT) {

                                    director.beginBinaryExpression(val, operator,
                                                expressionReference, highlight);

                                } else if (command == Code.RIGHT) {

                                    ExpressionActor ea = (ExpressionActor)
                                        director.getCurrentScratch().findActor(expressionReference);
                                    if (ea != null) {

                                        director.rightBinaryExpression(val, ea, highlight);

                                    } else {
                                        values.put(new Integer(expressionCounter), val);
                                    }
                                } else {
                                    values.put(new Integer(expressionCounter), val);
                                }

                            //If oper is a unary operator we will show it
                            //on the screen with operator
                            } else if (ECodeUtilities.isUnary(oper)) {

                                if (oper == Code.PRIE ||
                                    oper == Code.PRDE) {

                                        variables.put(new Integer(expressionCounter), var);

                                } else if (oper == Code.PIE  ||
                                           oper == Code.PDE) {

                                        variables.put(new Integer(expressionCounter), var);
                                        values.put(new Integer(expressionReference), val);


                                } else {

                                    values.put(new Integer(expressionCounter), val);
                                    int operator = ECodeUtilities.resolveUnOperator(oper);
                                    if (command == Code.RIGHT) {
                                        director.beginUnaryExpression(operator, val,
                                                        expressionReference, highlight);
                                    }
                                }

                            //If it is something else we will store it for later use.
                            } else {

                                values.put(new Integer(expressionCounter), val);
                                variables.put(new Integer(expressionCounter), var);

                            }


                            break;
                        }

                        //Array Length
                        case Code.AL: {

                            //Second token is the expression counter
                            int expressionCounter = Integer.parseInt(tokenizer.nextToken());
                            int arrayCounter = Integer.parseInt(tokenizer.nextToken());

                            String name = tokenizer.nextToken();
                            String value = "";
                            if (tokenizer.countTokens() >= 3) {
                                //Third token is the value of the literal
                                value = tokenizer.nextToken();
                            }

                            //Fourth token is the type of the literal
                            String type = tokenizer.nextToken();

                            //Fifth token is the highlight information.
                            //Not used because the whole expression is highlighted.
                            Highlight highlight = ECodeUtilities.makeHighlight(tokenizer.nextToken());

                            Reference ref = (Reference) values.remove(new Integer(arrayCounter));
                            ArrayInstance array = (ArrayInstance) ref.getInstance();

                            Value length = new Value(value, type);
                            director.introduceArrayLength(length, array);

                            handleExpression(length, expressionCounter);

                            break;
                        }
                        //Class information starts for a class
                        case Code.CLASS: {

                            String name = tokenizer.nextToken();
                            String extendedClass = "";

                            if (tokenizer.hasMoreTokens()) {
                                extendedClass = tokenizer.nextToken();
                            }

                            currentClass = new ClassInfo(name);
                            ClassInfo ci = (ClassInfo) classes.get(extendedClass);

                            //if extended class is user defined class
                            if (ci != null) {
                                currentClass.extendClass(ci);
                            }

                            break;
                        }

                        //Class information ends for a class
                        case Code.END_CLASS: {

                            if (currentClass != null) {
                                classes.put(currentClass.getName(), currentClass);
                            }

                            currentClass = null;

                            break;
                        }

                        //Class information for constructor
                        case Code.CONSTRUCTOR: {

                            String listOfParameters = "";
                            if (tokenizer.hasMoreTokens()) {
                                listOfParameters = tokenizer.nextToken();
                            }

                            currentClass.declareConstructor(currentClass.getName() +
                                                    Code.DELIM + listOfParameters, "");

                            break;
                        }

                        //Class information for method
                        case Code.METHOD: {

                            String name = tokenizer.nextToken();
                            String returnType = tokenizer.nextToken();
                            int modifiers = -1;
                            if (tokenizer.hasMoreTokens()) {
                                modifiers = Integer.parseInt(tokenizer.nextToken());
                            }

                            String listOfParameters = "";
                            if (tokenizer.hasMoreTokens()) {
                                listOfParameters = tokenizer.nextToken();
                            }

                            currentClass.declareMethod(name + Code.DELIM + listOfParameters,
                                                       "" + modifiers + Code.DELIM + returnType);

                            break;
                        }

                        //Class information for field
                        case Code.FIELD: {

                            String name = tokenizer.nextToken();
                            String type = tokenizer.nextToken();
                            int modifiers = Integer.parseInt(tokenizer.nextToken());
                            String value = "";
                            if (tokenizer.hasMoreTokens()) {
                                value = tokenizer.nextToken();
                            }

                            currentClass.declareField(name,
                                   "" + modifiers + Code.DELIM + type +
                                   Code.DELIM + value);

                            break;
                        }

                        //Error has occured during the execution
                        case Code.ERROR: {

                            String message = tokenizer.nextToken();
                            Highlight h = ECodeUtilities.makeHighlight(tokenizer.nextToken());

                            director.showErrorMessage(new InterpreterError(message, h));
                            running = false;

                            break;
                        }

                        //There is an error if the execution comes here.
                        default: {
                            director.showErrorMessage(new InterpreterError("<H1>Runtime Error</H1> <P>The feature is not yet implemented or " +
                                                                           "there is an error on the other side of the ecode interface.</P>", null));
                            break;
                        }
                    }
                }

            } else {
                running = false;
                removeInstances();
            }

        }
        director.closeScratch();
    }


    public ObjectFrame createNewInstance(ClassInfo ci, Highlight h) {
        ObjectFrame of = new ObjectFrame("-1", ci.getName(), ci.getFieldNumber());

        //director: create object
        director.showObjectCreation(of, h);

        //director: create variables and initialize them
        Hashtable fields = ci.getFields();

        for (Enumeration keyEnum = fields.keys(); keyEnum.hasMoreElements() ;) {

            String name = (String) keyEnum.nextElement();
            String info = (String) fields.get(name);
            StringTokenizer st = new StringTokenizer(info, Code.DELIM);
            String mods = st.nextToken();
            String type = st.nextToken();
            String value = "";
            if (st.hasMoreTokens()) {
                value = st.nextToken();
            }

            Variable var = director.declareObjectVariable(of, name, type, null);

            if (!value.equals(Code.UNKNOWN)) {

                Value casted = null;
                Value val = null;
                if (ECodeUtilities.isPrimitive(type)) {
                    casted = new Value(value, type);
                    val = new Value(value, type);
                    director.introduceLiteral(val);
                } else {
                    if (value.equals("null")) {
                        casted = new Reference();
                        val = new Reference();
                        director.introduceLiteral(val);
                    } else {
                        //This should be done differently!
                        //This does not work here if some things
                        //are not changed when the initial values of
                        //each field in the class are collected in DJ.
                        Instance inst = (Instance) instances.get(
                                        ECodeUtilities.getHashCode(value));

                        if (inst != null) {
                            casted = new Reference(inst);
                            val = new Reference(inst);
                        } else {
                            casted = new Reference();
                            val = new Reference();
                            director.introduceLiteral(val);
                        }
                    }
                    casted.setActor(var.getActor().getValue());
                }

                director.animateAssignment(var, val, casted, null, null);
            }

        }

        return of;
    }

    //Not in use at the moment.
    public void checkInstancesForRemoval() {
        Enumeration enum = instances.keys();
        while (enum.hasMoreElements()) {
            Object obj = enum.nextElement();
            Instance inst = (Instance)instances.get(obj);
            if (inst != null) {
                //For testing
                //System.out.println("number of references1: " + inst.getNumberOfReferences());
                //System.out.println("number of references2: " + inst.getActor().getNumberOfReferences());
                if (inst.getNumberOfReferences() == 0 &&
                    inst.getActor().getNumberOfReferences() == 0) {

                    instances.remove(obj);
                    director.removeInstance(inst.getActor());
                    inst = null;
                    //System.out.println("instance removed!");
                }
            }
        }
    }

    //Not in use at the moment
    public void removeInstances() {
        Enumeration enum = instances.keys();
        while (enum.hasMoreElements()) {
            Object obj = enum.nextElement();
            Instance inst = (Instance)instances.get(obj);
            if (inst != null) {
                instances.remove(obj);
                director.removeInstance(inst.getActor());
                inst.setActor(null);
                inst = null;
                //System.out.println("instance removed!");
            }
        }
    }

    private void handleExpression(Value val, int expressionCounter) {

        //command that wait for this expression (left, right)
        int command = -1;
        int oper = -1;
        int size = commands.size();

        //We find the command
        for (int i = size - 1; i >= 0; i--) {
            StringTokenizer commandTokenizer = new StringTokenizer(
                                                        (String) commands.elementAt(i),
                                                        Code.DELIM);

            int comm = Integer.parseInt(commandTokenizer.nextToken());
            int cid = Integer.parseInt(commandTokenizer.nextToken());
            if (expressionCounter == cid) {
                command = comm;
                commands.removeElementAt(i);
                break;
            }
        }

        /**
         * Look from the expression stack
         * what expression should be shown next
         */
        int expressionReference = 0;
        Highlight highlight = null;

        if (!exprs.empty()) {
            StringTokenizer expressionTokenizer =
                                        new StringTokenizer(
                                            (String) exprs.peek(),
                                            Code.DELIM);

            oper = Integer.parseInt(expressionTokenizer.nextToken());
            expressionReference = Integer.parseInt(expressionTokenizer.nextToken());

            //Make the location information for the location token
            highlight = ECodeUtilities.makeHighlight(expressionTokenizer.nextToken());
        }

        //Do different things depending on in what expression
        //the value is used.

        //If operator is assignment we just store the value
        if (oper == Code.A) {
            values.put(new Integer(expressionCounter), val);

            //If oper is other binary operator we will show it
            //on the screen with operator
        } else if (ECodeUtilities.isBinary(oper)) {

            int operator = ECodeUtilities.resolveBinOperator(oper);

            if (command == Code.LEFT) {

                director.beginBinaryExpression(val, operator,
                                               expressionReference, highlight);

            } else if (command == Code.RIGHT) {

                ExpressionActor ea = (ExpressionActor)
                                    director.getCurrentScratch().findActor(expressionReference);

                if (ea != null) {
                    director.rightBinaryExpression(val, ea, highlight);
                } else {
                    values.put(new Integer(expressionCounter), val);
                }

            } else {
                values.put(new Integer(expressionCounter), val);
            }

        /* If oper is a unary operator we will show it
         * on the screen with operator
         */
        } else if (ECodeUtilities.isUnary(oper)) {

            int operator = ECodeUtilities.resolveUnOperator(oper);

            values.put(new Integer(expressionCounter), val);

            if (command == Code.RIGHT) {
                director.beginUnaryExpression(operator, val,
                                expressionReference, highlight);
            }

        //If it is something else we will store it for later use.
        } else {
            values.put(new Integer(expressionCounter), val);
        }
    }


    public void doPostIncDec(Object[] postIncDecInfo) {

        Variable var = (Variable) variables.remove(((Integer) postIncDecInfo[1]));

        director.animatePreIncDec(((Integer) postIncDecInfo[0]).intValue(),
                                  var,
                                  ((Value) postIncDecInfo[2]),
                                  ((Highlight) postIncDecInfo[3]));
    }


}
