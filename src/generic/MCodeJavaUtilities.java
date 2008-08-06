package generic;

import koala.dynamicjava.interpreter.EvaluationVisitor;

public class MCodeJavaUtilities extends MCodeLangUtilities {
	
	public boolean isSetPreparing()
	{
		return (EvaluationVisitor.isSetPreparing());
	}
	
	public Boolean isConvertedToString(Object expType) {
		return (expType instanceof koala.dynamicjava.tree.Literal);
	}
}
