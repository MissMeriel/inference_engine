#########################################################################
# Makefile for a simple PA0 print(expr)* language
#   Wim Bohm, based on Michelle Strouts Circle example

all: mjparser/parser.java mjparser/Yylex.java parser

#### lexer Java
mjparser/Yylex.java: mjparser/prob.lex
	java -classpath ~/inference_engine -jar JLex.jar mjparser/prob.lex
	mv mjparser/prob.lex.java mjparser/Yylex.java

#### parser Java
mjparser/parser.java: mjparser/prob.cup
	java -classpath ~/inference_engine -jar java-cup-11a.jar  mjparser/prob.cup
	mv parser.java mjparser/ 
	mv sym.java mjparser/


#### parser
parser: mjparser/Yylex.java mjparser/parser.java
	javac -classpath .:java-cup-11a-runtime.jar:~/inference_engine/*:./mjparser/mjparser.jar -d . mjparser/parser.java mjparser/sym.java mjparser/Yylex.java

clean:
	rm mjparser/Yylex.class mjparser/parser.java
