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
"CONSTRAINTS" { return new Symbol(sym.CONSTRAINTS, new SymbolValue(yyline+1, yychar+1, yytext())); }
"P(" { return new Symbol(sym.P_LPAREN, new SymbolValue(yyline+1, yychar+1, yytext())); }
"|" { return new Symbol(sym.GIVEN, new SymbolValue(yyline+1, yychar+1, yytext())); }
")" { return new Symbol(sym.RPAREN, new SymbolValue(yyline+1, yychar+1, yytext())); }
"threshold" { return new Symbol(sym.THRESHOLD, new SymbolValue(yyline+1, yychar+1, yytext())); }
"delta" { return new Symbol(sym.DELTA, new SymbolValue(yyline+1, yychar+1, yytext())); }
":=" { return new Symbol(sym.DEF, new SymbolValue(yyline+1, yychar+1, yytext())); }
"," { return new Symbol(sym.COMMA, new SymbolValue(yyline+1, yychar+1, yytext())); }
";" { return new Symbol(sym.SEMI, new SymbolValue(yyline+1, yychar+1, yytext())); }
"==" { return new Symbol(sym.EQ, new SymbolValue(yyline+1, yychar+1, yytext())); }
"!=" { return new Symbol(sym.NOTEQ, new SymbolValue(yyline+1, yychar+1, yytext())); }
"<" { return new Symbol(sym.LT, new SymbolValue(yyline+1, yychar+1, yytext()));}
"<=" { return new Symbol(sym.LTEQ, new SymbolValue(yyline+1, yychar+1, yytext()));}
">" { return new Symbol(sym.GT, new SymbolValue(yyline+1, yychar+1, yytext()));}
">=" { return new Symbol(sym.GTEQ, new SymbolValue(yyline+1, yychar+1, yytext()));}
"&&" { return new Symbol(sym.LOGICAL_AND, new SymbolValue(yyline+1, yychar+1, yytext()));}
"||" { return new Symbol(sym.LOGICAL_OR, new SymbolValue(yyline+1, yychar+1, yytext()));}
"->" { return new Symbol(sym.STRING_TRANSITION, new SymbolValue(yyline+1, yychar+1, yytext()));}
"INT" { return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
"DOUBLE" { return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
"STRING" { return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
"INTEXP" { return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
"DOUBLEEXP" { return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
"STRINGEXP" { return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
"INTDELTA" { return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
"DOUBLEDELTA" { return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
"STRINGDELTA" { return new Symbol(sym.RAWTYPE, new SymbolValue(yyline+1, yychar+1, yytext())); }
([a-zA-Z]|[ \t])([a-zA-Z]|[0-9]|[ \t])* {return new Symbol(sym.STRING, new SymbolValue(yyline+1, yychar+1, yytext()));}
([a-zA-Z]|[_/])([a-zA-Z]|[0-9]|[_./])* {return new Symbol(sym.VARIABLE, new SymbolValue(yyline+1, yychar+1, yytext()));}
[ \t\r\n\f] { /* ignore white space. */ }
-*[0-9]+\.?[0-9]* {return new Symbol(sym.NUMBER, new SymbolValue(yyline+1, yychar+1, yytext())); }
. { System.err.println("Illegal character: "+yytext()); }
(/\*([^*]|[\r\n]|(\*+([^*/]|[\r\n])))*\*+/)|(//.*)*)    { return new Symbol(sym.COMMENT, new SymbolValue(yyline+1, yychar+1, yytext(), sym.COMMENT)); }

