package jeliot.lang;

import java.util.*;

public class ClassInfo {

    private Hashtable methods;
    private Hashtable fields;
    private Hashtable constructors;

    private String name;

    public ClassInfo(String name) {
        this.name = name;
        this.methods = new Hashtable();
        this.fields = new Hashtable();
        this.constructors = new Hashtable();
    }

    public String getName() {
        return name;
    }

    public void declareMethod(String key, String info) {
        methods.put(key, info);
    }

    public void declareField(String key, String info) {
        fields.put(key, info);
    }

    public void declareConstructor(String key, String info) {
        constructors.put(key, info);
    }

    public String getMethodInfo(String key) {
        return (String) methods.get(key);
    }

    public String getFieldInfo(String key) {
        return (String) fields.get(key);
    }

    public String getConstructorInfo(String key) {
        return (String) constructors.get(key);
    }

    public Hashtable getMethods() {
        return methods;
    }

    public Hashtable getFields() {
        return fields;
    }

    public Hashtable getConstructors() {
        return constructors;
    }

    public void extendClass(ClassInfo ci) {
        //Firstly the fields
        Hashtable hf = ci.getFields();
        Enumeration enum = hf.keys();

        while (enum.hasMoreElements()) {
            String name = (String) enum.nextElement();
            String info = (String) hf.get(name);
            if (name != null && info != null) {
                declareField(name, info);
            }
        }

        //Secondly the methods
        Hashtable hm = ci.getMehthods();
        enum = hm.keys();
        while (enum.hasMoreElements()) {
            String name = (String) enum.nextElement();
            String info = (String) hm.get(name);
            if (name != null && info != null) {
                declareMethod(name, info);
            }
        }
    }
}