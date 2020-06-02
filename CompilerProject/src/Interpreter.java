

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
        // b占쎈뮉 占쎈퉸占쎈룴筌랃옙
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
        // �뙴�뫂遊� 占쎈꽰�⑥눘�뻻 占쎈뼄占쎈뻻 占쎈즼�뵳占�
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
        throw new IllegalArgumentException("");
    }
}

    Value applyBinary (Operator op, Value v1, Value v2) {
        // Binary = BinaryOp op; Expression term1, term2
        // Operator占쎈퓠 占쎌뿳占쎈뮉 �겫占썽겫占� 占쎈뼄占쎈뻬

        // INT 占쎄텢燁살늿肉곤옙沅�
        if (op.val.equals(Operator.INT_PLUS)) 
            return new IntValue(v1.intValue( ) + v2.intValue( ));
        if (op.val.equals(Operator.INT_MINUS)) 
            return new IntValue(v1.intValue( ) - v2.intValue( ));
        if (op.val.equals(Operator.INT_TIMES)) 
            return new IntValue(v1.intValue( ) * v2.intValue( ));
        if (op.val.equals(Operator.INT_DIV)) 
            return new IntValue(v1.intValue( ) / v2.intValue( ));
        
        // FLOAT 占쎄텢燁살늿肉곤옙沅�
        if (op.val.equals(Operator.FLOAT_PLUS)) 
            return new FloatValue(v1.floatValue( ) + v2.floatValue( ));
        if (op.val.equals(Operator.FLOAT_MINUS)) 
            return new FloatValue(v1.floatValue( ) - v2.floatValue( ));
        if (op.val.equals(Operator.FLOAT_TIMES)) 
            return new FloatValue(v1.floatValue( ) * v2.floatValue( ));
        if (op.val.equals(Operator.FLOAT_DIV)) 
            return new FloatValue(v1.floatValue( ) / v2.floatValue( ));
        
        // INT �뜮袁㏉꺍占쎈염占쎄텦
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

        // FLOAT �뜮袁㏉꺍占쎈염占쎄텦
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

        // CHAR �뜮袁㏉꺍占쎈염占쎄텦
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

        // BOOL �뜮袁㏉꺍占쎈염占쎄텦
        if (op.val.equals(Operator.BOOL_EQ))
            return new BoolValue(v1.boolValue( ) == v2.boolValue( ));
        if (op.val.equals(Operator.BOOL_NE))
            return new BoolValue(v1.boolValue( ) != v2.boolValue( ));
        if (op.val.equals(Operator.AND))
            return new BoolValue(v1.boolValue( ) && v2.boolValue( ));
        if (op.val.equals(Operator.OR))
            return new BoolValue(v1.boolValue( ) || v2.boolValue( ));
        throw new IllegalArgumentException("");
    } 
    
    Value applyUnary (Operator op, Value v) {
        // Unary = UnaryOp op; Expression term

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
        throw new IllegalArgumentException("");
    } 


