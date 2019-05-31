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

%%
%cup
%line
%char
%public

%eofval{
  return new Symbol(sym.EOF, null);
%eofval}

%%
"GIVENS" { return new Symbol(sym.GIVENS, new SymbolValue(yyline+1, yychar+1, yytext())); }
"EVENTS" { return new Symbol(sym.EVENTS, new SymbolValue(yyline+1, yychar+1, yytext())); }
"," { return new Symbol(sym.COMMA, new SymbolValue(yyline+1, yychar+1, yytext())); }
";" { return new Symbol(sym.SEMI, new SymbolValue(yyline+1, yychar+1, yytext())); }
"<" { return new Symbol(sym.LT, new SymbolValue(yyline+1, yychar+1, yytext()));}
"<=" { return new Symbol(sym.LTEQ, new SymbolValue(yyline+1, yychar+1, yytext()));}
">" { return new Symbol(sym.GT, new SymbolValue(yyline+1, yychar+1, yytext()));}
">=" { return new Symbol(sym.GTEQ, new SymbolValue(yyline+1, yychar+1, yytext()));}
"INT" { return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
"DOUBLE" { return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
"STRING" { return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
([a-zA-Z]|_)([a-zA-Z]|[0-9]|[_])* {return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
[ \t\r\n\f] { /* ignore white space. */ }
-*[0-9]+ {return new Symbol(sym.NUMBER, new SymbolValue(yyline+1, yychar+1, yytext())); }
. { System.err.println("Illegal character: "+yytext()); }
(/\*([^*]|[\r\n]|(\*+([^*/]|[\r\n])))*\*+/)|(//.*)*)    { return new Symbol(sym.COMMENT, new SymbolValue(yyline+1, yychar+1, yytext())); }

