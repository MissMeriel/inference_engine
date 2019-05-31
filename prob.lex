/* PA0.lex */

/* Build instructions (as in Makefile)
   be in this (src) directory

    java -jar JLex.jar PA0.lex
    mv PA0.lex.java Yylex.java
    
*/
/* complete this ... */
/* ([a-zA-Z]|_)([a-zA-Z]|[0-9]|[_])* */

import java_cup.runtime.Symbol;
%%
%cup

%eofval{
  return new Symbol(sym.EOF, null);
%eofval}

%%
"GIVENS" { return new Symbol(sym.GIVENS, null); }
"EVENTS" { return new Symbol(sym.EVENTS, null); }
"," { return new Symbol(sym.COMMA, null); }
";" { return new Symbol(sym.SEMI, null); }
"<" { return new Symbol(sym.LT, null);}
"<=" { return new Symbol(sym.LTEQ, null);}
">" { return new Symbol(sym.GT, null);}
">=" { return new Symbol(sym.GTEQ, null);}
([a-zA-Z]|_)([a-zA-Z]|[0-9]|[_])* {return new Symbol(sym.VARIABLE, yytext());}
[ \t\r\n\f] { /* ignore white space. */ }
-*[0-9]+ {return new Symbol(sym.NUMBER, new Integer(yytext())); }
. { System.err.println("Illegal character: "+yytext()); }
(/\*([^*]|[\r\n]|(\*+([^*/]|[\r\n])))*\*+/)|(//.*)*)    { return new Symbol(sym.COMMENT, null); }

