/*
 * Created on Nov 4, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package koala.dynamicjava.util;

/**
 * @author amoreno
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */


public class ParamTypes {

	 private static Class resolveType(Class c) {
	 	
	 	String type = c.getName();
	 	
        if (type.equals(boolean.class.getName())){ 

        	return Boolean.class; 
 
        } else if (type.equals(byte.class.getName())){ 
            return Byte.class; 
 
        } else if (type.equals(short.class.getName())) {

            return Short.class;
 
        } else if (type.equals(int.class.getName())) {
 
            return Integer.class;
 
        } else if (type.equals(long.class.getName())) {
 
            return Long.class;
 
        } else if (type.equals(char.class.getName())) {
 
        return Character.class;
 
        } else if (type.equals(float.class.getName())){ 
 
            return Float.class;
 
        } else if (type.equals(double.class.getName())) {
 
            return Double.class;
         } 
        else return c;
    }
	/**
	 * Compares two method's list of parameters
	 * 
	 * @param paramTypes1
	 * @param paramTypes2
	 * @return Returns true if both of them are the same, false otherewise
	 */
	public static boolean compareSignatures(Class[] paramTypes1,
			Class[] paramTypes2) {

		boolean result = false;

		if (paramTypes1.length == 0) {
			if (paramTypes2.length == 0) {

				result = true;

			}
		} else if (paramTypes1.length == paramTypes2.length) {
			// && paramTypes1.length > 0 && paramTypes2.length > 0
			
			int i = 0;
			result = true;
			while ((i < paramTypes1.length) && result) {
				paramTypes1[i]=(paramTypes1[i].isPrimitive())?resolveType(paramTypes1[i]):paramTypes1[i];
				paramTypes2[i]=(paramTypes2[i].isPrimitive())?resolveType(paramTypes2[i]):paramTypes2[i];
				String p1 = paramTypes1[i].getName();
				String p2 = paramTypes2[i].getName();
				if (!p1.equals(p2)) {
					result = false;
				}
				i++;
			}

		}
		return result;
	}

}

