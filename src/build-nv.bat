@echo off
echo --- Starting build --------------------------------
echo Adding this directory to the classpath
set CLASSPATH=.;jgralab\src\.;common\lib\getopt\java-getopt-1.0.13.jar;common\lib\antlr\org.antlr_2.7.6.jar;%CLASSPATH%
echo ---------------------------------------------------
echo Generating treewalker
::java antlr.Tool -o javaextractor javaextractor\java15.tree.g
echo ---------------------------------------------------
echo Building javaextractor
javac javaextractor\*.java
javac javaextractor\resolvers\*.java
::javac javaextractor\adapters\*.java
::javac javaextractor\comments\*.java
echo ---------------------------------------------------
echo Testing javaextractor with ...
::java -Xmx1G javaextractor.JavaExtractor -complete "jgralab\src\de\uni_koblenz\jgralab"
::java -Xmx1G -Xss8M javaextractor.JavaExtractor "javaextractor\schema\impl"
::java -Xmx1G javaextractor.JavaExtractor -complete "antlr"
java -Xmx1G javaextractor.JavaExtractor -complete "javaextractor/schema"
::java -Xmx1G javaextractor.JavaExtractor "..\testit\wsr08"
echo ---------------------------------------------------
echo Generating a .DOT file from extracted graph
java -Xmx1G de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot --graph extractedgraph.tg --output extractedgraph.dot --domains --edgeattr --rolenames --reversed
dot -Tpng -ograph.png extractedgraph.dot
echo ----------------------------------- Build done! ---
pause
