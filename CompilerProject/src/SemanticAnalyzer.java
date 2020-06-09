// SemanticAnalyzer.java
// 2018125084 임예랑, 중간시험 제출용

import java.util.*;

public class SemanticAnalyzer {

    public static SymbolTable typing (Declarations d) {
        SymbolTable map = new SymbolTable();
        for (Declaration di : d) 
            map.put (di.v, di.t);
        return map;
    }

    public static void check(boolean test, String msg) {
        if (test)  return;
        System.err.println(msg);
        System.exit(1);
    }

    public static void V (Declarations d) {
        for (int i=0; i<d.size() - 1; i++)
            for (int j=i+1; j<d.size(); j++) {
                Declaration di = d.get(i);
                Declaration dj = d.get(j);
                check( ! (di.v.equals(dj.v)),
                       "duplicate declaration: " + dj.v);
            }
    } 

    public static void V (Program p) {
        V (p.decpart);
        V (p.body, typing (p.decpart));
    } 

    public static Type typeOf (Expression e, SymbolTable tm) {
        if (e instanceof Value) return ((Value)e).type;
        if (e instanceof Variable) {
            Variable v = (Variable)e;
            check (tm.containsKey(v), "undefined variable: " + v);
            return (Type) tm.get(v);
        }
        if (e instanceof Binary) {
            Binary b = (Binary)e;
            if (b.op.ArithmeticOp( ))
                if (typeOf(b.term1,tm)== Type.FLOAT)
                    return (Type.FLOAT);
                else return (Type.INT);
            if (b.op.RelationalOp( ) || b.op.BooleanOp( )) 
                return (Type.BOOL);
        }
        if (e instanceof Unary) {
            Unary u = (Unary)e;
            if (u.op.NotOp( ))        return (Type.BOOL);
            else if (u.op.NegateOp( )) return typeOf(u.term,tm);
            else if (u.op.intOp( ))    return (Type.INT);
            else if (u.op.floatOp( )) return (Type.FLOAT);
            else if (u.op.charOp( ))  return (Type.CHAR);
        }
        throw new IllegalArgumentException("should never reach here");
    } 

    public static void V (Expression e, SymbolTable tm) {
        if (e instanceof Value) 
            return;
        if (e instanceof Variable) { 
            Variable v = (Variable)e;
            check( tm.containsKey(v)
                   , "undeclared variable: " + v);
            return;
        }
        if (e instanceof Binary) {
            Binary b = (Binary) e;
            Type typ1 = typeOf(b.term1, tm);
            Type typ2 = typeOf(b.term2, tm);
            V (b.term1, tm);
            V (b.term2, tm);
            if (b.op.ArithmeticOp( ))  
                check( typ1 == typ2 &&
                       (typ1 == Type.INT || typ1 == Type.FLOAT)
                       , "type error for " + b.op);
            else if (b.op.RelationalOp( )) 
                check( typ1 == typ2 , "type error for " + b.op);
            else if (b.op.BooleanOp( )) 
                check( typ1 == Type.BOOL && typ2 == Type.BOOL,
                       b.op + ": non-bool operand");
            else
                throw new IllegalArgumentException("should never reach here");
            return;
        }
        
        
        // add code here
        // Unary Operator Valid Rule 적용
        if (e instanceof Unary) {
        	Unary u = (Unary) e;
            Type type = typeOf(u.term, tm);
            V (u.term, tm);
            if (u.op.NotOp( ))  
                check((type == Type.BOOL), "type error for NotOp " + u.op);
            else if (u.op.NegateOp( )) 
                check((type == Type.INT || type == Type.FLOAT), "type error for NegateOp " + u.op);
            else if (u.op.floatOp( )) 
                check((type == Type.INT), "type error for floatOp " + u.op);
            else if (u.op.charOp( )) 
                check((type == Type.INT), "type error for charOp " + u.op);
            else if (u.op.intOp( )) 
                check((type == Type.FLOAT || type == Type.CHAR), "type error for intOp " + u.op);
            return;
        }

        throw new IllegalArgumentException("should never reach here");
    }

    
    public static void V (Statement s, SymbolTable tm) {
        if ( s == null )
            throw new IllegalArgumentException( "AST error: null statement");
        if (s instanceof Skip) return;
        if (s instanceof Assignment) {
            Assignment a = (Assignment)s;
            check( tm.containsKey(a.target)
                   , " undefined target in assignment: " + a.target);
            V(a.source, tm);
            Type ttype = (Type)tm.get(a.target);
            Type srctype = typeOf(a.source, tm);
            if (ttype != srctype) {
                if (ttype == Type.FLOAT)
                    check( srctype == Type.INT
                           , "mixed mode assignment to " + a.target);
                else if (ttype == Type.INT)
                    check( srctype == Type.CHAR
                           , "mixed mode assignment to " + a.target);
                else
                    check( false
                           , "mixed mode assignment to " + a.target);
            }
            return;
        } 
        
        
        // add code here
        // Conditional Valid Rule 적용
        if (s instanceof Conditional) {
			Conditional c = (Conditional) s;
			V(c.test, tm);
			Type ttype = typeOf(c.test, tm);
			if (ttype == Type.BOOL) {
				V(c.thenbranch, tm);
				V(c.elsebranch, tm);
				return;
			} else {
				check(false, "type error for conditional if: " + c.test);
			}
		} 
        // Loop Valid Rule 적용
        if (s instanceof Loop) {
			Loop l = (Loop) s;
			V(l.test, tm);
			Type ttype = typeOf(l.test, tm);
			if (ttype == Type.BOOL) {
				V(l.body, tm);
				return;
			} else {
				check(false, "type error for loop if: " + l.test);
			}
		} 
        // Block Valid Rule 적용
        if (s instanceof Block) {
			Block b = (Block) s;
			for (Statement i : b.members) {
				V(i, tm);
			}
			return;
		}
        // Print Valid Rule 적용
        if (s instanceof Print) {
        	if (s instanceof PrintInt) {
            	PrintInt p = (PrintInt) s;
            	V(p.v, tm);            	
            	return;
            }
        	else if (s instanceof PrintFloat) {
        		PrintFloat p = (PrintFloat) s;
        		V(p.v, tm);
        		return;
        	}
        	else if (s instanceof PrintCh) {
        		PrintCh p = (PrintCh) s;
        		V(p.v, tm);
        		Type ttype = typeOf(p.v, tm);
        		if (ttype == Type.CHAR) {
        			return;
        		}
        		else {
            		check(false, "type error for print if: " + p.v);
            	}        		
        	}
        	else if (s instanceof PrintVar) {
        		PrintVar p = (PrintVar) s;
        		V(p.e, tm);
        		Type ttype = typeOf(p.e, tm);
            	if (ttype == Type.INT) {
            		return;
            	} else if (ttype == Type.FLOAT) {
            		return;
            	} else if (ttype == Type.CHAR) {
            		return;
            	}
            	else {
            		check(false, "type error for print if: " + p.e);
            	}        		
        	} else {
        		check(false, "type error");
        	}
        }       
        
        throw new IllegalArgumentException("should never reach here");
    }

    public static void main(String args[]) {
        Parser parser  = new Parser(new Lexer(args[0]));
        Program prog = parser.program();
        //prog.display();
        
        
        System.out.println("\nSemantic Analysis...");        
        V(prog); // 의미분석기
        
        System.out.println("\nSemantic Analysis Ended");  
    } //main

 // 타입검사결과 - p2, p4: 오류; p1, p3: 무오류
 // p2 - undefined target in assignment: n
 // p4 - type error for *
    
    
} // class SemanticAnalyzer

