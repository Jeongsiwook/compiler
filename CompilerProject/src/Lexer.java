import java.io.*;

public class Lexer {
    private boolean isEof = false;
    private char ch = ' ';
    private BufferedReader input;
    private String line = "";
    private int lineno = 0;
    private int col = 1;
    private final String letters = "abcdefghijklmnopqrstuvwxyz"
        + "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String digits = "0123456789";
    private final char eolnCh = '\n';
    private final char eofCh = '\004';

    public Lexer (String fileName) { // source filename
        try {
            input = new BufferedReader (new FileReader(fileName));
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
            System.exit(1);
        }
    }


    private char nextChar() { // Return next char
        // 파일 한 줄을 읽어오는 함수
        if (ch == eofCh)
            error("Attempt to read past end of file");
        col++;
        if (col >= line.length()) {
            try {
                line = input.readLine( );
            } catch (IOException e) {
                System.err.println(e);
                System.exit(1);
            } // try
            if (line == null) // at end of file
                line = "" + eofCh;
            else if (line.startsWith("//")) { // //로 시작하는 라인은 주석이기 때문에 보이지 않게 함
            	lineno++;
            	line += eolnCh;
            }
            else {
                System.out.println("line " + (lineno));
            	lineno++;
                line += eolnCh;
            } // if line
            col = 0;
        } // if col
        return line.charAt(col);
    }


    public Token next( ) { // Return next token
        // 문장에서 토큰을 읽어와서 식별하기 위한 함수
        do {
            if (isLetter(ch)) { // ident or keyword
                String spelling = concat(letters + digits);
                return Token.keyword(spelling);
            } else if (isDigit(ch)) { // int or float literal
                String number = concat(digits);
                if (ch != '.')  // int Literal
                    return Token.mkIntLiteral(number);
                number += concat(digits);
                return Token.mkFloatLiteral(number);
            } else switch (ch) {
            case ' ': case '\t': case '\r': case eolnCh:
                ch = nextChar();
                break;
                
            case '/':  // divide or comment
                ch = nextChar();
                if (ch != '/')  return Token.divideTok;
                // comment
                do {
                    ch = nextChar();
                } while (ch != eolnCh);
                ch = nextChar();
                break;
                
            case '\'':  // char literal
                char ch1 = nextChar();
                nextChar(); // get '
                ch = nextChar();
                return Token.mkCharLiteral("" + ch1);

            case eofCh: return Token.eofTok;
            
            case '+': ch = nextChar();
            	return Token.plusTok;
            // - * ( ) { } ; ,  student exercise
            case '-': ch = nextChar();
            	return Token.minusTok;
            case '*': ch = nextChar();
            	return Token.multiplyTok;
            case '(': ch = nextChar();
            	return Token.leftParenTok;
            case ')': ch = nextChar();
            	return Token.rightParenTok;
            case '{': ch = nextChar();
            	return Token.leftBraceTok;
            case '}': ch = nextChar();
            	return Token.rightBraceTok;
            case ';': ch = nextChar();
            	return Token.semicolonTok;
            case ',': ch = nextChar();
            	return Token.commaTok;

            case '&': check('&'); return Token.andTok;
            case '|': check('|'); return Token.orTok;

            case '=':
                return chkOpt('=', Token.assignTok,
                                   Token.eqeqTok);
            // < > !  student exercise
            case '<': 
            	return chkOpt('=', Token.ltTok,
                        		   Token.lteqTok);
            case '>': 
            	return chkOpt('=', Token.gtTok,
                        		   Token.gteqTok);	
            case '!': 
            	return chkOpt('=', Token.notTok,
                        		   Token.noteqTok);
            	
            default:  error("Illegal character " + ch);
            } // switch
        } while (true);
    } // next


    private boolean isLetter(char c) {
        // 알파벳인지 확인하는 함수
        return (c>='a' && c<='z' || c>='A' && c<='Z');
    }


    private boolean isDigit(char c) {
        // 입력받은 문자가 숫자인지 확인하는 함수
        return (c>='0' && c<='9'); // 0부터 9사이의 문자
    }
    

    private void check(char c) {
        // 다음에 올 문자가 c인지 확인
        ch = nextChar();
        if (ch != c)
            error("Illegal character, expecting " + c);
        ch = nextChar();
    }

    // student exercise
    private Token chkOpt(char c, Token one, Token two) {
    	ch = nextChar();
    	if (ch != c)
    		return one;
    	ch = nextChar();
        return two;
    }


    private String concat(String set) {
        // 문자열을 합치는 함수
        String r = "";
        do {
            r += ch;
            ch = nextChar();
        } while (set.indexOf(ch) >= 0);
        return r;
    }


    public void error (String msg) {
        // 에러 발생지점 알려주는 함수
        System.err.print(line);
        System.err.println("Error: column " + col + " " + msg);
        System.exit(1);
    }


    static public void main ( String[] argv ) {
        Lexer lexer = new Lexer(argv[0]);
        Token tok = lexer.next( );
        while (tok != Token.eofTok) {
            System.out.println("\t" + tok.toString());
            tok = lexer.next( );
        }
    } // main
}

