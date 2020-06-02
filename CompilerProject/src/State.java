package interpreter;

import java.util.*;

import syntax.expression.Variable;
import syntax.value.Value;


// State
// Change made here
// jhjhjhjhjhjhjh
public class State extends HashMap<Variable, Value> {
	public State() {
		super();
	}

	public State(Variable variable, Value val) {
		super();
		this.put(variable, val);
	}

	public State onion(Variable variable, Value val) {
		this.put(variable, val);
		return this;
	}

	public State onion(State other) {
		for (Variable key : other.keySet())
			put(key, other.get(key));
		return this;
	}
	
	public void display(){
		for(Variable v : this.keySet())
			System.out.println(v + ": " + this.get(v));
	}
}