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

    private Director director;
    private BufferedReader ecode;

    private String programCode;

    private boolean running = true;
    private boolean start = true;

    //Keeps track of current return value
    private boolean returned = false;
    private Value returnValue = null;
    private Actor returnActor= null;
    private int returnExpressionCounter = 0;

    Stack commands = new Stack();
    Stack exprs = new Stack();
    Hashtable values = new Hashtable();
    Hashtable variables = new Hashtable();
    Stack methodInvocation = new Stack();

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
    */
    Object[] currentMethodInvocation = null;

    protected Interpreter() { }

    public Interpreter(BufferedReader r, Director d, String programCode) {
        this.ecode = r;
        this.director = d;
        this.programCode = programCode;
    }

    public void initialize() {
        running = true;
        start = true;
        Actor returnActor = null;
        commands = new Stack();
        exprs = new Stack();
        values = new Hashtable();
        variables = new Hashtable();
        methodInvocation = new Stack();
        Value returnValue = null;
        Actor ReturnActor= null;
    }

    public boolean starting() {
        return start;
    }

    public void execute() {

        director.openScratch();

        while (running) {

            String line = null;

            try {

                line = ecode.readLine();
                System.out.println(line);

            } catch (Exception e) {}

            if (!line.equals("" + Code.END)) {

                StringTokenizer tokenizer = new StringTokenizer(line, Code.DELIM);

                if (tokenizer.hasMoreTokens()) {

                    int token = Integer.parseInt(tokenizer.nextToken());

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
                            String value = tokenizer.nextToken();
                            String type = tokenizer.nextToken();
                            Highlight h = ECodeUtilities.makeHighlight(
                                                         tokenizer.nextToken());

                            Variable toVariable = (Variable) variables.remove(new Integer(toExpression));
                            Value fromValue = (Value) values.remove(new Integer(fromExpression));
                            Value casted = new Value(value, type);
                            Value expressionValue = new Value(value, type);

                            director.animateAssignment(toVariable, fromValue, casted, expressionValue, h);
                            toVariable.assign(casted);
                            values.put(new Integer(expressionCounter), expressionValue);

                            exprs.pop();

                            director.closeScratch();
                            director.openScratch();

                            break;

                        }


                        // Unary Expressions
                        case Code.PLUS:     // Plus operator
                        case Code.MINUS:    // Minus operator
                        case Code.NO: {     // Boolean Not

                            int expressionCounter = Integer.parseInt(tokenizer.nextToken());
                            int expressionReference = Integer.parseInt(tokenizer.nextToken());
                            String value = tokenizer.nextToken();
                            String type;

                            if (token == Code.NO) {
                                type = boolean.class.toString();
                            } else {
                                type = tokenizer.nextToken();
                            }

                            Highlight h = ECodeUtilities.makeHighlight(
                                                         tokenizer.nextToken());
                            Value result = new Value(value, type);
                            Value val = (Value) values.remove(new Integer(expressionReference));

                            //ExpressionActor expr = director.getCurrentScratch().findActor(expressionCounter);

                            int operator = ECodeUtilities.resolveUnOperator(token);
                            // director.beginUnaryExpression(operator, lit, expressionCounter, highlight);
                            // Value expressionValue = director.finishUnaryExpression(operator, expr, result, expressionCounter, h);
                            Value expressionValue = director.animateUnaryExpression(operator, val, result, expressionCounter);
                            values.put(new Integer(expressionCounter), expressionValue);

                            //exprs.pop();

                            break;

                        }


                        // Binary Expressions
                        case Code.OR:       // Or Expression
                        case Code.EE:       // Equal Expression
                        case Code.NE:       // Not Equal Expression
                        case Code.LE:       // Less Expression
                        case Code.LQE:      // Less or Equal Expression
                        case Code.GQT:      // Greater or Equal Expression
                        case Code.ME:       // Multiplication Expression
                        case Code.RE:       // Remainder (mod) Expression
                        case Code.DE:       // Division Expression
                        case Code.AND:      // And Expression
                        case Code.GT:       // Greater Than
                        case Code.SE:       // Substract Expression
                        case Code.AE: {     // Add Expression

                            int expressionCounter = Integer.parseInt(tokenizer.nextToken());
                            int leftExpressionReference = Integer.parseInt(tokenizer.nextToken());
                            int rightExpressionReference = Integer.parseInt(tokenizer.nextToken());
                            String value = tokenizer.nextToken();

                            String type;
                            if (token == Code.AND || token == Code.OR ||
                                token == Code.NE || token == Code.EE ||
                                token == Code.GT || token == Code.LE ||
                                token == Code.LQE || token == Code.GQT) {
                                type = boolean.class.toString();
                            } else {
                                type = tokenizer.nextToken();
                            }

                            Highlight h = ECodeUtilities.makeHighlight(
                                                         tokenizer.nextToken());
                            Value result = new Value(value, type);

                            ExpressionActor expr = director.getCurrentScratch().findActor(expressionCounter);

                            Value expressionValue = director.finishBinaryExpression(result,
                                // token is declared and assigned in the line 91.
                                ECodeUtilities.resolveBinOperator(token),
                                expr, h);

                            exprs.pop();

                            values.put(new Integer(expressionCounter), expressionValue);
                            break;
                        }

                        //Variable Declaration
                        case Code.VD: {
                            String variableName = tokenizer.nextToken();
                            int initializerExpression = Integer.parseInt(tokenizer.nextToken());
                            String value = tokenizer.nextToken();
                            String type = tokenizer.nextToken();
                            String modifier = tokenizer.nextToken();

                            //Make the location information for the location token
                            Highlight highlight = ECodeUtilities.makeHighlight(
                                                tokenizer.nextToken());

                            Variable var = director.declareVariable(variableName, type, highlight);
                            Value casted = new Value(value, type);

                            if (initializerExpression > 0) {
                                Value val = (Value) values.remove(new Integer(initializerExpression));
                                director.animateAssignment(var, val, casted, null, highlight);
                            }

                            director.closeScratch();
                            director.openScratch();

                            break;
                        }

                        //Qualified Name (variable)
                        case Code.QN: {

                            int expressionCounter = Integer.parseInt(tokenizer.nextToken());
                            String variableName = tokenizer.nextToken();
                            String value = tokenizer.nextToken();
                            String type = tokenizer.nextToken();

                            Variable var = director.getCurrentMethodFrame().getVariable(variableName);

                            //command that waits for this expression
                            int command = -1;
                            int oper = -1;

                            //Find the command from the command stack
                            int size = commands.size();
                            for (int i = size - 1; i >= 0; i--) {
                                StringTokenizer commandTokenizer = new StringTokenizer(
                                                (String) commands.elementAt(i),
                                                Code.DELIM);
                                command = Integer.parseInt(commandTokenizer.nextToken());
                                int cid = Integer.parseInt(commandTokenizer.nextToken());
                                if (expressionCounter == cid) {
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

                            /**
                            * Do different kind of things depending on
                            * in what expression the variable is used.
                            */

                            //If operator is assignment we just store the value
                            if (oper == Code.A) {

                                if (command == Code.TO) {

                                    variables.put(new Integer(expressionCounter), var);

                                } else {

                                    Value val = new Value(value, type);
                                    ValueActor va = var.getActor().getValue();
                                    val.setActor(va);
                                    values.put(new Integer(expressionCounter), val);
                                }

                            //If oper is other binary operator we will show it
                            //on the screen with operator
                            } else if (ECodeUtilities.isBinary(oper)) {

                                int operator = ECodeUtilities.resolveBinOperator(oper);

                                Value val = new Value(value, type);
                                ValueActor va = var.getActor().getValue();
                                val.setActor(va);

                                if (command == Code.LEFT) {

                                    director.beginBinaryExpression(val, operator,
                                                expressionReference, highlight);

                                } else if (command == Code.RIGHT) {

                                    ExpressionActor ea = (ExpressionActor)
                                    director.getCurrentScratch().findActor(expressionReference);
                                    director.rightBinaryExpression(val, ea, highlight);
                                }

                            //If oper is a unary operator we will show it
                            //on the screen with operator
                            } else if (ECodeUtilities.isUnary(oper)) {

                                Value val = new Value(value, type);
                                ValueActor va = var.getActor().getValue();
                                val.setActor(va);
                                int operator = ECodeUtilities.resolveUnOperator(oper);
                                director.beginUnaryExpression(operator, val,
                                                expressionReference, highlight);

                            //If it is something else we will store it for later use.
                            } else {

                                Value val = new Value(value, type);
                                ValueActor va = var.getActor().getValue();
                                val.setActor(va);
                                values.put(new Integer(expressionCounter), val);
                                //variables.put(new Integer(expressionCounter), var);

                            }

                            break;
                        }

                        //Literal
                        case Code.L: {

                            //Second token is the expression counter
                            int expressionCounter = Integer.parseInt(tokenizer.nextToken());

                            //Third token is the value of the literal
                            String value = tokenizer.nextToken();

                            //Fourth token is the type of the literal
                            String type = tokenizer.nextToken();

                            //Fifth token is the highlight information.
                            //Not used because the whole expression is highlighted.
                            //Highlight highlight =
                            //ECodeUtilities.makeHighlight(tokenizer.nextToken());

                            //command that wait for this expression (left, right)
                            int command = -1;

                            int oper = -1;

                            int size = commands.size();

                            //We find the command
                            for (int i = size - 1; i >= 0; i--) {
                                StringTokenizer commandTokenizer = new StringTokenizer(
                                                (String) commands.elementAt(i),
                                                Code.DELIM);
                                command = Integer.parseInt(commandTokenizer.nextToken());
                                int cid = Integer.parseInt(commandTokenizer.nextToken());
                                if (expressionCounter == cid) {
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
                                    director.rightBinaryExpression(lit, ea, highlight);

                                }

                            //If oper is a unary operator we will show it
                            //on the screen with operator
                            } else if (ECodeUtilities.isUnary(oper)) {

                                int operator = ECodeUtilities.resolveUnOperator(oper);
                                director.beginUnaryExpression(operator, lit,
                                                expressionReference, highlight);

                            //If it is something else we will store it for later use.
                            } else {

                                values.put(new Integer(expressionCounter), lit);

                            }

                            break;
                        }

                        //Static Method Call
                        case Code.SMC: {
                            if (currentMethodInvocation != null) {
                                   methodInvocation.push(currentMethodInvocation);
                            }
                            currentMethodInvocation = new Object[7];

                            for (int i = 0; i < currentMethodInvocation.length; i++) {
                                currentMethodInvocation[i] = null;
                            }

                            currentMethodInvocation[0] = tokenizer.nextToken();
                            currentMethodInvocation[1] = tokenizer.nextToken();
                            int parameterCount = Integer.parseInt(tokenizer.nextToken());

                            Value[] parameterValues = new Value[parameterCount];
                            String[] parameterTypes = new String[parameterCount];
                            String[] parameterNames = new String[parameterCount];

                            for (int i = 0; i < parameterCount; i++) {
                                parameterValues[i] = null;
                                parameterTypes[i] = null;
                                parameterNames[i] = null;
                            }
                            currentMethodInvocation[2] = parameterValues;
                            currentMethodInvocation[3] = parameterTypes;
                            currentMethodInvocation[4] = parameterNames;
                            currentMethodInvocation[5] =
                            ECodeUtilities.makeHighlight(
                                                tokenizer.nextToken());

                            break;
                        }

                        //Parameter
                        case Code.P: {

                            int expressionReference = Integer.parseInt(tokenizer.nextToken());

                            Value[] parameterValues = (Value[]) currentMethodInvocation[2];
                            String[] parameterTypes = (String[]) currentMethodInvocation[3];

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

                            exprs.pop();

                            break;
                        }

                        //Method declaration
                        case Code.MD: {
                            //Make the location information for the location token
                            currentMethodInvocation[6] =
                                ECodeUtilities.makeHighlight(tokenizer.nextToken());
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

                            if (!methodInvocation.empty()) {
                                currentMethodInvocation = (Object[]) methodInvocation.pop();
                            }
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
                            String value = tokenizer.nextToken();
                            String type = tokenizer.nextToken();
                            Highlight h = ECodeUtilities.makeHighlight(
                                                         tokenizer.nextToken());

                            if (type.equals(Void.TYPE.getName())) {

                                //director.finishMethod(null, expressionCounter);
                                returned = false;

                            } else {

                                Value ret = (Value) values.remove(new Integer(expressionReference));
                                Value casted = new Value(value, type);
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

                                Value rv = (Value) returnValue.clone();
                                ValueActor va = director.finishMethod(
                                                    returnActor,
                                                    returnExpressionCounter);
                                rv.setActor(va);


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

                                /**
                                * Look from the expression stack
                                * what expression should be shown next
                                */
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

                                //Do different things depending on in what expression
                                //the literal is used.

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

                                    }

                                //If oper is a unary operator we will show it
                                //on the screen with operator
                                } else if (ECodeUtilities.isUnary(oper)) {

                                    int operator = ECodeUtilities.resolveUnOperator(oper);
                                    director.beginUnaryExpression(operator, rv,
                                                expressionReference, highlight);

                                //If it is something else we will store it for later use.
                                } else {

                                    values.put(new Integer(returnExpressionCounter), rv);

                                }

                            }

                            returned = false;

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

                            int expressionReference =
                                Integer.parseInt(tokenizer.nextToken());
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

                        //Opening and closing scopes
                        case Code.SCOPE: {

                            int scope = Integer.parseInt(tokenizer.nextToken());

                            //Open the scope
                            if (scope == 1) {

                                director.getCurrentMethodFrame().openScope();

                            //Close the scope
                            } else if (scope == 0) {

                                director.getCurrentMethodFrame().closeScope();

                            }

                            director.closeScratch();
                            director.openScratch();

                            break;
                        }

                        //There is an error if the execution comes here.
                        default: {
                            System.out.println("Error! The feature not yet implemented or " +
                                               "there is an error on the other side.");
                            break;
                        }
                    }
                }

            } else {
                running = false;
            }
        }
        director.closeScratch();
    }

}