/* PA0.lex */
/* Build instructions (as in Makefile)
   be in this (src) directory
    java -jar JLex.jar PA0.lex
    mv PA0.lex.java Yylex.java
*/
/* complete this ... */
/* ([a-zA-Z]|_)([a-zA-Z]|[0-9]|[_])* */
package mjparser;
import java_cup.runtime.Symbol;


public class Yylex implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private int yychar;
	private int yyline;
	private boolean yy_at_bol;
	private int yy_lexical_state;

	public Yylex (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	public Yylex (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private Yylex () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yychar = 0;
		yyline = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;
	}

	private boolean yy_eof_done = false;
	private final int YYINITIAL = 0;
	private final int yy_state_dtrans[] = {
		0
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		int i;
		for (i = yy_buffer_start; i < yy_buffer_index; ++i) {
			if ('\n' == yy_buffer[i] && !yy_last_was_cr) {
				++yyline;
			}
			if ('\r' == yy_buffer[i]) {
				++yyline;
				yy_last_was_cr=true;
			} else yy_last_was_cr=false;
		}
		yychar = yychar
			+ yy_buffer_index - yy_buffer_start;
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NO_ANCHOR,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NOT_ACCEPT,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NOT_ACCEPT,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NOT_ACCEPT,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"24:9,25,22,24,25,22,24:18,25,24:9,27,24,8,23,24,26,21:10,24,9,10,11,12,24:2" +
",19,16,19,13,4,19,1,19,2,19:2,17,19,5,14,19:2,18,6,7,15,3,19:4,24:4,20,24,1" +
"9:26,24:5,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,47,
"0,1,2,1:2,3,1,4,5,1:3,6,7:5,8,9,6,10,1,11,12,13,14,15,16,17,18,19,20,21,22," +
"23,24,25,26,7,27,28,29,7,30,31,32")[0];

	private int yy_nxt[][] = unpackFromString(33,28,
"1,2,29,43,44,43,45,43,3,4,5,6,7,46,43:7,8,9,20,6,9,24,6,-1:29,43,38,43:5,-1" +
":5,43:7,39:2,-1:17,10,-1:27,11,-1:37,8,-1:27,8,-1,12,-1:5,43:7,-1:5,43:7,39" +
":2,-1:7,18:21,-1,18:5,-1,43:6,13,-1:5,43:7,39:2,-1:7,21:26,25,-1,43:5,14,43" +
",-1:5,43:7,39:2,-1:32,18,21,-1,21:25,22,25,-1,43:5,15,43,-1:5,43:7,39:2,-1:" +
"7,16,43:6,-1:5,43:7,39:2,-1:7,43:3,17,43:3,-1:5,43:7,39:2,-1:7,43:4,19,43:2" +
",-1:5,43:7,39:2,-1:7,43:4,23,43:2,-1:5,43:7,39:2,-1:7,43:6,26,-1:5,43:7,39:" +
"2,-1:7,43:4,27,43:2,-1:5,43:7,39:2,-1:7,43:7,-1:5,43:4,28,43:2,39:2,-1:7,43" +
":3,30,43:3,-1:5,43:7,39:2,-1:7,43:4,31,43:2,-1:5,43:7,39:2,-1:7,43,32,43:5," +
"-1:5,43:7,39:2,-1:7,43:7,-1:5,43:3,33,43:3,39:2,-1:7,43:2,34,43:4,-1:5,43:7" +
",39:2,-1:7,43:3,35,43:3,-1:5,43:7,39:2,-1:7,43:7,-1:5,43:5,36,43,39:2,-1:7," +
"43:7,-1:5,43:2,37,43:4,39:2,-1:7,43:2,40,43:4,-1:5,43:7,39:2,-1:7,43:6,41,-" +
"1:5,43:7,39:2,-1:7,43:7,-1:5,43,42,43:5,39:2,-1:6");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

  return new Symbol(sym.EOF, null);
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 0:
						{ return new Symbol(sym.COMMENT, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -2:
						break;
					case 1:
						
					case -3:
						break;
					case 2:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -4:
						break;
					case 3:
						{ return new Symbol(sym.COMMA, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -5:
						break;
					case 4:
						{ return new Symbol(sym.SEMI, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -6:
						break;
					case 5:
						{ return new Symbol(sym.LT, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -7:
						break;
					case 6:
						{ System.err.println("Illegal character: "+yytext()); }
					case -8:
						break;
					case 7:
						{ return new Symbol(sym.GT, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -9:
						break;
					case 8:
						{return new Symbol(sym.NUMBER, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -10:
						break;
					case 9:
						{ /* ignore white space. */ }
					case -11:
						break;
					case 10:
						{ return new Symbol(sym.LTEQ, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -12:
						break;
					case 11:
						{ return new Symbol(sym.GTEQ, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -13:
						break;
					case 13:
						{ return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -14:
						break;
					case 14:
						{ return new Symbol(sym.GIVENS, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -15:
						break;
					case 15:
						{ return new Symbol(sym.EVENTS, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -16:
						break;
					case 16:
						{ return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -17:
						break;
					case 17:
						{ return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -18:
						break;
					case 18:
						{ return new Symbol(sym.COMMENT, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -19:
						break;
					case 19:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -20:
						break;
					case 20:
						{ System.err.println("Illegal character: "+yytext()); }
					case -21:
						break;
					case 22:
						{ return new Symbol(sym.COMMENT, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -22:
						break;
					case 23:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -23:
						break;
					case 24:
						{ System.err.println("Illegal character: "+yytext()); }
					case -24:
						break;
					case 26:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -25:
						break;
					case 27:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -26:
						break;
					case 28:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -27:
						break;
					case 29:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -28:
						break;
					case 30:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -29:
						break;
					case 31:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -30:
						break;
					case 32:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -31:
						break;
					case 33:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -32:
						break;
					case 34:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -33:
						break;
					case 35:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -34:
						break;
					case 36:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -35:
						break;
					case 37:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -36:
						break;
					case 38:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -37:
						break;
					case 39:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -38:
						break;
					case 40:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -39:
						break;
					case 41:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -40:
						break;
					case 42:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -41:
						break;
					case 43:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -42:
						break;
					case 44:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -43:
						break;
					case 45:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -44:
						break;
					case 46:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -45:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
