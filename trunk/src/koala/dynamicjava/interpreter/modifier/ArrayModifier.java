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

package koala.dynamicjava.interpreter.modifier;

import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;

import koala.dynamicjava.interpreter.EvaluationVisitor;
import koala.dynamicjava.interpreter.NodeProperties;
import koala.dynamicjava.interpreter.context.Context;
import koala.dynamicjava.interpreter.error.ExecutionError;
import koala.dynamicjava.tree.ArrayAccess;
import koala.dynamicjava.tree.visitor.Visitor;

/**
 * This interface represents objets that modify an array
 *
 * @author Stephane Hillion
 * @version 1.1 - 1999/11/28
 */

public class ArrayModifier extends LeftHandSideModifier {
    /**
     * The array expression
     */
    protected ArrayAccess node;

    /**
     * The array reference
     */
    protected Object array;

    /**
     * The cell number
     */
    protected Number cell;

    /**
     * A list used to manage recursive calls
     */
    protected List arrays = new LinkedList();

    /**
     * A list used to manage recursive calls
     */
    protected List cells = new LinkedList();

    /**
     * Creates a new array modifier
     * @param node the node of that represents this array
     */
    public ArrayModifier(ArrayAccess node) {
        this.node = node;
    }

    /**
     * Prepares the modifier for modification
     */
    public Object prepare(Visitor v, Context ctx) {
        arrays.add(0, array);
        cells.add(0, cell);
        
        // Jeliot 3: Indicates to evaluation visitor not to return M-Code
        EvaluationVisitor.setPreparing(); 
        
        array = node.getExpression().acceptVisitor(v);
        Object o = node.getCellNumber().acceptVisitor(v);
        
        EvaluationVisitor.unsetPreparing();
        
        if (o instanceof Character) {
            o = new Integer(((Character) o).charValue());
        }
        cell = (Number) o;
        try {
            return Array.get(array, cell.intValue());
        } catch (ArrayIndexOutOfBoundsException e) {
            node.setProperty(NodeProperties.ERROR_STRINGS, new String[] { "" + (Array.getLength(array) - 1), "" + cell.intValue()});
            throw new ExecutionError("j3.array.index.out.of.bounds", node);
        }
    }

    /**
     * Sets the value of the underlying left hand side expression
     */
    public void modify(Context ctx, Object value) {
        try {
            Array.set(array, cell.intValue(), value);
        } catch (IllegalArgumentException e) {
            // !!! Hummm ...
            if (e.getMessage().equals("array element type mismatch")) {
                throw new ExecutionError(e.getMessage(), node);
                //throw new ArrayStoreException();
            }
            throw e;
        } catch (ArrayIndexOutOfBoundsException e) {
            node.setProperty(NodeProperties.ERROR_STRINGS, new String[] { "" + (Array.getLength(array) - 1), "" + cell.intValue()});
            throw new ExecutionError("j3.array.index.out.of.bounds", node);
        } finally {
            array = arrays.remove(0);
            cell = (Number) cells.remove(0);
        }
    }
}
