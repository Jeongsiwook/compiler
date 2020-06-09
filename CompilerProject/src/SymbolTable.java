import java.util.*;

public class SymbolTable extends HashMap<Variable, Type> { 

// SymbolTable is implemented as a Java HashMap.  
// Plus a 'display' method to facilitate experimentation.
	
	public SymbolTable() {
		super();
	}

	public SymbolTable(Variable variable, Type t) {
		super();
		this.put(variable, t);
	}
	
	void display() {
		// print the SymbolTable
		System.out.printf("SymbolTable:\n");
    	for (Map.Entry<Variable, Type> entry : entrySet()) 
    		System.out.printf("%10s %10s\n", entry.getKey(), entry.getValue());
	}
	
    public static void main(String args[]) {
    	// test driver for SymbolTable
    	SymbolTable tm = new SymbolTable();
    	   	
    	tm.put(new Variable("var1"), Type.INT);
    	tm.put(new Variable("var2"), Type.FLOAT);
    	tm.put(new Variable("var3"), Type.CHAR);
    	tm.put(new Variable("var4"), Type.BOOL);
    	
    	for (Map.Entry<Variable, Type> entry : tm.entrySet()) 
    		System.out.printf("%10s %10s\n", entry.getKey(), entry.getValue());
    } //main
}


