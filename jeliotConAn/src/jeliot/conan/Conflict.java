package jeliot.conan;

public class Conflict {
	String type;
	private int conflictLine;
	public static final String FOR = "forUpdate";
	public static final String OVERRIDING = "overriding";
	public static final String IMPLICIT_SUPER = "implicit";
	
	Conflict(String type){
		this.type = type; 
	}

	public Conflict(String type, int line) {
		this(type);
		conflictLine = line;
		System.out.println(this.toString());
	}

	public boolean isInheritanceConflict(){
		return type.equals(Conflict.IMPLICIT_SUPER);
	}
	public boolean isOverridingConflict(){
		return type.equals(Conflict.OVERRIDING);
	}

	public String getType() {
		
		return type;
	}

	public int getConflictLine() {
		return conflictLine;
	}

	public void setConflictLine(int line) {
		conflictLine = line;
	}
	public String toString(){
		return "Conflict of type " + type + " in line " + conflictLine; 
	}
}
