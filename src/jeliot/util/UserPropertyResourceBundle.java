/*
 * Created on 6.10.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package jeliot.util;

import java.io.IOException;
import java.util.ResourceBundle;


/**
 * @author Niko Myller
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class UserPropertyResourceBundle {

    ResourceBundle userProperties;
    ResourceBundle defaultProperties;
    
    /**
     * @param arg0
     * @throws IOException
     */
    public UserPropertyResourceBundle(ResourceBundle userProperties, ResourceBundle defaultProperties){
        this.userProperties = userProperties;
        this.defaultProperties = defaultProperties;
    }

    public String getString(String key) {
        String value = null;
        try {
            value = userProperties.getString(key);
        } catch (Exception e) {
        }
        if (value == null) {
            value = defaultProperties.getString(key);
        }
        return value;
    }
}
