/*
 * Created on 28.10.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jeliot.util;


/**
 * @author Niko Myller
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class DebugUtil {

   //TODO: Change this to false for released versions.
   public static boolean DEBUGGING = false; 
    
   private DebugUtil() {
   }

   public static void printDebugInfo(String str) {
       if (DEBUGGING) {
           System.out.println(str);
       }
   }
   
}
