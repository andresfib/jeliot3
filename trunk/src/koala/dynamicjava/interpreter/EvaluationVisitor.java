/*
 * DynamicJava - Copyright (C) 1999-2001
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL DYADE BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * Except as contained in this notice, the name of Dyade shall not be
 * used in advertising or otherwise to promote the sale, use or other
 * dealings in this Software without prior written authorization from
 * Dyade.
 *
 */

package koala.dynamicjava.interpreter;

import java.lang.reflect.*;
import java.util.*;

import koala.dynamicjava.interpreter.context.*;
import koala.dynamicjava.interpreter.error.*;
import koala.dynamicjava.interpreter.modifier.*;
import koala.dynamicjava.interpreter.throwable.*;
import koala.dynamicjava.tree.*;
import koala.dynamicjava.tree.visitor.*;
import koala.dynamicjava.util.*;

import jeliot.ecode.Code;
/**
 * This tree visitor evaluates each node of a syntax tree
 *
 * @author Stephane Hillion
 * @version 1.2 - 2001/01/23
 */

public class EvaluationVisitor extends VisitorObject {

    private String locationToString(Node node) {
        return ""+node.getBeginLine()+Code.LOC_DELIM+node.getBeginColumn()+
        Code.LOC_DELIM+node.getEndLine()+Code.LOC_DELIM+ node.getEndColumn();
    }

    /**
      * Identifies each expression
      */
    private static long counter=1;
    private boolean evaluating=true;

    private static Stack returnExpressionCounterStack = new Stack();

    /**
     * The current context
     */
    private Context context;

    /**
     * Creates a new visitor
     * @param ctx the current context
     */
    public EvaluationVisitor(Context ctx) {
        context = ctx;
    }

     /**
     * Visits a WhileStatement
     * @param node the node to visit
     */
    public Object visit(WhileStatement node) {
        long condcounter=counter;
        long round=0;       // Number of iterations
        boolean breakc=false;// Exiting while loop because of break
        try {

            while (((Boolean)node.getCondition().acceptVisitor(this)).booleanValue()) {
                try {
                    Code.write(""+Code.WHI+Code.DELIM+condcounter+Code.DELIM+Code.TRUE+
                               Code.DELIM+round+Code.DELIM+locationToString(node.getBody()));
                    node.getBody().acceptVisitor(this);
                    condcounter=counter;
                    round++;
                } catch (ContinueException e) {
                    Code.write(""+Code.CONT+Code.DELIM+Code.WHI+Code.DELIM+locationToString(node.getBody()));
                    condcounter=counter;
                    // 'continue' statement management
                    if (e.isLabeled() && !node.hasLabel(e.getLabel())) {
                        throw e;
                    }
                }
            }
        } catch (BreakException e) {
            // 'break' statement management
            Code.write(""+Code.BR+Code.DELIM+Code.WHI+Code.DELIM+locationToString(node.getBody()));
            breakc=true;
            if (e.isLabeled() && !node.hasLabel(e.getLabel())) {
                throw e;
            }
        }
        if (!breakc)
            Code.write(""+Code.WHI+Code.DELIM+condcounter+Code.DELIM+Code.FALSE+
                       Code.DELIM+round+Code.DELIM+locationToString(node.getBody()));

        return null;
    }

    /**
     * Visits a ForStatement
     * @param node the node to visit
     */
    public Object visit(ForStatement node) {
        long condcounter=counter;
        long round=0;       // Number of iterations
        boolean breakc=false;// Exiting while loop because of break

        try {
            Set vars = (Set)node.getProperty(NodeProperties.VARIABLES);
            context.enterScope(vars);

            // Interpret the initialization expressions
            if (node.getInitialization() != null) {
                Iterator it = node.getInitialization().iterator();
                while (it.hasNext()) {
                    ((Node)it.next()).acceptVisitor(this);
                }
            }

            // Interpret the loop
            try {

                Expression cond   = node.getCondition();
                List       update = node.getUpdate();
                condcounter=counter;
                while (cond == null ||
                      ((Boolean)cond.acceptVisitor(this)).booleanValue()) {
                    try {
                        Code.write(""+Code.FOR+Code.DELIM+condcounter+Code.DELIM+Code.TRUE+
                                   Code.DELIM+round+Code.DELIM+locationToString(node.getBody()));
                        node.getBody().acceptVisitor(this);
                        condcounter=counter;
                        round++;
                    } catch (ContinueException e) {
                        condcounter=counter;
                        // 'continue' statement management
                        if (e.isLabeled() && !node.hasLabel(e.getLabel())) {
                            throw e;
                        }
                    }
                    // Interpret the update statements
                    if (update != null) {
                        Iterator it = update.iterator();
                        while (it.hasNext()) {
                            ((Node)it.next()).acceptVisitor(this);
                        }
                    }
                }
            } catch (BreakException e) {
                // 'break' statement management
                if (e.isLabeled() && !node.hasLabel(e.getLabel())) {
                    throw e;
                }
            }
        } finally {
            // Always leave the current scope
            context.leaveScope();
        }
        if (!breakc)
            Code.write(""+Code.FOR+Code.DELIM+condcounter+Code.DELIM+Code.FALSE+
                       Code.DELIM+round+Code.DELIM+locationToString(node.getBody()));

        return null;
    }

    /**
     * Visits a DoStatement
     * @param node the node to visit
     */
    public Object visit(DoStatement node) {
        long condcounter=counter;
        long round=0;       // Number of iterations
        boolean breakc=false;// Exiting while loop because of break

        try {
            // Interpret the loop
            do {

                Code.write(""+Code.DO+Code.DELIM+condcounter+Code.DELIM+Code.TRUE+
                           Code.DELIM+round+Code.DELIM+locationToString(node.getBody()));

                try {
                    node.getBody().acceptVisitor(this);
                    round++;
                    condcounter=counter;
                } catch (ContinueException e) {
                    // 'continue' statement management
                    if (e.isLabeled() && !node.hasLabel(e.getLabel())) {
                        condcounter=counter;
                        throw e;
                    }
                }
            } while (((Boolean)node.getCondition().acceptVisitor(this)).booleanValue());
        } catch (BreakException e) {
            // 'break' statement management
            if (e.isLabeled() && !node.hasLabel(e.getLabel())) {
                throw e;
            }
        }
        if (!breakc)
            Code.write(""+Code.DO+Code.DELIM+condcounter+Code.DELIM+Code.FALSE+
                       Code.DELIM+round+Code.DELIM+locationToString(node.getBody()));

        return null;
    }

    /**
     * Visits a SwitchStatement
     * @param node the node to visit
     */
    public Object visit(SwitchStatement node) {
        try {
            boolean processed = false;

            // Evaluate the choice expression
            Object o = node.getSelector().acceptVisitor(this);
            if (o instanceof Character) {
                o = new Integer(((Character)o).charValue());
            }
            Number n = (Number)o;

            // Search for the matching label
            ListIterator it = node.getBindings().listIterator();
            ListIterator dit = null;
            loop: while (it.hasNext()) {
                SwitchBlock sc = (SwitchBlock)it.next();
                Number l = null;
                if (sc.getExpression() != null) {
                    o = sc.getExpression().acceptVisitor(this);
                    if (o instanceof Character) {
                        o = new Integer(((Character)o).charValue());
                    }
                    l= (Number)o;
                } else {
                    dit = node.getBindings().listIterator(it.nextIndex() - 1);
                }

                if (l != null && n.intValue() == l.intValue()) {
                    processed = true;
                    // When a matching label is found, interpret all the
                    // remaining statements
                    for(;;) {
                        if (sc.getStatements() != null) {
                            Iterator it2 = sc.getStatements().iterator();
                            while (it2.hasNext()) {
                                ((Node)it2.next()).acceptVisitor(this);
                            }
                        }
                        if (it.hasNext()) {
                            sc = (SwitchBlock)it.next();
                        } else {
                            break loop;
                        }
                    }
                }
            }

            if (!processed && dit != null) {
                SwitchBlock sc = (SwitchBlock)dit.next();
                for(;;) {
                    if (sc.getStatements() != null) {
                        Iterator it2 = sc.getStatements().iterator();
                        while (it2.hasNext()) {
                            ((Node)it2.next()).acceptVisitor(this);
                        }
                    }
                    if (dit.hasNext()) {
                        sc = (SwitchBlock)dit.next();
                    } else {
                        break;
                    }
                }
            }
        } catch (BreakException e) {
            // 'break' statement management
            if (e.isLabeled()) {
                throw e;
            }
        }
        return null;
    }

    /**
     * Visits a LabeledStatement
     * @param node the node to visit
     */
    public Object visit(LabeledStatement node) {
        try {
            node.getStatement().acceptVisitor(this);
        } catch (BreakException e) {
            // 'break' statement management
            if (!e.isLabeled() || !e.getLabel().equals(node.getLabel())) {
                throw e;
            }
        }
        return null;
    }

    /**
     * Visits a SynchronizedStatement
     * @param node the node to visit
     */
    public Object visit(SynchronizedStatement node) {
        synchronized(node.getLock().acceptVisitor(this)) {
            node.getBody().acceptVisitor(this);
        }
        return null;
    }

    /**
     * Visits a BreakStatement
     * @param node the node to visit
     */
    public Object visit(BreakStatement node) {
        throw new BreakException("unexpected.break", node.getLabel());
    }

    /**
     * Visits a ContinueStatement
     * @param node the node to visit
     */
    public Object visit(ContinueStatement node) {
        throw new ContinueException("unexpected.continue", node.getLabel());
    }

    /**
     * Visits a TryStatement
     * @param node the node to visit
     */
    public Object visit(TryStatement node) {

        boolean handled = false;
        try {
            node.getTryBlock().acceptVisitor(this);
        } catch (Throwable e) {
            Throwable t = e;
            if (e instanceof ThrownException) {
                t = ((ThrownException)e).getException();
            } else if (e instanceof CatchedExceptionError) {
                t = ((CatchedExceptionError)e).getException();
            }

            // Find the exception handler
            Iterator it = node.getCatchStatements().iterator();
            while (it.hasNext()) {
                CatchStatement cs = (CatchStatement)it.next();
                Class c = NodeProperties.getType(cs.getException().getType());
                if (c.isAssignableFrom(t.getClass())) {
                    handled = true;

                    // Define the exception in a new scope
                    context.enterScope();
                    context.define(cs.getException().getName(), t);

                    // Interpret the handler
                    cs.getBlock().acceptVisitor(this);
                    break;
                }
            }

            if (!handled) {
                if (e instanceof Error) {
                    throw (Error)e;
                } else if (e instanceof RuntimeException) {
                    throw (RuntimeException)e;
                } else {
                    throw new CatchedExceptionError((Exception)e, node);
                }
            }
        } finally {
            // Leave the current scope if entered
            if (handled) {
                context.leaveScope();
            }

            // Interpret the 'finally' block
            Node n;
            if ((n = node.getFinallyBlock()) != null) {
            n.acceptVisitor(this);
            }
        }
        return null;
    }

    /**
     * Visits a ThrowStatement
     * @param node the node to visit
     */
    public Object visit(ThrowStatement node) {
        throw new ThrownException((Throwable)node.getExpression().acceptVisitor(this));
    }

    /**
     * Visits a ReturnStatement
     * @param node the node to visit
     */
    public Object visit(ReturnStatement node) {

        Object o = null;

        if (node.getExpression() != null) {

            long auxcounter=counter;
            Code.write("" + Code.BEGIN+Code.DELIM+Code.R+Code.DELIM+counter+
                        Code.DELIM+locationToString(node));

            o = node.getExpression().acceptVisitor(this);//

            Long l = (Long) returnExpressionCounterStack.pop();

            Code.write("" + Code.R+Code.DELIM+
                        l.toString()+
                        Code.DELIM+auxcounter+
                        Code.DELIM+o.toString()+Code.DELIM+
                        NodeProperties.getType(node.getExpression())+
                        Code.DELIM+locationToString(node));//+Code.DELIM+m.getName());

            throw new ReturnException("return.statement",
                                      o,
                                      node);
        } else {

            returnExpressionCounterStack.pop();

            Code.write("" + Code.R+Code.DELIM+(counter++)+
                        Code.DELIM+Code.NO_REFERENCE+Code.DELIM+
                        Void.TYPE.getName()+Code.DELIM+Code.UNKNOWN+
                        Code.DELIM+locationToString(node));

            throw new ReturnException("return.statement", node);
        }
    }

    /**
     * Visits a IfThenStatement
     * @param node the node to visit
     */
    public Object visit(IfThenStatement node) {
    long condcounter=counter;
    if (((Boolean)node.getCondition().acceptVisitor(this)).booleanValue()) {

        Code.write("" + Code.IFT+Code.DELIM+condcounter+Code.DELIM+Code.TRUE+
                        Code.DELIM+locationToString(node.getThenStatement()));

        node.getThenStatement().acceptVisitor(this);
    }
    else{
        Code.write("" + Code.IFT+Code.DELIM+condcounter+Code.DELIM+Code.FALSE);
    }
    return null;
    }

    /**
     * Visits a IfThenElseStatement
     * @param node the node to visit
     */
    public Object visit(IfThenElseStatement node) {
    long condcounter=counter;
    String value;
    if (((Boolean)node.getCondition().acceptVisitor(this)).booleanValue()) {
        Code.write("" + Code.IFTE+Code.DELIM+condcounter+Code.DELIM+Code.TRUE+
                        Code.DELIM+locationToString(node.getThenStatement()));

        node.getThenStatement().acceptVisitor(this);

    } else {
        Code.write("" + Code.IFTE+Code.DELIM+condcounter+Code.DELIM+Code.FALSE+
                        Code.DELIM+locationToString(node.getElseStatement()));

        node.getElseStatement().acceptVisitor(this);
    }

    return null;
    }

    /**
     * Visits a BlockStatement
     * @param node the node to visit
     */
    public Object visit(BlockStatement node) {
        try {
            // Enter a new scope and define the local variables
            Code.write(Code.SCOPE+Code.DELIM+"1");
            Set vars = (Set)node.getProperty(NodeProperties.VARIABLES);
            context.enterScope(vars);

            // Interpret the statements
            Iterator it = node.getStatements().iterator();
            while (it.hasNext()) {
                ((Node)it.next()).acceptVisitor(this);
            }
        } finally {
            // Always leave the current scope
            Code.write("" + Code.SCOPE+Code.DELIM+"0");
            context.leaveScope();
        }

        return null;
    }

    /**
     * Visits a Literal
     * @param node the node to visit
     */
    public Object visit(Literal node) {

        if (node.getType()==null) {
            Code.write("" + Code.L+Code.DELIM+(counter++)+Code.DELIM+node.getValue()+
            Code.DELIM+Code.REFERENCE+Code.DELIM+locationToString(node));
        }
        else {
            Code.write(Code.L+Code.DELIM+(counter++)+Code.DELIM+node.getValue()+
            Code.DELIM+node.getType().getName()+Code.DELIM+locationToString(node));
        }

        return node.getValue();
    }

     /**
     * Visits a VariableDeclaration
     * @param node the node to visit
     */
    public Object visit(VariableDeclaration node) {
        Class c = NodeProperties.getType(node.getType());
        int type;
        String value;
        long auxcounter=counter;

        if (node.getInitializer() != null) {

            Object o = performCast(c, node.getInitializer().acceptVisitor(this));

            if (node.isFinal()) {

                context.setConstant(node.getName(), o);
                type=Code.FINAL;

            } else {

                type=Code.NOT_FINAL;
                context.set(node.getName(), o);

            }

            value=o.toString();

        } else {
            if (node.isFinal()) {

                context.setConstant(node.getName(), UninitializedObject.INSTANCE);
                type=Code.FINAL;

            } else {

                context.set(node.getName(), UninitializedObject.INSTANCE);
                type=Code.NOT_FINAL;


            }
            value=Code.UNKNOWN;
            auxcounter=Code.NO_REFERENCE;
        }

        Code.write(""+Code.VD+Code.DELIM+node.getName()+Code.DELIM+auxcounter+
                      Code.DELIM+value+Code.DELIM+c.getName()+Code.DELIM+type+
                      Code.DELIM+locationToString(node));
        return null;
    }

    /**
     * Visits an ObjectFieldAccess
     * @param node the node to visit
     */
    public Object visit(ObjectFieldAccess node) {
        Class c = NodeProperties.getType(node.getExpression());

        // Evaluate the object
        Object obj  = node.getExpression().acceptVisitor(this);

        if (!c.isArray()) {
            Field f = (Field)node.getProperty(NodeProperties.FIELD);
            // Relax the protection for members
            if (context.getAccessible()) {
                f.setAccessible(true);
            }
            try {
                return f.get(obj);
            } catch (Exception e) {
                throw new CatchedExceptionError(e, node);
            }
        } else {
            // If the object is an array, the field must be 'length'.
            // This field is not a normal field and it is the only
            // way to get it
            return new Integer(Array.getLength(obj));
        }
    }

   /**
     * Visits an ObjectMethodCall
     * @param node the node to visit
     */
    public Object visit(ObjectMethodCall node) {
        Expression exp = node.getExpression();

        // Evaluate the receiver first
        Object obj  = exp.acceptVisitor(this);

        if (node.hasProperty(NodeProperties.METHOD)) {
            Method   m    = (Method)node.getProperty(NodeProperties.METHOD);
            Class[]  typs = m.getParameterTypes();

            // Relax the protection for members?
            if (context.getAccessible()) {
                m.setAccessible(true);
            }

            List     larg = node.getArguments();
            Object[] args = Constants.EMPTY_OBJECT_ARRAY;

            // Fill the arguments
            if (larg != null) {
                args = new Object[larg.size()];
                Iterator it = larg.iterator();
                int      i  = 0;
                while (it.hasNext()) {
                    Object p  = ((Expression)it.next()).acceptVisitor(this);
                    args[i] = performCast(typs[i], p);
                    i++;
                }
            }
            // Invoke the method
            try {
                return m.invoke(obj, args);
            } catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof Error) {
                    throw (Error)e.getTargetException();
                } else if (e.getTargetException() instanceof RuntimeException) {
                    throw (RuntimeException)e.getTargetException();
                }
                throw new ThrownException(e.getTargetException(), node);
            } catch (Exception e) {
                throw new CatchedExceptionError(e, node);
            }
        } else {
            // If the 'method' property is not set, the object must be
            // an array and the called method must be 'clone'.
            // Since the 'clone' method of an array is not a normal
            // method, the only way to invoke it is to simulate its
            // behaviour.
            Class c = NodeProperties.getType(exp);
            int len = Array.getLength(obj);
            Object result = Array.newInstance(c.getComponentType(), len);
            for (int i = 0; i < len; i++) {
                Array.set(result, i, Array.get(obj, i));
            }
            return result;
        }
    }

    /**
     * Visits a StaticFieldAccess
     * @param node the node to visit
     */
    public Object visit(StaticFieldAccess node) {
        Field f = (Field)node.getProperty(NodeProperties.FIELD);
        try {
            return f.get(null);
        } catch (Exception e) {
            throw new CatchedExceptionError(e, node);
        }
    }

    /**
     * Visits a SuperFieldAccess
     * @param node the node to visit
     */
    public Object visit(SuperFieldAccess node) {
        Field f = (Field)node.getProperty(NodeProperties.FIELD);
        try {
            return f.get(context.getHiddenArgument());
        } catch (Exception e) {
            throw new CatchedExceptionError(e, node);
        }
    }

    /**
     * Visits a SuperMethodCall
     * @param node the node to visit
     */
    public Object visit(SuperMethodCall node) {
        Method   m     = (Method)node.getProperty(NodeProperties.METHOD);
        List     larg  = node.getArguments();
        Object[] args  = Constants.EMPTY_OBJECT_ARRAY;

        // Fill the arguments
        if (larg != null) {
            Iterator it = larg.iterator();
            int      i  = 0;
            args        = new Object[larg.size()];
            while (it.hasNext()) {
                args[i] = ((Expression)it.next()).acceptVisitor(this);
                i++;
            }
        }

        // Invoke the method
        try {
            return m.invoke(context.getHiddenArgument(), args);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof Error) {
                throw (Error)e.getTargetException();
            } else if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException)e.getTargetException();
            }
            throw new ThrownException(e.getTargetException());
        } catch (Exception e) {
            throw new CatchedExceptionError(e, node);
        }
    }

    /**
     * Visits a StaticMethodCall
     * @param node the node to visit
     */
    public Object visit(StaticMethodCall node) {

        Method   m    = (Method)node.getProperty(NodeProperties.METHOD);
        List     larg = node.getArguments();
        Object[] args = Constants.EMPTY_OBJECT_ARRAY;

        /* JELIOT 3 */
        if (larg != null) {

            Code.write("" + Code.SMC+Code.DELIM+m.getName()+Code.DELIM+m.getDeclaringClass().getName()
              +Code.DELIM+larg.size()+Code.DELIM+locationToString(node));

        } else {

             Code.write("" + Code.SMC+Code.DELIM+m.getName()+Code.DELIM+m.getDeclaringClass().getName()
               +Code.DELIM+"0"+Code.DELIM+locationToString(node));//0 arguments
        }

        Long l = new Long(counter);
        returnExpressionCounterStack.push(l);
        counter++;

        // Fill the arguments
        if (larg != null) {

            args = new Object[larg.size()];
            Iterator it = larg.iterator();
            int      i  = 0;
            long     auxcounter; //Records the previous counter value
            Object   auxarg; //Stores the current argument
            Class[]  typs = m.getParameterTypes();


            while (it.hasNext()) {

                Code.write("" + Code.BEGIN+Code.DELIM+Code.P+Code.DELIM+counter+
                                Code.DELIM+locationToString(node));//arguments construction
                auxcounter=counter;
                args[i] = ((Expression)it.next()).acceptVisitor(this);

                Code.write("" + Code.P+Code.DELIM+auxcounter+Code.DELIM+typs[i].getName());
                i++;
            }
        }

        // Invoke the method
        try {
            Object o = m.invoke(null, args);
            Code.write("" + Code.SMCC); //the method call is closed
            if (((Long) returnExpressionCounterStack.peek()).equals(l)) {

                returnExpressionCounterStack.pop();
            }
            return o;
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof Error) {
                throw (Error)e.getTargetException();
            } else if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException)e.getTargetException();
            }
            throw new ThrownException(e.getTargetException());
        } catch (Exception e) {
            throw new CatchedExceptionError(e, node);
        }
    }

    /**
     * Visits a SimpleAssignExpression
     * @param node the node to visit
     */
    public Object visit(SimpleAssignExpression node) {

        long assigncounter=counter++;
        Code.write("" + Code.BEGIN+Code.DELIM+Code.A+Code.DELIM+assigncounter+Code.DELIM+locationToString(node)); //
        Node ln = node.getLeftExpression();
        LeftHandSideModifier mod = NodeProperties.getModifier(ln);
        mod.prepare(this, context);

        long auxcounter=counter;

        Object val  = node.getRightExpression().acceptVisitor(this);

        Code.write("" + Code.TO+Code.DELIM+counter);
        long auxcounter2=counter;

        evaluating=false;
        node.getLeftExpression().acceptVisitor(this);
        evaluating=true;
        val = performCast(NodeProperties.getType(node), val);
        mod.modify(context, val);

        Code.write("" + Code.A+Code.DELIM+assigncounter+Code.DELIM+auxcounter+
                        Code.DELIM+auxcounter2+Code.DELIM+val.toString()+
                        Code.DELIM+NodeProperties.getType(node).getName()+
                        Code.DELIM+locationToString(node));

        return val;
    }


    /**
     * Displays a QualifiedName without worrying about initialization
     * @param node the node to visit
     * @return the value of the local variable represented by this node
     */
    public Object display(QualifiedName node) {

        Object result = context.get(node.getRepresentation());
        Class c = NodeProperties.getType(node);
        String value=Code.UNKNOWN;

        if (result != UninitializedObject.INSTANCE) {
            value=result.toString();
        }

        Code.write("" + Code.QN+Code.DELIM+(counter++)+Code.DELIM+node.getRepresentation()+
          Code.DELIM+value+Code.DELIM+c.getName());

        return result;
    }

    /**
     * Visits a QualifiedName
     * @param node the node to visit
     * @return the value of the local variable represented by this node
     */
    public Object visit(QualifiedName node) {
        if(evaluating) {

            Object result = context.get(node.getRepresentation());
            Class c = NodeProperties.getType(node);

            if (result == UninitializedObject.INSTANCE) {
                node.setProperty(NodeProperties.ERROR_STRINGS,
                    new String[] { node.getRepresentation() });
                throw new ExecutionError("uninitialized.variable", node);
            }

            Code.write(""+Code.QN+Code.DELIM+(counter++)+Code.DELIM+
            node.getRepresentation()+Code.DELIM+result+Code.DELIM+c.getName());

            return result;
        }
        else {
            return display(node);
        }
    }

    /**
     * Visits a TypeExpression
     * @param node the node to visit
     */
    public Object visit(TypeExpression node) {
        return node.getProperty(NodeProperties.VALUE);
    }

    /**
     * Visits a SimpleAllocation
     * @param node the node to visit
     */
    public Object visit(SimpleAllocation node) {
        List        larg = node.getArguments();
        Object[]    args = Constants.EMPTY_OBJECT_ARRAY;

        // Fill the arguments
        if (larg != null) {
            args = new Object[larg.size()];
            Iterator it = larg.iterator();
            int      i  = 0;
            while (it.hasNext()) {
                args[i++] = ((Expression)it.next()).acceptVisitor(this);
            }
        }

        return context.invokeConstructor(node, args);
    }

    /**
     * Visits an ArrayAllocation
     * @param node the node to visit
     */
    public Object visit(ArrayAllocation node) {
        // Visits the initializer if one
        if (node.getInitialization() != null) {
            return node.getInitialization().acceptVisitor(this);
        }

        // Evaluate the size expressions
        int[]    dims = new int[node.getSizes().size()];
        Iterator it = node.getSizes().iterator();
        int      i  = 0;
        while (it.hasNext()) {
            Number n = (Number)((Expression)it.next()).acceptVisitor(this);
            dims[i++] = n.intValue();
        }

        // Create the array
        if (node.getDimension() != dims.length) {
            Class c = NodeProperties.getComponentType(node);
            c = Array.newInstance(c, 0).getClass();
            return Array.newInstance(c, dims);
        } else {
            return Array.newInstance(NodeProperties.getComponentType(node), dims);
        }
    }

    /**
     * Visits a ArrayInitializer
     * @param node the node to visit
     */
    public Object visit(ArrayInitializer node) {
        Object result = Array.newInstance(NodeProperties.getType(node.getElementType()),
                      node.getCells().size());

        Iterator it = node.getCells().iterator();
        int      i  = 0;
        while (it.hasNext()) {
            Object o = ((Expression)it.next()).acceptVisitor(this);
            Array.set(result, i++, o);
        }

        return result;
    }

    /**
     * Visits an ArrayAccess
     * @param node the node to visit
     */
    public Object visit(ArrayAccess node) {
        Object t = node.getExpression().acceptVisitor(this);
        Object o = node.getCellNumber().acceptVisitor(this);
        if (o instanceof Character) {
            o = new Integer(((Character)o).charValue());
        }
        return Array.get(t, ((Number)o).intValue());
    }

    /**
     * Visits a InnerAllocation
     * @param node the node to visit
     */
    public Object visit(InnerAllocation node) {
        Constructor cons = (Constructor)node.getProperty(NodeProperties.CONSTRUCTOR);
        Class       c    = NodeProperties.getType(node);

        List        larg = node.getArguments();
        Object[]    args = null;

        if (larg != null) {
            args = new Object[larg.size() + 1];
            args[0] = node.getExpression().acceptVisitor(this);

            Iterator it = larg.iterator();
            int      i  = 1;
            while (it.hasNext()) {
                args[i++] = ((Expression)it.next()).acceptVisitor(this);
            }
        } else {
            args = new Object[] { node.getExpression().acceptVisitor(this) };
        }

        // Invoke the constructor
        try {
            return cons.newInstance(args);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof Error) {
                throw (Error)e.getTargetException();
            }  else if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException)e.getTargetException();
            }
            throw new ThrownException(e.getTargetException());
        } catch (Exception e) {
            throw new CatchedExceptionError(e, node);
        }
    }

     /**
     * Visits a ClassAllocation
     * @param node the node to visit
     */
    public Object visit(ClassAllocation node) {
        List        larg = node.getArguments();
        Object[]    args = Constants.EMPTY_OBJECT_ARRAY;

        // Fill the arguments
        if (larg != null) {
            args = new Object[larg.size()];
            Iterator it = larg.iterator();
            int      i  = 0;
            while (it.hasNext()) {
                args[i++] = ((Expression)it.next()).acceptVisitor(this);
            }
        }

        return context.invokeConstructor(node, args);
    }

    /**
     * Visits a NotExpression
     * @param node the node to visit
     */
    public Object visit(NotExpression node) {
        if (node.hasProperty(NodeProperties.VALUE)) {
            // The expression is constant
            Code.write(""+Code.NO+Code.DELIM+(counter++)+Code.DELIM+Code.NO_REFERENCE+Code.DELIM
                       +node.getProperty(NodeProperties.VALUE)+Code.DELIM+locationToString(node));
            return node.getProperty(NodeProperties.VALUE);
        } else {
            long notcounter=counter++;
            long auxcounter=counter;
            Code.write(""+Code.BEGIN+Code.DELIM+Code.NO+Code.DELIM+notcounter+
                    Code.DELIM+locationToString(node));
            Boolean b = (Boolean)node.getExpression().acceptVisitor(this);
            Code.write(""+Code.NO+Code.DELIM+notcounter+
                       Code.DELIM+auxcounter+
                       Code.DELIM+!b.booleanValue()+
                       Code.DELIM+NodeProperties.getType(node).getName()+
                       Code.DELIM+locationToString(node));
            if (b.booleanValue()) {
                return Boolean.FALSE;
            } else {
                return Boolean.TRUE;
            }
        }
    }

    /**
     * Visits a ComplementExpression
     * @param node the node to visit
     */
    public Object visit(ComplementExpression node) {
        if (node.hasProperty(NodeProperties.VALUE)) {
            // The expression is constant
            Code.write(""+Code.NO+Code.DELIM+(counter++)+Code.DELIM+Code.NO_REFERENCE+
                       Code.DELIM+node.getProperty(NodeProperties.VALUE)+
                       Code.DELIM+locationToString(node));

            return node.getProperty(NodeProperties.VALUE);
        } else {
            long compcounter=counter++;
            long auxcounter=counter;
            Code.write(""+Code.BEGIN+Code.DELIM+Code.COMP+
                       Code.DELIM+compcounter+
                       Code.DELIM+locationToString(node));

            Class  c = NodeProperties.getType(node);
            Object o = node.getExpression().acceptVisitor(this);

            if (o instanceof Character) {
                o = new Integer(((Character)o).charValue());
            }
            if (c == int.class) {
                Code.write(""+Code.COMP+Code.DELIM+compcounter+
                           Code.DELIM+auxcounter+
                           Code.DELIM+(~((Number)o).intValue())+
                           Code.DELIM+NodeProperties.getType(node).getName()+
                           Code.DELIM+locationToString(node));
                return new Integer(~((Number)o).intValue());
            } else {
                Code.write(""+Code.COMP+Code.DELIM+compcounter+
                           Code.DELIM+auxcounter+
                           Code.DELIM+(~((Number)o).longValue())+
                           Code.DELIM+NodeProperties.getType(node).getName()+
                           Code.DELIM+locationToString(node));
                return new Long(~((Number)o).longValue());
            }

        }
    }

    /**
     * Visits a PlusExpression
     * @param node the node to visit
     */
    public Object visit(PlusExpression node) {
        if (node.hasProperty(NodeProperties.VALUE)) {
            // The expression is constant
            return node.getProperty(NodeProperties.VALUE);
        } else {
            return InterpreterUtilities.plus
            (NodeProperties.getType(node),
             node.getExpression().acceptVisitor(this));
        }
    }

    /**
     * Visits a MinusExpression
     * @param node the node to visit
     */
    public Object visit(MinusExpression node) {

    if (node.hasProperty(NodeProperties.VALUE)) {
        // The expression is constant
        Code.write(""+Code.MINUS+Code.DELIM+(counter++)+
                   Code.DELIM+Code.NO_REFERENCE+
                   Code.DELIM+node.getProperty(NodeProperties.VALUE)+
                   Code.DELIM+locationToString(node));

        return node.getProperty(NodeProperties.VALUE);
    } else {
        long minuscounter =counter++;
        long auxcounter =counter;
        Code.write(""+Code.BEGIN+Code.DELIM+Code.MINUS+Code.DELIM+minuscounter+
                   Code.DELIM+locationToString(node));
        Class c = NodeProperties.getType(node);
        Object robj =node.getExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.minus(c, robj);
        Code.write(""+Code.MINUS+Code.DELIM+minuscounter+Code.DELIM+auxcounter
               +Code.DELIM+o.toString()+Code.DELIM+NodeProperties.getType(node).getName()+
               Code.DELIM+locationToString(node));
        return o;
    }
    /*
      if (node.hasProperty(NodeProperties.VALUE)) {
      // The expression is constant
      return node.getProperty(NodeProperties.VALUE);
      } else {
      return InterpreterUtilities.minus
      (NodeProperties.getType(node),
      node.getExpression().acceptVisitor(this));
      }
    */
    }

    /**
     * Visits a AddExpression
     * @param node the node to visit
     */
    public Object visit(AddExpression node) {

        Class c = NodeProperties.getType(node);
        long addcounter=counter++;
        long auxcounter=counter;

        Code.write(""+Code.BEGIN+Code.DELIM+Code.AE+Code.DELIM+addcounter+
                    Code.DELIM+locationToString(node));
        Code.write(""+Code.LEFT+Code.DELIM+counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2=counter;

        Code.write(""+Code.RIGHT+Code.DELIM+counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.add(c, lobj, robj);

        Code.write(""+Code.AE+Code.DELIM+addcounter+Code.DELIM+auxcounter+
            Code.DELIM+auxcounter2+Code.DELIM+o.toString()+
            Code.DELIM+NodeProperties.getType(node).getName()+
            Code.DELIM+locationToString(node));
        return o;

    }

    /**
     * Visits an AddAssignExpression
     * @param node the node to visit
     */
    public Object visit(AddAssignExpression node) {
        //Simulate that a+=3-b -->> a= a+ (3-b)
        //Added 2 evaluations to left hand side ...
        long assigncounter=counter++;
        long assignauxcounter=counter;
        //Start assignment
        Code.write("" + Code.BEGIN+Code.DELIM+Code.A+Code.DELIM+assigncounter+Code.DELIM+locationToString(node)); //
        //Start ad expression
        long addcounter=counter++;
        long auxcounter=counter;

        Code.write(""+Code.BEGIN+Code.DELIM+Code.AE+Code.DELIM+addcounter+
                    Code.DELIM+locationToString(node));
        // Get left hand side for the add expression
        Code.write(""+Code.LEFT+Code.DELIM+counter);

        Node   left = node.getLeftExpression();
        evaluating=false;
        left.acceptVisitor(this);
        evaluating=true;

        LeftHandSideModifier mod = NodeProperties.getModifier(left);
        Object lhs = mod.prepare(this, context);
        long auxcounter2=counter;

        // Get right hand side for the add expression
        Code.write(""+Code.RIGHT+Code.DELIM+counter);
        Object val  = node.getRightExpression().acceptVisitor(this);


        // Perform the operation
        Object result = InterpreterUtilities.add(
            NodeProperties.getType(node),
            lhs,
	    val);

        // Cast the result
        result = performCast(NodeProperties.getType(left), result);

        Code.write(""+Code.AE+Code.DELIM+addcounter+Code.DELIM+auxcounter+
            Code.DELIM+auxcounter2+Code.DELIM+result.toString()+
            Code.DELIM+NodeProperties.getType(node).getName()+
            Code.DELIM+locationToString(node));

        long assignauxcounter2=counter;
        Code.write("" + Code.TO+Code.DELIM+counter);
        evaluating=false;
        left.acceptVisitor(this);
        evaluating=true;


        // Modify the variable and return
        mod.modify(context, result);
        Code.write("" + Code.A+Code.DELIM+assigncounter+Code.DELIM+assignauxcounter+
                   Code.DELIM+assignauxcounter2+Code.DELIM+result.toString()+
                   Code.DELIM+NodeProperties.getType(node).getName()+
                   Code.DELIM+locationToString(node));


        return result;
    }

    /**
     * Visits a SubtractExpression
     * @param node the node to visit
     */
    public Object visit(SubtractExpression node) {

        Class c = NodeProperties.getType(node);
        long substractcounter=counter++;
        long auxcounter=counter;

        Code.write(""+Code.BEGIN+Code.DELIM+Code.SE+Code.DELIM+substractcounter+Code.DELIM+locationToString(node));
        Code.write(""+Code.LEFT+Code.DELIM+counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2=counter;

        Code.write(""+Code.RIGHT+Code.DELIM+counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.subtract(c, lobj, robj);

        Code.write(""+Code.SE+Code.DELIM+substractcounter+Code.DELIM+auxcounter+
            Code.DELIM+auxcounter2+Code.DELIM+o.toString()+Code.DELIM+
            NodeProperties.getType(node).getName()+Code.DELIM+locationToString(node));

        return o;

    }

    /**
     * Visits an SubtractAssignExpression
     * @param node the node to visit
     */
    public Object visit(SubtractAssignExpression node) {
        long assigncounter=counter++;
        long assignauxcounter=counter;
        //Start assignment
        Code.write("" + Code.BEGIN+Code.DELIM+Code.A+Code.DELIM+assigncounter+Code.DELIM+locationToString(node)); //
        //Start ad expression
        long subcounter=counter++;
        long auxcounter=counter;

        Code.write(""+Code.BEGIN+Code.DELIM+Code.SE+Code.DELIM+subcounter+
                    Code.DELIM+locationToString(node));
        // Get left hand side for the subtract expression
        Code.write(""+Code.LEFT+Code.DELIM+counter);

        Node   left = node.getLeftExpression();
        evaluating=false;
        left.acceptVisitor(this);
        evaluating=true;

        LeftHandSideModifier mod = NodeProperties.getModifier(left);
        Object lhs = mod.prepare(this, context);
        long auxcounter2=counter;

        // Get right hand side for the subtract expression
        Code.write(""+Code.RIGHT+Code.DELIM+counter);
        Object val  = node.getRightExpression().acceptVisitor(this);


        // Perform the operation
        Object result = InterpreterUtilities.subtract(
            NodeProperties.getType(node),
            lhs,
	    val);

        // Cast the result
        result = performCast(NodeProperties.getType(left), result);

        Code.write(""+Code.SE+Code.DELIM+subcounter+Code.DELIM+auxcounter+
            Code.DELIM+auxcounter2+Code.DELIM+result.toString()+
            Code.DELIM+NodeProperties.getType(node).getName()+
            Code.DELIM+locationToString(node));

        long assignauxcounter2=counter;
        Code.write("" + Code.TO+Code.DELIM+counter);
        evaluating=false;
        left.acceptVisitor(this);
        evaluating=true;


        // Modify the variable and return
        mod.modify(context, result);
        Code.write("" + Code.A+Code.DELIM+assigncounter+Code.DELIM+assignauxcounter+
                   Code.DELIM+assignauxcounter2+Code.DELIM+result.toString()+
                   Code.DELIM+NodeProperties.getType(node).getName()+
                   Code.DELIM+locationToString(node));


        return result;
        /*
        Node   left = node.getLeftExpression();
        LeftHandSideModifier mod = NodeProperties.getModifier(left);
        Object lhs = mod.prepare(this, context);

        // Perform the operation
        Object result = InterpreterUtilities.subtract(
                NodeProperties.getType(node),
                lhs,
                node.getRightExpression().acceptVisitor(this));

        // Cast the result
        result = performCast(NodeProperties.getType(left), result);

        // Modify the variable and return
        mod.modify(context, result);

        return result;*/
    }

    /**
     * Visits a MultiplyExpression
     * @param node the node to visit
     */
    public Object visit(MultiplyExpression node) {

        Class c = NodeProperties.getType(node);
        long multiplycounter=counter++;
        long auxcounter=counter;

        Code.write(""+Code.BEGIN+Code.DELIM+Code.ME+Code.DELIM+multiplycounter+
                    Code.DELIM+locationToString(node));
        Code.write(""+Code.LEFT+Code.DELIM+counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2=counter;

        Code.write(""+Code.RIGHT+Code.DELIM+counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.multiply(c, lobj, robj);

        Code.write(""+Code.ME+Code.DELIM+multiplycounter+Code.DELIM+auxcounter+
            Code.DELIM+auxcounter2+Code.DELIM+o.toString()+
            Code.DELIM+NodeProperties.getType(node).getName()+
            Code.DELIM+locationToString(node));
        return o;

    /*
        if (node.hasProperty(NodeProperties.VALUE)) {
            // The expression is constant
            return node.getProperty(NodeProperties.VALUE);
        } else {
            return InterpreterUtilities.multiply(
                NodeProperties.getType(node),
                node.getLeftExpression().acceptVisitor(this),
                node.getRightExpression().acceptVisitor(this));
        }
    */
    }

    /**
     * Visits an MultiplyAssignExpression
     * @param node the node to visit
     */
    public Object visit(MultiplyAssignExpression node) {
        long assigncounter=counter++;
        long assignauxcounter=counter;
        //Start assignment
        Code.write("" + Code.BEGIN+Code.DELIM+Code.A+Code.DELIM+assigncounter+Code.DELIM+locationToString(node)); //
        //Start ad expression
        long multcounter=counter++;
        long auxcounter=counter;

        Code.write(""+Code.BEGIN+Code.DELIM+Code.AE+Code.DELIM+multcounter+
                    Code.DELIM+locationToString(node));
        // Get left hand side for the add expression
        Code.write(""+Code.LEFT+Code.DELIM+counter);

        Node   left = node.getLeftExpression();
        evaluating=false;
        left.acceptVisitor(this);
        evaluating=true;

        LeftHandSideModifier mod = NodeProperties.getModifier(left);
        Object lhs = mod.prepare(this, context);
        long auxcounter2=counter;

        // Get right hand side for the add expression
        Code.write(""+Code.RIGHT+Code.DELIM+counter);
        Object val  = node.getRightExpression().acceptVisitor(this);


        // Perform the operation
        Object result = InterpreterUtilities.multiply(
            NodeProperties.getType(node),
            lhs,
	    val);

        // Cast the result
        result = performCast(NodeProperties.getType(left), result);

        Code.write(""+Code.AE+Code.DELIM+multcounter+Code.DELIM+auxcounter+
            Code.DELIM+auxcounter2+Code.DELIM+result.toString()+
            Code.DELIM+NodeProperties.getType(node).getName()+
            Code.DELIM+locationToString(node));

        long assignauxcounter2=counter;
        Code.write("" + Code.TO+Code.DELIM+counter);
        evaluating=false;
        left.acceptVisitor(this);
        evaluating=true;


        // Modify the variable and return
        mod.modify(context, result);
        Code.write("" + Code.A+Code.DELIM+assigncounter+Code.DELIM+assignauxcounter+
                   Code.DELIM+assignauxcounter2+Code.DELIM+result.toString()+
                   Code.DELIM+NodeProperties.getType(node).getName()+
                   Code.DELIM+locationToString(node));


        return result;
        /*
        Node   left = node.getLeftExpression();
        LeftHandSideModifier mod = NodeProperties.getModifier(left);
        Object lhs = mod.prepare(this, context);

        // Perform the operation
        Object result = InterpreterUtilities.multiply(
            NodeProperties.getType(node),
            lhs,
            node.getRightExpression().acceptVisitor(this));

        // Cast the result
        result = performCast(NodeProperties.getType(left), result);

        // Modify the variable and return
        mod.modify(context, result);

        return result;*/
    }

    /**
     * Visits a DivideExpression
     * @param node the node to visit
     */
    public Object visit(DivideExpression node) {
        Class c = NodeProperties.getType(node);
        long dividecounter=counter++;
        long auxcounter=counter;

        Code.write(""+Code.BEGIN+Code.DELIM+Code.DE+Code.DELIM+dividecounter+
                    Code.DELIM+locationToString(node));
        Code.write(""+Code.LEFT+Code.DELIM+counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2=counter;

        Code.write(""+Code.RIGHT+Code.DELIM+counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.divide(c, lobj, robj);

        Code.write(""+Code.DE+Code.DELIM+dividecounter+Code.DELIM+auxcounter+
            Code.DELIM+auxcounter2+Code.DELIM+o.toString()+
            Code.DELIM+NodeProperties.getType(node).getName()+
            Code.DELIM+locationToString(node));
        return o;

    /*
        if (node.hasProperty(NodeProperties.VALUE)) {
    // The expression is constant
    return node.getProperty(NodeProperties.VALUE);
        } else {
    return InterpreterUtilities.divide(
    NodeProperties.getType(node),
    node.getLeftExpression().acceptVisitor(this),
    node.getRightExpression().acceptVisitor(this));
    }*/
    }

    /**
     * Visits an DivideAssignExpression
     * @param node the node to visit
     */
    public Object visit(DivideAssignExpression node) {
        long assigncounter=counter++;
        long assignauxcounter=counter;
        //Start assignment
        Code.write("" + Code.BEGIN+Code.DELIM+Code.A+Code.DELIM+assigncounter+Code.DELIM+locationToString(node)); //
        //Start divide expression
        long divcounter=counter++;
        long auxcounter=counter;

        Code.write(""+Code.BEGIN+Code.DELIM+Code.DE+Code.DELIM+divcounter+
                    Code.DELIM+locationToString(node));
        // Get left hand side for the divide expression
        Code.write(""+Code.LEFT+Code.DELIM+counter);

        Node   left = node.getLeftExpression();
        evaluating=false;
        left.acceptVisitor(this);
        evaluating=true;

        LeftHandSideModifier mod = NodeProperties.getModifier(left);
        Object lhs = mod.prepare(this, context);
        long auxcounter2=counter;

        // Get right hand side for the divide expression
        Code.write(""+Code.RIGHT+Code.DELIM+counter);
        Object val  = node.getRightExpression().acceptVisitor(this);


        // Perform the operation
        Object result = InterpreterUtilities.divide(
            NodeProperties.getType(node),
            lhs,
	    val);

        // Cast the result
        result = performCast(NodeProperties.getType(left), result);

        Code.write(""+Code.DE+Code.DELIM+divcounter+Code.DELIM+auxcounter+
            Code.DELIM+auxcounter2+Code.DELIM+result.toString()+
            Code.DELIM+NodeProperties.getType(node).getName()+
            Code.DELIM+locationToString(node));

        long assignauxcounter2=counter;
        Code.write("" + Code.TO+Code.DELIM+counter);
        evaluating=false;
        left.acceptVisitor(this);
        evaluating=true;


        // Modify the variable and return
        mod.modify(context, result);
        Code.write("" + Code.A+Code.DELIM+assigncounter+Code.DELIM+assignauxcounter+
                   Code.DELIM+assignauxcounter2+Code.DELIM+result.toString()+
                   Code.DELIM+NodeProperties.getType(node).getName()+
                   Code.DELIM+locationToString(node));


        return result;
        /*
        Node   left = node.getLeftExpression();
        LeftHandSideModifier mod = NodeProperties.getModifier(left);
        Object lhs = mod.prepare(this, context);

        // Perform the operation
        Object result = InterpreterUtilities.divide(
            NodeProperties.getType(node),
            lhs,
            node.getRightExpression().acceptVisitor(this));

        // Cast the result
        result = performCast(NodeProperties.getType(left), result);

        // Modify the variable and return
        mod.modify(context, result);

        return result;*/
    }

    /**
     * Visits a RemainderExpression
     * @param node the node to visit
     */
    public Object visit(RemainderExpression node) {

        Class c = NodeProperties.getType(node);
        long remaindercounter=counter++;
        long auxcounter=counter;

        Code.write(""+Code.BEGIN+Code.DELIM+Code.RE+Code.DELIM+remaindercounter+
                    Code.DELIM+locationToString(node));
        Code.write(""+Code.LEFT+Code.DELIM+counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2=counter;

        Code.write(""+Code.RIGHT+Code.DELIM+counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.divide(c, lobj, robj);

        Code.write(""+Code.RE+Code.DELIM+remaindercounter+Code.DELIM+auxcounter+
            Code.DELIM+auxcounter2+Code.DELIM+o.toString()+
            Code.DELIM+NodeProperties.getType(node).getName()+
            Code.DELIM+locationToString(node));
        return o;
    /*
        if (node.hasProperty(NodeProperties.VALUE)) {

            // The expression is constant
            return node.getProperty(NodeProperties.VALUE);
        } else {
            return InterpreterUtilities.remainder(
                NodeProperties.getType(node),
                node.getLeftExpression().acceptVisitor(this),
                node.getRightExpression().acceptVisitor(this));
        }*/
    }

    /**
     * Visits an RemainderAssignExpression
     * @param node the node to visit
     */
    public Object visit(RemainderAssignExpression node) {

        Node   left = node.getLeftExpression();
        LeftHandSideModifier mod = NodeProperties.getModifier(left);
        Object lhs = mod.prepare(this, context);

        // Perform the operation
        Object result = InterpreterUtilities.remainder(
            NodeProperties.getType(node),
            lhs,
            node.getRightExpression().acceptVisitor(this));

        // Cast the result
        result = performCast(NodeProperties.getType(left), result);

        // Modify the variable and return
        mod.modify(context, result);
        return result;
    }

    /**
     * Visits an EqualExpression
     * @param node the node to visit
     */
    public Object visit(EqualExpression node) {

        long eecounter=counter++;
        long auxcounter=counter;

        Code.write(""+Code.BEGIN+Code.DELIM+Code.EE+Code.DELIM+eecounter+
                      Code.DELIM+locationToString(node));
        Code.write(""+Code.LEFT+Code.DELIM+counter);

        Node leftexp=node.getLeftExpression();
        Object lobj = leftexp.acceptVisitor(this);
        long auxcounter2=counter;

        Code.write(""+Code.RIGHT+Code.DELIM+counter);

        Node rightexp=node.getRightExpression();
        Object robj = rightexp.acceptVisitor(this);
        Object o = InterpreterUtilities.equalTo(NodeProperties.getType(leftexp),
                                                NodeProperties.getType(rightexp),lobj, robj);

        Code.write(""+Code.EE+Code.DELIM+eecounter+Code.DELIM+auxcounter+
                   Code.DELIM+auxcounter2+Code.DELIM+o.toString()+
                   Code.DELIM+NodeProperties.getType(node).getName()+
                   Code.DELIM+locationToString(node));

        return o;
    }

    /**
     * Visits a NotEqualExpression
     * @param node the node to visit
     */
    public Object visit(NotEqualExpression node) {

        long necounter=counter++;
        long auxcounter=counter;

        Code.write(""+Code.BEGIN+Code.DELIM+Code.NE+Code.DELIM+necounter+
                      Code.DELIM+locationToString(node));
        Code.write(""+Code.LEFT+Code.DELIM+counter);

        Node leftexp=node.getLeftExpression();
        Object lobj = leftexp.acceptVisitor(this);
        long auxcounter2=counter;

        Code.write(""+Code.RIGHT+Code.DELIM+counter);

        Node rightexp=node.getRightExpression();
        Object robj = rightexp.acceptVisitor(this);
        Object o = InterpreterUtilities.notEqualTo(NodeProperties.getType(leftexp),
                        NodeProperties.getType(rightexp),lobj, robj);

        Code.write(""+Code.NE+Code.DELIM+necounter+Code.DELIM+auxcounter+
                   Code.DELIM+auxcounter2+Code.DELIM+o.toString()+
                   Code.DELIM+NodeProperties.getType(node).getName()+
                   Code.DELIM+locationToString(node));

        return o;
    }

    /**
     * Visits a LessExpression
     * @param node the node to visit
     */
    public Object visit(LessExpression node) {

        long lecounter=counter++;
        long auxcounter=counter;

        Code.write(""+Code.BEGIN+Code.DELIM+Code.LE+Code.DELIM+lecounter+
                      Code.DELIM+locationToString(node));
        Code.write(""+Code.LEFT+Code.DELIM+counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2=counter;

        Code.write(""+Code.RIGHT+Code.DELIM+counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.lessThan(lobj, robj);

        Code.write(""+Code.LE+Code.DELIM+lecounter+Code.DELIM+auxcounter+
                   Code.DELIM+auxcounter2+Code.DELIM+o.toString()+
                   Code.DELIM+NodeProperties.getType(node).getName()+
                   Code.DELIM+locationToString(node));

        return o;
    }

    /**
     * Visits a LessOrEqualExpression
     * @param node the node to visit
     */
    public Object visit(LessOrEqualExpression node) {

        long lqecounter=counter++;
        long auxcounter=counter;

        Code.write(""+Code.BEGIN+Code.DELIM+Code.LQE+Code.DELIM+lqecounter+
                   Code.DELIM+locationToString(node));
        Code.write(""+Code.LEFT+Code.DELIM+counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2=counter;

        Code.write(""+Code.RIGHT+Code.DELIM+counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.lessOrEqual(lobj, robj);

        Code.write(""+Code.LQE+Code.DELIM+lqecounter+Code.DELIM+auxcounter+
                   Code.DELIM+auxcounter2+Code.DELIM+o.toString()+
                   Code.DELIM+NodeProperties.getType(node).getName()+
                   Code.DELIM+locationToString(node));

        return o;
    }

    /**
     * Visits a GreaterExpression
     * @param node the node to visit
     */

    public Object visit(GreaterExpression node) {

        long gtcounter=counter++;
        long auxcounter=counter;

        Code.write(""+Code.BEGIN+Code.DELIM+Code.GT+Code.DELIM+gtcounter+
                      Code.DELIM+locationToString(node));
        Code.write(""+Code.LEFT+Code.DELIM+counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2=counter;

        Code.write(""+Code.RIGHT+Code.DELIM+counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.greaterThan(lobj, robj);

        Code.write(""+Code.GT+Code.DELIM+gtcounter+Code.DELIM+auxcounter+
                   Code.DELIM+auxcounter2+Code.DELIM+o.toString()+
                   Code.DELIM+NodeProperties.getType(node).getName()+
                   Code.DELIM+locationToString(node));

        return o;
    }



    /**
     * Visits a GreaterOrEqualExpression
     * @param node the node to visit
     */
    public Object visit(GreaterOrEqualExpression node) {
        long gqtcounter=counter++;
        long auxcounter=counter;

        Code.write(""+Code.BEGIN+Code.DELIM+Code.GQT+Code.DELIM+gqtcounter+
                      Code.DELIM+locationToString(node));
        Code.write(""+Code.LEFT+Code.DELIM+counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2=counter;

        Code.write(""+Code.RIGHT+Code.DELIM+counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.greaterOrEqual(lobj, robj);

        Code.write(""+Code.GQT+Code.DELIM+gqtcounter+Code.DELIM+auxcounter+
                   Code.DELIM+auxcounter2+Code.DELIM+o.toString()+
                   Code.DELIM+NodeProperties.getType(node).getName()+
                   Code.DELIM+locationToString(node));

        return o;
    }


    /**
     * Visits a InstanceOfExpression
     * @param node the node to visit
     */
    public Object visit(InstanceOfExpression node) {
        Object v = node.getExpression().acceptVisitor(this);
        Class  c = NodeProperties.getType(node.getReferenceType());

        return (c.isInstance(v)) ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * Visits a ConditionalExpression
     * @param node the node to visit
     */
    public Object visit(ConditionalExpression node) {
        if (node.hasProperty(NodeProperties.VALUE)) {
            // The expression is constant
            return node.getProperty(NodeProperties.VALUE);
        } else {
            Boolean b = (Boolean)node.getConditionExpression().acceptVisitor(this);
            if (b.booleanValue()) {
                return node.getIfTrueExpression().acceptVisitor(this);
            } else {
                return node.getIfFalseExpression().acceptVisitor(this);
            }
        }
    }

    /**
     * Visits a PostIncrement
     * @param node the node to visit
     */
    public Object visit(PostIncrement node) {

        long postinccounter=counter++;

        Code.write("" + Code.BEGIN+Code.DELIM+Code.PIE+Code.DELIM+postinccounter+Code.DELIM+locationToString(node)); //

        Node exp = node.getExpression();
        long auxcounter=counter;

        exp.acceptVisitor(this);

        LeftHandSideModifier mod = NodeProperties.getModifier(exp);
        Object v = mod.prepare(this, context);
        Object result=  InterpreterUtilities.add(NodeProperties.getType(node),
                                                 v,
                                                 InterpreterUtilities.ONE);

        mod.modify(context,result);

        Code.write("" + Code.PIE+Code.DELIM+postinccounter+Code.DELIM+auxcounter+
                        Code.DELIM+result.toString()+
                        Code.DELIM+NodeProperties.getType(node).getName()+
                        Code.DELIM+locationToString(node));

        return v;
    }

    /**
     * Visits a PreIncrement
     * @param node the node to visit
     */
    public Object visit(PreIncrement node) {
        long preinccounter=counter++;
        Code.write("" + Code.BEGIN+Code.DELIM+Code.PRIE+Code.DELIM+preinccounter+Code.DELIM+locationToString(node)); //

        Node exp = node.getExpression();
        long auxcounter=counter;
        exp.acceptVisitor(this);
        LeftHandSideModifier mod = NodeProperties.getModifier(exp);
        Object v = mod.prepare(this, context);
        mod.modify(context,
                   v = InterpreterUtilities.add(NodeProperties.getType(node),
                                                v,
                                                InterpreterUtilities.ONE));
        Code.write("" + Code.PRIE+Code.DELIM+preinccounter+Code.DELIM+auxcounter+
                        Code.DELIM+v.toString()+
                        Code.DELIM+NodeProperties.getType(node).getName()+
                        Code.DELIM+locationToString(node));

        return v;
    }

    /**
     * Visits a PostDecrement
     * @param node the node to visit
     */
    public Object visit(PostDecrement node) {
        long postdeccounter=counter++;
        Code.write("" + Code.BEGIN+Code.DELIM+Code.PDE+Code.DELIM+postdeccounter+Code.DELIM+locationToString(node)); //

        Node exp = node.getExpression();
        long auxcounter=counter;
        exp.acceptVisitor(this);

        LeftHandSideModifier mod = NodeProperties.getModifier(exp);
        Object v = mod.prepare(this, context);
        Object result=  InterpreterUtilities.subtract(NodeProperties.getType(node),
                                                      v,
                                                      InterpreterUtilities.ONE);

        mod.modify(context,result);
        Code.write("" + Code.PDE+Code.DELIM+postdeccounter+Code.DELIM+auxcounter+
                        Code.DELIM+result.toString()+
                        Code.DELIM+NodeProperties.getType(node).getName()+
                        Code.DELIM+locationToString(node));

        return v;
    }

    /**
     * Visits a PreDecrement
     * @param node the node to visit
     */
    public Object visit(PreDecrement node) {
        long predeccounter=counter++;
        Code.write("" + Code.BEGIN+Code.DELIM+Code.PRDE+Code.DELIM+predeccounter+Code.DELIM+locationToString(node)); //

        Node exp = node.getExpression();
        long auxcounter=counter;
        exp.acceptVisitor(this);
        LeftHandSideModifier mod = NodeProperties.getModifier(exp);
        Object v = mod.prepare(this, context);

        mod.modify(context,
                    v = InterpreterUtilities.subtract(NodeProperties.getType(node),
                    v,
                    InterpreterUtilities.ONE));
        Code.write("" + Code.PRDE+Code.DELIM+predeccounter+Code.DELIM+auxcounter+
                        Code.DELIM+v.toString()+
                        Code.DELIM+NodeProperties.getType(node).getName()+
                        Code.DELIM+locationToString(node));
        return v;
    }

    /**
     * Visits a CastExpression
     * @param node the node to visit
     */
    public Object visit(CastExpression node) {
        return performCast(NodeProperties.getType(node),
               node.getExpression().acceptVisitor(this));
    }

    /**
     * Visits a BitAndExpression
     * @param node the node to visit
     */
    public Object visit(BitAndExpression node) {
        Class c = NodeProperties.getType(node);
        long bitAndcounter=counter++;
        long auxcounter=counter;
        int expression;
        if(NodeProperties.getType(node).getName()=="boolean"){
            expression=Code.AND;
        }
        else expression=Code.BITAND;

        Code.write(""+Code.BEGIN+Code.DELIM+expression+Code.DELIM+bitAndcounter+
                    Code.DELIM+locationToString(node));
        Code.write(""+Code.LEFT+Code.DELIM+counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2=counter;

        Code.write(""+Code.RIGHT+Code.DELIM+counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.bitAnd(c, lobj, robj);

        Code.write(""+expression+Code.DELIM+bitAndcounter+Code.DELIM+auxcounter+
            Code.DELIM+auxcounter2+Code.DELIM+o.toString()+
            Code.DELIM+NodeProperties.getType(node).getName()+
            Code.DELIM+locationToString(node));
        return o;
        /*

        if (node.hasProperty(NodeProperties.VALUE)) {
            // The expression is constant
            return node.getProperty(NodeProperties.VALUE);
        } else {
            return InterpreterUtilities.bitAnd(
                NodeProperties.getType(node),
                node.getLeftExpression().acceptVisitor(this),
                node.getRightExpression().acceptVisitor(this));
                }*/
    }

    /**
     * Visits a BitAndAssignExpression
     * @param node the node to visit
     */
    public Object visit(BitAndAssignExpression node) {
        //Simulate that a&=b -->> a= a&b
        //Added 2 evaluations to left hand side ...
        long assigncounter=counter++;
        long assignauxcounter=counter;
        //Start assignment
        Code.write("" + Code.BEGIN+Code.DELIM+Code.A+Code.DELIM+assigncounter+Code.DELIM+locationToString(node)); //
        //Start bit and expression
        long bitAndcounter=counter++;
        long auxcounter=counter;
        int expression;
        if(NodeProperties.getType(node).getName()=="boolean"){
            expression=Code.AND;
        }
        else expression=Code.BITAND;

        Code.write(""+Code.BEGIN+Code.DELIM+expression+Code.DELIM+bitAndcounter+
                    Code.DELIM+locationToString(node));
        // Get left hand side for the bitand expression
        Code.write(""+Code.LEFT+Code.DELIM+counter);

        Node   left = node.getLeftExpression();
        evaluating=false;
        left.acceptVisitor(this);
        evaluating=true;

        LeftHandSideModifier mod = NodeProperties.getModifier(left);
        Object lhs = mod.prepare(this, context);
        long auxcounter2=counter;

        // Get right hand side for the bitand expression
        Code.write(""+Code.RIGHT+Code.DELIM+counter);
        Object val  = node.getRightExpression().acceptVisitor(this);


        // Perform the operation
        Object result = InterpreterUtilities.bitAnd(
            NodeProperties.getType(node),
            lhs,
	    val);


        // Cast the result
        result = performCast(NodeProperties.getType(left), result);

        Code.write(""+expression+Code.DELIM+bitAndcounter+Code.DELIM+auxcounter+
            Code.DELIM+auxcounter2+Code.DELIM+result.toString()+
            Code.DELIM+NodeProperties.getType(node).getName()+
            Code.DELIM+locationToString(node));

        long assignauxcounter2=counter;
        Code.write("" + Code.TO+Code.DELIM+counter);
        evaluating=false;
        left.acceptVisitor(this);
        evaluating=true;


        // Modify the variable and return
        mod.modify(context, result);
        Code.write("" + Code.A+Code.DELIM+assigncounter+Code.DELIM+assignauxcounter+
                   Code.DELIM+assignauxcounter2+Code.DELIM+result.toString()+
                   Code.DELIM+NodeProperties.getType(node).getName()+
                   Code.DELIM+locationToString(node));


        return result;
    }

    /**
     * Visits a ExclusiveOrExpression
     * @param node the node to visit
     */
    public Object visit(ExclusiveOrExpression node) {
        Class c = NodeProperties.getType(node);
        long xOrcounter=counter++;
        long auxcounter=counter;

        int expression;
        if(NodeProperties.getType(node).getName()=="boolean"){
            expression=Code.XOR;
        }
        else expression=Code.BITXOR;

        Code.write(""+Code.BEGIN+Code.DELIM+expression+Code.DELIM+xOrcounter+
                    Code.DELIM+locationToString(node));
        Code.write(""+Code.LEFT+Code.DELIM+counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2=counter;

        Code.write(""+Code.RIGHT+Code.DELIM+counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.xOr(c, lobj, robj);

        Code.write(""+expression+Code.DELIM+xOrcounter+Code.DELIM+auxcounter+
                   Code.DELIM+auxcounter2+Code.DELIM+o.toString()+
                   Code.DELIM+NodeProperties.getType(node).getName()+
                   Code.DELIM+locationToString(node));
        return o;
    }

    /**
     * Visits a ExclusiveOrAssignExpression
     * @param node the node to visit
     */
    public Object visit(ExclusiveOrAssignExpression node) {
        //Simulate that a^=b -->> a= a^b
        //Added 2 evaluations to left hand side ...
        long assigncounter=counter++;
        long assignauxcounter=counter;
        //Start assignment
        Code.write("" + Code.BEGIN+Code.DELIM+Code.A+Code.DELIM+assigncounter+Code.DELIM+locationToString(node)); //
        //Start bit and expression
        long xOrcounter=counter++;
        long auxcounter=counter;
	int expression;
        if(NodeProperties.getType(node).getName()=="boolean"){
            expression=Code.XOR;
        }
        else expression=Code.BITXOR;

        Code.write(""+Code.BEGIN+Code.DELIM+expression+Code.DELIM+xOrcounter+
                    Code.DELIM+locationToString(node));
        // Get left hand side for the bitand expression
        Code.write(""+Code.LEFT+Code.DELIM+counter);

        Node   left = node.getLeftExpression();
        evaluating=false;
        left.acceptVisitor(this);
        evaluating=true;

        LeftHandSideModifier mod = NodeProperties.getModifier(left);
        Object lhs = mod.prepare(this, context);
        long auxcounter2=counter;

        // Get right hand side for the XOR expression
        Code.write(""+Code.RIGHT+Code.DELIM+counter);
        Object val  = node.getRightExpression().acceptVisitor(this);


        // Perform the operation
        Object result = InterpreterUtilities.xOr(
            NodeProperties.getType(node),
            lhs,
	    val);

        // Cast the result
        result = performCast(NodeProperties.getType(left), result);

        Code.write(""+expression+Code.DELIM+xOrcounter+Code.DELIM+auxcounter+
            Code.DELIM+auxcounter2+Code.DELIM+result.toString()+
            Code.DELIM+NodeProperties.getType(node).getName()+
            Code.DELIM+locationToString(node));

        long assignauxcounter2=counter;
        Code.write("" + Code.TO+Code.DELIM+counter);
        evaluating=false;
        left.acceptVisitor(this);
        evaluating=true;


        // Modify the variable and return
        mod.modify(context, result);
        Code.write("" + Code.A+Code.DELIM+assigncounter+Code.DELIM+assignauxcounter+
                   Code.DELIM+assignauxcounter2+Code.DELIM+result.toString()+
                   Code.DELIM+NodeProperties.getType(node).getName()+
                   Code.DELIM+locationToString(node));


        return result;
    }

    /**
     * Visits a BitOrExpression
     * @param node the node to visit
     */
    public Object visit(BitOrExpression node) {
        Class c = NodeProperties.getType(node);
        long bitOrcounter=counter++;
        long auxcounter=counter;
        int expression;

        if(NodeProperties.getType(node).getName()=="boolean"){
            expression=Code.OR;

        }
        else {
            expression=Code.BITOR;
        }
        Code.write(""+Code.BEGIN+Code.DELIM+expression+Code.DELIM+bitOrcounter+
                    Code.DELIM+locationToString(node));
        Code.write(""+Code.LEFT+Code.DELIM+counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2=counter;

        Code.write(""+Code.RIGHT+Code.DELIM+counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.bitOr(c, lobj, robj);

        Code.write(""+expression+Code.DELIM+bitOrcounter+Code.DELIM+auxcounter+
            Code.DELIM+auxcounter2+Code.DELIM+o.toString()+
            Code.DELIM+NodeProperties.getType(node).getName()+
            Code.DELIM+locationToString(node));
        return o;
    }

    /**
     * Visits a BitOrAssignExpression
     * @param node the node to visit
     */
    public Object visit(BitOrAssignExpression node) {
        //Simulate that a|=b -->> a= a|b
        //Added 2 evaluations to left hand side ...
        long assigncounter=counter++;
        long assignauxcounter=counter;
        //Start assignment
        Code.write("" + Code.BEGIN+Code.DELIM+Code.A+Code.DELIM+assigncounter+Code.DELIM+locationToString(node)); //
        //Start bitor expression
        long bitOrcounter=counter++;
        long auxcounter=counter;
        int expression;

        if(NodeProperties.getType(node).getName()=="boolean"){
            expression=Code.OR;

        }
        else {
            expression=Code.BITOR;
        }

        Code.write(""+Code.BEGIN+Code.DELIM+expression+Code.DELIM+bitOrcounter+
                    Code.DELIM+locationToString(node));
        // Get left hand side for the bitor expression
        Code.write(""+Code.LEFT+Code.DELIM+counter);

        Node   left = node.getLeftExpression();
        evaluating=false;
        left.acceptVisitor(this);
        evaluating=true;

        LeftHandSideModifier mod = NodeProperties.getModifier(left);
        Object lhs = mod.prepare(this, context);
        long auxcounter2=counter;

        // Get right hand side for the bitor expression
        Code.write(""+Code.RIGHT+Code.DELIM+counter);
        Object val  = node.getRightExpression().acceptVisitor(this);


        // Perform the operation
        Object result = InterpreterUtilities.bitOr(
            NodeProperties.getType(node),
            lhs,
	    val);

        // Cast the result
        result = performCast(NodeProperties.getType(left), result);

        Code.write(""+expression+Code.DELIM+bitOrcounter+Code.DELIM+auxcounter+
            Code.DELIM+auxcounter2+Code.DELIM+result.toString()+
            Code.DELIM+NodeProperties.getType(node).getName()+
            Code.DELIM+locationToString(node));

        long assignauxcounter2=counter;
        Code.write("" + Code.TO+Code.DELIM+counter);
        evaluating=false;
        left.acceptVisitor(this);
        evaluating=true;


        // Modify the variable and return
        mod.modify(context, result);
        Code.write("" + Code.A+Code.DELIM+assigncounter+Code.DELIM+assignauxcounter+
                   Code.DELIM+assignauxcounter2+Code.DELIM+result.toString()+
                   Code.DELIM+NodeProperties.getType(node).getName()+
                   Code.DELIM+locationToString(node));


        return result;
    }

    /**
     * Visits a ShiftLeftExpression
     * @param node the node to visit
     */
    public Object visit(ShiftLeftExpression node) {
        Class c = NodeProperties.getType(node);
        long shiftcounter=counter++;
        long auxcounter=counter;

        Code.write(""+Code.BEGIN+Code.DELIM+Code.LSHIFT+Code.DELIM+shiftcounter+
                    Code.DELIM+locationToString(node));
        Code.write(""+Code.LEFT+Code.DELIM+counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2=counter;

        Code.write(""+Code.RIGHT+Code.DELIM+counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.shiftLeft(c, lobj, robj);

        Code.write(""+Code.LSHIFT+Code.DELIM+shiftcounter+Code.DELIM+auxcounter+
            Code.DELIM+auxcounter2+Code.DELIM+o.toString()+
            Code.DELIM+NodeProperties.getType(node).getName()+
            Code.DELIM+locationToString(node));
        return o;
    }
        /*
        if (node.hasProperty(NodeProperties.VALUE)) {
            // The expression is constant
            return node.getProperty(NodeProperties.VALUE);
        } else {
            return InterpreterUtilities.shiftLeft(
                NodeProperties.getType(node),
                node.getLeftExpression().acceptVisitor(this),
                node.getRightExpression().acceptVisitor(this));
        }
    }
        */
    /**
     * Visits a ShiftLeftAssignExpression
     * @param node the node to visit
     */
    public Object visit(ShiftLeftAssignExpression node) {
        //Simulate that a<<=b -->> a= a<<b
        //Added 2 evaluations to left hand side ...
        long assigncounter=counter++;
        long assignauxcounter=counter;
        //Start assignment
        Code.write("" + Code.BEGIN+Code.DELIM+Code.A+Code.DELIM+assigncounter+Code.DELIM+locationToString(node)); //
        //Start shift expression
        long shiftcounter=counter++;
        long auxcounter=counter;

        Code.write(""+Code.BEGIN+Code.DELIM+Code.LSHIFT+Code.DELIM+shiftcounter+
                    Code.DELIM+locationToString(node));
        // Get left hand side for the shift expression
        Code.write(""+Code.LEFT+Code.DELIM+counter);

        Node   left = node.getLeftExpression();
        evaluating=false;
        left.acceptVisitor(this);
        evaluating=true;

        LeftHandSideModifier mod = NodeProperties.getModifier(left);
        Object lhs = mod.prepare(this, context);
        long auxcounter2=counter;

        // Get right hand side for the shift expression
        Code.write(""+Code.RIGHT+Code.DELIM+counter);
        Object val  = node.getRightExpression().acceptVisitor(this);


        // Perform the operation
        Object result = InterpreterUtilities.shiftLeft(
                                                 NodeProperties.getType(node),
                                                 lhs,
						 val);

        // Cast the result
        result = performCast(NodeProperties.getType(left), result);

        Code.write(""+Code.LSHIFT+Code.DELIM+shiftcounter+Code.DELIM+auxcounter+
            Code.DELIM+auxcounter2+Code.DELIM+result.toString()+
            Code.DELIM+NodeProperties.getType(node).getName()+
            Code.DELIM+locationToString(node));

        long assignauxcounter2=counter;
        Code.write("" + Code.TO+Code.DELIM+counter);
        evaluating=false;
        left.acceptVisitor(this);
        evaluating=true;


        // Modify the variable and return
        mod.modify(context, result);
        Code.write("" + Code.A+Code.DELIM+assigncounter+Code.DELIM+assignauxcounter+
                   Code.DELIM+assignauxcounter2+Code.DELIM+result.toString()+
                   Code.DELIM+NodeProperties.getType(node).getName()+
                   Code.DELIM+locationToString(node));


        return result;
    }

    /**
     * Visits a ShiftRightExpression
     * @param node the node to visit
     */
    public Object visit(ShiftRightExpression node) {
        Class c = NodeProperties.getType(node);
        long shiftcounter=counter++;
        long auxcounter=counter;

        Code.write(""+Code.BEGIN+Code.DELIM+Code.RSHIFT+Code.DELIM+shiftcounter+
                    Code.DELIM+locationToString(node));
        Code.write(""+Code.LEFT+Code.DELIM+counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2=counter;

        Code.write(""+Code.RIGHT+Code.DELIM+counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.shiftRight(c, lobj, robj);

        Code.write(""+Code.RSHIFT+Code.DELIM+shiftcounter+Code.DELIM+auxcounter+
            Code.DELIM+auxcounter2+Code.DELIM+o.toString()+
            Code.DELIM+NodeProperties.getType(node).getName()+
            Code.DELIM+locationToString(node));
        return o;
        /*
        if (node.hasProperty(NodeProperties.VALUE)) {
            // The expression is constant
            return node.getProperty(NodeProperties.VALUE);
        } else {
            return InterpreterUtilities.shiftRight(
                NodeProperties.getType(node),
                node.getLeftExpression().acceptVisitor(this),
                node.getRightExpression().acceptVisitor(this));
                }*/
    }

    /**
     * Visits a ShiftRightAssignExpression
     * @param node the node to visit
     */
    public Object visit(ShiftRightAssignExpression node) {
        //Simulate that a>>=b -->> a= a>>b
        //Added 2 evaluations to left hand side ...
        long assigncounter=counter++;
        long assignauxcounter=counter;
        //Start assignment
        Code.write("" + Code.BEGIN+Code.DELIM+Code.A+Code.DELIM+assigncounter+Code.DELIM+locationToString(node)); //
        //Start shift expression
        long shiftcounter=counter++;
        long auxcounter=counter;

        Code.write(""+Code.BEGIN+Code.DELIM+Code.RSHIFT+Code.DELIM+shiftcounter+
                    Code.DELIM+locationToString(node));
        // Get left hand side for the shift expression
        Code.write(""+Code.LEFT+Code.DELIM+counter);

        Node   left = node.getLeftExpression();
        evaluating=false;
        left.acceptVisitor(this);
        evaluating=true;

        LeftHandSideModifier mod = NodeProperties.getModifier(left);
        Object lhs = mod.prepare(this, context);
        long auxcounter2=counter;

        // Get right hand side for the shift expression
        Code.write(""+Code.RIGHT+Code.DELIM+counter);
        Object val  = node.getRightExpression().acceptVisitor(this);


        // Perform the operation
        Object result = InterpreterUtilities.shiftRight(
                                                 NodeProperties.getType(node),
                                                 lhs,
						 val);                                                

        // Cast the result
        result = performCast(NodeProperties.getType(left), result);

        Code.write(""+Code.RSHIFT+Code.DELIM+shiftcounter+Code.DELIM+auxcounter+
            Code.DELIM+auxcounter2+Code.DELIM+result.toString()+
            Code.DELIM+NodeProperties.getType(node).getName()+
            Code.DELIM+locationToString(node));

        long assignauxcounter2=counter;
        Code.write("" + Code.TO+Code.DELIM+counter);
        evaluating=false;
        left.acceptVisitor(this);
        evaluating=true;


        // Modify the variable and return
        mod.modify(context, result);
        Code.write("" + Code.A+Code.DELIM+assigncounter+Code.DELIM+assignauxcounter+
                   Code.DELIM+assignauxcounter2+Code.DELIM+result.toString()+
                   Code.DELIM+NodeProperties.getType(node).getName()+
                   Code.DELIM+locationToString(node));


        return result;
    }

    /**
     * Visits a UnsignedShiftRightExpression
     * @param node the node to visit
     */
    public Object visit(UnsignedShiftRightExpression node) {
        Class c = NodeProperties.getType(node);
        long shiftcounter=counter++;
        long auxcounter=counter;

        Code.write(""+Code.BEGIN+Code.DELIM+Code.URSHIFT+Code.DELIM+shiftcounter+
                    Code.DELIM+locationToString(node));
        Code.write(""+Code.LEFT+Code.DELIM+counter);

        Object lobj = node.getLeftExpression().acceptVisitor(this);
        long auxcounter2=counter;

        Code.write(""+Code.RIGHT+Code.DELIM+counter);

        Object robj = node.getRightExpression().acceptVisitor(this);
        Object o = InterpreterUtilities.unsignedShiftRight(c, lobj, robj);

        Code.write(""+Code.URSHIFT+Code.DELIM+shiftcounter+Code.DELIM+auxcounter+
            Code.DELIM+auxcounter2+Code.DELIM+o.toString()+
            Code.DELIM+NodeProperties.getType(node).getName()+
            Code.DELIM+locationToString(node));
        return o;
        /*
        if (node.hasProperty(NodeProperties.VALUE)) {
            // The expression is constant
            return node.getProperty(NodeProperties.VALUE);
        } else {
            return InterpreterUtilities.unsignedShiftRight(
                NodeProperties.getType(node),
                node.getLeftExpression().acceptVisitor(this),
                node.getRightExpression().acceptVisitor(this));
        }
        */
    }

    /**
     * Visits a UnsignedShiftRightAssignExpression
     * @param node the node to visit
     */
    public Object visit(UnsignedShiftRightAssignExpression node) {
        //Simulate that a>>>=b -->> a= a>>>b
        //Added 2 evaluations to left hand side ...
        long assigncounter=counter++;
        long assignauxcounter=counter;
        //Start assignment
        Code.write("" + Code.BEGIN+Code.DELIM+Code.A+Code.DELIM+assigncounter+Code.DELIM+locationToString(node)); //
        //Start shift expression
        long shiftcounter=counter++;
        long auxcounter=counter;

        Code.write(""+Code.BEGIN+Code.DELIM+Code.URSHIFT+Code.DELIM+shiftcounter+
                    Code.DELIM+locationToString(node));
        // Get left hand side for the shift expression
        Code.write(""+Code.LEFT+Code.DELIM+counter);

        Node   left = node.getLeftExpression();
        evaluating=false;
        left.acceptVisitor(this);
        evaluating=true;

        LeftHandSideModifier mod = NodeProperties.getModifier(left);
        Object lhs = mod.prepare(this, context);
        long auxcounter2=counter;

        // Get right hand side for the shift expression
        Code.write(""+Code.RIGHT+Code.DELIM+counter);
        Object val  = node.getRightExpression().acceptVisitor(this);


        // Perform the operation
        Object result = InterpreterUtilities.unsignedShiftRight(
                                                 NodeProperties.getType(node),
                                                 lhs,
						 val);

        // Cast the result
        result = performCast(NodeProperties.getType(left), result);

        Code.write(""+Code.URSHIFT+Code.DELIM+shiftcounter+Code.DELIM+auxcounter+
            Code.DELIM+auxcounter2+Code.DELIM+result.toString()+
            Code.DELIM+NodeProperties.getType(node).getName()+
            Code.DELIM+locationToString(node));

        long assignauxcounter2=counter;
        Code.write("" + Code.TO+Code.DELIM+counter);
        evaluating=false;
        left.acceptVisitor(this);
        evaluating=true;


        // Modify the variable and return
        mod.modify(context, result);
        Code.write("" + Code.A+Code.DELIM+assigncounter+Code.DELIM+assignauxcounter+
                   Code.DELIM+assignauxcounter2+Code.DELIM+result.toString()+
                   Code.DELIM+NodeProperties.getType(node).getName()+
                   Code.DELIM+locationToString(node));


        return result;
    }
    /**
     * Visits an AndExpression
     * @param node the node to visit
     */
    public Object visit(AndExpression node) {
        long andcounter=counter++;
        long auxcounter=counter;
        Code.write(""+Code.BEGIN+Code.DELIM+Code.AND+Code.DELIM+andcounter+
                      Code.DELIM+locationToString(node));
        Code.write(""+Code.LEFT+Code.DELIM+counter);
        boolean left =((Boolean)node.getLeftExpression().acceptVisitor(this)).booleanValue();
        long auxcounter2=counter;
        Code.write(""+Code.RIGHT+Code.DELIM+counter);
        boolean right = ((Boolean)node.getRightExpression().acceptVisitor(this)).booleanValue();
        if (left &&  right) {

            Code.write(""+Code.AND+Code.DELIM+andcounter+Code.DELIM+auxcounter+
		       Code.DELIM+auxcounter2+Code.DELIM+Code.TRUE+
                       Code.DELIM+NodeProperties.getType(node).getName()+
		       Code.DELIM+locationToString(node));

            return Boolean.TRUE;

        } else {

            Code.write(""+Code.AND+Code.DELIM+andcounter+Code.DELIM+auxcounter+
                       Code.DELIM+auxcounter2+Code.DELIM+Code.FALSE+
                       Code.DELIM+NodeProperties.getType(node).getName()+
                       Code.DELIM+locationToString(node));

            return Boolean.FALSE;
        }
    }



    /**
     * Visits an OrExpression
     * @param node the node to visit
     */
    public Object visit(OrExpression node) {
        long orcounter=counter++;
        long auxcounter=counter;
        Code.write(""+Code.BEGIN+Code.DELIM+Code.OR+Code.DELIM+orcounter+
                      Code.DELIM+locationToString(node));
        Code.write(""+Code.LEFT+Code.DELIM+counter);
        boolean left =((Boolean)node.getLeftExpression().acceptVisitor(this)).booleanValue();
        long auxcounter2=counter;
        Code.write(""+Code.RIGHT+Code.DELIM+counter);
        boolean right = ((Boolean)node.getRightExpression().acceptVisitor(this)).booleanValue();
        if (left || right) {

            Code.write(""+Code.OR+Code.DELIM+orcounter+Code.DELIM+auxcounter+
		       Code.DELIM+auxcounter2+Code.DELIM+Code.TRUE+
		       Code.DELIM+NodeProperties.getType(node).getName()+
		       Code.DELIM+locationToString(node));

            return Boolean.TRUE;

        } else {

            Code.write(""+Code.OR+Code.DELIM+orcounter+Code.DELIM+auxcounter+
                       Code.DELIM+auxcounter2+Code.DELIM+Code.FALSE+
                       Code.DELIM+NodeProperties.getType(node).getName()+
                       Code.DELIM+locationToString(node));

            return Boolean.FALSE;
        }
    }

    /**
     * Visits a FunctionCall
     * @param node the node to visit
     */
    public Object visit(FunctionCall node) {
        MethodDeclaration md;
        md = (MethodDeclaration)node.getProperty(NodeProperties.FUNCTION);

        // Enter a new scope and define the parameters as local variables
        Context c = new GlobalContext(context.getInterpreter());
        if (node.getArguments() != null) {
            Iterator it  = md.getParameters().iterator();
            Iterator it2 = node.getArguments().iterator();
            while (it.hasNext()) {
                FormalParameter fp = (FormalParameter)it.next();
                if (fp.isFinal()) {
                    c.setConstant(fp.getName(), ((Node)it2.next()).acceptVisitor(this));
                } else {
                    c.setVariable(fp.getName(), ((Node)it2.next()).acceptVisitor(this));
                }
            }
        }

        // Do the type checking of the body if needed
        Node body = md.getBody();
        if (!body.hasProperty("visited")) {
            body.setProperty("visited", null);
            ImportationManager im =
            (ImportationManager)md.getProperty(NodeProperties.IMPORTATION_MANAGER);
            Context ctx = new GlobalContext(context.getInterpreter());
            ctx.setImportationManager(im);

            Visitor v = new NameVisitor(ctx);
            Iterator it = md.getParameters().iterator();
            while (it.hasNext()) {
                ((Node)it.next()).acceptVisitor(v);
            }
            body.acceptVisitor(v);

            ctx = new GlobalContext(context.getInterpreter());
            ctx.setImportationManager(im);
            ctx.setFunctions((List)md.getProperty(NodeProperties.FUNCTIONS));

            v = new TypeChecker(ctx);
            it = md.getParameters().iterator();
            while (it.hasNext()) {
                ((Node)it.next()).acceptVisitor(v);
            }
            body.acceptVisitor(v);
        }

        // Interpret the body of the function
        try {
            body.acceptVisitor(new EvaluationVisitor(c));
        } catch (ReturnException e) {
            return e.getValue();
        }
        return null;
    }

    /**
     * Performs a dynamic cast. This method acts on primitive wrappers.
     * @param tc the target class
     * @param o  the object to cast
     */
    private static Object performCast(Class tc, Object o) {
        Class ec = (o != null) ? o.getClass() : null;

        if (tc != ec && tc.isPrimitive() && ec != null) {
            if (tc != char.class && ec == Character.class) {
                o = new Integer(((Character)o).charValue());
            } else if (tc == byte.class) {
                o = new Byte(((Number)o).byteValue());
            } else if (tc == short.class) {
                o = new Short(((Number)o).shortValue());
            } else if (tc == int.class) {
                o = new Integer(((Number)o).intValue());
            } else if (tc == long.class) {
                o = new Long(((Number)o).longValue());
            } else if (tc == float.class) {
                o = new Float(((Number)o).floatValue());
            } else if (tc == double.class) {
                o = new Double(((Number)o).doubleValue());
            } else if (tc == char.class && ec != Character.class) {
                o = new Character((char)((Number)o).shortValue());
            }
        }
        return o;
    }
}
