/*
 * Copyright (c) 2004 Roland K�stermann. All Rights Reserved.
 */
package jeliot.mcode.input;

import jeliot.mcode.Code;
import jeliot.mcode.MCodeUtilities;
import koala.dynamicjava.tree.StaticMethodCall;

import java.io.BufferedReader;
import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: roku
 * Date: 04.08.2004
 * Time: 14:10:19
 * To change this template use File | Settings | File Templates.
 */
public abstract class InputHandler  {

    /**
     * @param aClass
     * @param counter
     * @param m
     * @param node
     * @param prompt  indivual prompt string, maybe empty or null for default value
     * @return Input Handle, may throw NoSuchMethod Exception
     */

    public Object handleInput(Class aClass, long counter, Method m, StaticMethodCall node, String prompt) {
        MCodeUtilities.write("" + Code.INPUT + Code.DELIM + (counter) + Code.DELIM
                + m.getDeclaringClass().getName() + Code.DELIM + m.getName() + Code.DELIM
                + aClass.getName() + Code.DELIM + prompt + Code.DELIM + MCodeUtilities.locationToString(node));
        return handleInput(aClass);
    }


    protected abstract Object handleInput(Class aClass);

    public abstract void setInputReader(BufferedReader in);

    public abstract boolean isIntegerInputMethod(Method m);

    public abstract boolean isDoubleInputMethod(Method m);

    public abstract boolean isLongInputMethod(Method m);

    public abstract boolean isByteInputMethod(Method m);

    public abstract boolean isCharInputMethod(Method m);

    public abstract boolean isFloatInputMethod(Method m);

    public abstract boolean isStringInputMethod(Method m);

    public abstract boolean isShortInputMethod(Method m);

}