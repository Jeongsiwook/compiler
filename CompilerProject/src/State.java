
import java.util.*;


// State
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

    public State onion (State t) {
        for (Variable key : t.keySet( ))
            put(key, t.get(key));
        return this;
    }
	
	public void display(){
		for(Variable v : this.keySet())
			System.out.println(v + ": " + this.get(v));
	}
}