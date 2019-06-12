#########################################################################
# Makefile for inference engine and config file language parser
# Meriel Stein

all: mjparser/parser.java mjparser/Yylex.java parser engine

parser: mjparser/parser.java mjparser/Yylex.java

#### lexer Java
mjparser/Yylex.java: mjparser/prob.lex
	javac mjparser/Bound.java mjparser/BoundList.java
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

#### engine Java
engine: inference_engine/*.java
	javac -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/* inference_engine/*.java

clean:
	rm mjparser/Yylex.class mjparser/parser.java mjparser/*.class
