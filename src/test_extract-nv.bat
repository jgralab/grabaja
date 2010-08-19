@echo off
echo --- Starting build --------------------------------
echo Adding this directory to classpath
set CLASSPATH=.;jgralab\src\.;common\lib\getopt\java-getopt-1.0.13.jar;common\lib\antlr\org.antlr_2.7.6.jar;%CLASSPATH%
echo ---------------------------------------------------
echo Testing javaextractor with some stuff...
java -Xmx768M javaextractor.JavaExtractor javaextractor\schema\impl -out extractedtestgraph.tg -name "extrahiertes Test-Program... ;-)" -log testextract.log -eager
echo ---------------------------------------------------
echo Generating a .DOT file from extracted graph
java -Xmx768M de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot --graph extractedtestgraph.tg --output extractedtestgraph.dot --domains --edgeattr --rolenames --reversed
echo ----------------------------------- Build done! ---
pause
