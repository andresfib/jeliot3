/*
 * Created on 6.10.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jeliot.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * @author Niko Myller
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ResourceBundles {

    private static UserPropertyResourceBundle callTreeProperties;

    private static UserPropertyResourceBundle guiProperties;

    private static UserPropertyResourceBundle mCodeProperties;

    private static UserPropertyResourceBundle theaterProperties;

    private static ResourceBundle guiMessages;

    private static ResourceBundle mCodeMessages;

    private static ResourceBundle theaterMessages;

    private static PropertyResourceBundle userProperties;
    
    public static PropertyResourceBundle getUserProperties() {
        if (userProperties == null) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(new File("properties.properties"));
                userProperties = new PropertyResourceBundle(fis);
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            }
        }
        return userProperties;
    }
    
    
    public static UserPropertyResourceBundle getCallTreeUserPropertyResourceBundle() {
        if (callTreeProperties == null) {
                callTreeProperties = new UserPropertyResourceBundle(getUserProperties(), ResourceBundle.getBundle(
                        "jeliot.calltree.resources.properties", Locale.getDefault()));
        }
        
        return callTreeProperties;
    }

    public static UserPropertyResourceBundle getGuiUserPropertyResourceBundle() {
        if (guiProperties == null) {
            guiProperties = new UserPropertyResourceBundle(getUserProperties(), ResourceBundle.getBundle(
                    "jeliot.gui.resources.properties", Locale.getDefault()));
        }
        
        return guiProperties;
    }

    public static UserPropertyResourceBundle getMCodeUserPropertyResourceBundle() {
        if (mCodeProperties == null) {
            mCodeProperties = new UserPropertyResourceBundle(getUserProperties(), ResourceBundle.getBundle(
                    "jeliot.mcode.resources.properties", Locale.getDefault()));
        }
        
        return mCodeProperties;
    }

    public static UserPropertyResourceBundle getTheaterUserPropertyResourceBundle() {
        if (theaterProperties == null) {
            theaterProperties = new UserPropertyResourceBundle(getUserProperties(), ResourceBundle.getBundle(
                    "jeliot.theater.resources.properties", Locale.getDefault()));
        }
        
        return theaterProperties;
    }
    
    public static ResourceBundle getGuiMessageResourceBundle() {
        if (guiMessages == null) {
            guiMessages = ResourceBundle.getBundle("jeliot.gui.resources.messages", Locale.getDefault());
        }
        
        return guiMessages;
    }

    public static ResourceBundle getMCodeMessageResourceBundle() {
        if (mCodeMessages == null) {
            mCodeMessages = ResourceBundle.getBundle("jeliot.mcode.resources.messages", Locale.getDefault());
        }
        
        return mCodeMessages;
    }
    
    public static ResourceBundle getTheaterMessageResourceBundle() {
        if (theaterMessages == null) {
            theaterMessages = ResourceBundle.getBundle("jeliot.theater.resources.messages", Locale.getDefault());
        }
        
        return theaterMessages;
    }
    
}