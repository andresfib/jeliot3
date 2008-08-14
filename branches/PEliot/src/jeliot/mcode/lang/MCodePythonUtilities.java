package jeliot.mcode.lang;


import org.python.compiler.CodeEvaluator;
import org.python.core.PyComplex;
import org.python.core.PyFloat;
import org.python.core.PyInteger;
import org.python.core.PyLong;
import org.python.core.PyObject;
import org.python.parser.ast.Name;
import org.python.parser.ast.Str;

public class MCodePythonUtilities extends MCodeLangUtilities {
	public String getValue(Object o)
	{		
		if (o == null) {
            return "null";
        }
        Object value = null;
        
    	if (o instanceof PyInteger) {
            value = ((PyInteger) o).getValue();            
        } else if (o instanceof PyLong) {
            value = ((PyObject)o).__str__().toString();            
        } else if (o instanceof PyFloat) {
            value = ((PyFloat) o).getValue();
        } else if (o instanceof PyComplex) {
            value = ((PyComplex) o).imag;
        }
    	if (value != null)
    		return value.toString();
    	return null;
	}
	
	public boolean isSetPreparing()
	{		
		return (CodeEvaluator.isSetPreparing());
	}
	
	public Boolean isConvertedToString(Object expType) {
		return (expType.getClass().getName().equals(Name.class.getName()) ||
				(expType.getClass().getName().equals(Str.class.getName())) ||
				(getValue(expType) != null));
	}

	public Boolean getBoolValue(Object toBool)
	{
		if (toBool instanceof PyInteger)
			return (((PyInteger)toBool).getValue() != 0);
		return null;
	}
}
