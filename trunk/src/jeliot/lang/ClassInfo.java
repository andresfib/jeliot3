package jeliot.lang;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;

import jeliot.mcode.*;

public class ClassInfo {

    private Hashtable methods;
    private Hashtable fields;
    private Hashtable constructors;
    private String extendedClass;
    private String name;

    public ClassInfo(String name) {
        this.name = name;
        this.methods = new Hashtable();
        this.fields = new Hashtable();
        this.constructors = new Hashtable();
    }

    public ClassInfo(Class declaredClass) {
        this(declaredClass.getName());
        try {
            setDeclaredConstructors(declaredClass.getDeclaredConstructors());
        } catch (Exception e) {
            this.constructors = new Hashtable();
            setDeclaredConstructors(declaredClass.getConstructors());
        }

        try {
            setDeclaredFields(declaredClass.getDeclaredFields());
        } catch (Exception e) {
            this.fields = new Hashtable();
            setDeclaredFields(declaredClass.getFields());
        }

        try {
            setDeclaredMethods(declaredClass.getDeclaredMethods());
        } catch (Exception e) {
            this.methods = new Hashtable();
            setDeclaredMethods(declaredClass.getMethods());
        }
    }

    public void setDeclaredConstructors(Constructor[] constructors) {
        int n = constructors.length;
        for (int i = 0; i < n; i++) {

            Class[] classes = constructors[i].getParameterTypes();
            String typeList = "";
            int m = classes.length;
            for (int j = 0; j < m; j++) {
                typeList += classes[j].getName();
                if (j != (m - 1)) {
                    typeList += ",";
                }
            }

            declareConstructor(constructors[i].getName() + Code.DELIM + typeList, "");
        }
    }

    public void setDeclaredFields(Field[] fields) {
        int n = fields.length;
        for (int i = 0; i < n; i++) {

            String name = fields[i].getName();
            String type = fields[i].getType().getName();
            int modifiers = fields[i].getModifiers();
            String value = Code.UNKNOWN;

            declareField(name,
               "" + modifiers + Code.DELIM + type + Code.DELIM + value);
        }
    }

    public void setDeclaredMethods(Method[] methods) {

        int n = methods.length;
        for (int i = 0; i < n; i++) {

            String name = methods[i].getName();
            String returnType = methods[i].getReturnType().getName();
            int modifiers = methods[i].getModifiers();
            String listOfParameters = "";
            Class[] classes = methods[i].getParameterTypes();
            String typeList = "";
            int m = classes.length;
            for (int j = 0; j < m; j++) {
                typeList += classes[j].getName();
                if (j != (m - 1)) {
                    typeList += ",";
                }
            }

            declareMethod(name + Code.DELIM + typeList,
                          "" + modifiers + Code.DELIM + returnType);
        }
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

    public int getFieldNumber() {
        return fields.size();
    }

    public void extendClass(ClassInfo ci) {

        extendedClass = ci.getName();

        //Firstly the fields
        Hashtable hf = ci.getFields();
        Enumeration enum = hf.keys();

        while (enum.hasMoreElements()) {
            String name = (String) enum.nextElement();
            String info = (String) hf.get(name);
            if (name != null && info != null) {
                declareField(name, info + Code.DELIM + "<E>");
            }
        }

        //Secondly the methods
        Hashtable hm = ci.getMethods();
        enum = hm.keys();
        while (enum.hasMoreElements()) {
            String name = (String) enum.nextElement();
            String info = (String) hm.get(name);
            if (name != null && info != null) {
                declareMethod(name, info + Code.DELIM + "<E>");
            }
        }
    }
}