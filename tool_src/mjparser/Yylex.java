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
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NOT_ACCEPT,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
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
		/* 42 */ YY_NOT_ACCEPT,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NOT_ACCEPT,
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
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NO_ANCHOR,
		/* 91 */ YY_NO_ANCHOR,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NO_ANCHOR,
		/* 95 */ YY_NO_ANCHOR,
		/* 96 */ YY_NO_ANCHOR,
		/* 97 */ YY_NO_ANCHOR,
		/* 98 */ YY_NO_ANCHOR,
		/* 99 */ YY_NO_ANCHOR,
		/* 100 */ YY_NO_ANCHOR,
		/* 101 */ YY_NO_ANCHOR,
		/* 102 */ YY_NO_ANCHOR,
		/* 103 */ YY_NO_ANCHOR,
		/* 104 */ YY_NO_ANCHOR,
		/* 105 */ YY_NO_ANCHOR,
		/* 106 */ YY_NO_ANCHOR,
		/* 107 */ YY_NO_ANCHOR,
		/* 108 */ YY_NO_ANCHOR,
		/* 109 */ YY_NO_ANCHOR,
		/* 110 */ YY_NO_ANCHOR,
		/* 111 */ YY_NO_ANCHOR,
		/* 112 */ YY_NO_ANCHOR,
		/* 113 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"45:9,40,44,45,46,44,45:18,40,29,45:4,32,45,13,15,48,45,27,33,43,47,41:10,25" +
",28,30,26,31,45:2,11,36,8,34,4,39,1,39,2,39:2,37,39,5,9,12,39,10,6,7,35,3,3" +
"9,38,39:2,45:4,42,45,24,39:2,23,19,39:2,17,39:3,22,39:2,21,39:2,18,20,16,39" +
":6,45,14,45:3,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,114,
"0,1,2,1,3,1:3,4,5,6,7,1:10,8,9,10:4,11,12,10:7,13,14,15,16,17,18,1,19,20,21" +
",22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46" +
",47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,10,66,67,68,69,70" +
",71,72,73,74,75,76,77,78,79,80,81,10,82,83,84,85,86")[0];

	private int yy_nxt[][] = unpackFromString(87,49,
"1,2,65,108,109,108,110,108,111,108:3,38,3,4,5,112,108:6,89,108,39,45,6,7,49" +
",8,9,51,53,113,108:5,44,10,11,3,12,3,12,41,3,-1:50,108,90,108:10,-1:3,108:9" +
",-1:9,108:6,44,91,11:2,-1:3,11,-1:15,14,-1:60,18,-1:48,19,-1:63,10,-1,40,-1" +
":6,11:12,-1:3,11:9,-1:9,11:6,-1,11:3,-1:3,11,-1:34,22,-1:7,10,-1:8,108:3,68" +
",108:8,-1:3,108:9,-1:9,99,108:5,44,91,11:2,-1:3,11,-1:2,108:12,-1:3,108:9,-" +
"1:9,108:6,44,91,11:2,-1:3,11,-1:2,108:3,73,108:8,-1:3,108:9,-1:9,105,108:5," +
"44,91,11:2,-1:3,11,-1:2,108:3,75,108:8,-1:3,108:9,-1:9,107,108:5,44,91,11:2" +
",-1:3,11,-1:2,37:43,-1,37:4,-1,108:12,13,-1:2,108:9,-1:9,108:6,44,91,11:2,-" +
"1:3,11,-1:27,15,-1:63,40,-1:8,11:12,-1:3,11:9,-1:9,11:6,-1,11:3,-1:3,46,42," +
"-1,42:47,47,-1,44:12,-1:3,44:9,-1:9,44:8,-1:33,16,-1:23,46:12,37:3,46:9,37:" +
"9,46:6,37,46:3,-1,37:2,46,37,-1,42:46,43,47,-1,108:6,23,108:5,-1:3,108:9,-1" +
":9,108:6,44,91,11:2,-1:3,11,-1:27,17,-1:23,108:12,-1:3,108:8,24,-1:9,108:6," +
"44,91,11:2,-1:3,11,-1:33,20,-1:17,108:5,25,108:6,-1:3,108:9,-1:9,108:6,44,9" +
"1,11:2,-1:3,11,-1:32,21,-1,22,-1:7,10,-1:8,108:11,26,-1:3,108:9,-1:9,108:6," +
"44,91,11:2,-1:3,11,-1:2,108:5,27,108:6,-1:3,108:9,-1:9,108:6,44,91,11:2,-1:" +
"3,11,-1:2,28,108:11,-1:3,108:9,-1:9,108:6,44,91,11:2,-1:3,11,-1:2,108:3,29," +
"108:8,-1:3,108:9,-1:9,108:6,44,91,11:2,-1:3,11,-1:2,108:10,30,108,-1:3,108:" +
"9,-1:9,108:6,44,91,11:2,-1:3,11,-1:2,108:11,31,-1:3,108:9,-1:9,108:6,44,91," +
"11:2,-1:3,11,-1:2,108:12,-1:3,108:7,32,108,-1:9,108:6,44,91,11:2,-1:3,11,-1" +
":2,108:11,33,-1:3,108:9,-1:9,108:6,44,91,11:2,-1:3,11,-1:2,108:10,34,108,-1" +
":3,108:9,-1:9,108:6,44,91,11:2,-1:3,11,-1:2,108:5,35,108:6,-1:3,108:9,-1:9," +
"108:6,44,91,11:2,-1:3,11,-1:2,108:10,36,108,-1:3,108:9,-1:9,108:6,44,91,11:" +
"2,-1:3,11,-1:2,108:4,48,108:7,-1:3,108:9,-1:9,108:6,44,91,11:2,-1:3,11,-1:2" +
",108:12,-1:3,50,108:8,-1:9,108:6,44,91,11:2,-1:3,11,-1:2,108:4,52,108:7,-1:" +
"3,108:9,-1:9,108:6,44,91,11:2,-1:3,11,-1:2,108:12,-1:3,108:9,-1:9,108:4,54," +
"108,44,91,11:2,-1:3,11,-1:2,108:6,55,108:5,-1:3,108:9,-1:9,108:6,44,91,11:2" +
",-1:3,11,-1:2,108:4,56,108:7,-1:3,108:9,-1:9,108:6,44,91,11:2,-1:3,11,-1:2," +
"108:12,-1:3,108:9,-1:9,108:3,57,108:2,44,91,11:2,-1:3,11,-1:2,108:6,58,108:" +
"5,-1:3,108:9,-1:9,108:6,44,91,11:2,-1:3,11,-1:2,108:12,-1:3,108:9,-1:9,108:" +
"4,59,108,44,91,11:2,-1:3,11,-1:2,108:12,-1:3,108:6,60,108:2,-1:9,108:6,44,9" +
"1,11:2,-1:3,11,-1:2,108:12,-1:3,108:9,-1:9,108:4,61,108,44,91,11:2,-1:3,11," +
"-1:2,108:6,62,108:5,-1:3,108:9,-1:9,108:6,44,91,11:2,-1:3,11,-1:2,108:6,63," +
"108:5,-1:3,108:9,-1:9,108:6,44,91,11:2,-1:3,11,-1:2,108:6,64,108:5,-1:3,108" +
":9,-1:9,108:6,44,91,11:2,-1:3,11,-1:2,108:12,-1:3,108:6,66,108:2,-1:9,108:6" +
",44,91,11:2,-1:3,11,-1:2,108:3,67,108:8,-1:3,108:9,-1:9,108:6,44,91,11:2,-1" +
":3,11,-1:2,108:4,69,108:7,-1:3,108:9,-1:9,108:6,44,91,11:2,-1:3,11,-1:2,108" +
",70,108:10,-1:3,108:9,-1:9,108:6,44,91,11:2,-1:3,11,-1:2,108:12,-1:3,108:9," +
"-1:9,108:2,71,108:3,44,91,11:2,-1:3,11,-1:2,108:12,-1:3,108:9,-1:9,108:3,72" +
",108:2,44,91,11:2,-1:3,11,-1:2,108:12,-1:3,108:5,74,108:3,-1:9,108:6,44,91," +
"11:2,-1:3,11,-1:2,108:12,-1:3,108:9,-1:9,108:3,76,108:2,44,91,11:2,-1:3,11," +
"-1:2,108:4,77,108:7,-1:3,108:9,-1:9,108:6,44,91,11:2,-1:3,11,-1:2,108:12,-1" +
":3,108:9,-1:9,108:3,78,108:2,44,91,11:2,-1:3,11,-1:2,108:12,-1:3,108:3,79,1" +
"08:5,-1:9,108:6,44,91,11:2,-1:3,11,-1:2,108:2,80,108:9,-1:3,108:9,-1:9,108:" +
"6,44,91,11:2,-1:3,11,-1:2,108:3,81,108:8,-1:3,108:9,-1:9,108:6,44,91,11:2,-" +
"1:3,11,-1:2,108:9,82,108:2,-1:3,108:9,-1:9,108:6,44,91,11:2,-1:3,11,-1:2,10" +
"8:4,97,108:7,-1:3,108:9,-1:9,108:6,44,91,11:2,-1:3,11,-1:2,108:12,-1:3,108:" +
"2,98,108:6,-1:9,108:6,44,91,11:2,-1:3,11,-1:2,108:12,-1:3,108:9,-1:9,108,83" +
",108:4,44,91,11:2,-1:3,11,-1:2,108:5,100,108:6,-1:3,108:9,-1:9,108:6,44,91," +
"11:2,-1:3,11,-1:2,108:12,-1:3,108:3,101,108:5,-1:9,108:6,44,91,11:2,-1:3,11" +
",-1:2,108:3,84,108:8,-1:3,108:9,-1:9,108:6,44,91,11:2,-1:3,11,-1:2,108:6,10" +
"2,108:5,-1:3,108:9,-1:9,108:6,44,91,11:2,-1:3,11,-1:2,108:12,-1:3,108:4,103" +
",108:4,-1:9,108:6,44,91,11:2,-1:3,11,-1:2,108:9,104,108:2,-1:3,108:9,-1:9,1" +
"08:6,44,91,11:2,-1:3,11,-1:2,108:12,-1:3,108,85,108:7,-1:9,108:6,44,91,11:2" +
",-1:3,11,-1:2,108:10,106,108,-1:3,108:9,-1:9,108:6,44,91,11:2,-1:3,11,-1:2," +
"108:3,86,108:8,-1:3,108:9,-1:9,108:6,44,91,11:2,-1:3,11,-1:2,108,87,108:10," +
"-1:3,108:9,-1:9,108:6,44,91,11:2,-1:3,11,-1:2,108:3,88,108:8,-1:3,108:9,-1:" +
"9,108:6,44,91,11:2,-1:3,11,-1:2,108:2,92,108:9,-1:3,108:9,-1:9,108:6,44,91," +
"11:2,-1:3,11,-1:2,108:6,93,108:5,-1:3,108:9,-1:9,108:6,44,91,11:2,-1:3,11,-" +
"1:2,108:8,94,108:3,-1:3,108:9,-1:9,108:6,44,91,11:2,-1:3,11,-1:2,108:12,-1:" +
"3,108,95,108:7,-1:9,108:6,44,91,11:2,-1:3,11,-1:2,108:8,96,108:3,-1:3,108:9" +
",-1:9,108:6,44,91,11:2,-1:3,11,-1");

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
						{ return new Symbol(sym.COMMENT, new SymbolValue(yyline+1, yychar+1, yytext(), sym.COMMENT)); }
					case -2:
						break;
					case 1:
						
					case -3:
						break;
					case 2:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -4:
						break;
					case 3:
						{ System.err.println("Illegal character: "+yytext()); }
					case -5:
						break;
					case 4:
						{ return new Symbol(sym.GIVEN, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -6:
						break;
					case 5:
						{ return new Symbol(sym.RPAREN, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -7:
						break;
					case 6:
						{ return new Symbol(sym.COMMA, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -8:
						break;
					case 7:
						{ return new Symbol(sym.SEMI, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -9:
						break;
					case 8:
						{ return new Symbol(sym.LT, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -10:
						break;
					case 9:
						{ return new Symbol(sym.GT, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -11:
						break;
					case 10:
						{return new Symbol(sym.NUMBER, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -12:
						break;
					case 11:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -13:
						break;
					case 12:
						{ /* ignore white space. */ }
					case -14:
						break;
					case 13:
						{ return new Symbol(sym.P_LPAREN, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -15:
						break;
					case 14:
						{ return new Symbol(sym.LOGICAL_OR, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -16:
						break;
					case 15:
						{ return new Symbol(sym.DEF, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -17:
						break;
					case 16:
						{ return new Symbol(sym.EQ, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -18:
						break;
					case 17:
						{ return new Symbol(sym.NOTEQ, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -19:
						break;
					case 18:
						{ return new Symbol(sym.LTEQ, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -20:
						break;
					case 19:
						{ return new Symbol(sym.GTEQ, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -21:
						break;
					case 20:
						{ return new Symbol(sym.LOGICAL_AND, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -22:
						break;
					case 21:
						{ return new Symbol(sym.STRING_TRANSITION, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -23:
						break;
					case 23:
						{ return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -24:
						break;
					case 24:
						{ return new Symbol(sym.DELTA, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -25:
						break;
					case 25:
						{ return new Symbol(sym.GIVENS, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -26:
						break;
					case 26:
						{ return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -27:
						break;
					case 27:
						{ return new Symbol(sym.EVENTS, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -28:
						break;
					case 28:
						{ return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -29:
						break;
					case 29:
						{ return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -30:
						break;
					case 30:
						{ return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -31:
						break;
					case 31:
						{ return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -32:
						break;
					case 32:
						{ return new Symbol(sym.THRESHOLD, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -33:
						break;
					case 33:
						{ return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -34:
						break;
					case 34:
						{ return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -35:
						break;
					case 35:
						{ return new Symbol(sym.CONSTRAINTS, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -36:
						break;
					case 36:
						{ return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -37:
						break;
					case 37:
						{ return new Symbol(sym.COMMENT, new SymbolValue(yyline+1, yychar+1, yytext(), sym.COMMENT)); }
					case -38:
						break;
					case 38:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -39:
						break;
					case 39:
						{ System.err.println("Illegal character: "+yytext()); }
					case -40:
						break;
					case 40:
						{return new Symbol(sym.NUMBER, new SymbolValue(yyline+1, yychar+1, yytext())); }
					case -41:
						break;
					case 41:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -42:
						break;
					case 43:
						{ return new Symbol(sym.COMMENT, new SymbolValue(yyline+1, yychar+1, yytext(), sym.COMMENT)); }
					case -43:
						break;
					case 44:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -44:
						break;
					case 45:
						{ System.err.println("Illegal character: "+yytext()); }
					case -45:
						break;
					case 46:
						{return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -46:
						break;
					case 48:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -47:
						break;
					case 49:
						{ System.err.println("Illegal character: "+yytext()); }
					case -48:
						break;
					case 50:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -49:
						break;
					case 51:
						{ System.err.println("Illegal character: "+yytext()); }
					case -50:
						break;
					case 52:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -51:
						break;
					case 53:
						{ System.err.println("Illegal character: "+yytext()); }
					case -52:
						break;
					case 54:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -53:
						break;
					case 55:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -54:
						break;
					case 56:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -55:
						break;
					case 57:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -56:
						break;
					case 58:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -57:
						break;
					case 59:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -58:
						break;
					case 60:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -59:
						break;
					case 61:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -60:
						break;
					case 62:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -61:
						break;
					case 63:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -62:
						break;
					case 64:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -63:
						break;
					case 65:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -64:
						break;
					case 66:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -65:
						break;
					case 67:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -66:
						break;
					case 68:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -67:
						break;
					case 69:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -68:
						break;
					case 70:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -69:
						break;
					case 71:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -70:
						break;
					case 72:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -71:
						break;
					case 73:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -72:
						break;
					case 74:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -73:
						break;
					case 75:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -74:
						break;
					case 76:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -75:
						break;
					case 77:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -76:
						break;
					case 78:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -77:
						break;
					case 79:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -78:
						break;
					case 80:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -79:
						break;
					case 81:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -80:
						break;
					case 82:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -81:
						break;
					case 83:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -82:
						break;
					case 84:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -83:
						break;
					case 85:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -84:
						break;
					case 86:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -85:
						break;
					case 87:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -86:
						break;
					case 88:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -87:
						break;
					case 89:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -88:
						break;
					case 90:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -89:
						break;
					case 91:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -90:
						break;
					case 92:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -91:
						break;
					case 93:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -92:
						break;
					case 94:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -93:
						break;
					case 95:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -94:
						break;
					case 96:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -95:
						break;
					case 97:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -96:
						break;
					case 98:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -97:
						break;
					case 99:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -98:
						break;
					case 100:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -99:
						break;
					case 101:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -100:
						break;
					case 102:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -101:
						break;
					case 103:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -102:
						break;
					case 104:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -103:
						break;
					case 105:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -104:
						break;
					case 106:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -105:
						break;
					case 107:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -106:
						break;
					case 108:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -107:
						break;
					case 109:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -108:
						break;
					case 110:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -109:
						break;
					case 111:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -110:
						break;
					case 112:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -111:
						break;
					case 113:
						{return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
					case -112:
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
