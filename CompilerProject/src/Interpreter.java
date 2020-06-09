import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;

// CLite Interpreter

public class Interpreter {

    State M (Program p) { 
        // Program = Declarations decpart; Statement body
        return M (p.body, initialState(p.decpart)); 
    }
  
    State initialState (Declarations d) {
        State state = new State();
        for (Declaration decl : d)
            state.put(decl.v, Value.mkValue(decl.t));
        return state;
    }
  
  
    State M (Statement s, State state) {
        // Statement = Skip | Block | Assignment | Loop | Conditional
        if (s instanceof Skip) return M((Skip)s, state);
        if (s instanceof Assignment)  return M((Assignment)s, state);
        if (s instanceof Conditional)  return M((Conditional)s, state);
        if (s instanceof Loop)  return M((Loop)s, state);
        if (s instanceof Block)  return M((Block)s, state);
        if (s instanceof Print) return M((Print)s, state);
        throw new IllegalArgumentException();
    }
  
    State M (Skip s, State state) {
        return state;
    }
  
    State M (Assignment a, State state) {
        // Assignment = Variable target; Expression source
        return state.onion(a.target, M (a.source, state));
    }
  
    State M (Block b, State state) {
        // Block = Statements
        // b는 해쉬맵
        for (Statement s : b.members)
            state = M (s, state);
        return state;
    }
  
    State M (Conditional c, State state) {
        // Conditional = Expression test; Statement thenbranch, elsebranch
        if (M(c.test, state).boolValue( ))
            return M (c.thenbranch, state);
        else
            return M (c.elsebranch, state);
    }
  
    State M (Loop l, State state) {
        // Loop = Expression test; Statement body
        if (M (l.test, state).boolValue( ))
        // 루프 통과시 다시 돌림
            return M(l, M (l.body, state));
        else return state;
    }

    Value M (Expression e, State state) {
        // Expression = Variable | Value | Binary | Unary
        if (e instanceof Value) 
            return (Value)e;
        if (e instanceof Variable) 
            return (Value)(state.get(e));
        if (e instanceof Binary) {
            Binary b = (Binary)e;
            return applyBinary (b.op, 
                                M(b.term1, state), M(b.term2, state));
        }
        if (e instanceof Unary) {
            Unary u = (Unary)e;
            return applyUnary(u.op, M(u.term, state));
        }
        throw new IllegalArgumentException("should never reach here");
    }
    
    State M (Print p, State state) {
    	if (p instanceof PrintInt) {
    		PrintInt i = (PrintInt)p;
    		System.out.println(i.v);
        	return state; 
    	}
    	if (p instanceof PrintFloat) {
    		PrintFloat f = (PrintFloat)p;
    		System.out.println(f.v);
        	return state; 
    	}
    	if (p instanceof PrintCh) {
    		PrintCh c = (PrintCh)p;
    		System.out.println(c.v);
        	return state; 
    	}
    	if (p instanceof PrintVar) {
    		PrintVar var = (PrintVar)p;
    		System.out.println(state.get(var.e));
        	return state; 
    	}
        throw new IllegalArgumentException("should never reach here");
    }
    
    

    Value applyBinary (Operator op, Value v1, Value v2) {
        // Binary = BinaryOp op; Expression term1, term2
        // Operator에 있는 부분 실행
    	
        SemanticAnalyzer.check( ! v1.isUndef( ) && ! v2.isUndef( ),
                "reference to undef value");
        //+, -, *, /
      	if (op.ArithmeticOp()) {
      		if (v1.type() == Type.INT && v1.type() == Type.INT) {
      				if (op.val.equals(Operator.PLUS))
      					return new IntValue(v1.intValue() + v2.intValue());
      				if (op.val.equals(Operator.MINUS))
      					return new IntValue(v1.intValue() - v2.intValue());
      				if (op.val.equals(Operator.TIMES))
      					return new IntValue(v1.intValue() * v2.intValue());
      				if (op.val.equals(Operator.DIV))
      					return new IntValue(v1.intValue() / v2.intValue());
      		}
      		// float (+, -, *,/) float 
      		 else if(v1.type() == Type.FLOAT && v2.type() == Type.FLOAT) {
      				if (op.val.equals(Operator.PLUS))
      					return new FloatValue(v1.floatValue() + v2.floatValue());
      				if (op.val.equals(Operator.MINUS))
      					return new FloatValue(v1.floatValue() - v2.floatValue());
      				if (op.val.equals(Operator.TIMES))
      					return new FloatValue(v1.floatValue() * v2.floatValue());
      				if (op.val.equals(Operator.DIV))
      					return new FloatValue(v1.floatValue() / v2.floatValue());
      		}
	      		// 두 개의 타입이 다른 타입일때 캐스팅 후 다시 진행
      		else if((v1.type() == Type.INT && v2.type() == Type.FLOAT) ||
      				(v1.type() == Type.FLOAT && v2.type() == Type.INT)) {
      				if(v1.type() == Type.INT)
      					v1 = new FloatValue((float)v1.intValue());
      				else if(v2.type() == Type.INT)
      					v2 = new FloatValue((float)v2.intValue());
      				return applyBinary(op, v1, v2);
      		}
      	}
      		
      	// &&, ||
      	else if (op.BooleanOp()){
      		// AND 연산은 bool 타입일 때만 진행
      		if(!(v1.type() == Type.BOOL && v2.type() == Type.BOOL))
      			throw new IllegalArgumentException("Attemped boolean op on " + v1.type() + ", not allowed");
      		else {
      			if(op.val.equals(Operator.AND))
      				return new BoolValue(v1.boolValue() && v2.boolValue());
      			else if(op.val.equals(Operator.OR))
      				return new BoolValue(v1.boolValue() || v2.boolValue());
      		}
      	}
      		
      	// <, >, <=, >=, ==, !=
      	else if(op.RelationalOp()){
      		// int (<, >, <=, >=, ==, !=) int
      		if (v1.type() == Type.INT && v1.type() == Type.INT) {
      					if(op.val.equals(Operator.LT))
      						return new BoolValue(v1.intValue() < v2.intValue());
      					else if(op.val.equals(Operator.GT))
      						return new BoolValue(v1.intValue() > v2.intValue());
      					else if(op.val.equals(Operator.LE))
      						return new BoolValue(v1.intValue() <= v2.intValue());
      					else if(op.val.equals(Operator.GE))
      						return new BoolValue(v1.intValue() >= v2.intValue());
      					else if(op.val.equals(Operator.EQ))
      						return new BoolValue(v1.intValue() == v2.intValue());
      					else if(op.val.equals(Operator.NE))
      						return new BoolValue(v1.intValue() != v2.intValue());
      		}	
      		// float (<, >, <=, >=, ==, !=) float
      		else if(v1.type() == Type.FLOAT && v1.type() == Type.FLOAT){
      					if(op.val.equals(Operator.LT))
      						return new BoolValue(v1.floatValue() < v2.floatValue());
      					else if(op.val.equals(Operator.GT))
      						return new BoolValue(v1.floatValue() > v2.floatValue());
      					else if(op.val.equals(Operator.LE))
      						return new BoolValue(v1.floatValue() <= v2.floatValue());
      					else if(op.val.equals(Operator.GE))
      						return new BoolValue(v1.floatValue() >= v2.floatValue());
      					else if(op.val.equals(Operator.EQ))
      						return new BoolValue(v1.floatValue() == v2.floatValue());
      					else if(op.val.equals(Operator.NE))
      						return new BoolValue(v1.floatValue() != v2.floatValue());
      		}
      	    // 두 개의 타입이 다른 타입일때 캐스팅 후 다시 진행
      		else if((v1.type() == Type.INT && v2.type() == Type.FLOAT) ||
      						  (v1.type() == Type.FLOAT && v2.type() == Type.INT)) {
      					if(v1.type() == Type.INT)
      						v1 = new FloatValue((float)v1.intValue());
      					else if(v2.type() == Type.INT)
      						v2 = new FloatValue((float)v2.intValue());
          				return applyBinary(op, v1, v2);
      		}	
      		// chars
      		else if(v1.type() == Type.CHAR && v2.type() == Type.CHAR){
      					if(op.val.equals(Operator.LT))
      						return new BoolValue(v1.charValue() < v2.charValue());
      					else if(op.val.equals(Operator.GT))
      						return new BoolValue(v1.charValue() > v2.charValue());
      					else if(op.val.equals(Operator.LE))
      						return new BoolValue(v1.charValue() <= v2.charValue());
      					else if(op.val.equals(Operator.GE))
      						return new BoolValue(v1.charValue() >= v2.charValue());
      					else if(op.val.equals(Operator.EQ))
      						return new BoolValue(v1.charValue() == v2.charValue());
      					else if(op.val.equals(Operator.NE))
      						return new BoolValue(v1.charValue() != v2.charValue());
      		}
      				
      		// bools can be compared with == and !=
      		else if(v1.type() == Type.BOOL && v2.type() == Type.BOOL){
      					if(op.val.equals(Operator.EQ))
      						return new BoolValue(v1.boolValue() == v2.boolValue());
      					else if(op.val.equals(Operator.NE))
      						return new BoolValue(v1.boolValue() != v2.boolValue());
      					else
      						throw new IllegalArgumentException("Attempted illegal relational op " + op + " on two booleans (v1: " + v1 + " v2: " + v2 + ")");
      		}
      		else {
      					throw new IllegalArgumentException("Attemped relational op on a " + v1.type() + " and a " + v2.type() + ", not allowed (v1: " + v1 + " v2: " + v2 + ")");
      		}
      	}
        throw new IllegalArgumentException("should never reach here");
    } 
    
    Value applyUnary (Operator op, Value v) {
        // Unary = UnaryOp op; Expression term
        SemanticAnalyzer.check( ! v.isUndef( ) ,
                "reference to undef value");
        
        
		// boolean not
		if (op.val.equals(Operator.NOT)){
			if(v.type() != Type.BOOL)
				throw new IllegalArgumentException("Can only apply ! operator to bool (attempted on " + v + ")");
			else
				return new BoolValue(!v.boolValue());
		}

		// negate
		else if(op.val.equals(Operator.NEG)){
			if(v.type() == Type.FLOAT)
				return new FloatValue(-v.floatValue());
			else if(v.type() == Type.INT)
				return new IntValue(-v.intValue());
			else
				throw new IllegalArgumentException("Can only apply - operator to int or float (attempted on " + v + ")");
		}
		
		// float cast
		else if (op.val.equals(Operator.FLOAT)){
			if(v.type() != Type.INT)
				throw new IllegalArgumentException("Can only cast int to float (tried to cast " + v + ")");
			else
				return new FloatValue((float)v.intValue());
		}
		
		// int cast
		else if (op.val.equals(Operator.INT)){
			if(v.type() == Type.FLOAT)
				return new IntValue((int)v.floatValue());
			else if(v.type() == Type.CHAR)
				return new IntValue((int)v.charValue());
			else
				throw new IllegalArgumentException("Can only cast float or char to int (tried to cast " + v + ")");
		}
		
		
		// char cast
		else if(op.val.equals(Operator.CHAR)){
			if(v.type() == Type.INT)
				return new CharValue((char)v.intValue());
			else
				throw new IllegalArgumentException("Can only cast int to char (tried to cast " + v + ")");
		}
       
        throw new IllegalArgumentException("should never reach here");
    } 
    


    public static void main(String args[]) {
        Parser parser  = new Parser(new Lexer(args[0]));
        Program prog = parser.program();
        
        System.out.println("\nAbstract Syntax Tree...");  
        JTree tree;
    	JFrame frame = new JFrame();
        tree = new JTree(prog.makeRoot());
    	tree.setVisibleRowCount(10);
    	
    	JScrollPane treeScroll = new JScrollPane(tree);
    	frame.add(treeScroll);
    	
    	frame.setTitle("Abstract Syntax Tree");
    	frame.setSize(400, 500);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	
		frame.setVisible(true);
		
		

        System.out.println("\nSemantic Analysis...");        
        SemanticAnalyzer.V(prog); // 의미분석기
   
        System.out.println("\nSemantic Analysis Ended");  
        

        Interpreter interpreter = new Interpreter( );
        State state = interpreter.M(prog);
        System.out.println("Final State");
        state.display( );
    }
    
}

