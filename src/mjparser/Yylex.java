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
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NOT_ACCEPT,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NOT_ACCEPT,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NOT_ACCEPT,
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
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"37:9,38,35,37,38,35,37:18,38,21,37:8,40,37,19,36,37,39,34:10,17,20,22,18,23" +
",37:2,32,27,32,24,4,32,1,32,2,32:2,28,32,5,25,31,32,29,6,7,26,3,32,30,32:2," +
"37:4,33,37,16,32:2,15,11,32:2,9,32:3,14,32:2,13,32:2,10,12,8,32:6,37:5,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,73,
"0,1,2,3,1:2,4,5,6,1:6,7,8,9:5,10,9:2,11,12,13,14,1,15,16,17,18,7,19,1,20,21" +
",22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,9,43,44,45," +
"46,47,48,49,9,50,51,52,53")[0];

	private int yy_nxt[][] = unpackFromString(54,41,
"1,2,43,68,69,68,70,68,71,68:6,58,68,3,27,4,5,31,6,7,72,68:9,8,9,34,36,9,38," +
"36,-1:42,68,59,68:14,-1:7,68:9,60:2,-1:24,10,-1:40,13,-1:40,14,-1:56,8,-1:4" +
"0,8,-1,15,-1:5,68:3,46,68:12,-1:7,68:9,60:2,-1:7,68:16,-1:7,68:9,60:2,-1:7," +
"68:3,51,68:12,-1:7,68:9,60:2,-1:7,25:34,-1,25:5,-1,68:6,16,68:9,-1:7,68:9,6" +
"0:2,-1:24,11,-1:23,28:39,32,-1,68:15,17,-1:7,68:9,60:2,-1:24,12,-1:23,28:38" +
",29,32,-1,68:5,18,68:10,-1:7,68:9,60:2,-1:7,68:16,-1:7,68:7,19,68,60:2,-1:7" +
",68:5,20,68:10,-1:7,68:9,60:2,-1:45,25,28,-1,21,68:15,-1:7,68:9,60:2,-1:7,6" +
"8:3,22,68:12,-1:7,68:9,60:2,-1:7,68:14,23,68,-1:7,68:9,60:2,-1:7,68:16,-1:7" +
",68:7,24,68,60:2,-1:7,68:4,26,68:11,-1:7,68:9,60:2,-1:7,68:7,30,68:8,-1:7,6" +
"8:9,60:2,-1:7,68:4,33,68:11,-1:7,68:9,60:2,-1:7,68:16,-1:7,68:6,35,68:2,60:" +
"2,-1:7,68:6,37,68:9,-1:7,68:9,60:2,-1:7,68:4,39,68:11,-1:7,68:9,60:2,-1:7,6" +
"8:16,-1:7,68:4,40,68:4,60:2,-1:7,68:13,41,68:2,-1:7,68:9,60:2,-1:7,68:16,-1" +
":7,68:6,42,68:2,60:2,-1:7,68:13,44,68:2,-1:7,68:9,60:2,-1:7,68:3,45,68:12,-" +
"1:7,68:9,60:2,-1:7,68:4,47,68:11,-1:7,68:9,60:2,-1:7,68,48,68:14,-1:7,68:9," +
"60:2,-1:7,68:16,-1:7,68:3,49,68:5,60:2,-1:7,68:12,50,68:3,-1:7,68:9,60:2,-1" +
":7,68:10,52,68:5,-1:7,68:9,60:2,-1:7,68:2,53,68:13,-1:7,68:9,60:2,-1:7,68:3" +
",54,68:12,-1:7,68:9,60:2,-1:7,68:16,-1:7,68:5,55,68:3,60:2,-1:7,68:9,65,68:" +
"6,-1:7,68:9,60:2,-1:7,68:16,-1:7,68:2,56,68:6,60:2,-1:7,68:10,66,68:5,-1:7," +
"68:9,60:2,-1:7,68:11,67,68:4,-1:7,68:9,60:2,-1:7,68:8,57,68:7,-1:7,68:9,60:" +
"2,-1:7,68:2,61,68:13,-1:7,68:9,60:2,-1:7,68:6,62,68:9,-1:7,68:9,60:2,-1:7,6" +
"8:8,63,68:7,-1:7,68:9,60:2,-1:7,68:16,-1:7,68,64,68:7,60:2,-1:6");

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
						{ System.err.println("Illegal character: "+yytext()); }
					case -5:
						break;
					case 4:
						{ return new Symbol(sym.COMMA, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -6:
						break;
					case 5:
						{ return new Symbol(sym.SEMI, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -7:
						break;
					case 6:
						{ return new Symbol(sym.LT, new SymbolValue(yyline+1, yychar+1, yytext()));}
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
						{ return new Symbol(sym.DEF, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -12:
						break;
					case 11:
						{ return new Symbol(sym.EQ, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -13:
						break;
					case 12:
						{ return new Symbol(sym.NOTEQ, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -14:
						break;
					case 13:
						{ return new Symbol(sym.LTEQ, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -15:
						break;
					case 14:
						{ return new Symbol(sym.GTEQ, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -16:
						break;
					case 16:
						{ return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -17:
						break;
					case 17:
						{ return new Symbol(sym.DELTA, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -18:
						break;
					case 18:
						{ return new Symbol(sym.GIVENS, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -19:
						break;
					case 19:
						{ return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -20:
						break;
					case 20:
						{ return new Symbol(sym.EVENTS, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -21:
						break;
					case 21:
						{ return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -22:
						break;
					case 22:
						{ return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -23:
						break;
					case 23:
						{ return new Symbol(sym.THRESHOLD, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -24:
						break;
					case 24:
						{ return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -25:
						break;
					case 25:
						{ return new Symbol(sym.COMMENT, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -26:
						break;
					case 26:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -27:
						break;
					case 27:
						{ System.err.println("Illegal character: "+yytext()); }
					case -28:
						break;
					case 29:
						{ return new Symbol(sym.COMMENT, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -29:
						break;
					case 30:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -30:
						break;
					case 31:
						{ System.err.println("Illegal character: "+yytext()); }
					case -31:
						break;
					case 33:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -32:
						break;
					case 34:
						{ System.err.println("Illegal character: "+yytext()); }
					case -33:
						break;
					case 35:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -34:
						break;
					case 36:
						{ System.err.println("Illegal character: "+yytext()); }
					case -35:
						break;
					case 37:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -36:
						break;
					case 38:
						{ System.err.println("Illegal character: "+yytext()); }
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
					case 47:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -46:
						break;
					case 48:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -47:
						break;
					case 49:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -48:
						break;
					case 50:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -49:
						break;
					case 51:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -50:
						break;
					case 52:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -51:
						break;
					case 53:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -52:
						break;
					case 54:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -53:
						break;
					case 55:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -54:
						break;
					case 56:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -55:
						break;
					case 57:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -56:
						break;
					case 58:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -57:
						break;
					case 59:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -58:
						break;
					case 60:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -59:
						break;
					case 61:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -60:
						break;
					case 62:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -61:
						break;
					case 63:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -62:
						break;
					case 64:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -63:
						break;
					case 65:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -64:
						break;
					case 66:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -65:
						break;
					case 67:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -66:
						break;
					case 68:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -67:
						break;
					case 69:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -68:
						break;
					case 70:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -69:
						break;
					case 71:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -70:
						break;
					case 72:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -71:
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
