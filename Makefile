ifeq ($(OS),Windows_NT)
  CPS=;
else
  CPS=:
endif

ANTLR=../common/lib/antlr/org.antlr_2.7.6.jar

.PHONY: all clean schema parser

all: clean schema parser

clean:
#	rm -rf grammar/SLParser.java grammar/SLLexer.java grammar/SLParserTokenTypes.java
	rm -rf src/schema

parser:
#	rm -rf grammar/SLParser.java grammar/SLLexer.java grammar/SLParserTokenTypes.java
	java -cp "$(ANTLR)" antlr.Tool -o src/javaextractor src/javaextractor/java15.g 
	java -cp "$(ANTLR)" antlr.Tool -o src/javaextractor src/javaextractor/java15.tree.g 

schema:
	rm -rf src/schema
	java -cp ".$(CPS)../jgralab/bin$(CPS)../common/lib/getopt/java-getopt-1.0.13.jar" de.uni_koblenz.jgralab.utilities.tgschema2java.TgSchema2Java -f src/java5.tg -i array -p src
