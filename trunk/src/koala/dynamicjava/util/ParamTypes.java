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
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ParamTypes {
	
	/**
	 * Compares two method's list of parameters 
	 * @param paramTypes1
	 * @param paramTypes2
	 * @return Returns true if both of them are the same, false otherewise
	 */
	public static boolean compareSignatures (Class[] paramTypes1, Class[] paramTypes2){
		
		boolean result = false;
		
		if (paramTypes1.length == 0){			
			if (paramTypes2.length == 0){
				
				result = true;
				
			}
		}else if (paramTypes1.length  == paramTypes2.length){ 
			// && paramTypes1.length > 0 && paramTypes2.length > 0
			int i= 0;
			result = true;
			while ((i < paramTypes1.length) && result ){
				String p1 = paramTypes1[i].getName().toString();
				String p2 = paramTypes2[i].getName().toString();
				if (!p1.equals(p2)){
					result=false;
				}
				i++;
			}
			
		}
		return result;
	}
}
