package generic;

// TODO: generics
import java.io.InputStream;
import java.io.Reader;

import koala.dynamicjava.interpreter.InterpreterException;
import koala.dynamicjava.parser.wrapper.ParserFactory;
import koala.dynamicjava.util.LibraryFinder;

public interface Interpreter {
	/**
     * Runs the interpreter
     * @param is    the input stream from which the statements are read
     * @param fname the name of the parsed stream
     * @return the result of the evaluation of the last statement
     */
    Object interpret(InputStream is, String fname) throws InterpreterException;
    

    /**
     * Loads an interpreted class
     * @param s the fully qualified name of the class to load
     * @exception ClassNotFoundException if the class cannot be find
     */
    Class loadClass(String name) throws ClassNotFoundException;
    
    /**
     * Gets the class loader
     */
    ClassLoader getClassLoader();

    /**
     * Gets the library finder
     */
    LibraryFinder getLibraryFinder();

    /**
     * Gets the parser factory
     */
    ParserFactory getParserFactory();
	
}
