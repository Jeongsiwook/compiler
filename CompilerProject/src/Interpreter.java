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
        // Statement = Skip | Block | Assigment | Loop | Conditional
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
        // Assigment = Variable target; Expression source
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
        // Expression = Variable | Value | Binaray | Unary
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
    		System.out.println(state.get(var.v));
        	return state; 
    	}
        throw new IllegalArgumentException("should never reach here");
    }
    
    

    Value applyBinary (Operator op, Value v1, Value v2) {
        // Binary = BinaryOp op; Expression term1, term2
        // Operator에 있는 부분 실행
    	
        SemanticAnalyzer.check( ! v1.isUndef( ) && ! v2.isUndef( ),
                "reference to undef value");

        // INT 사칙연산
        if (op.val.equals(Operator.INT_PLUS)) 
            return new IntValue(v1.intValue( ) + v2.intValue( ));
        if (op.val.equals(Operator.INT_MINUS)) 
            return new IntValue(v1.intValue( ) - v2.intValue( ));
        if (op.val.equals(Operator.INT_TIMES)) 
            return new IntValue(v1.intValue( ) * v2.intValue( ));
        if (op.val.equals(Operator.INT_DIV)) 
            return new IntValue(v1.intValue( ) / v2.intValue( ));
        
        // FLOAT 사칙연산
        if (op.val.equals(Operator.FLOAT_PLUS)) 
            return new FloatValue(v1.floatValue( ) + v2.floatValue( ));
        if (op.val.equals(Operator.FLOAT_MINUS)) 
            return new FloatValue(v1.floatValue( ) - v2.floatValue( ));
        if (op.val.equals(Operator.FLOAT_TIMES)) 
            return new FloatValue(v1.floatValue( ) * v2.floatValue( ));
        if (op.val.equals(Operator.FLOAT_DIV)) 
            return new FloatValue(v1.floatValue( ) / v2.floatValue( ));
        
        // INT 비교연산
        if (op.val.equals(Operator.INT_LT))
            return new BoolValue(v1.intValue( ) < v2.intValue( ));
        if (op.val.equals(Operator.INT_LE))
            return new BoolValue(v1.intValue( ) <= v2.intValue( ));
        if (op.val.equals(Operator.INT_EQ))
            return new BoolValue(v1.intValue( ) == v2.intValue( ));
        if (op.val.equals(Operator.INT_NE))
            return new BoolValue(v1.intValue( ) != v2.intValue( ));
        if (op.val.equals(Operator.INT_GT))
            return new BoolValue(v1.intValue( ) > v2.intValue( ));
        if (op.val.equals(Operator.INT_GE))
            return new BoolValue(v1.intValue( ) >= v2.intValue( ));

        // FLOAT 비교연산
        if (op.val.equals(Operator.FLOAT_LT))
            return new BoolValue(v1.floatValue( ) <  v2.floatValue( ));
        if (op.val.equals(Operator.FLOAT_LE))
            return new BoolValue(v1.floatValue( ) <= v2.floatValue( ));
        if (op.val.equals(Operator.FLOAT_EQ))
            return new BoolValue(v1.floatValue( ) == v2.floatValue( ));
        if (op.val.equals(Operator.FLOAT_NE))
            return new BoolValue(v1.floatValue( ) != v2.floatValue( ));
        if (op.val.equals(Operator.FLOAT_GT))
            return new BoolValue(v1.floatValue( ) >  v2.floatValue( ));
        if (op.val.equals(Operator.FLOAT_GE))
            return new BoolValue(v1.floatValue( ) >= v2.floatValue( ));

        // CHAR 비교연산
        if (op.val.equals(Operator.CHAR_LT))
            return new BoolValue(v1.charValue( ) <  v2.charValue( ));
        if (op.val.equals(Operator.CHAR_LE))
            return new BoolValue(v1.charValue( ) <= v2.charValue( ));
        if (op.val.equals(Operator.CHAR_EQ))
            return new BoolValue(v1.charValue( ) == v2.charValue( ));
        if (op.val.equals(Operator.CHAR_NE))
            return new BoolValue(v1.charValue( ) != v2.charValue( ));
        if (op.val.equals(Operator.CHAR_GT))
            return new BoolValue(v1.charValue( ) >  v2.charValue( ));
        if (op.val.equals(Operator.CHAR_GE))
            return new BoolValue(v1.charValue( ) >= v2.charValue( ));

        // BOOL 비교연산
        if (op.val.equals(Operator.BOOL_EQ))
            return new BoolValue(v1.boolValue( ) == v2.boolValue( ));
        if (op.val.equals(Operator.BOOL_NE))
            return new BoolValue(v1.boolValue( ) != v2.boolValue( ));
        if (op.val.equals(Operator.AND))
            return new BoolValue(v1.boolValue( ) && v2.boolValue( ));
        if (op.val.equals(Operator.OR))
            return new BoolValue(v1.boolValue( ) || v2.boolValue( ));
        throw new IllegalArgumentException("should never reach here");
    } 
    
    Value applyUnary (Operator op, Value v) {
        // Unary = UnaryOp op; Expression term
        SemanticAnalyzer.check( ! v.isUndef( ) ,
                "reference to undef value");
        if (op.val.equals(Operator.NOT))
            return new BoolValue(!v.boolValue( ));
        else if (op.val.equals(Operator.INT_NEG))
            return new IntValue(-v.intValue( ));
        else if (op.val.equals(Operator.FLOAT_NEG))
            return new FloatValue(-v.floatValue( ));
        else if (op.val.equals(Operator.I2F))
            return new FloatValue((float)(v.intValue( ))); 
        else if (op.val.equals(Operator.F2I))
            return new IntValue((int)(v.floatValue( )));
        else if (op.val.equals(Operator.C2I))
            return new IntValue((int)(v.charValue( )));
        else if (op.val.equals(Operator.I2C))
            return new CharValue((char)(v.intValue( )));
        throw new IllegalArgumentException("should never reach here");
        
        
        
        
        
        
        
		Operator op = b.operator();
		// interpret the operator's terms
		Value v1 = interpret(b.term1(), funcs, state);
		Value v2 = interpret(b.term2(), funcs, state);
		
		//+, -, *, /
		if (op.isArithmeticOp()) {
			if (v1.type() == Type.INT && v1.type() == Type.INT) {
				if (op.val.equals(Operator.PLUS))
					return new IntValue(v1.intValue() + v2.intValue());
				if (op.val.equals(Operator.MINUS))
					return new IntValue(v1.intValue() - v2.intValue());
				if (op.val.equals(Operator.TIMES))
					return new IntValue(v1.intValue() * v2.intValue());
				if (op.val.equals(Operator.DIV))
					return new IntValue(v1.intValue() / v2.intValue());
				
			// float (+, -, *,/) float 
			} else if(v1.type() == Type.FLOAT && v2.type() == Type.FLOAT) {
				if (op.val.equals(Operator.PLUS))
					return new FloatValue(v1.floatValue() + v2.floatValue());
				if (op.val.equals(Operator.MINUS))
					return new FloatValue(v1.floatValue() - v2.floatValue());
				if (op.val.equals(Operator.TIMES))
					return new FloatValue(v1.floatValue() * v2.floatValue());
				if (op.val.equals(Operator.DIV))
					return new FloatValue(v1.floatValue() / v2.floatValue());
				
			// if attempting op on int and float, cast int to float and do it again
			} else if((v1.type() == Type.INT && v2.type() == Type.FLOAT) ||
					  (v1.type() == Type.FLOAT && v2.type() == Type.INT)) {
				if(v1.type() == Type.INT)
					v1 = new FloatValue((float)v1.intValue());
				else if(v2.type() == Type.INT)
					v2 = new FloatValue((float)v2.intValue());
				return interpret(new Binary(op, v1, v2), funcs, state);
			} else {
				throw new IllegalArgumentException("Attemped arithmetic op on a " + v1.type() + " and a " + v2.type() + ", not allowed (v1: " + v1 + " v2: " + v2 + ")");
			}
			
		// &&, ||
		} else if (op.isBooleanOp()){
			// boolean op and only be performed on booleans
			if(!(v1.type() == Type.BOOL && v1.type() == Type.BOOL))
				throw new IllegalArgumentException("Attemped boolean op on " + v1.type() + ", not allowed");
			else {
				if(op.val.equals(Operator.AND))
					return new BoolValue(v1.boolValue() && v2.boolValue());
				else if(op.val.equals(Operator.OR))
					return new BoolValue(v1.boolValue() || v2.boolValue());
			}
			
		// <, >, <=, >=, ==, !=
		} else if(op.isRelationalOp()){
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
				
			// float (<, >, <=, >=, ==, !=) float
			} else if(v1.type() == Type.FLOAT && v1.type() == Type.FLOAT){
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
				
			// if attempting op on int and float, cast int to float and do it again
			} else if((v1.type() == Type.INT && v2.type() == Type.FLOAT) ||
					  (v1.type() == Type.FLOAT && v2.type() == Type.INT)) {
				if(v1.type() == Type.INT)
					v1 = new FloatValue((float)v1.intValue());
				else if(v2.type() == Type.INT)
					v2 = new FloatValue((float)v2.intValue());
				return interpret(new Binary(op, v1, v2), funcs, state);
				
			// chars
			} else if(v1.type() == Type.CHAR && v2.type() == Type.CHAR){
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
		throw new IllegalArgumentException("should never reach here (in DynamicTyping.applyBinary)");
	}
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

