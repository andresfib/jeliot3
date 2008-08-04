package generic;

public abstract class MCodeLangUtilities {
	public static String getValue(Object o)
	{
		return o.toString();
	}
	
    public static String getValue(String value, String type)
    {
    	return value;
    }
}
